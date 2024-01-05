package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.ConcurrentFuzzySearchService;
import uk.nhs.digital.uec.api.service.DosServiceSearch;
import uk.nhs.digital.uec.api.service.NHSChoicesSearchService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ConcurrentFuzzySearchServiceImpl implements ConcurrentFuzzySearchService {
  @Autowired
  private DosServiceSearch dosSearchService;
  @Autowired
  private NHSChoicesSearchService nhsChoicesSearchService;

  @Override
//  @Async("fuzzyTaskExecutor")
  public CompletableFuture<List<DosService>> fuzzySearch(String searchLatitude, String searchLongitude, Double distanceRange, List<String> searchTerms, String searchPostcode) throws NotFoundException {
    log.info("Init NHS choices async call");
    CompletableFuture<List<DosService>> nhsChoicesModelMappedToDosServicesList = nhsChoicesSearchService.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, searchTerms, searchPostcode
    );

    log.info("Init DOS services async call");
    CompletableFuture<List<DosService>> dosServicesList = CompletableFuture.supplyAsync(() -> {
      try {
        return dosSearchService.retrieveServicesByGeoLocation(searchLatitude, searchLongitude, distanceRange, searchTerms, searchPostcode);
      } catch (NotFoundException | InvalidParameterException e) {
        log.error("An error occurred whilst retrieving DOS services from datastore");
        throw new RuntimeException(e);
      }
    }).exceptionally(ex -> {
      log.error("Error in DOS services search", ex);
      return Collections.emptyList();
    });

    return dosServicesList.thenCombine(nhsChoicesModelMappedToDosServicesList, (dosServices, nhsChoicesServices) -> {
      final boolean didDOSFail = dosServicesList.isCompletedExceptionally();
      final boolean didNHSFail = nhsChoicesModelMappedToDosServicesList.isCompletedExceptionally();
      if (didNHSFail) {
        log.error("Could not retrieve NHS choices Services");
      }

      if (didDOSFail) {
        log.error("Could not retrieve DOS Services");
      }


      List<DosService> combinedList = Stream.concat(
          didDOSFail ? Stream.empty() : dosServices.stream(),
          didNHSFail ? Stream.empty() : nhsChoicesServices.stream())
        .sorted(Comparator.comparingDouble(service -> sortByDistanceFromSearch(Double.parseDouble(searchLatitude),
          Double.parseDouble(searchLongitude),
          service.getLocation().getLat(), service.getLocation().getLon())))
        .collect(Collectors.toList());

      log.info("Services sorted successfully based on distance.");

      return combinedList;
    });

  }

  private double sortByDistanceFromSearch(Double searchLatitude, Double searchLongitude, Double serviceLatitude, Double serviceLongitude) {
    final double EARTH_RADIUS_KM = 6371;

    try {
      // Convert degrees to radians
      double lat1 = Math.toRadians(searchLatitude);
      double lon1 = Math.toRadians(searchLongitude);
      double lat2 = Math.toRadians(serviceLatitude);
      double lon2 = Math.toRadians(serviceLongitude);

      // Haversine formula
      double dLat = lat2 - lat1;
      double dLon = lon2 - lon1;
      double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(lat1) * Math.cos(lat2)
        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      return EARTH_RADIUS_KM * c;
    } catch (Exception e) {
      log.error("Error occurred while sorting by distance.", e);

      return Double.MAX_VALUE;
    }
  }

}
