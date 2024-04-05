package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiSearchParamsResponseTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testDeserializesFromJson() throws JsonProcessingException {
    String json = """
      {
        "search_criteria": [
          "testcriteria1"  ],
        "search_location": "location",
        "search_latitude": "123",
        "search_longitude": "321",
        "distance_range": "60",
        "referral_role": "testreferralrole",
        "fuzz_level": "testfuzzlevel",
        "address_priority": 1,
        "name_priority": 2,
        "postcode_priority": 3,
        "public_name_priority": 4,
        "max_number_of_services_to_return": 5
      }""";
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
