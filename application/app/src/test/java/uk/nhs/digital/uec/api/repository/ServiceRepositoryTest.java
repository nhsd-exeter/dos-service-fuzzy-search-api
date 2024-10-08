package uk.nhs.digital.uec.api.repository;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;
import uk.nhs.digital.uec.api.util.Constants;

@ExtendWith(SpringExtension.class)
public class ServiceRepositoryTest {

  @InjectMocks ServiceRepository serviceRepository;

  @Mock private ApiRequestParams apiRequestParams;

  @Mock private Environment environment;

  @Mock private ElasticsearchOperations operations;

  List<DosService> dosServicesList = new ArrayList<>();

  private static final String DOS_NAME = "Exeter NHS Service";
  private static final Integer EASTING = 23453;
  private static final Integer NORTHING = 45322;

  @BeforeEach
  public void setup() {
    DosService dosService = new DosService();
    dosService.setName(DOS_NAME);
    dosService.setEasting(EASTING);
    dosService.setNorthing(NORTHING);
    dosService.setPostcode("EX1 1SR");
    dosService.setReferral_roles(Arrays.asList("Referral", Constants.PROFESSIONAL_REFERRAL_FILTER));
    dosService.setLocation(new GeoPoint(0D, 0D));
    dosService.setPublic_name("Exeter NHS Service");
    dosService.setProfessional_referral_info("ProfessionalReferralInfo");
    dosServicesList.add(dosService);

    DosService dosService2 = new DosService();
    dosService2.setName(DOS_NAME);
    dosService2.setEasting(EASTING);
    dosService2.setNorthing(NORTHING);
    dosService2.setPostcode("EX1 1SR");
    dosService.setLocation(new GeoPoint(23.456, -0.2345));
    dosService2.setReferral_roles(new ArrayList<>());
    dosService.setPublic_name("Exeter NHS Service");
    dosService.setProfessional_referral_info("ProfessionalReferralInfo");

    dosServicesList.add(dosService2);

    DosService dosService3 = new DosService();
    dosService3.setName(DOS_NAME);
    dosService3.setEasting(EASTING);
    dosService3.setNorthing(NORTHING);
    dosService3.setPostcode("EX1 1SR");
    dosService3.setReferral_roles(Arrays.asList(Constants.PROFESSIONAL_REFERRAL_FILTER));
    dosService.setPublic_name(null);
    dosService.setProfessional_referral_info(null);
    dosServicesList.add(dosService3);

    DosService dosService4 = new DosService();
    dosService4.setName(DOS_NAME);
    dosService4.setEasting(EASTING);
    dosService4.setNorthing(NORTHING);
    dosService4.setPostcode("EX1 1SR");
    dosService.setPublic_name(null);
    dosService.setProfessional_referral_info(null);
    dosServicesList.add(dosService4);
  }

  @Test
  public void shouldReturnOnlyProfessionalReferralWhenProfileIsDemoOrProdTest()
      throws NotFoundException {
    final List<String> searchTerms = asList("Search1, Search3");
    final Double searchLatitude = 24.34;
    final Double searchLongitude = -0.2345;
    final Double distanceRange = 25D;

    SearchHits<DosService> searchHits = mock(SearchHits.class);

    SearchHit<DosService> dosServiceSearchHit1 = mock(SearchHit.class);
    when(dosServiceSearchHit1.getSortValues()).thenReturn(Arrays.asList(10.345));
    when(dosServiceSearchHit1.getContent()).thenReturn(dosServicesList.get(0));
    when(searchHits.isEmpty()).thenReturn(false);

    SearchHit<DosService> dosServiceSearchHit2 = mock(SearchHit.class);
    when(dosServiceSearchHit2.getSortValues()).thenReturn(Arrays.asList(13.345));
    when(dosServiceSearchHit2.getContent()).thenReturn(dosServicesList.get(1));
    when(searchHits.isEmpty()).thenReturn(false);

    List<SearchHit<DosService>> hits = new ArrayList<>();
    hits.add(dosServiceSearchHit1);
    hits.add(dosServiceSearchHit2);

    when(searchHits.getSearchHits()).thenReturn(hits);
    when(operations.search(any(Query.class), any(Class.class))).thenReturn(searchHits);

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(2);
    when(apiRequestParams.getFilterReferralRole())
        .thenReturn(Constants.PROFESSIONAL_REFERRAL_FILTER);
    when(environment.getActiveProfiles()).thenReturn(new String[] {"prod"});

    List<DosService> findServiceBySearchTerms =
        serviceRepository.findAllServicesByGeoLocationWithSearchTerms(
            searchLatitude, searchLongitude, distanceRange, searchTerms);

    assertEquals(findServiceBySearchTerms.size(), 2);
  }

