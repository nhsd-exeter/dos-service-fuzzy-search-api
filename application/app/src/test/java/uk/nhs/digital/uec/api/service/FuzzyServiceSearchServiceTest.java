package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;
import uk.nhs.digital.uec.api.service.impl.FuzzyServiceSearchService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
public class FuzzyServiceSearchServiceTest {

  private int maxNumServicesToReturn = 10;

  @InjectMocks private FuzzyServiceSearchService fuzzyServiceSearchService;

  @Mock private ServiceRepository serviceRepository;

  @Mock private ApiRequestParams apiRequestParams;

  @Mock private ApiUtilsServiceInterface apiUtilsService;

  @Mock private LocationServiceInterface locationService;

  @BeforeEach
  public void setup() {
    when(apiRequestParams.getMaxNumServicesToReturn()).thenReturn(maxNumServicesToReturn);
    when(locationService.getLocationForPostcode(null)).thenReturn(null);
  }

  @Test
  public void retrieveServicesByFuzzySearchSuccess() {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findServiceBySearchTerms(eq(searchCriteria))).thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(null, searchCriteria);

    // Assert
    assertEquals(2, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchNoResults() {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term0");

    List<DosService> dosServices = new ArrayList<>();

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findServiceBySearchTerms(eq(searchCriteria))).thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(null, searchCriteria);

    // Assert
    assertEquals(0, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchTooManyResults() {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("All");

    List<DosService> dosServices = new ArrayList<>();
    for (Map.Entry<Integer, DosService> entry : MockDosServicesUtil.mockDosServices.entrySet()) {
      dosServices.add(entry.getValue());
    }

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findServiceBySearchTerms(eq(searchCriteria))).thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(null, searchCriteria);

    // Assert
    assertEquals(maxNumServicesToReturn, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchMaxReturn() {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("Max");

    List<DosService> dosServices = new ArrayList<>();
    for (Map.Entry<Integer, DosService> entry : MockDosServicesUtil.mockDosServices.entrySet()) {
      dosServices.add(entry.getValue());
    }
    List<DosService> maxDosServices = dosServices.subList(0, maxNumServicesToReturn);

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findServiceBySearchTerms(eq(searchCriteria))).thenReturn(maxDosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(null, searchCriteria);

    // Assert
    assertEquals(maxNumServicesToReturn, services.size());
  }

  @Test
  public void retrieveServicesByFuzzySearchNullReturn() {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(null, searchCriteria);
    // Assert
    assertEquals(0, services.size());
  }

  @Test
  public void retrieveServicesWithSearchLocation() {
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    String searchLocation = "EX8 5SE";

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findServiceBySearchTerms(eq(searchCriteria))).thenReturn(dosServices);
    when(locationService.getLocationForPostcode(any(String.class)))
        .thenReturn(new PostcodeLocation());
    when(locationService.distanceBetween(any(PostcodeLocation.class), any(PostcodeLocation.class)))
        .thenReturn(5.0, 10.0);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchLocation, searchCriteria);

    verify(locationService).getLocationForPostcode(eq(searchLocation));
    verify(locationService, times(2))
        .distanceBetween(any(PostcodeLocation.class), any(PostcodeLocation.class));
    assertEquals(2, services.size());
    assertEquals(5.0, services.get(0).getDistance());
    assertEquals(10.0, services.get(1).getDistance());
  }

  @Test
  public void retrieveServicesWithNoSearchLocationFromDos() {
    List<String> searchCriteria = new ArrayList<>();
    PostcodeLocation dynamoPostCodeLocation = new PostcodeLocation();
    dynamoPostCodeLocation.setEasting(558439);
    dynamoPostCodeLocation.setNorthing(140222);
    dynamoPostCodeLocation.setPostCode("EX78PR");
    List<PostcodeLocation> postcodesLocations = new ArrayList<>();
    postcodesLocations.add(dynamoPostCodeLocation);
    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX7 8PR");
    postCodes.add("EX7 8PR");

    searchCriteria.add("term1");
    searchCriteria.add("term2");

    String searchLocation = "EX8 5SE";

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findServiceBySearchTerms(eq(searchCriteria))).thenReturn(dosServices);
    when(locationService.getLocationForPostcode(any(String.class)))
        .thenReturn(new PostcodeLocation());
    when(locationService.getLocationsForPostcodes(anyList())).thenReturn(postcodesLocations);
    when(apiUtilsService.removeBlankSpaces(anyString())).thenReturn("EX78PR");
    when(locationService.distanceBetween(any(PostcodeLocation.class), any(PostcodeLocation.class)))
        .thenReturn(357.7);

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchLocation, searchCriteria);

    verify(locationService).getLocationForPostcode(eq(searchLocation));
    verify(locationService).getLocationsForPostcodes(eq(postCodes));
    verify(locationService, times(2))
        .distanceBetween(any(PostcodeLocation.class), any(PostcodeLocation.class));
    assertEquals(2, services.size());
    assertEquals(357.7, services.get(0).getDistance());
  }
}
