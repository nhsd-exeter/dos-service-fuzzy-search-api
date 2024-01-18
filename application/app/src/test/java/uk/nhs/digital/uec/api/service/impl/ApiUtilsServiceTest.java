package uk.nhs.digital.uec.api.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.ApiRequestParams;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ApiUtilsServiceTest {

  @Mock
  private ApiRequestParams apiRequestParams;

  @InjectMocks
  private ApiUtilsService apiUtilsService;

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
      assertTrue(sanitisedSearchCriteria.contains("term5"));
      assertTrue(sanitisedSearchCriteria.contains("term 6"));
      assertTrue(sanitisedSearchCriteria.contains("term6"));
      assertEquals(8, sanitisedSearchCriteria.size());
  }


  @Test
  public void configureApiRequestParamsTest() {

      // Arrange
      Integer fuzzLevel=2;
      String referralRole="Role One";
      Integer maxNumServicesToReturnFromEs=2;
      Integer maxNumServicesToReturn=2;
      Integer namePriority=1;
      Integer addressPriority=1;
      Integer postcodePriority=3;
      Integer publicNamePriority=1;

      when(apiRequestParams.getFuzzLevel()).thenReturn(fuzzLevel);
      when(apiRequestParams.getFilterReferralRole()).thenReturn(referralRole);
      when(apiRequestParams.getMaxNumServicesToReturnFromElasticsearch()).thenReturn(maxNumServicesToReturnFromEs);
      when(apiRequestParams.getMaxNumServicesToReturn()).thenReturn(maxNumServicesToReturn);
      when(apiRequestParams.getNamePriority()).thenReturn(namePriority);
      when(apiRequestParams.getAddressPriority()).thenReturn(addressPriority);
      when(apiRequestParams.getPostcodePriority()).thenReturn(postcodePriority);
      when(apiRequestParams.getPublicNamePriority()).thenReturn(publicNamePriority);

    // Act
      apiUtilsService.configureApiRequestParams(fuzzLevel, referralRole, maxNumServicesToReturnFromEs, maxNumServicesToReturn, namePriority, addressPriority, postcodePriority, publicNamePriority);

    // Assert
    verify(apiRequestParams).setFuzzLevel(fuzzLevel);
    verify(apiRequestParams).setFilterReferralRole(referralRole);
    verify(apiRequestParams).setMaxNumServicesToReturnFromElasticsearch(maxNumServicesToReturnFromEs);
    verify(apiRequestParams).setMaxNumServicesToReturn(maxNumServicesToReturn);
    verify(apiRequestParams).setNamePriority(namePriority);
    verify(apiRequestParams).setAddressPriority(addressPriority);
    verify(apiRequestParams).setPostcodePriority(postcodePriority);
    verify(apiRequestParams).setPublicNamePriority(publicNamePriority);

    // Assert
    assertEquals(fuzzLevel,apiRequestParams.getFuzzLevel());
    assertEquals(referralRole,apiRequestParams.getFilterReferralRole());
    assertEquals(maxNumServicesToReturnFromEs,apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());
    assertEquals(maxNumServicesToReturn,apiRequestParams.getMaxNumServicesToReturn());
    assertEquals(namePriority,apiRequestParams.getNamePriority());
    assertEquals(addressPriority,apiRequestParams.getAddressPriority());
    assertEquals(postcodePriority,apiRequestParams.getPostcodePriority());
    assertEquals(publicNamePriority,apiRequestParams.getPublicNamePriority());
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

  @Test
  public void removeBlankSpacesIn() {

    List<String> postCodes = Arrays.asList("TN4 9NH", "EX7 1PR");
    List<String> returnedListValue = apiUtilsService.removeBlankSpacesIn(postCodes);

    assertEquals(returnedListValue.get(0), "TN49NH");
    assertEquals(returnedListValue.get(1), "EX71PR");
  }

  @Test
  public void testAddAdditionalSearchTerms() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    List<String> sanitisedSearchTerms = new ArrayList<>();
    String searchTerm = "term1 term2 term3";

    Method addAdditionalSearchTermsMethod = ApiUtilsService.class.getDeclaredMethod("addAdditionalSearchTerms", List.class, String.class);
    addAdditionalSearchTermsMethod.setAccessible(true);
    addAdditionalSearchTermsMethod.invoke(apiUtilsService, sanitisedSearchTerms, searchTerm);

    List<String> expectedSearchTerms = Arrays.asList("term1term2", "term2term3");
    assertEquals(expectedSearchTerms, sanitisedSearchTerms);
  }

}
