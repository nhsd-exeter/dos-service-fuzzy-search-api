package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.service.impl.LocationService;

@ExtendWith(SpringExtension.class)
public class LocationServiceTest {

  @InjectMocks private LocationService locationService;

  @Mock private ApiUtilsServiceInterface apiUtilsService;

  @Mock private ExternalApiHandshakeInterface apiHandshakeService;

  private PostcodeLocation postcodeLocation = null;

  @BeforeEach
  public void initialise() {
    postcodeLocation = new PostcodeLocation();
    postcodeLocation.setPostCode("EX88PR");
    postcodeLocation.setEasting(297717);
    postcodeLocation.setNorthing(81762);
  }

  @Test
  public void getLocationForNullPostcode() {
    PostcodeLocation location = locationService.getLocationForPostcode(null, null);
    assertNull(location);
  }

  @Test
  public void getPostcodeMappingLocationForValidPostcode() {

    String postCode = "EX88PR";
    List<PostcodeLocation> listLocations = new ArrayList<>();
    listLocations.add(postcodeLocation);
    when(apiHandshakeService.getPostcodeMappings(anyList(), any())).thenReturn(listLocations);
    PostcodeLocation returnedLocation = locationService.getLocationForPostcode(postCode, any());
    assertEquals(postCode, returnedLocation.getPostCode());
    assertEquals(297717, returnedLocation.getEasting());
    assertEquals(81762, returnedLocation.getNorthing());
  }

  @Test
  public void getLocationForValidPostcode() {
    String postCode = "EX88PR";
    List<PostcodeLocation> listLocations = new ArrayList<>();
    listLocations.add(postcodeLocation);
    when(apiHandshakeService.getPostcodeMappings(anyList(), any())).thenReturn(listLocations);
    PostcodeLocation returnedLocation = locationService.getLocationForPostcode(postCode, any());
    assertEquals(postCode, returnedLocation.getPostCode());
  }

  @Test
  public void getLocationForValidPostcodes() {
    String postCode = "EX88PR";
    List<String> postCodes = new ArrayList<>();
    List<PostcodeLocation> listLocations = new ArrayList<>();
    listLocations.add(postcodeLocation);
    postCodes.add(postCode);
    when(apiHandshakeService.getPostcodeMappings(anyList(), any())).thenReturn(listLocations);
    List<PostcodeLocation> locationsForPostcodes =
        locationService.getLocationsForPostcodes(postCodes, any());
    PostcodeLocation returnedLocation = locationsForPostcodes.get(0);
    assertEquals(postCode, returnedLocation.getPostCode());
  }

  @Test
  public void distanceWithNullSourceAndDestination() {
    Double distanceReturned = locationService.distanceBetween(null, null);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithSourceEastingAndNorthingNull() {
    PostcodeLocation source = new PostcodeLocation();
    source.setPostCode("EX26ER");
    source.setEasting(null);
    source.setNorthing(null);
    PostcodeLocation destination = new PostcodeLocation();
    destination.setPostCode("EX26ER");
    destination.setEasting(237765);
    destination.setNorthing(176543);
    Double distanceReturned = locationService.distanceBetween(source, destination);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithDestinationEastingAndNorthingNull() {
    PostcodeLocation destination = new PostcodeLocation();
    destination.setPostCode("EX26ER");
    destination.setEasting(null);
    destination.setNorthing(null);
    PostcodeLocation source = new PostcodeLocation();
    source.setPostCode("EX26ER");
    source.setEasting(237765);
    source.setNorthing(176543);
    Double distanceReturned = locationService.distanceBetween(source, destination);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithNullSource() {

    PostcodeLocation destinationLocation = new PostcodeLocation();
    destinationLocation.setPostCode("EX26ER");
    destinationLocation.setEasting(12345);
    destinationLocation.setNorthing(12345);

    Double distanceReturned = locationService.distanceBetween(null, destinationLocation);

    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithNullDestination() {

    PostcodeLocation sourceLocation = new PostcodeLocation();
    sourceLocation.setPostCode("EX26ER");
    sourceLocation.setEasting(12345);
    sourceLocation.setNorthing(12345);

    Double distanceReturned = locationService.distanceBetween(sourceLocation, null);

    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithSourceAndDestination() {

    PostcodeLocation sourceLocation = new PostcodeLocation();
    sourceLocation.setPostCode("EX26ER");
    sourceLocation.setEasting(12345);
    sourceLocation.setNorthing(12345);

    PostcodeLocation destinationLocation = new PostcodeLocation();
    destinationLocation.setPostCode("EX86PJ");
    destinationLocation.setEasting(14355);
    destinationLocation.setNorthing(12445);

    Double distanceReturned = locationService.distanceBetween(sourceLocation, destinationLocation);

    assertEquals(1.3, distanceReturned);
  }
}
