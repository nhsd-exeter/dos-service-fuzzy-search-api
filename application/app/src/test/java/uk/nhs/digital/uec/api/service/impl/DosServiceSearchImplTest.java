package uk.nhs.digital.uec.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponse;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponseResult;
import uk.nhs.digital.uec.api.model.google.Geometry;
import uk.nhs.digital.uec.api.model.google.Location;
import uk.nhs.digital.uec.api.repository.elasticsearch.impl.ServiceRepository;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;
import uk.nhs.digital.uec.api.service.LocationServiceInterface;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class DosServiceSearchImplTest {

  private int maxNumServicesToReturn = 10;

  @InjectMocks
  private DosServiceSearchImpl classUnderTest;

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
  @DisplayName("retrieveServicesByGeoLocationSearchSuccess")
  public void retrieveServicesByGeoLocation(CapturedOutput log)
      throws NotFoundException, InvalidParameterException {
    String searchLatitude = "0.0";
    String searchLongitude = "0.0";
    Double distanceRange = 0.0;
    List<String> searchTerms = null;
    // Arrange
    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    // when(apiUtilsService.sanitiseSearchTerms(searchCriteria)).thenReturn(searchCriteria);
    when(serviceRepository.findAllServicesByGeoLocation(
            Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude), distanceRange))
        .thenReturn(dosServices);
    when(mockValidationService.isPostcodeValid(anyString())).thenReturn(true);

    // Act
    List<DosService> services =
        classUnderTest.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms, "");

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
            Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude), distanceRange))
        .thenReturn(dosServices);
    when(mockValidationService.isPostcodeValid(anyString())).thenReturn(false);

    // Act
    List<DosService> services =
        classUnderTest.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms, "");

    // Assert
    verify(serviceRepository, only())
        .findAllServicesByGeoLocation(
            Double.parseDouble(searchLatitude), Double.parseDouble(searchLongitude), distanceRange);
    assertEquals(0, services.size()); // because no valid postcode in dos services
  }

  @Test
  public void shouldCallfindAllServicesByGeoLocationMethodWhenOnlyPostCodePassed()
      throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchPostCode = "XX1 1XX";

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    GeoPoint geoPoint = new GeoPoint(12.45, 23.45);
    Geometry geometry = mock(Geometry.class);
    Location location = mock(Location.class);
    when(location.getLat()).thenReturn(22.0);
    when(location.getLng()).thenReturn(22.0);
    when(geometry.getLocation()).thenReturn(location);

    GeoLocationResponse geoLocationResponse = mock(GeoLocationResponse.class);
    GeoLocationResponseResult[] geoLocationResponseResults = new GeoLocationResponseResult[1];
    GeoLocationResponseResult geoLocationResponseResult = mock(GeoLocationResponseResult.class);
    when(geoLocationResponseResult.getGeometry()).thenReturn(geometry);
    geoLocationResponseResults[0] = geoLocationResponseResult;
    when(geoLocationResponse.getGeoLocationResponseResults())
        .thenReturn(geoLocationResponseResults);
    when(locationService.distanceBetween(geoPoint, geoPoint)).thenReturn(99.00);
    when(apiHandshakeService.getGeoCoordinates(anyString())).thenReturn(geoLocationResponse);
    when(mockValidationService.isPostcodeValid(anyString())).thenReturn(true);

    // Act
    List<DosService> services =
        classUnderTest.retrieveServicesByGeoLocation(null, null, 24.5, null, searchPostCode);

    // Assert
    assertEquals(0, services.size()); // because no valid postcode in dos services
  }

  @Test
  public void shouldThrowInvalidParameterExceptionWhenGoogleAPIReturnsZeroResults()
      throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchPostCode = "XX1 1XX";

    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    GeoPoint geoPoint = new GeoPoint(12.45, 23.45);
    Geometry geometry = mock(Geometry.class);
    Location location = mock(Location.class);
    when(location.getLat()).thenReturn(22.0);
    when(location.getLng()).thenReturn(22.0);
    when(geometry.getLocation()).thenReturn(location);

    GeoLocationResponse geoLocationResponse = mock(GeoLocationResponse.class);
    GeoLocationResponseResult[] geoLocationResponseResults = new GeoLocationResponseResult[0];
    when(geoLocationResponse.getGeoLocationResponseResults())
        .thenReturn(geoLocationResponseResults);
    when(locationService.distanceBetween(geoPoint, geoPoint)).thenReturn(99.00);
    when(apiHandshakeService.getGeoCoordinates(anyString())).thenReturn(geoLocationResponse);
    when(mockValidationService.isPostcodeValid(anyString())).thenReturn(true);
    // Act
    InvalidParameterException exception =
        assertThrows(
            InvalidParameterException.class,
            () -> {
              classUnderTest.retrieveServicesByGeoLocation(null, null, null, null, searchPostCode);
            });
    assertEquals(exception.getMessage(), ErrorMessageEnum.INVALID_POSTCODE.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidParameterException  when all values null")
  public void shouldThrowInvalidParameterExceptionWhenValuesNullOrEmpty() {
    InvalidParameterException exception =
        assertThrows(
            InvalidParameterException.class,
            () -> {
              classUnderTest.retrieveServicesByGeoLocation(null, null, null, null, null);
            });
    assertEquals(exception.getMessage(), ErrorMessageEnum.INVALID_LAT_LON_VALUES.getMessage());
  }

  @Test
  public void dosReturnsEmptyLocationsTest(CapturedOutput log)
      throws NotFoundException, InvalidParameterException {
    // Arrange
    String searchLatitude = "23.45";
    String searchLongitude = "-0.3456";
    Double distanceRange = 0.0;
    List<String> searchTerms = Arrays.asList("term1");
    String postcode = "XX1 1XX";

    List<DosService> dosServices = new ArrayList<>();
    DosService dosService = MockDosServicesUtil.mockDosServices.get(1);
    dosService.setLocation(null);
    dosServices.add(dosService);
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));
    GeoPoint geoPoint = new GeoPoint(12.45, 23.45);
    Geometry geometry = mock(Geometry.class);
    Location location = mock(Location.class);
    when(location.getLat()).thenReturn(22.0);
    when(location.getLng()).thenReturn(22.0);
    when(geometry.getLocation()).thenReturn(location);

    GeoLocationResponse geoLocationResponse = mock(GeoLocationResponse.class);
    GeoLocationResponseResult[] geoLocationResponseResults = new GeoLocationResponseResult[1];
    GeoLocationResponseResult geoLocationResponseResult = mock(GeoLocationResponseResult.class);
    when(geoLocationResponseResult.getGeometry()).thenReturn(geometry);
    geoLocationResponseResults[0] = geoLocationResponseResult;
    when(geoLocationResponse.getGeoLocationResponseResults())
        .thenReturn(geoLocationResponseResults);
    when(locationService.distanceBetween(geoPoint, geoPoint)).thenReturn(99.00);
    when(apiHandshakeService.getGeoCoordinates(anyString())).thenReturn(geoLocationResponse);

    when(mockValidationService.isPostcodeValid(anyString())).thenReturn(true);
    when(apiUtilsService.sanitiseSearchTerms(searchTerms)).thenReturn(searchTerms);
    when(apiUtilsService.removeBlankSpaces(postcode)).thenReturn("XX11XX");
    when(serviceRepository.findAllServicesByGeoLocationWithSearchTerms(
            Double.parseDouble(searchLatitude),
            Double.parseDouble(searchLongitude),
            distanceRange,
            searchTerms))
        .thenReturn(dosServices);
    // Act
    List<DosService> services =
        classUnderTest.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms, postcode);
    assertTrue(log.getOut().contains("Searching using location & search terms:"));
    verify(mockValidationService, times(1)).validateSearchCriteria(searchTerms);
    // Assert
    assertEquals(2, services.size());
  }

  @Test
  public void googleAPIUsedToPopulateServicesWithoutLatLongValues()
      throws NotFoundException, InvalidParameterException {
    // Given
    final String searchLatitude = "0";
    final String searchLongitude = "0";
    final Double distanceRange = 0D;
    final String postcode = "EX7 8PR";
    final List<String> searchTerms = Arrays.asList("search", "terms");
    List<DosService> dosServices = new ArrayList<>();
    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(21));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(21));
    // final MultiValueMap<String, String> mockHeaders = new
    // LinkedMultiValueMap<>();
    GeoPoint geoPoint = new GeoPoint(12.45, 23.45);
    Geometry geometry = mock(Geometry.class);
    Location location = mock(Location.class);
    when(location.getLat()).thenReturn(22.0);
    when(location.getLng()).thenReturn(22.0);
    when(geometry.getLocation()).thenReturn(location);

    GeoLocationResponse geoLocationResponse = mock(GeoLocationResponse.class);
    GeoLocationResponseResult[] geoLocationResponseResults = new GeoLocationResponseResult[1];
    GeoLocationResponseResult geoLocationResponseResult = mock(GeoLocationResponseResult.class);
    when(geoLocationResponseResult.getGeometry()).thenReturn(geometry);
    geoLocationResponseResults[0] = geoLocationResponseResult;
    when(geoLocationResponse.getGeoLocationResponseResults())
        .thenReturn(geoLocationResponseResults);
    when(locationService.distanceBetween(geoPoint, geoPoint)).thenReturn(99.00);
    when(apiHandshakeService.getGeoCoordinates(anyString())).thenReturn(geoLocationResponse);

    doNothing().when(mockValidationService).validateSearchCriteria(searchTerms);
    when(serviceRepository.findAllServicesByGeoLocationWithSearchTerms(
            Double.parseDouble(searchLatitude),
            Double.parseDouble(searchLongitude),
            distanceRange,
            searchTerms))
        .thenReturn(dosServices);

    when(mockValidationService.isPostcodeValid(anyString())).thenReturn(true);
    // when(apiHandshakeService.getAccessTokenHeader()).thenReturn(mockHeaders);
    // when(locationService.getLocationForPostcode(postcode,mockHeaders)).thenReturn(searchLocation);
    // When
    List<DosService> fuzzyResults =
        classUnderTest.retrieveServicesByGeoLocation(
            searchLatitude, searchLongitude, distanceRange, searchTerms, postcode);
    // Then
    assertEquals(3, fuzzyResults.size());
  }
}
