package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.digital.uec.api.exception.ValidationException;
import uk.nhs.digital.uec.api.service.impl.ValidationService;

@ExtendWith(SpringExtension.class)
public class ValidationServiceTest {

  @InjectMocks private ValidationService validationService;

  private int minSearchTermLength = 3;

  private int maxSearchCriteria = 10;

  private String validationMessage;

  private String searchCriteriaEmptyMessage;

  private String codeVal1;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(validationService, "minSearchTermLength", minSearchTermLength);
    ReflectionTestUtils.setField(validationService, "maxSearchCriteria", maxSearchCriteria);
    validationMessage = "Validation error not expected: ";
    searchCriteriaEmptyMessage =
        "No validation exception raised when expected because the search criteria is empty.";
    codeVal1 = "VAL-001";
  }

  @Test
  public void validateSearchCriteriaMinSuccess() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (ValidationException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaSuccess() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");
    searchCriteria.add("term3");

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (ValidationException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLimitSuccess() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    for (int i = 0; i < maxSearchCriteria; i++) {
      searchCriteria.add("term" + i);
    }

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (ValidationException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaEmpty() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteria);
      fail(searchCriteriaEmptyMessage);
    } catch (ValidationException ve) {
      assertEquals(codeVal1, ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaNull() {
    // Act and Assert
    try {
      validationService.validateSearchCriteria(null);
      fail(searchCriteriaEmptyMessage);
    } catch (ValidationException ve) {
      assertEquals(codeVal1, ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaTooManyTerms() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();

    for (int i = 0; i < maxSearchCriteria + 1; i++) {
      searchCriteria.add("term" + i);
    }

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteria);
      fail(
          "No validation exception raised when expected because the number of search criteria is"
              + " greater than the max amount.");
    } catch (ValidationException ve) {
      assertEquals("VAL-002", ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthSuccess() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("123");

    // Act and Assert
    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
    } catch (ValidationException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthOneTermValidSuccess() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("1");
    searchCriteria.add("12");
    searchCriteria.add("123");

    // Act and Assert
    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
    } catch (ValidationException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLengthEmpty() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();

    // Act and Assert
    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
      fail(searchCriteriaEmptyMessage);
    } catch (ValidationException ve) {
      assertEquals(codeVal1, ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLengthNull() {
    // Act and Assert
    try {
      validationService.validateMinSearchCriteriaLength(null);
      fail(searchCriteriaEmptyMessage);
    } catch (ValidationException ve) {
      assertEquals(codeVal1, ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthTooSmallError() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("12");

    // Act and Assert
    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
      fail(
          "No validation exception raised when expected because no search term is greater than the"
              + " min number of required characters.");
    } catch (ValidationException ve) {
      assertEquals("VAL-003", ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }
}
