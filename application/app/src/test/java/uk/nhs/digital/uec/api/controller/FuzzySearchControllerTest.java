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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.model.ApiResponse;
import uk.nhs.digital.uec.api.model.ApiSuccessResponse;
import uk.nhs.digital.uec.api.model.ApiValidationErrorResponse;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.impl.FuzzyServiceSearchService;
import uk.nhs.digital.uec.api.service.impl.ValidationService;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@ExtendWith(SpringExtension.class)
public class FuzzySearchControllerTest {

  @InjectMocks FuzzyServiceSearchController fuzzyServiceSearchController;

  @Mock ValidationService mockValidationService;
  @Mock FuzzyServiceSearchService mockFuzzyServiceSearchService;

  private static final String VALIDATION_ERROR_MSG = "A validation error has occurred";

  private static final String VALIDATION_ERROR_CODE = "VAL-001";

  @Test
  public void getServicesByFuzzySearchTestSucc() throws ValidationException {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    // Act
    when(mockFuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria))
        .thenReturn(getDosServices());

    ResponseEntity<ApiResponse> responseEntity =
        fuzzyServiceSearchController.getServicesByFuzzySearch(searchCriteria, null, 0, 0);

    // Assert
    final ApiSuccessResponse response = (ApiSuccessResponse) responseEntity.getBody();
    final List<DosService> returnedServices = response.getServices();

    verify(mockValidationService, times(1)).validateSearchCriteria(searchCriteria);
    verify(mockValidationService, times(1)).validateMinSearchCriteriaLength(searchCriteria);
    verify(mockFuzzyServiceSearchService, times(1)).retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    assertEquals(response.getNumberOfServices(), 2);
    assertTrue(isExpectedServiceReturned("service1", returnedServices));
    assertTrue(isExpectedServiceReturned("service2", returnedServices));
  }

  @Test
  public void getServicesByFuzzySearchTestValidationError() throws ValidationException {
    // Arrange
    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    // Act
    doThrow(new ValidationException(VALIDATION_ERROR_MSG, VALIDATION_ERROR_CODE))
        .when(mockValidationService)
        .validateSearchCriteria(searchCriteria);

    ResponseEntity<ApiResponse> responseEntity =
        fuzzyServiceSearchController.getServicesByFuzzySearch(searchCriteria, null, 0, 0);

    // Assert
    final ApiValidationErrorResponse response =
        (ApiValidationErrorResponse) responseEntity.getBody();

    verify(mockValidationService, times(1)).validateSearchCriteria(searchCriteria);
    verify(mockFuzzyServiceSearchService, never()).retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    assertEquals(response.getValidationCode(), VALIDATION_ERROR_CODE);
    assertEquals(response.getValidationError(), VALIDATION_ERROR_MSG);
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
