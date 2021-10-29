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

  private String postCode;

  @BeforeEach
  public void initialise() {
    postcodeLocation = new PostcodeLocation();
    postcodeLocation.setPostCode("EX88PR");
    postcodeLocation.setEasting(297717);
    postcodeLocation.setNorthing(81762);
    postCode = "EX88PR";
  }

  @Test
  public void getLocationForNullPostcode() {
    PostcodeLocation location = locationService.getLocationForPostcode(null, null);
    assertNull(location);
  }

  @Test
  public void getPostcodeMappingLocationForValidPostcode() {
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
    List<PostcodeLocation> listLocations = new ArrayList<>();
    listLocations.add(postcodeLocation);
    when(apiHandshakeService.getPostcodeMappings(anyList(), any())).thenReturn(listLocations);
    PostcodeLocation returnedLocation = locationService.getLocationForPostcode(postCode, any());
    assertEquals(postCode, returnedLocation.getPostCode());
  }

  @Test
  public void getLocationForValidPostcodes() {
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
    source.setPostCode("EX21PR");
    source.setEasting(null);
    source.setNorthing(null);
    PostcodeLocation destination = new PostcodeLocation();
    destination.setPostCode("EX22PR");
    destination.setEasting(237765);
    destination.setNorthing(176543);
    Double distanceReturned = locationService.distanceBetween(source, destination);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithDestinationEastingAndNorthingNull() {
    PostcodeLocation destination = new PostcodeLocation();
    destination.setPostCode("EX21SR");
    destination.setEasting(null);
    destination.setNorthing(null);
    PostcodeLocation source = new PostcodeLocation();
    source.setPostCode("EX22SR");
    source.setEasting(221133);
    source.setNorthing(298223);
    Double distanceReturned = locationService.distanceBetween(source, destination);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithNullSource() {

    PostcodeLocation destinationLocation = new PostcodeLocation();
    destinationLocation.setPostCode("EX26PR");
    destinationLocation.setEasting(43212);
    destinationLocation.setNorthing(87896);

    Double distanceReturned = locationService.distanceBetween(null, destinationLocation);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithNullDestination() {

    Double distanceReturned = locationService.distanceBetween(postcodeLocation, null);
    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithSourceAndDestination() {

    PostcodeLocation sourceLocation = new PostcodeLocation();
    sourceLocation.setPostCode("EX1QPR");
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
