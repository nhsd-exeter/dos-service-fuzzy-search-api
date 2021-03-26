package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.service.impl.ApiUtilsService;

@ExtendWith(SpringExtension.class)
public class ApiUtilsServiceTest {

  @InjectMocks private ApiUtilsService apiUtilsService;

  @Test
  public void sanitiseSearchTerms() {
    // Arrange
    final List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("   term2");
    searchCriteria.add("term3   ");
    searchCriteria.add("   term4   ");
    searchCriteria.add("term 5");
    searchCriteria.add("  term 6  ");

    // Act
    final List<String> sanitisedSearchCriteria =
        apiUtilsService.sanitiseSearchTerms(searchCriteria);

    // Assert
    assertTrue(sanitisedSearchCriteria.contains("term1"));
    assertTrue(sanitisedSearchCriteria.contains("term2"));
    assertTrue(sanitisedSearchCriteria.contains("term3"));
    assertTrue(sanitisedSearchCriteria.contains("term4"));
    assertTrue(sanitisedSearchCriteria.contains("term 5"));
    assertTrue(sanitisedSearchCriteria.contains("term 6"));
    assertEquals(sanitisedSearchCriteria.size(), searchCriteria.size());
  }

  @Test
  public void removeBlankSpacesFromNull() {

    String returnedValue = apiUtilsService.removeBlankSpaces(null);

    assertTrue(returnedValue.isEmpty());
  }

  @Test
  public void removeBlankSpacesFromEmpty() {

    String returnedValue = apiUtilsService.removeBlankSpaces("");

    assertTrue(returnedValue.isEmpty());
  }

  @Test
  public void removeBlankSpaces() {

    String valueWithSpaces = "    aaa    b   ccc d    ";
    String expectedResult = "aaabcccd";

    String returnedValue = apiUtilsService.removeBlankSpaces(valueWithSpaces);

    assertEquals(expectedResult, returnedValue);
  }
}
