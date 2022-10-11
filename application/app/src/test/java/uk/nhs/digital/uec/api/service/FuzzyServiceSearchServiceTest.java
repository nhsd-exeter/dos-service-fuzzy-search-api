package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;
import uk.nhs.digital.uec.api.service.impl.ExternalApiHandshakeService;
import uk.nhs.digital.uec.api.service.impl.FuzzyServiceSearchService;
import uk.nhs.digital.uec.api.service.impl.ValidationService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
public class FuzzyServiceSearchServiceTest {

  private int maxNumServicesToReturn = 10;

  @InjectMocks private FuzzyServiceSearchService fuzzyServiceSearchService;

  @Mock private ServiceRepository serviceRepository;

  @Mock private ApiRequestParams apiRequestParams;

  @Mock private ApiUtilsServiceInterface apiUtilsService;

  @Mock private LocationServiceInterface locationService;

  @Mock private ExternalApiHandshakeService apiHandshakeService;

  @Mock private ValidationService mockValidationService;

  private MultiValueMap<String, String> headers = null;

  private List<String> searchCriteria;

  @BeforeEach
  public void setup() throws NotFoundException, InvalidParameterException {
    when(apiRequestParams.getMaxNumServicesToReturn()).thenReturn(maxNumServicesToReturn);
    when(locationService.getLocationForPostcode(null, null)).thenReturn(null);

    headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + "Mock accessToken");

    searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");
  }

  @Test
  public void retrieveServicesByGeoLocationSearchSuccess()
      throws NotFoundException, InvalidParameterException {

    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    Double distanceRange = 0.0;
    List<String> searchTerms = null;
    // Arrange
    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findAllServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange))
        .thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms);

    // Assert
    assertEquals(2, services.size());
  }

  @Test
  public void shouldCallfindAllServicesByGeoLocationMethodWhenSearchTermIsEmptyOrNull()
      throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    Double distanceRange = 0.0;
    List<String> searchTerms = null;

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(serviceRepository.findAllServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange))
        .thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms);

    // Assert
    verify(serviceRepository, only())
        .findAllServicesByGeoLocation(searchLatitude, searchLongitude, distanceRange);
    assertEquals(2, services.size());
  }

  @Test
  public void shouldCallfindServicesByGeoLocationMethodWhenSearchTermIsNotEmptyOrNotNull()
      throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    Double distanceRange = 0.0;
    List<String> searchTerms = new ArrayList<>();
    searchTerms.add("pharmacy");

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(serviceRepository.findServicesByGeoLocation(
            searchTerms, searchLatitude, searchLongitude, distanceRange))
        .thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms);

    // Assert
    verify(serviceRepository, only())
        .findServicesByGeoLocation(searchTerms, searchLatitude, searchLongitude, distanceRange);
    assertEquals(2, services.size());
  }
}