  @Test
  public void findAllServiceByGeoLocationWithAllParametersTest() throws NotFoundException {
    final Double searchLatitude = 24.34;
    final Double searchLongitude = -0.2345;
    final Double distanceRange = 25D;
    final List<String> searchTerms = Arrays.asList("term1");

    SearchHit<DosService> dosServiceSearchHit1 = mock(SearchHit.class);
    when(dosServiceSearchHit1.getSortValues()).thenReturn(Arrays.asList(10.345));
    when(dosServiceSearchHit1.getContent()).thenReturn(dosServicesList.get(0));
    SearchHits<DosService> searchHits = mock(SearchHits.class);
    when(searchHits.isEmpty()).thenReturn(false);
    List<SearchHit<DosService>> hits = new ArrayList<>();
    hits.add(dosServiceSearchHit1);
    when(searchHits.getSearchHits()).thenReturn(hits);
    when(operations.search(any(Query.class), any(Class.class))).thenReturn(searchHits);

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getFilterReferralRole())
        .thenReturn(Constants.PROFESSIONAL_REFERRAL_FILTER);
    when(apiRequestParams.getNamePriority()).thenReturn(2);
    when(apiRequestParams.getAddressPriority()).thenReturn(2);
    when(apiRequestParams.getPostcodePriority()).thenReturn(2);
    when(apiRequestParams.getPublicNamePriority()).thenReturn(2);
    when(environment.getActiveProfiles()).thenReturn(new String[] {"DEV"});

    List<DosService> findAllServiceByGeoLocation =
        serviceRepository.findAllServicesByGeoLocationWithSearchTerms(
            searchLatitude, searchLongitude, distanceRange, searchTerms);

    DosService dosServiceResponse = findAllServiceByGeoLocation.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }

  @Test
  public void findAllServiceByGeoLocationWithOutSearchTermsTest() throws NotFoundException {
    final Double searchLatitude = 24.34;
    final Double searchLongitude = -0.2345;
    final Double distanceRange = 25D;

    SearchHit<DosService> dosServiceSearchHit1 = mock(SearchHit.class);
    when(dosServiceSearchHit1.getSortValues()).thenReturn(Arrays.asList(10.345));
    when(dosServiceSearchHit1.getContent()).thenReturn(dosServicesList.get(0));
    SearchHits<DosService> searchHits = mock(SearchHits.class);
    when(searchHits.isEmpty()).thenReturn(false);
    List<SearchHit<DosService>> hits = new ArrayList<>();
    hits.add(dosServiceSearchHit1);

    when(searchHits.getSearchHits()).thenReturn(hits);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFilterReferralRole())
        .thenReturn(Constants.PROFESSIONAL_REFERRAL_FILTER);
    when(apiRequestParams.getNamePriority()).thenReturn(2);
    when(apiRequestParams.getAddressPriority()).thenReturn(2);
    when(apiRequestParams.getPostcodePriority()).thenReturn(2);
    when(apiRequestParams.getPublicNamePriority()).thenReturn(2);
    when(environment.getActiveProfiles()).thenReturn(new String[] {"local"});

    when(operations.search(any(Query.class), any(Class.class))).thenReturn(searchHits);

    List<DosService> findAllServiceByGeoLocation =
        serviceRepository.findAllServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange);

    DosService dosServiceResponse = findAllServiceByGeoLocation.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }

  @Test
  public void shouldThrowErrorWhenNoGeoValuesAndNoSearchTerms() throws NotFoundException {
    final Double searchLatitude = null;
    final Double searchLongitude = null;
    final Double distanceRange = 25D;
    final List<String> searchTerms = null;
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);

    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> {
              serviceRepository.findAllServicesByGeoLocationWithSearchTerms(
                  searchLatitude, searchLongitude, distanceRange, searchTerms);
            });
    assertEquals(exception.getMessage(), ErrorMessageEnum.INVALID_LOCATION.getMessage());
  }

  @Test
  public void shouldThrowErrorWhenNoGeoValues() throws NotFoundException {
    final Double searchLatitude = null;
    final Double searchLongitude = null;
    final Double distanceRange = 25D;

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);

    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> {
              serviceRepository.findAllServicesByGeoLocation(
                  searchLatitude, searchLongitude, distanceRange);
            });

    assertEquals(exception.getMessage(), ErrorMessageEnum.INVALID_LOCATION.getMessage());
  }
}
