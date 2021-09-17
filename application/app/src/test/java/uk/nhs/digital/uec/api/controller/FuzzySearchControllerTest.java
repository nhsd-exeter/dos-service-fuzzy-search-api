package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.impl.ApiUtilsService;
import uk.nhs.digital.uec.api.service.impl.FuzzyServiceSearchService;
import uk.nhs.digital.uec.api.service.impl.ValidationService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
public class FuzzySearchControllerTest {

  @InjectMocks FuzzyServiceSearchController fuzzyServiceSearchController;
  @Spy ApiSuccessResponse mockResponse;
  @Spy ApiRequestParams mockRequestParams;
  @Mock ApiUtilsService mockUtilService;

  @Mock ValidationService mockValidationService;
  @Mock FuzzyServiceSearchService mockFuzzyServiceSearchService;

  private static final String VALIDATION_ERROR_MSG = "A validation error has occurred";

  private static final String VALIDATION_ERROR_CODE = "VAL-001";

  private static final String FILTER_REFERRAL_ROLE = null;
  private static final Integer MAX_SERVICES_TO_RETURN_FROM_ES = 10;
  private static final Integer MAX_SERVICES_TO_RETURN = 5;
  private static final Integer FUZZ_LEVEL = 0;
  private static final Integer NAME_PRIORITY = 0;
  private static final Integer ADDRESS_PRIORITY = 0;
  private static final Integer POSTCODE_PRIORITY = 0;
  private static final Integer PUBLIC_NAME_PRIORITY = 0;
  private static final String SEARCH_POSTCODE = "EX2 3SE";

  @Test
  public void getServicesByFuzzySearchTestSucc() throws ValidationException {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    when(mockRequestParams.getAddressPriority()).thenReturn(ADDRESS_PRIORITY);
    when(mockRequestParams.getNamePriority()).thenReturn(NAME_PRIORITY);
    when(mockRequestParams.getPostcodePriority()).thenReturn(POSTCODE_PRIORITY);
    when(mockRequestParams.getPublicNamePriority()).thenReturn(PUBLIC_NAME_PRIORITY);
    when(mockRequestParams.getFuzzLevel()).thenReturn(FUZZ_LEVEL);
    when(mockRequestParams.getMaxNumServicesToReturn()).thenReturn(MAX_SERVICES_TO_RETURN);
    when(mockFuzzyServiceSearchService.retrieveServicesByFuzzySearch(
            SEARCH_POSTCODE, searchCriteria, null))
        .thenReturn(getDosServices());

    // Act
    ResponseEntity<ApiResponse> responseEntity =
        fuzzyServiceSearchController.getServicesByFuzzySearch(
            searchCriteria,
            SEARCH_POSTCODE,
            FILTER_REFERRAL_ROLE,
            MAX_SERVICES_TO_RETURN_FROM_ES,
            MAX_SERVICES_TO_RETURN,
            FUZZ_LEVEL,
            NAME_PRIORITY,
            ADDRESS_PRIORITY,
            POSTCODE_PRIORITY,
            PUBLIC_NAME_PRIORITY,
            null);

    // Assert
    final ApiSuccessResponse response = (ApiSuccessResponse) responseEntity.getBody();
    final List<DosService> returnedServices = response.getSearchResults().getServices();

    verify(mockValidationService, times(1)).validateSearchCriteria(searchCriteria);
    verify(mockValidationService, times(1)).validateMinSearchCriteriaLength(searchCriteria);
    verify(mockFuzzyServiceSearchService, times(1))
        .retrieveServicesByFuzzySearch(SEARCH_POSTCODE, searchCriteria, null);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    assertEquals(2, response.getSearchResults().getNumberOfServicesFound());
    assertTrue(isExpectedServiceReturned("service1", returnedServices));
    assertTrue(isExpectedServiceReturned("service2", returnedServices));
  }

  @Test
  public void getServicesByFuzzySearchTestValidationError() throws ValidationException {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    when(mockRequestParams.getAddressPriority()).thenReturn(ADDRESS_PRIORITY);
    when(mockRequestParams.getNamePriority()).thenReturn(NAME_PRIORITY);
    when(mockRequestParams.getPostcodePriority()).thenReturn(POSTCODE_PRIORITY);
    when(mockRequestParams.getPublicNamePriority()).thenReturn(PUBLIC_NAME_PRIORITY);
    when(mockRequestParams.getFuzzLevel()).thenReturn(FUZZ_LEVEL);
    when(mockRequestParams.getMaxNumServicesToReturn()).thenReturn(MAX_SERVICES_TO_RETURN);

    doThrow(new ValidationException(VALIDATION_ERROR_MSG, VALIDATION_ERROR_CODE))
        .when(mockValidationService)
        .validateSearchCriteria(searchCriteria);

    // Act
    ResponseEntity<ApiResponse> responseEntity =
        fuzzyServiceSearchController.getServicesByFuzzySearch(
            searchCriteria,
            SEARCH_POSTCODE,
            FILTER_REFERRAL_ROLE,
            MAX_SERVICES_TO_RETURN_FROM_ES,
            MAX_SERVICES_TO_RETURN,
            FUZZ_LEVEL,
            NAME_PRIORITY,
            ADDRESS_PRIORITY,
            POSTCODE_PRIORITY,
            PUBLIC_NAME_PRIORITY,
            null);

    // Assert
    final ApiValidationErrorResponse response =
        (ApiValidationErrorResponse) responseEntity.getBody();

    verify(mockValidationService, times(1)).validateSearchCriteria(searchCriteria);
    verify(mockFuzzyServiceSearchService, never())
        .retrieveServicesByFuzzySearch(SEARCH_POSTCODE, searchCriteria, null);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(VALIDATION_ERROR_CODE, response.getValidationCode());
    assertEquals(VALIDATION_ERROR_MSG, response.getValidationError());
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
