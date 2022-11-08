package uk.nhs.digital.uec.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;

@ExtendWith(SpringExtension.class)
public class ServiceRepositoryTest {

  @InjectMocks ServiceRepository serviceRepository;

  @Mock private ServicesRepositoryInterface servicesRepo;

  @Mock private ApiRequestParams apiRequestParams;

  @Mock private Environment environment;

  List<DosService> services = new ArrayList<>();
  Page<DosService> pageItems;

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
    dosService.setReferral_roles(List.of("Referral", "Professional Referral"));
    services.add(dosService);

    DosService dosService2 = new DosService();
    dosService2.setName(DOS_NAME);
    dosService2.setEasting(EASTING);
    dosService2.setNorthing(NORTHING);
    dosService2.setPostcode("EX1 1SR");
    dosService2.setReferral_roles(new ArrayList<>());
    services.add(dosService2);

    DosService dosService3 = new DosService();
    dosService3.setName(DOS_NAME);
    dosService3.setEasting(EASTING);
    dosService3.setNorthing(NORTHING);
    dosService3.setPostcode("EX1 1SR");
    dosService3.setReferral_roles(List.of("Professional Referral"));
    services.add(dosService3);

    DosService dosService4 = new DosService();
    dosService4.setName(DOS_NAME);
    dosService4.setEasting(EASTING);
    dosService4.setNorthing(NORTHING);
    dosService4.setPostcode("EX1 1SR");
    services.add(dosService4);

    pageItems = new PageImpl<>(services);
  }

  @Test
  public void findServiceBySearchTermsTest() throws UnauthorisedException {
    final List<String> searchTerms = Arrays.asList("Search1, Search3");

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");
    when(environment.getActiveProfiles()).thenReturn(new String[] {"local"});

    when(servicesRepo.findBySearchTerms(
            anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
        .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
        serviceRepository.findServiceBySearchTerms(searchTerms);

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }

  @Test
  public void findServiceByLongerSearchTermsListTest() throws UnauthorisedException {
    final List<String> searchTerms = Arrays.asList("Search1", "Search3", "Search4");

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");
    when(environment.getActiveProfiles()).thenReturn(new String[] {"local"});

    when(servicesRepo.findBySearchTerms(
            anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
        .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
        serviceRepository.findServiceBySearchTerms(searchTerms);

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }

  @Test
  public void findServiceByLongerThan3SearchTermsListTest() throws UnauthorisedException {
    final List<String> searchTerms = Arrays.asList("Search1", "Search3", "Search4", "Search5");

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");
    when(environment.getActiveProfiles()).thenReturn(new String[] {"local","mock-auth","dev"});

    when(servicesRepo.findBySearchTerms(
            anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
        .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
        serviceRepository.findServiceBySearchTerms(searchTerms);

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }

  @Test
  public void findAllServiceByGeoLocationWithAllParametersTest() throws NotFoundException {
    final Double searchLatitude = 24.34;
    final Double searchLongitude = -0.2345;
    final Double distanceRange = 25D;
    final List<String> searchTerms = List.of("term1");

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");
    when(apiRequestParams.getNamePriority()).thenReturn(2);
    when(apiRequestParams.getAddressPriority()).thenReturn(2);
    when(apiRequestParams.getPostcodePriority()).thenReturn(2);
    when(apiRequestParams.getPublicNamePriority()).thenReturn(2);
    when(environment.getActiveProfiles()).thenReturn(new String[] {"demo"});


    when(servicesRepo.findSearchTermsByGeoLocation(String.join(" ",searchTerms),
      searchLatitude, searchLongitude, distanceRange,2,2,2,2,2, PageRequest.of(0, 2)))
      .thenReturn(pageItems);


    List<DosService> findAllServiceByGeoLocation =
        serviceRepository.findAllServicesByGeoLocationAndSearchTerms(
            searchLatitude, searchLongitude, distanceRange,searchTerms);

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

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");
    when(apiRequestParams.getNamePriority()).thenReturn(2);
    when(apiRequestParams.getAddressPriority()).thenReturn(2);
    when(apiRequestParams.getPostcodePriority()).thenReturn(2);
    when(apiRequestParams.getPublicNamePriority()).thenReturn(2);
    when(environment.getActiveProfiles()).thenReturn(new String[] {"local"});
    when(servicesRepo.findAllByGeoLocation(
      searchLatitude, searchLongitude, distanceRange, PageRequest.of(0, 2)))
      .thenReturn(pageItems);

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

    NotFoundException exception =  assertThrows(NotFoundException.class, () -> {
      serviceRepository.findAllServicesByGeoLocationAndSearchTerms(
        searchLatitude, searchLongitude, distanceRange,searchTerms);
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

    NotFoundException exception =  assertThrows(NotFoundException.class, () -> {
      serviceRepository.findAllServicesByGeoLocation(
        searchLatitude, searchLongitude, distanceRange);
    });


    assertEquals(exception.getMessage(), ErrorMessageEnum.INVALID_LOCATION.getMessage());

  }



}
