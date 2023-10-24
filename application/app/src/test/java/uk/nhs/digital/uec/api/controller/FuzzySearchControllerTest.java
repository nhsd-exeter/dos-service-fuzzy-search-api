package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.impl.ApiUtilsService;
import uk.nhs.digital.uec.api.service.impl.DosServiceSearchImpl;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
public class FuzzySearchControllerTest {

  @InjectMocks FuzzyServiceSearchController fuzzyServiceSearchController;

  @Spy ApiSuccessResponse mockResponse;

  @Spy ApiRequestParams mockRequestParams;

  @Mock ApiUtilsService mockUtilService;

  @Mock
  DosServiceSearchImpl mockFuzzyServiceSearchService;

  private static final Integer MAX_SERVICES_TO_RETURN_FROM_ES = 10;
  private static final Integer MAX_SERVICES_TO_RETURN = 5;
  private static final Integer FUZZ_LEVEL = 0;
  private static final Integer NAME_PRIORITY = 0;
  private static final Integer ADDRESS_PRIORITY = 0;
  private static final Integer POSTCODE_PRIORITY = 0;
  private static final Integer PUBLIC_NAME_PRIORITY = 0;
  private static final String SEARCH_POSTCODE = "EX2 3SE";
  private static final String SEARCH_LATITUDE = "23.45";
  private static final String SEARCH_LONGITUDE = "-2.34";
  private static final String PROFESSIONAL_REFERRAL_FILTER = "Professional Referral";
  private static final Double DEFAULT_DISTANCE_RANGE = 60.0D;

  List<String> searchCriteria;

  @BeforeEach
  public void setup() {
    searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");
  }

  @Test
  public void getServicesByFuzzySearchTestSucc()
      throws NotFoundException, InvalidParameterException {
    when(mockRequestParams.getAddressPriority()).thenReturn(ADDRESS_PRIORITY);
    when(mockRequestParams.getNamePriority()).thenReturn(NAME_PRIORITY);
    when(mockRequestParams.getPostcodePriority()).thenReturn(POSTCODE_PRIORITY);
    when(mockRequestParams.getPublicNamePriority()).thenReturn(PUBLIC_NAME_PRIORITY);
    when(mockRequestParams.getFuzzLevel()).thenReturn(FUZZ_LEVEL);
    when(mockRequestParams.getMaxNumServicesToReturn()).thenReturn(MAX_SERVICES_TO_RETURN);
    when(mockRequestParams.getFilterReferralRole()).thenReturn(PROFESSIONAL_REFERRAL_FILTER);
    when(mockFuzzyServiceSearchService.retrieveServicesByGeoLocation(
            SEARCH_LATITUDE,
            SEARCH_LONGITUDE,
            DEFAULT_DISTANCE_RANGE,
            searchCriteria,
            SEARCH_POSTCODE))
        .thenReturn(getDosServices());

    // Act
    ResponseEntity<ApiResponse> responseEntity =
        fuzzyServiceSearchController.getServicesByFuzzySearch(
            searchCriteria,
            SEARCH_POSTCODE,
            SEARCH_LATITUDE,
            SEARCH_LONGITUDE,
            null,
            PROFESSIONAL_REFERRAL_FILTER,
            MAX_SERVICES_TO_RETURN_FROM_ES,
            MAX_SERVICES_TO_RETURN,
            FUZZ_LEVEL,
            NAME_PRIORITY,
            ADDRESS_PRIORITY,
            POSTCODE_PRIORITY,
            PUBLIC_NAME_PRIORITY);

    // Assert
    final ApiSuccessResponse response = (ApiSuccessResponse) responseEntity.getBody();
    final List<DosService> returnedServices = response.getSearchResults().getServices();

    verify(mockFuzzyServiceSearchService, times(1))
        .retrieveServicesByGeoLocation(
            SEARCH_LATITUDE,
            SEARCH_LONGITUDE,
            DEFAULT_DISTANCE_RANGE,
            searchCriteria,
            SEARCH_POSTCODE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    assertEquals(2, response.getSearchResults().getNumberOfServicesFound());
    assertTrue(isExpectedServiceReturned("service1", returnedServices));
    assertTrue(isExpectedServiceReturned("service2", returnedServices));
  }


  public void whenCordinatesNotValidShouldGetBadResponse()
      throws NotFoundException, InvalidParameterException {
    when(mockRequestParams.getAddressPriority()).thenReturn(ADDRESS_PRIORITY);
    when(mockRequestParams.getNamePriority()).thenReturn(NAME_PRIORITY);
    when(mockRequestParams.getPostcodePriority()).thenReturn(POSTCODE_PRIORITY);
    when(mockRequestParams.getPublicNamePriority()).thenReturn(PUBLIC_NAME_PRIORITY);
    when(mockRequestParams.getFuzzLevel()).thenReturn(FUZZ_LEVEL);
    when(mockRequestParams.getMaxNumServicesToReturn()).thenReturn(MAX_SERVICES_TO_RETURN);
    when(mockRequestParams.getFilterReferralRole()).thenReturn(PROFESSIONAL_REFERRAL_FILTER);
    when(mockFuzzyServiceSearchService.retrieveServicesByGeoLocation(
            SEARCH_LATITUDE,
            SEARCH_LONGITUDE,
            DEFAULT_DISTANCE_RANGE,
            searchCriteria,
            SEARCH_POSTCODE))
        .thenReturn(getDosServices());

    // Act
    ResponseEntity<ApiResponse> responseEntity =
        fuzzyServiceSearchController.getServicesByFuzzySearch(
            searchCriteria,
            null,
            "23.45",
            "abc",
            null,
            PROFESSIONAL_REFERRAL_FILTER,
            MAX_SERVICES_TO_RETURN_FROM_ES,
            MAX_SERVICES_TO_RETURN,
            FUZZ_LEVEL,
            NAME_PRIORITY,
            ADDRESS_PRIORITY,
            POSTCODE_PRIORITY,
            PUBLIC_NAME_PRIORITY);

    // Assert
    final ApiValidationErrorResponse response =
        (ApiValidationErrorResponse) responseEntity.getBody();
    final String errorMessage = response.getValidationError();

    verify(mockFuzzyServiceSearchService, times(0))
        .retrieveServicesByGeoLocation(
            SEARCH_LATITUDE,
            SEARCH_LONGITUDE,
            DEFAULT_DISTANCE_RANGE,
            searchCriteria,
            SEARCH_POSTCODE);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorMessageEnum.INVALID_LAT_LON_VALUES.getMessage(), errorMessage);
  }

