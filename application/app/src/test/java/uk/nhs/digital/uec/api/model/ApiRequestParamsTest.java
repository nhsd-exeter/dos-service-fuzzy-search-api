package uk.nhs.digital.uec.api.model;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.uec.api.util.Constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiRequestParamsTest {

  private final ApiRequestParams apiRequestParams = new ApiRequestParams();

  @Test
  public void testMaxNumServicesToReturnFromElasticSearchDefaultsCorrectly() {
    apiRequestParams.setDefaultMaxNumServicesToReturnFromElasticsearch(10);
    assertEquals(10, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());
    apiRequestParams.setMaxNumServicesToReturnFromElasticsearch(20);
    assertEquals(20, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());
    assertEquals(10, apiRequestParams.getDefaultMaxNumServicesToReturnFromElasticsearch());

  }

  @Test
  public void testMaxNumServicesToReturnFromElasticsearch3SearchTermsDefaultsCorrectly() {
    apiRequestParams.setDefaultMaxNumServicesToReturnFromElasticsearch3SearchTerms(10);
    assertEquals(10, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms());
    apiRequestParams.setMaxNumServicesToReturnFromElasticsearch3SearchTerms(20);
    assertEquals(20, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms());
    assertEquals(10, apiRequestParams.getDefaultMaxNumServicesToReturnFromElasticsearch3SearchTerms());
  }

  @Test
  public void testMaxNumServicesToReturnDefaultsCorrectly() {
    apiRequestParams.setDefaultMaxNumServicesToReturn(10);
    assertEquals(10, apiRequestParams.getMaxNumServicesToReturn());
    apiRequestParams.setMaxNumServicesToReturn(20);
    assertEquals(20, apiRequestParams.getMaxNumServicesToReturn());
    assertEquals(10, apiRequestParams.getDefaultMaxNumServicesToReturn());
  }

  @Test
  public void testFuzzLevelDefaultsCorrectly() {
    apiRequestParams.setDefaultFuzzLevel(5);
    assertEquals(5, apiRequestParams.getFuzzLevel());
    apiRequestParams.setFuzzLevel(6);
    assertEquals(6, apiRequestParams.getFuzzLevel());
    assertEquals(5, apiRequestParams.getDefaultFuzzLevel());
  }

  @Test
  public void testNamePrioritySetsViaDefaultsCorrectly() {
    apiRequestParams.setDefaultNamePriority(5);
    apiRequestParams.setNamePriority(null);
    assertEquals(5, apiRequestParams.getNamePriority());
    apiRequestParams.setNamePriority(6);
    assertEquals(6, apiRequestParams.getNamePriority());
    assertEquals(5, apiRequestParams.getDefaultNamePriority());
  }

  @Test
  public void testAddressPriorityDefaultsCorrectly() {
    apiRequestParams.setDefaultAddressPriority(5);
    apiRequestParams.setAddressPriority(null);
    assertEquals(5, apiRequestParams.getAddressPriority());
    apiRequestParams.setAddressPriority(6);
    assertEquals(6, apiRequestParams.getAddressPriority());
    assertEquals(5, apiRequestParams.getDefaultAddressPriority());
  }

  @Test
  public void testPostcodePrioritySetsViaDefaultCorrectly() {
    apiRequestParams.setDefaultPostcodePriority(5);
    apiRequestParams.setPostcodePriority(null);
    assertEquals(5, apiRequestParams.getPostcodePriority());
    apiRequestParams.setPostcodePriority(6);
    assertEquals(6, apiRequestParams.getPostcodePriority());
    assertEquals(5, apiRequestParams.getDefaultPostcodePriority());
  }

  @Test
  public void testPublicNamePriorityDefaultsCorrectly() {
    apiRequestParams.setDefaultPublicNamePriority(5);
    apiRequestParams.setPublicNamePriority(null);
    assertEquals(5, apiRequestParams.getPublicNamePriority());
    apiRequestParams.setPublicNamePriority(6);
    assertEquals(6, apiRequestParams.getPublicNamePriority());
    assertEquals(5, apiRequestParams.getDefaultPublicNamePriority());
  }

  @Test
  public void testFilterReferralRoleDefaultsCorrectly() {
    assertEquals(Constants.PROFESSIONAL_REFERRAL_FILTER, apiRequestParams.getFilterReferralRole());
    apiRequestParams.setFilterReferralRole("test filter referral role");
    assertEquals("test filter referral role", apiRequestParams.getFilterReferralRole());
  }

}
