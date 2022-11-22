package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.digital.uec.api.model.ErrorMappingEnum;
import uk.nhs.digital.uec.api.model.ErrorMappingEnum.ValidationCodes;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.impl.ValidationService;

@ExtendWith(SpringExtension.class)
public class ValidationServiceTest {

  @InjectMocks private ValidationService validationService;

  private int minSearchTermLength = 3;

  private int maxSearchCriteria = 10;

  private String validationMessage;

  private String searchCriteriaEmptyMessage;

  private String codeVal1;

  private List<String> searchCriteria = null;

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(validationService, "minSearchTermLength", minSearchTermLength);
    ReflectionTestUtils.setField(validationService, "maxSearchCriteria", maxSearchCriteria);
    validationMessage = "Validation error not expected: ";
    searchCriteriaEmptyMessage =
        "No validation exception raised when expected because the search criteria is empty.";
    codeVal1 = "VAL-001";
    searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");
    searchCriteria.add("term3");
  }

  @Test
  public void validateSearchCriteriaMinSuccess() {
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (NotFoundException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaSuccess() {
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (NotFoundException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLimitSuccess() {
    // Arrange
    final List<String> searchCriteriaLimit = new ArrayList<>();
    for (int i = 0; i < maxSearchCriteria; i++) {
      searchCriteriaLimit.add("term" + i);
    }
    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteriaLimit);
    } catch (NotFoundException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaEmpty() {
    // Arrange
    final List<String> searchCriteriaEmpty = new ArrayList<>();

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteriaEmpty);
      fail(searchCriteriaEmptyMessage);
    } catch (NotFoundException ve) {
      assertEquals(codeVal1, getValidationCodeForErrorMessage(ve.getMessage()));
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaNull() {
    try {
      validationService.validateSearchCriteria(null);
      fail(searchCriteriaEmptyMessage);
    } catch (NotFoundException ve) {
      assertEquals(codeVal1, getValidationCodeForErrorMessage(ve.getMessage()));
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaTooManyTerms() {
    String validationCode = null;
    // Arrange
    final List<String> searchCriteriaMaxTerms = new ArrayList<>();

    for (int i = 0; i < maxSearchCriteria + 1; i++) {
      searchCriteriaMaxTerms.add("term" + i);
    }

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteriaMaxTerms);
      fail(
          "No validation exception raised when expected because the number of search criteria is"
              + " greater than the max amount.");
    } catch (NotFoundException ve) {
      if (getValidationCodeForErrorMessage(ve.getMessage()) == null
          && ve.getMessage().contains(String.valueOf(maxSearchCriteria))) {
        validationCode = ValidationCodes.VAL002.getValidationCode();
      }
    }
    assertEquals("VAL-002", validationCode);
  }

  @Test
  public void validateMinSearchTermLengthSuccess() {
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (NotFoundException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthOneTermValidSuccess() {
    try {
      validationService.validateSearchCriteria(searchCriteria);
    } catch (NotFoundException ve) {
      fail(validationMessage + ve.getMessage());
    }
  }

  @Test
  public void validateSearchCriteriaLengthNull() {
    // Act and Assert
    try {
      validationService.validateSearchCriteria(null);
      fail(searchCriteriaEmptyMessage);
    } catch (NotFoundException ve) {
      assertEquals(codeVal1, getValidationCodeForErrorMessage(ve.getMessage()));
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void validateMinSearchTermLengthTooSmallError() {
    // Arrange
    final List<String> searchCriteriaLessThanMinLength = new ArrayList<>();
    searchCriteriaLessThanMinLength.add("te");

    // Act and Assert
    try {
      validationService.validateSearchCriteria(searchCriteriaLessThanMinLength);
      fail(
          "No validation exception raised when expected because no search term is greater than the"
              + " min number of required characters.");
    } catch (NotFoundException ve) {
      assertEquals("VAL-003", getValidationCodeForErrorMessage(ve.getMessage()));
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void GivenPostcodesShouldBeValidPostcodes() {
    assertEquals(true,validationService.isPostcodeValid("MK8 1AS"));
    assertEquals(true,validationService.isPostcodeValid("MK130LG"));
    assertEquals(true,validationService.isPostcodeValid("mk130lg"));
    assertEquals(true,validationService.isPostcodeValid("mk13 0lg"));
    assertEquals(true,validationService.isPostcodeValid("DN1 1AA"));
    assertEquals(true,validationService.isPostcodeValid("BL0 0AE"));

  }

  @Test
  public void GivenPostcodesShouldBeInValidPostcodes() {
    assertEquals(false,validationService.isPostcodeValid("MKK 1AS"));
    assertEquals(false,validationService.isPostcodeValid("not available"));
    assertEquals(false,validationService.isPostcodeValid("null"));
    assertEquals(false,validationService.isPostcodeValid("blank"));
    assertEquals(false,validationService.isPostcodeValid("ZZ1123"));

  }


  private String getValidationCodeForErrorMessage(String errorMessage) {
    Optional<ValidationCodes> validationCodesOptional =
        ErrorMappingEnum.getValidationEnum().entrySet().stream()
            .filter(entry -> errorMessage.startsWith(entry.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();
    return validationCodesOptional.isPresent()
        ? validationCodesOptional.get().getValidationCode()
        : null;
  }
}
