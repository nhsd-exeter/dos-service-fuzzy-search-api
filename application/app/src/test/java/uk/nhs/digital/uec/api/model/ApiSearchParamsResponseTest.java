package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiSearchParamsResponseTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testDeserializesFromJson() throws JsonProcessingException {
    String json = "{\n" +
      "  \"search_criteria\": [\n" +
      "    \"testcriteria1\"" +
      "  ],\n" +
      "  \"search_location\": \"location\",\n" +
      "  \"search_latitude\": \"123\",\n" +
      "  \"search_longitude\": \"321\",\n" +
      "  \"distance_range\": \"60\",\n" +
      "  \"referral_role\": \"testreferralrole\",\n" +
      "  \"fuzz_level\": \"testfuzzlevel\",\n" +
      "  \"address_priority\": 1,\n" +
      "  \"name_priority\": 2,\n" +
      "  \"postcode_priority\": 3,\n" +
      "  \"public_name_priority\": 4,\n" +
      "  \"max_number_of_services_to_return\": 5\n" +
      "}";
    ApiSearchParamsResponse response = objectMapper.readValue(json, ApiSearchParamsResponse.class);
    assertEquals("testcriteria1", response.getSearchCriteria().get(0));
    assertEquals("location", response.getSearchPostcode());
    assertEquals("123", response.getSearchLatitude());
    assertEquals("321", response.getSearchLongitude());
    assertEquals(60, response.getDistanceRange());
    assertEquals("testreferralrole", response.getReferralRole());
    assertEquals("testfuzzlevel", response.getFuzzLevel());
    assertEquals(1, response.getAddressPriority());
    assertEquals(2, response.getNamePriority());
    assertEquals(3, response.getPostcodePriority());
    assertEquals(4, response.getPublicNamePriority());
    assertEquals(5, response.getMaxNumServicesToReturn());

//    response.setSearchLatitude("asdf");
  }

}
