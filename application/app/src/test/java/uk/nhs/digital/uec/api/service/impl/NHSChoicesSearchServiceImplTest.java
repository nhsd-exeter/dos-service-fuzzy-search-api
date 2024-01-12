package uk.nhs.digital.uec.api.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.util.NHSChoicesSearchMapperToDosServicesMapperUtil;
import uk.nhs.digital.uec.api.util.WebClientUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NHSChoicesSearchServiceImplTest {

  @Mock
  private WebClientUtil webClientUtil;

  @Mock
  private NHSChoicesSearchMapperToDosServicesMapperUtil servicesMapperUtil;

  @InjectMocks private NHSChoicesSearchServiceImpl service;

  @Test
  void retrieveParsedNhsChoicesV2Model() {
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");
    String searchPostcode = "WV10 6BZ";
    Integer maxNumServicesToReturn = 10;

    List<NHSChoicesV2DataModel> mockNhsChoicesData = new ArrayList<>();
    when(webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, "term1term2"))
      .thenReturn(CompletableFuture.completedFuture(mockNhsChoicesData));

    NHSChoicesSearchServiceImpl service = new NHSChoicesSearchServiceImpl(webClientUtil, servicesMapperUtil);

    CompletableFuture<List<DosService>> result = service.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, maxNumServicesToReturn);

    assertDoesNotThrow(() -> result.get());
    List<DosService> dosServices = result.join();
    assertNotNull(dosServices);
  }

  @Test
  void retrieveParsedNhsChoicesV2Model_testNullSearchTerms() {
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    String searchPostcode = "WV10 6BZ";
    Integer maxNumServicesToReturn = 10;

    List<NHSChoicesV2DataModel> mockNhsChoicesData = new ArrayList<>();
    when(webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, searchPostcode))
      .thenReturn(CompletableFuture.completedFuture(mockNhsChoicesData));

    NHSChoicesSearchServiceImpl service = new NHSChoicesSearchServiceImpl(webClientUtil, servicesMapperUtil);

    CompletableFuture<List<DosService>> result = service.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, null, searchPostcode, maxNumServicesToReturn);

    assertDoesNotThrow(() -> result.get());
    List<DosService> dosServices = result.join();
    assertNotNull(dosServices);
    assertTrue(dosServices.isEmpty(), "When searchTerms are null, the result should be an empty list");
  }

  @Test
  void retrieveParsedNhsChoicesV2Model_testEmptySearchTerms() {
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    String searchPostcode = "WV10 6BZ";
    Integer maxNumServicesToReturn = 10;

    List<NHSChoicesV2DataModel> mockNhsChoicesData = new ArrayList<>();
    when(webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, searchPostcode))
      .thenReturn(CompletableFuture.completedFuture(mockNhsChoicesData));

    NHSChoicesSearchServiceImpl service = new NHSChoicesSearchServiceImpl(webClientUtil, servicesMapperUtil);

    CompletableFuture<List<DosService>> result = service.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, new ArrayList<>(), searchPostcode, maxNumServicesToReturn);

    assertDoesNotThrow(() -> result.get());
    List<DosService> dosServices = result.join();
    assertNotNull(dosServices);
    assertTrue(dosServices.isEmpty(), "When searchTerms are empty, the result should be an empty list");
  }

  @Test
  void retrieveParsedNhsChoicesV2Model_testMinimumMaxNumServicesToReturn() {
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");
    String searchPostcode = "WV10 6BZ";

    Integer minNumServicesToReturn = 0;
    CompletableFuture<List<DosService>> resultMin = simulateServiceCallAndConvert(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, minNumServicesToReturn);
    assertNotNull(resultMin.join());
    assertTrue(resultMin.join().isEmpty(), "When maxNumServicesToReturn is 0, the result should be an empty list");

    Integer maxNumServicesToReturn = Integer.MAX_VALUE;
    CompletableFuture<List<DosService>> resultMax = simulateServiceCallAndConvert(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, maxNumServicesToReturn);
    assertNotNull(resultMax.join());
  }

  private CompletableFuture<List<DosService>> simulateServiceCallAndConvert(
    String searchLatitude, String searchLongitude, List<String> searchTerms, String searchPostcode,
    Integer maxNumServicesToReturn) {

    List<NHSChoicesV2DataModel> mockNhsChoicesData = new ArrayList<>();
    when(webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, "term1term2"))
      .thenReturn(CompletableFuture.completedFuture(mockNhsChoicesData));

    NHSChoicesSearchServiceImpl service = new NHSChoicesSearchServiceImpl(webClientUtil, servicesMapperUtil);

    return service.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, maxNumServicesToReturn);
  }

  @Test
  void retrieveParsedNhsChoicesV2Model_testConcurrency() throws InterruptedException, ExecutionException {
    // Arrange
    String searchLatitude = "52.5868";
    String searchLongitude = "2.1257";
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");
    String searchPostcode = "WV10 6BZ";
    Integer maxNumServicesToReturn = 10;

    List<NHSChoicesV2DataModel> mockNhsChoicesData = new ArrayList<>();
    when(webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, "term1term2"))
      .thenReturn(CompletableFuture.completedFuture(mockNhsChoicesData));

    NHSChoicesSearchServiceImpl service = new NHSChoicesSearchServiceImpl(webClientUtil, servicesMapperUtil);

    CompletableFuture<List<DosService>> future1 = service.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, maxNumServicesToReturn);

    CompletableFuture<List<DosService>> future2 = service.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, maxNumServicesToReturn);

    CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);

    // Block until all CompletableFuture tasks are completed
    allOf.join();

    assertDoesNotThrow(() -> future1.get());
    assertDoesNotThrow(() -> future2.get());

    List<DosService> dosServices1 = future1.join();
    List<DosService> dosServices2 = future2.join();

    assertNotNull(dosServices1);
    assertNotNull(dosServices2);
  }

}
