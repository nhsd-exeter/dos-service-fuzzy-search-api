package uk.nhs.digital.uec.api.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ServiceRepositoryTest {

  @InjectMocks
  ServiceRepository serviceRepository;
  @Mock
  private ServicesRepositoryInterface servicesRepo;
  @Mock
  private ApiRequestParams apiRequestParams;

  List<DosService> services = new ArrayList<>();
  Page<DosService> pageItems;

  private static final String DOS_NAME = "Exeter NHS Service";
  private static final Integer EASTING = 23453;
  private static final Integer NORTHING = 45322;
  @BeforeEach
  public void setup(){

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
    final List<String> searchTerms = Arrays.asList("Search1", "Search3", "Search4","Search5");

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");

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
  public void findServiceByPostcodeTest() throws UnauthorisedException, NotFoundException {
    final String searchPostcode = "EX8 8XE";

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");

    when(servicesRepo.findBySearchTerms(
      anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
      .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
      serviceRepository.findServiceByPostcode(searchPostcode);

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }

  @Test
  public void findServiceByPostcodeValidationExceptionTest() {

    final String searchPostcode = "EdscXzxcxz8 sccasc8XdcsdcasE";

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);

    NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> serviceRepository.findServiceByPostcode(searchPostcode));
    assertNotNull(notFoundException);

  }

  @Test
  public void findServiceByLatitudeAndLongitudeTest() throws NotFoundException {
    final String searchLatitude = "24.34";
    final String searchLongitude = "-0.2345";
    final String distanceRange = "25mi";

    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getFuzzLevel()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);
    when(apiRequestParams.getFilterReferralRole()).thenReturn("Professional Referral");

    when(servicesRepo.findSearchTermsByGeoLocation(anyString(), anyString(), anyString(), anyString(),any(),
      anyInt(), anyInt(), anyInt(), anyInt(), any()))
      .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
      serviceRepository.findServicesByGeoLocation(List.of(""),searchLatitude, searchLongitude, distanceRange) ;

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(DOS_NAME, dosServiceResponse.getName());
    assertEquals(EASTING, dosServiceResponse.getEasting());
    assertEquals(NORTHING, dosServiceResponse.getNorthing());
  }



}
