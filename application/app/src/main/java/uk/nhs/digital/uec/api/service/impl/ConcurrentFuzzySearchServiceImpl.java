package uk.nhs.digital.uec.api.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.DosServiceSearchException;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.ConcurrentFuzzySearchService;
import uk.nhs.digital.uec.api.service.DosServiceSearch;
import uk.nhs.digital.uec.api.service.NHSChoicesSearchService;
import uk.nhs.digital.uec.api.util.PostcodeFormatterUtil;

@Service
@Slf4j
public class ConcurrentFuzzySearchServiceImpl implements ConcurrentFuzzySearchService {

  private final DosServiceSearch dosSearchService;

  // private final NHSChoicesSearchService nhsChoicesSearchService;

  @Autowired
  public ConcurrentFuzzySearchServiceImpl(
      DosServiceSearch dosSearchService, NHSChoicesSearchService nhsChoicesSearchService) {
    this.dosSearchService = dosSearchService;
    // this.nhsChoicesSearchService = nhsChoicesSearchService;
  }

  @Override
  @Async
  public CompletableFuture<List<DosService>> fuzzySearch(
      String searchLatitude,
      String searchLongitude,
      Double distanceRange,
      List<String> searchTerms,
      String searchPostcode,
      Integer maxNumServicesToReturn)
      throws NotFoundException {
    log.info("Init NHS choices async call");

    // Format the searchPostcode using the PostcodeFormatterUtil
    String formattedPostcode = PostcodeFormatterUtil.formatPostcode(searchPostcode);

    // Commented out NHS Choices search until API issue is resolved
    CompletableFuture<List<DosService>> nhsChoicesServicesFuture =
        CompletableFuture.completedFuture(Collections.emptyList());
    // nhsChoicesSearchService.retrieveParsedNhsChoicesV2Model(
    //   searchLatitude, searchLongitude, searchTerms, formattedPostcode, maxNumServicesToReturn
    // );

    log.info("Init DOS services async call");
    CompletableFuture<List<DosService>> dosServicesFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  try {
                    return dosSearchService.retrieveServicesByGeoLocation(
                        searchLatitude,
                        searchLongitude,
                        distanceRange,
                        searchTerms,
                        formattedPostcode);
                  } catch (NotFoundException | InvalidParameterException e) {
                    log.error("Error retrieving DOS services", e);
                    throw new DosServiceSearchException("Error retrieving DOS services", e);
                  }
                })
            .exceptionally(
                ex -> {
                  log.error("Error in DOS services search", ex);
                  return Collections.emptyList();
                });

    return CompletableFuture.allOf(dosServicesFuture, nhsChoicesServicesFuture)
        .thenApply(
            ignored -> {
              log.info("Combining results");

              List<DosService> combinedList =
                  Stream.concat(
                          dosServicesFuture.join().stream(),
                          nhsChoicesServicesFuture.join().stream())
                      .collect(Collectors.toList());

              log.info("Number of DOS Services: {}", dosServicesFuture.join().size());
              log.info(
                  "Number of NHS Choices Services: {}", nhsChoicesServicesFuture.join().size());

              log.info("Sorting services based on distance in ascending order.");
              combinedList.sort(Comparator.comparingDouble(DosService::getDistance));

              log.info("Services sorted successfully based on distance.");

              return combinedList;
            });
  }
}
