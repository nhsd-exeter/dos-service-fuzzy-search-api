package uk.nhs.digital.uec.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

@ExtendWith(SpringExtension.class)
public class ServiceRepositoryTest {

  @InjectMocks ServiceRepository serviceRepository;
  @Mock private ServicesRepositoryInterface servicesRepo;
  @Mock private ApiRequestParams apiRequestParams;

  @Test
  public void findServiceBySearchTermsTest() throws UnauthorisedException {

    DosService dosService = new DosService();
    dosService.setName("Exeter NHS Service");
    dosService.setEasting(23453);
    dosService.setNorthing(45322);
    dosService.setPostcode("EX1 1SR");
    dosService.setReferral_roles(List.of("Referral","Professional Referral"));
    List<DosService> services = new ArrayList<>();
    services.add(dosService);

    Page<DosService> pageItems = new PageImpl<>(services);
    List<String> searchList = Arrays.asList("Search1, Search3");

    services.add(dosService);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);

    when(servicesRepo.findBySearchTerms(
            anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
        .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
        serviceRepository.findServiceBySearchTerms(searchList);

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(dosService.getName(), dosServiceResponse.getName());
    assertEquals(dosService.getEasting(), dosServiceResponse.getEasting());
    assertEquals(dosService.getNorthing(), dosServiceResponse.getNorthing());
  }

  @Test
  public void findServiceByLocationTest() throws UnauthorisedException, NotFoundException {

    DosService dosService = new DosService();
    dosService.setName("Exeter NHS Service");
    dosService.setEasting(23453);
    dosService.setNorthing(45322);
    dosService.setPostcode("EX1 1SR");
    dosService.setReferral_roles(List.of("Referral","Professional Referral"));
    List<DosService> services = new ArrayList<>();
    services.add(dosService);

    Page<DosService> pageItems = new PageImpl<>(services);
    String searchLocation = "EX8 8XE";


    services.add(dosService);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);

    when(servicesRepo.findBySearchTerms(
            anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
        .thenReturn(pageItems);

    List<DosService> findServiceBySearchTerms =
        serviceRepository.findServiceByLocation(searchLocation);

    DosService dosServiceResponse = findServiceBySearchTerms.get(0);

    assertEquals(dosService.getName(), dosServiceResponse.getName());
    assertEquals(dosService.getEasting(), dosServiceResponse.getEasting());
    assertEquals(dosService.getNorthing(), dosServiceResponse.getNorthing());
  }

  @Test
  public void findServiceByLocationValidationExceptionTest() throws UnauthorisedException, NotFoundException {

    DosService dosService = new DosService();
    dosService.setName("Exeter NHS Service");
    dosService.setEasting(23453);
    dosService.setNorthing(45322);
    dosService.setPostcode("EX1 1SR");
    dosService.setReferral_roles(List.of("Referral","Professional Referral"));
    List<DosService> services = new ArrayList<>();
    services.add(dosService);

    Page<DosService> pageItems = new PageImpl<>(services);
    String searchLocation = "EdscXzxcxz8 sccasc8XdcsdcasE";


    services.add(dosService);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(2);
    when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()).thenReturn(3);

    when(servicesRepo.findBySearchTerms(
            anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), any()))
        .thenReturn(pageItems);

    NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->serviceRepository.findServiceByLocation(searchLocation));
    assertNotNull(notFoundException);

//    DosService dosServiceResponse = findServiceBySearchTerms.get(0);
//
//    assertEquals(dosService.getName(), dosServiceResponse.getName());
//    assertEquals(dosService.getEasting(), dosServiceResponse.getEasting());
//    assertEquals(dosService.getNorthing(), dosServiceResponse.getNorthing());
  }
}
