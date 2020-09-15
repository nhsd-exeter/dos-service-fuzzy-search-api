package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;
import uk.nhs.digital.uec.api.service.impl.FuzzyServiceSearchService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
public class FuzzyServiceSearchServiceTest {

  private int maxNumServicesToReturn = 10;

  @InjectMocks private FuzzyServiceSearchService fuzzyServiceSearchService;

  @Mock private ServiceRepository serviceRepository;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(
        fuzzyServiceSearchService, "maxNumServicesToReturn", maxNumServicesToReturn);
  }

  @Test
  public void retrieveServicesByFuzzySearchSuccess() {

    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(serviceRepository.findServiceBySearchTerms(searchCriteria)).thenReturn(dosServices);

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(2, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchNoResults() {

    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term0");

    List<DosService> dosServices = new ArrayList<>();

    when(serviceRepository.findServiceBySearchTerms(searchCriteria)).thenReturn(dosServices);

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(0, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchTooManyResults() {

    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("All");

    List<DosService> dosServices = new ArrayList<>();
    for (Map.Entry<Integer, DosService> entry : MockDosServicesUtil.mockDosServices.entrySet()) {
      dosServices.add(entry.getValue());
    }

    when(serviceRepository.findServiceBySearchTerms(searchCriteria)).thenReturn(dosServices);

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(maxNumServicesToReturn, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchMaxReturn() {

    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("Max");

    List<DosService> dosServices = new ArrayList<>();
    for (Map.Entry<Integer, DosService> entry : MockDosServicesUtil.mockDosServices.entrySet()) {
      dosServices.add(entry.getValue());
    }
    List<DosService> maxDosServices = dosServices.subList(0, maxNumServicesToReturn);

    when(serviceRepository.findServiceBySearchTerms(searchCriteria)).thenReturn(maxDosServices);

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(maxNumServicesToReturn, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchNullReturn() {

    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("Null");

    when(serviceRepository.findServiceBySearchTerms(searchCriteria)).thenReturn(null);

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(0, services.size());
  }
}