  @Test
  public void whenDistanceRangeIsSuppliedItShouldUseTheGivenValue()
    throws NotFoundException, InvalidParameterException {

    Double distance_range = 99.99;
    String searchLatitude = "23.45";
    String searchLongitude = "8.00";
    String searchPostCode = null;

    when(mockRequestParams.getAddressPriority()).thenReturn(ADDRESS_PRIORITY);
    when(mockRequestParams.getNamePriority()).thenReturn(NAME_PRIORITY);
    when(mockRequestParams.getPostcodePriority()).thenReturn(POSTCODE_PRIORITY);
    when(mockRequestParams.getPublicNamePriority()).thenReturn(PUBLIC_NAME_PRIORITY);
    when(mockRequestParams.getFuzzLevel()).thenReturn(FUZZ_LEVEL);
    when(mockRequestParams.getMaxNumServicesToReturn()).thenReturn(MAX_SERVICES_TO_RETURN);
    when(mockRequestParams.getFilterReferralRole()).thenReturn(PROFESSIONAL_REFERRAL_FILTER);
    when(mockFuzzyServiceSearchService.retrieveServicesByGeoLocation(
      searchLatitude,
      searchLongitude,
      distance_range,
      searchCriteria,
      searchPostCode))
      .thenReturn(getDosServices());

    // Act
    ResponseEntity<ApiResponse> responseEntity =
      fuzzyServiceSearchController.getServicesByFuzzySearch(
        searchCriteria,
        null,
        searchLatitude,
        searchLongitude,
        distance_range,
        PROFESSIONAL_REFERRAL_FILTER,
        MAX_SERVICES_TO_RETURN_FROM_ES,
        MAX_SERVICES_TO_RETURN,
        FUZZ_LEVEL,
        NAME_PRIORITY,
        ADDRESS_PRIORITY,
        POSTCODE_PRIORITY,
        PUBLIC_NAME_PRIORITY);

    // Assert
      verify(mockFuzzyServiceSearchService, times(1))
      .retrieveServicesByGeoLocation(
        searchLatitude,
        searchLongitude,
        distance_range,
        searchCriteria,
        searchPostCode);
  }






  private boolean isExpectedServiceReturned(
      String expectedServiceName, List<DosService> returnedServices) {
    boolean servicePresent = false;
    for (DosService returnedService : returnedServices) {
      if (returnedService.getName().equals(expectedServiceName)) {
        servicePresent = true;
        break;
      }
    }
    return servicePresent;
  }

  private List<DosService> getDosServices() {
    List<DosService> dosServices = new ArrayList<>();

    dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
    dosServices.add(MockDosServicesUtil.mockDosServices.get(2));

    return dosServices;
  }
}
