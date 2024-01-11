package uk.nhs.digital.uec.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.ConcurrentFuzzySearchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class FuzzySearchConcurrentSearchControllerTest {
  @Mock
  private ConcurrentFuzzySearchService concurrentFuzzySearchService;
  @Mock
  private ApiUtilsServiceInterface utils;
  @Mock
  private ApiRequestParams requestParams;
  @InjectMocks
  private FuzzySearchConcurrentSearchController fuzzySearchController;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Disabled
  @Test
  @DisplayName("Assert that getServicesByFuzzySearch when is invoked with the expected parameters it will execute and return an asynchronous result")
  void getServicesByFuzzySearch() throws Exception {
    String searchLatitude = "53.4817";
    String searchLongitude = "2.2346";
    Double distanceRange = 10.0;
    List<String> searchCriteria = Collections.singletonList("term1");
    String searchPostcode = "M1 1EZ";

    List<DosService> dosServiceList = new ArrayList<>();
    DosService dosService = new DosService();

    List<NHSChoicesV2DataModel> nhsChoicesList = new ArrayList<>();
    NHSChoicesV2DataModel nhsChoicesDataModel = new NHSChoicesV2DataModel();

    when(concurrentFuzzySearchService.fuzzySearch(anyString(), anyString(), anyDouble(), anyList(), anyString(),anyInt()))
      .thenReturn(CompletableFuture.completedFuture(dosServiceList));

    CompletableFuture<ResponseEntity<ApiResponse>> resultFuture = fuzzySearchController.getServicesByFuzzySearch(
      searchCriteria, searchPostcode, searchLatitude, searchLongitude,
      distanceRange, null, null,  null, null, null, null, null, null);

    // Get the result from CompletableFuture
    ResponseEntity<ApiResponse> result = resultFuture.get();

    //assert that fuzzySearch method is invoked with the expected parameters
    verify(concurrentFuzzySearchService).fuzzySearch(
      eq(searchLatitude), eq(searchLongitude), eq(distanceRange), eq(searchCriteria), eq(searchPostcode), anyInt());

    //assert that the result is completed asynchronously without an exception
    assertEquals(200, result.getStatusCodeValue());
    assertFalse(resultFuture.isCompletedExceptionally());
  }

}
