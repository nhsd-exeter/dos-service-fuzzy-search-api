package uk.nhs.digital.uec.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.service.DosServiceSearch;
import uk.nhs.digital.uec.api.service.NHSChoicesSearchService;

class ConcurrentFuzzySearchServiceImplTest {

  @Mock private DosServiceSearch dosSearchService;

  @Mock private NHSChoicesSearchService nhsChoicesSearchService;

  @InjectMocks private ConcurrentFuzzySearchServiceImpl fuzzySearchService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName(
      "Assert that when fuzzySearch method when invoked, will combine list of DosServices and work"
          + " accordingly even when list from the sources are empty")
  void fuzzySearch()
      throws ExecutionException, InterruptedException, InvalidParameterException,
          NotFoundException {
    String searchLatitude = "53.4817";
    String searchLongitude = "2.2346";
    Double distanceRange = 10.0;
    List<String> searchTerms = Arrays.asList("term1", "term2");
    String searchPostcode = "M1 1EZ";
    Integer maxNumberOfServicesTOReturn = 50;

    List<DosService> dosServiceList = new ArrayList<>();
    List<NHSChoicesV2DataModel> nhsChoicesList = new ArrayList<>();

    when(dosSearchService.retrieveServicesByGeoLocation(
            anyString(), anyString(), anyDouble(), anyList(), anyString()))
        .thenAnswer(invocation -> CompletableFuture.completedFuture(dosServiceList));

    when(nhsChoicesSearchService.retrieveParsedNhsChoicesV2Model(
            anyString(), anyString(), anyList(), anyString(), anyInt()))
        .thenAnswer(invocation -> CompletableFuture.completedFuture(nhsChoicesList));

    CompletableFuture<List<DosService>> result =
        fuzzySearchService.fuzzySearch(
            searchLatitude,
            searchLongitude,
            distanceRange,
            searchTerms,
            searchPostcode,
            maxNumberOfServicesTOReturn);

    List<DosService> combinedList = result.get();

    assertEquals(0, combinedList.size());
  }

  @Test
  @DisplayName(
      "Assert that when one of the services fails, the fuzzySearch method handles the exception and"
          + " returns an empty list")
  void fuzzySearchExceptionHandling()
      throws ExecutionException, InterruptedException, InvalidParameterException,
          NotFoundException {
    // Arrange
    String searchLatitude = "53.4817";
    String searchLongitude = "2.2346";
    Double distanceRange = 10.0;
    List<String> searchTerms = Arrays.asList("term1", "term2");
    String searchPostcode = "M1 1EZ";
    Integer maxNumberOfServicesTOReturn = 50;

    when(dosSearchService.retrieveServicesByGeoLocation(
            anyString(), anyString(), anyDouble(), anyList(), anyString()))
        .thenAnswer(
            invocation ->
                CompletableFuture.supplyAsync(
                    () -> {
                      throw new RuntimeException("DOS service not found");
                    }));

    when(nhsChoicesSearchService.retrieveParsedNhsChoicesV2Model(
            anyString(), anyString(), anyList(), anyString(), anyInt()))
        .thenAnswer(invocation -> CompletableFuture.completedFuture(new ArrayList<>()));

    CompletableFuture<List<DosService>> result =
        fuzzySearchService.fuzzySearch(
            searchLatitude,
            searchLongitude,
            distanceRange,
            searchTerms,
            searchPostcode,
            maxNumberOfServicesTOReturn);

    assertEquals(0, result.get().size());
  }
}
