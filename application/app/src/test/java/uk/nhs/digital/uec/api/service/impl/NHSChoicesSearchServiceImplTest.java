package uk.nhs.digital.uec.api.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.*;
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

  private final String searchLatitude = "52.5868";
  private final String searchLongitude = "2.1257";

  private final String searchPostcode = "WV10 6BZ";
  private final Integer maxNumServicesToReturn = 10;

  @Mock
  private WebClientUtil webClientUtil;

  @Mock
  private NHSChoicesSearchMapperToDosServicesMapperUtil servicesMapperUtil;

  @InjectMocks private NHSChoicesSearchServiceImpl service;

  @Test
  void retrieveParsedNhsChoicesV2Model() {
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");

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
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");

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
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");

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

  @Test
  void testNHSChoicesDataTransformedIntoDosService() {
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("term1");
    searchTerms.add("term2");

    List<NHSChoicesV2DataModel> mockNhsChoicesData = List.of(createNHSChoicesV2DataModel());
    when(webClientUtil.retrieveNHSChoicesServices(searchLatitude, searchLongitude, "term1term2"))
      .thenReturn(CompletableFuture.completedFuture(mockNhsChoicesData));

    NHSChoicesSearchServiceImpl serviceImpl = new NHSChoicesSearchServiceImpl(webClientUtil, new NHSChoicesSearchMapperToDosServicesMapperUtil());

    CompletableFuture<List<DosService>> result = serviceImpl.retrieveParsedNhsChoicesV2Model(
      searchLatitude, searchLongitude, searchTerms, searchPostcode, maxNumServicesToReturn);

    assertDoesNotThrow(() -> result.get());
    List<DosService> dosServices = result.join();
    assertNotNull(dosServices);
    assertFalse(dosServices.isEmpty());
    DosService service = dosServices.get(0);
    assertNotNull(service);
    assertEquals("organisationName", service.getName());
    assertEquals("postcode", service.getPostcode());
    assertEquals("odsCode", service.getOds_code());
    assertEquals(52.5868d, service.getLocation().getLat());
    assertEquals(2.1257d, service.getLocation().getLon());
    assertEquals("0123 456 7890", service.getPublic_phone_number());
    assertEquals("test@test.com", service.getEmail());
    assertEquals("https://example.com", service.getWeb());
    assertEquals(0.0, service.getDistance());

  }

  private NHSChoicesV2DataModel createNHSChoicesV2DataModel() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setLatitude(52.5868);
    dataModel.setLongitude(2.1257);
    dataModel.setAddress1("address1");
    dataModel.setAddress2("address2");
    dataModel.setCity("city");
    dataModel.setCounty("county");
    dataModel.setOrganisationName("organisationName");
    dataModel.setOrganisationStatus("organisationStatus");
    dataModel.setOrganisationSubType("organisationSubType");
    dataModel.setOrganisationType("organisationType");
    dataModel.setOrganisationTypeId("organisationTypeId");
    dataModel.setOdsCode("odsCode");
    dataModel.setPostcode("postcode");
    dataModel.setSearchKey("searchKey");
    dataModel.setSearchScore(0.0);
    dataModel.setSummaryText("summaryText");
    dataModel.setUrl("url");

    Geocode geocode = new Geocode();
    ArrayList<Double> coordinates = new ArrayList<>();
    coordinates.add(52.5868);
    coordinates.add(2.1257);
    geocode.setCoordinates(coordinates);

    Crs crs = new Crs();
    crs.setType("type");

    Properties properties = new Properties();
    properties.setName("name");
    crs.setProperties(properties);
    geocode.setCrs(crs);

    dataModel.setGeocode(geocode);

    Contact telephone = new Contact();
    telephone.setContactType("contactType");
    telephone.setContactValue("0123 456 7890");
    telephone.setContactMethodType("Telephone");
    telephone.setContactAvailabilityType("contactAvailabilityType");
    Contact email = new Contact();
    email.setContactType("contactType");
    email.setContactValue("test@test.com");
    email.setContactMethodType("Email");
    email.setContactAvailabilityType("contactAvailabilityType");
    Contact web = new Contact();
    web.setContactType("contactType");
    web.setContactValue("https://example.com");
    web.setContactMethodType("Website");
    web.setContactAvailabilityType("contactAvailabilityType");
    dataModel.setContacts(List.of(telephone, email, web));

    Facility facility = new Facility();
    facility.setName("name");
    facility.setId(123);
    facility.setValue("value");
    facility.setFacilityGroupName("facilityGroupName");
    dataModel.setFacilities(List.of(facility));

    dataModel.setGsd(new GSD());
    GSD gsd = dataModel.getGsd();

    GsdService gsdService = new GsdService();
    gsdService.setServiceId("serviceId");
    gsdService.setServiceName("serviceName");
    gsd.setGsdServices(List.of(gsdService));

    GsdDataSupplier gsdDataSupplier = new GsdDataSupplier();
    gsdDataSupplier.setProvidedBy("providedBy");
    gsdDataSupplier.setProvidedByUrl("providedByUrl");
    gsdDataSupplier.setProvidedByImage("providedByImage");
    gsdDataSupplier.setProvidedOn("providedOn");
    gsd.setDataSupplier(List.of(gsdDataSupplier));

    GsdMetrics gsdMetrics = new GsdMetrics();
    gsdMetrics.setElementOrder(1);
    gsdMetrics.setMetricId("metricId");
    gsdMetrics.setElementText("elementText");
    gsdMetrics.setElementTitle("elementTitle");
    gsd.setMetrics(List.of(gsdMetrics));

    dataModel.setOpeningTimes(new ArrayList<>());
    OpeningTime openingTime = new OpeningTime();
    openingTime.setWeekday("weekday");
    openingTime.setOpeningTime("openingTime");
    openingTime.setClosingTime("closingTime");
    openingTime.setOpen(true);
    dataModel.getOpeningTimes().add(openingTime);

    dataModel.setParentOrganisation(new ParentOrganisation());
    ParentOrganisation parentOrganisation = dataModel.getParentOrganisation();
    parentOrganisation.setOrganisationName("organisationName");
    parentOrganisation.setODSCode("ODSCode");

    dataModel.setServices(new ArrayList<>());
    Service service = new Service();
    service.setServiceProvider(new ServiceProvider());
    ServiceProvider serviceProvider = service.getServiceProvider();
    serviceProvider.setOdsCode("odsCode");
    serviceProvider.setOrganisationName("organisationName");
    service.setServiceProvider(serviceProvider);
    dataModel.getServices().add(service);

    return dataModel;
  }

}
