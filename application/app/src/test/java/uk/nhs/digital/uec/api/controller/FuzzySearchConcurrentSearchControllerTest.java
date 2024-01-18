package uk.nhs.digital.uec.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.ConcurrentFuzzySearchService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class FuzzySearchConcurrentSearchControllerTest {
  @Mock
  private ConcurrentFuzzySearchService concurrentFuzzySearchService;
  @Mock
  private ApiUtilsServiceInterface utils;
  @Mock
  private ApiRequestParams requestParams;
  @InjectMocks
  private FuzzySearchConcurrentSearchController fuzzySearchController;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  void getServicesByFuzzySearch() throws InterruptedException, ExecutionException, InvalidParameterException, NotFoundException {
    List<String> searchCriteria = List.of("term1", "term2");
    String searchPostcode = "WV10 6BZ";
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    Double distanceRange = 10.0;
    Integer maxNumServicesToReturn = 50;
    Integer fuzzLevel = 2;
    Integer namePriority = 1;
    Integer addressPriority = 2;
    Integer postcodePriority = 3;
    Integer publicNamePriority = 4;

    doNothing().when(utils).configureApiRequestParams(
      eq(fuzzLevel), any(), any(), eq(maxNumServicesToReturn),
      eq(namePriority), eq(addressPriority), eq(postcodePriority), eq(publicNamePriority));

    List<DosService> mockDosServices = Collections.singletonList(new DosService());
    CompletableFuture<List<DosService>> dosServicesFuture = CompletableFuture.completedFuture(mockDosServices);

    when(concurrentFuzzySearchService.fuzzySearch(eq(searchLatitude), eq(searchLongitude), eq(distanceRange),
      eq(searchCriteria), eq(searchPostcode), eq(maxNumServicesToReturn)))
      .thenReturn(dosServicesFuture);

    ResponseEntity<ApiResponse> responseEntity = fuzzySearchController.getServicesByFuzzySearch(
      searchCriteria, searchPostcode, searchLatitude, searchLongitude, distanceRange, null,
      null, maxNumServicesToReturn, fuzzLevel, namePriority, addressPriority,
      postcodePriority, publicNamePriority).get();

    assertNotNull(responseEntity);
    assertEquals(200, responseEntity.getStatusCodeValue());

    ApiResponse response = responseEntity.getBody();
      assert response != null;
      assertNotNull(response);

    verify(utils).configureApiRequestParams(
      eq(fuzzLevel), any(), any(), eq(maxNumServicesToReturn),
      eq(namePriority), eq(addressPriority), eq(postcodePriority), eq(publicNamePriority));
  }

  @Test
  void getServicesByFuzzySearch_testMaximumNumberOfServices() throws InterruptedException, ExecutionException, InvalidParameterException, NotFoundException {
    List<String> searchCriteria = List.of("term1", "term2");
    String searchPostcode = "WV10 6BZ";
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    Double distanceRange = 10.0;
    Integer maxNumServicesToReturn = Integer.MAX_VALUE;
    Integer fuzzLevel = 2;
    Integer namePriority = 1;
    Integer addressPriority = 2;
    Integer postcodePriority = 3;
    Integer publicNamePriority = 4;

    doNothing().when(utils).configureApiRequestParams(
      eq(fuzzLevel), any(), any(), eq(maxNumServicesToReturn),
      eq(namePriority), eq(addressPriority), eq(postcodePriority), eq(publicNamePriority));

    List<DosService> mockDosServices = Collections.singletonList(new DosService());
    CompletableFuture<List<DosService>> dosServicesFuture = CompletableFuture.completedFuture(mockDosServices);

    when(concurrentFuzzySearchService.fuzzySearch(eq(searchLatitude), eq(searchLongitude), eq(distanceRange),
      eq(searchCriteria), eq(searchPostcode), eq(maxNumServicesToReturn)))
      .thenReturn(dosServicesFuture);

    ResponseEntity<ApiResponse> responseEntity = fuzzySearchController.getServicesByFuzzySearch(
      searchCriteria, searchPostcode, searchLatitude, searchLongitude, distanceRange, null,
      null, maxNumServicesToReturn, fuzzLevel, namePriority, addressPriority,
      postcodePriority, publicNamePriority).get();

    assertNotNull(responseEntity);
    assertEquals(200, responseEntity.getStatusCodeValue());

    ApiResponse response = responseEntity.getBody();
      assert response != null;
      assertNotNull(response);

    verify(utils).configureApiRequestParams(
      eq(fuzzLevel), any(), any(), eq(maxNumServicesToReturn),
      eq(namePriority), eq(addressPriority), eq(postcodePriority), eq(publicNamePriority));
  }

  @Test
  void getServicesByFuzzySearch_testConcurrentRequests() throws InterruptedException, ExecutionException, InvalidParameterException, NotFoundException {
    List<String> searchCriteria = List.of("term1", "term2");
    String searchPostcode = "WV10 6BZ";
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    Double distanceRange = 10.0;
    Integer maxNumServicesToReturn = 50;
    Integer fuzzLevel = 2;
    Integer namePriority = 1;
    Integer addressPriority = 2;
    Integer postcodePriority = 3;
    Integer publicNamePriority = 4;

    doNothing().when(utils).configureApiRequestParams(
      eq(fuzzLevel), any(), any(), eq(maxNumServicesToReturn),
      eq(namePriority), eq(addressPriority), eq(postcodePriority), eq(publicNamePriority));

    List<DosService> mockDosServices = Collections.singletonList(new DosService());
    CompletableFuture<List<DosService>> dosServicesFuture = CompletableFuture.completedFuture(mockDosServices);

    when(concurrentFuzzySearchService.fuzzySearch(eq(searchLatitude), eq(searchLongitude), eq(distanceRange),
      eq(searchCriteria), eq(searchPostcode), eq(maxNumServicesToReturn)))
      .thenReturn(dosServicesFuture);

    int numberOfThreads = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CompletableFuture<ResponseEntity<ApiResponse>>[] futures = new CompletableFuture[numberOfThreads];

    for (int i = 0; i < numberOfThreads; i++) {
      futures[i] = CompletableFuture.supplyAsync(() -> {
        try {
          return fuzzySearchController.getServicesByFuzzySearch(
            searchCriteria, searchPostcode, searchLatitude, searchLongitude,
            distanceRange, null, null, maxNumServicesToReturn, fuzzLevel,
            namePriority, addressPriority, postcodePriority, publicNamePriority).get();
        } catch (InterruptedException | ExecutionException | NotFoundException | InvalidParameterException e) {
          e.printStackTrace();
          return null;
        }
      }, executorService);
    }

    CompletableFuture.allOf(futures).join();

    for (CompletableFuture<ResponseEntity<ApiResponse>> future : futures) {
      ResponseEntity<ApiResponse> responseEntity = future.get();
      assertNotNull(responseEntity);
      assertEquals(200, responseEntity.getStatusCodeValue());

      ApiResponse response = responseEntity.getBody();
        assert response != null;
        assertNotNull(response);
    }

    executorService.shutdown();
  }

}
