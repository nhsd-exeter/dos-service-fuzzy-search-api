package uk.nhs.digital.uec.api.model.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeoLocationResponseTest {

  private ObjectMapper mapper = new ObjectMapper();


  @Test
  public void testDeserialization() throws JsonProcessingException {
    String json = "{" +
      "  \"status\":\"ok\"," +
      "  \"results\":[" +
      "    {" +
      "      \"formatted_address\":\"1 My Street, Anytown, AN1 123\"," +
      "      \"place_id\":\"ChIJdd4hrwug2EcRmSrV3Vo6llI\"," +
      "      \"geometry\":{" +
      "        \"location\":{" +
      "          \"lat\":51.5073509," +
      "          \"lng\":-0.1277583" +
      "        }," +
      "        \"location_type\":\"APPROXIMATE\"" +
      "      }" +
      "    }" +
      "  ]" +
      "}";

    GeoLocationResponse response = mapper.readValue(json, GeoLocationResponse.class);
    assertEquals("ok", response.getStatus());
    assertEquals(1, response.getGeoLocationResponseResults().length);
    assertEquals("1 My Street, Anytown, AN1 123", response.getGeoLocationResponseResults()[0].getFormattedAddress());
    assertEquals("ChIJdd4hrwug2EcRmSrV3Vo6llI", response.getGeoLocationResponseResults()[0].getPlaceId());
    assertEquals(51.5073509, response.getGeoLocationResponseResults()[0].getGeometry().getLocation().getLat());
    assertEquals(-0.1277583, response.getGeoLocationResponseResults()[0].getGeometry().getLocation().getLng());
    assertEquals("APPROXIMATE", response.getGeoLocationResponseResults()[0].getGeometry().getLocation_type());
  }


}
