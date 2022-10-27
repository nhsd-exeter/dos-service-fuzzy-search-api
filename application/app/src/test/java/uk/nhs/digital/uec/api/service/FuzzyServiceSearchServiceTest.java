package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.CustomServicesRepository;
import uk.nhs.digital.uec.api.service.impl.ExternalApiHandshakeService;
import uk.nhs.digital.uec.api.service.impl.FuzzyServiceSearchService;
import uk.nhs.digital.uec.api.service.impl.ValidationService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class FuzzyServiceSearchServiceTest {

  private int maxNumServicesToReturn = 10;

  @InjectMocks private FuzzyServiceSearchService fuzzyServiceSearchService;

  @Mock private CustomServicesRepository customServiceRepository;

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
  @DisplayName("Should return dos services when searchterms and geo values passed")
  public void retrieveServicesByGeoLocationSearchSuccess(CapturedOutput log)
      throws NotFoundException, InvalidParameterException {

    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    Double distanceRange = 0.0;
    List<String> searchTerms = List.of("term1");
    String searchPostCode = "XX1 1XX";
    // Arrange
    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(customServiceRepository.findAllServicesByGeoLocation(
            Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude), distanceRange, searchTerms))
        .thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms, searchPostCode);
    assertTrue(log.getOut().contains("Searching using search terms and Lat Lng values"));
    verify(mockValidationService,times(1)).validateSearchCriteria(searchTerms);

    // Assert
    assertEquals(2, services.size());
  }

  @Test
  @DisplayName("Should return dos services when search terms null")
  public void retrieveServicesByGeoLocationSearchWhenSearchTermIsEmptyOrNull(CapturedOutput log)
      throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    Double distanceRange = 0.0;
    List<String> searchTerms = null;

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(customServiceRepository.findAllServicesByGeoLocation(
            Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude), distanceRange,searchTerms))
        .thenReturn(dosServices);

    // Act
    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms, "");

    // Assert
    verify(customServiceRepository, only())
        .findAllServicesByGeoLocation(
            Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude), distanceRange,searchTerms);
    verify(mockValidationService,times(0)).validateSearchCriteria(searchTerms);
    assertTrue(log.getOut().contains("Searching using lat: "));
    assertEquals(2, services.size());
  }


  @Test
  @DisplayName("Should return dos services when geo values null")
  public void retrieveServicesByGeoLocationSearchWhenGeovaluesNull(CapturedOutput log)
    throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchLatitude = null;
    String searchLongitude = null;
    Double distanceRange = 0.0;
    List<String> searchTerms = List.of("term1");

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    when(customServiceRepository.findServiceBySearchTerms(searchTerms))
      .thenReturn(dosServices);

    when(apiUtilsService.sanitiseSearchTerms(searchTerms)).thenReturn(searchTerms);

    // Act
    List<DosService> services =
      fuzzyServiceSearchService.retrieveServicesByGeoLocation(searchLatitude,searchLongitude,distanceRange,
        searchTerms, "");


    // Assert
    verify(customServiceRepository, times(1))
      .findServiceBySearchTerms(searchTerms);
    verify(mockValidationService,times(1)).validateSearchCriteria(searchTerms);
    assertTrue(log.getOut().contains("Searching using search terms:"));
    assertEquals(4, services.size());
  }


  @Test
  @DisplayName("Should throw InvalidParameterException  when all values null")
  public void shouldThrowInvalidParameterExceptionWhenValuesNullOrEmpty() {
    assertThrows(InvalidParameterException.class, () -> {
      fuzzyServiceSearchService.retrieveServicesByGeoLocation(null,null,null,
        null, null);
    });
  }


}
