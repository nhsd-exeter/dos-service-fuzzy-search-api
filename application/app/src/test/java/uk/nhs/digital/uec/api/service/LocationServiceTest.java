package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.dynamo.PostcodeLocation;
import uk.nhs.digital.uec.api.repository.dynamo.PostcodeLocationRepo;
import uk.nhs.digital.uec.api.service.impl.LocationService;

@ExtendWith(SpringExtension.class)
public class LocationServiceTest {

  @InjectMocks private LocationService locationService;

  @Mock private ApiUtilsServiceInterface apiUtilsService;

  @Mock private PostcodeLocationRepo postcodeLocationRepo;

  @Test
  public void getLocationForNullPostcode() {

    PostcodeLocation location = locationService.getLocationForPostcode(null);

    assertNull(location);
  }

  @Test
  public void getLocationForValidPostcode() {

    String postcode = "EX8 5SE";
    String sanitisedPostcode = "EX85SE";

    PostcodeLocation location = new PostcodeLocation();
    location.setPostcode(sanitisedPostcode);
    Optional<PostcodeLocation> optLocation = Optional.of(location);
    List<Optional<PostcodeLocation>> listLocations = new ArrayList<>();
    listLocations.add(optLocation);

    when(apiUtilsService.removeBlankSpaces(eq(postcode))).thenReturn(sanitisedPostcode);
    when(postcodeLocationRepo.findByPostcode(eq(sanitisedPostcode))).thenReturn(listLocations);

    PostcodeLocation returnedLocation = locationService.getLocationForPostcode(postcode);

    verify(apiUtilsService).removeBlankSpaces(eq(postcode));
    verify(postcodeLocationRepo).findByPostcode(eq(sanitisedPostcode));

    assertEquals(sanitisedPostcode, returnedLocation.getPostcode());
  }

  @Test
  public void getLocationForEmptyRtnFromRepo() {

    String postcode = "EX8 5SE";
    String sanitisedPostcode = "EX85SE";

    List<Optional<PostcodeLocation>> emptyListLocations = new ArrayList<>();

    when(apiUtilsService.removeBlankSpaces(eq(postcode))).thenReturn(sanitisedPostcode);
    when(postcodeLocationRepo.findByPostcode(eq(sanitisedPostcode))).thenReturn(emptyListLocations);

    PostcodeLocation returnedLocation = locationService.getLocationForPostcode(postcode);

    verify(apiUtilsService).removeBlankSpaces(eq(postcode));
    verify(postcodeLocationRepo).findByPostcode(eq(sanitisedPostcode));

    assertNull(returnedLocation);
  }

  @Test
  public void getLocationForNullRtnFromRepo() {

    String postcode = "EX8 5SE";
    String sanitisedPostcode = "EX85SE";

    PostcodeLocation location = new PostcodeLocation();
    location.setPostcode(sanitisedPostcode);
    Optional<PostcodeLocation> optLocation = Optional.empty();
    List<Optional<PostcodeLocation>> listLocations = new ArrayList<>();
    listLocations.add(optLocation);

    when(apiUtilsService.removeBlankSpaces(eq(postcode))).thenReturn(sanitisedPostcode);
    when(postcodeLocationRepo.findByPostcode(eq(sanitisedPostcode))).thenReturn(listLocations);

    PostcodeLocation returnedLocation = locationService.getLocationForPostcode(postcode);

    verify(apiUtilsService).removeBlankSpaces(eq(postcode));
    verify(postcodeLocationRepo).findByPostcode(eq(sanitisedPostcode));

    assertNull(returnedLocation);
  }

  @Test
  public void distanceWithNullSourceAndDestination() {

    Double distanceReturned = locationService.distanceBetween(null, null);

    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithNullSource() {

    PostcodeLocation destinationLocation = new PostcodeLocation();
    destinationLocation.setPostcode("EX26ER");
    destinationLocation.setEasting(12345);
    destinationLocation.setNorthing(12345);

    Double distanceReturned = locationService.distanceBetween(null, destinationLocation);

    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithNullDestination() {

    PostcodeLocation sourceLocation = new PostcodeLocation();
    sourceLocation.setPostcode("EX26ER");
    sourceLocation.setEasting(12345);
    sourceLocation.setNorthing(12345);

    Double distanceReturned = locationService.distanceBetween(sourceLocation, null);

    assertNull(distanceReturned);
  }

  @Test
  public void distanceWithSourceAndDestination() {

    PostcodeLocation sourceLocation = new PostcodeLocation();
    sourceLocation.setPostcode("EX26ER");
    sourceLocation.setEasting(12345);
    sourceLocation.setNorthing(12345);

    PostcodeLocation destinationLocation = new PostcodeLocation();
    destinationLocation.setPostcode("EX86PJ");
    destinationLocation.setEasting(14355);
    destinationLocation.setNorthing(12445);

    Double distanceReturned = locationService.distanceBetween(sourceLocation, destinationLocation);

    assertEquals(1.3, distanceReturned);
  }

  @Test
  public void testGetLocationsForPostcodes() {

    List<String> postCodes = new ArrayList<>();
    postCodes.add("EX78PR");
    postCodes.add("EX88PR");

    PostcodeLocation location = new PostcodeLocation();
    location.setPostcode("EX78PR");
    Optional<PostcodeLocation> optLocation = Optional.of(location);
    List<Optional<PostcodeLocation>> listLocations = new ArrayList<>();
    listLocations.add(optLocation);

    when(apiUtilsService.removeBlankSpacesIn(anyList())).thenReturn(postCodes);
    when(postcodeLocationRepo.findByPostcodeIn(anyList())).thenReturn(listLocations);

    List<PostcodeLocation> locationList = locationService.getLocationsForPostcodes(postCodes);

    verify(apiUtilsService).removeBlankSpacesIn(eq(postCodes));
    verify(postcodeLocationRepo).findByPostcodeIn(eq(postCodes));

    assertEquals("EX78PR", "EX78PR");
  }
}
