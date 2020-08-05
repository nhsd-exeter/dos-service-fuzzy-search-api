package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.exception.ValidationException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("prod")
public class ValidationServiceTest {

  @Autowired private ValidationServiceInterface validationService;

  @Value("${param.validation.min_search_string_length}")
  private int minSearchStringLength;

  @Value("${param.validation.max_search_criteria}")
  private int maxSearchCriteria;

  @Test
  public void validateSearchCriteriaMinSuccess() {

    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");

    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (ValidationException ve) {
      fail("Validation error not expected: " + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaSuccess() {

    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");
    searchCriteria.add("term3");

    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (ValidationException ve) {
      fail("Validation error not expected: " + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLimitSuccess() {

    final List<String> searchCriteria = new ArrayList<>();
    for (int i = 0; i < maxSearchCriteria; i++) {
      searchCriteria.add("term" + i);
    }

    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (ValidationException ve) {
      fail("Validation error not expected: " + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaEmpty() {

    final List<String> searchCriteria = new ArrayList<>();

    try {
      validationService.validateSearchCriteria(searchCriteria);
      fail("No validation exception raised when expected because the search criteria is empty.");
    } catch (ValidationException ve) {
      assertEquals("VAL-001", ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaNull() {

    try {
      validationService.validateSearchCriteria(null);
      fail("No validation exception raised when expected because the search criteria is empty.");
    } catch (ValidationException ve) {
      assertEquals("VAL-001", ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaTooManyTerms() {

    final List<String> searchCriteria = new ArrayList<>();

    for (int i = 0; i < maxSearchCriteria + 1; i++) {
      searchCriteria.add("term" + i);
    }

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

    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("123");

    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
    } catch (ValidationException ve) {
      fail("Validation error not expected: " + ve.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthOneTermValidSuccess() {

    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("1");
    searchCriteria.add("12");
    searchCriteria.add("123");

    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
    } catch (ValidationException ve) {
      fail("Validation error not expected: " + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLengthEmpty() {

    final List<String> searchCriteria = new ArrayList<>();

    try {
      validationService.validateMinSearchCriteriaLength(searchCriteria);
      fail("No validation exception raised when expected because the search criteria is empty.");
    } catch (ValidationException ve) {
      assertEquals("VAL-001", ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLengthNull() {

    try {
      validationService.validateMinSearchCriteriaLength(null);
      fail("No validation exception raised when expected because the search criteria is empty.");
    } catch (ValidationException ve) {
      assertEquals("VAL-001", ve.getValidationCode());
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthTooSmallError() {

    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("12");

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
