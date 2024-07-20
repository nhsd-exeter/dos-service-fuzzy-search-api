package uk.nhs.digital.uec.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ApiSearchResultsResponseTest {

  ApiSearchResultsResponse apiSearchResultsResponse = new ApiSearchResultsResponse();

  @Test
  public void initialiseSizeWhenSettingDosServices() {
    apiSearchResultsResponse.setServices(getDosServices());

    assertEquals(1, apiSearchResultsResponse.getNumberOfServicesFound());
    assertEquals("123", apiSearchResultsResponse.getServices().get(0).getId());
  }

  @Test
  public void testDosServiceCountCanBeOverwritten() {
    apiSearchResultsResponse.setServices(getDosServices());
    apiSearchResultsResponse.setNumberOfServicesFound(2);

    assertEquals(2, apiSearchResultsResponse.getNumberOfServicesFound());
  }

  private List<DosService> getDosServices() {
    DosService dosService = new DosService();
    dosService.setId("123");
    return List.of(dosService);
  }
}
