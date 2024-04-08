package uk.nhs.digital.uec.api.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import uk.nhs.digital.uec.api.model.nhschoices.Contact;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.model.nhschoices.OpeningTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NHSChoicesSearchMapperToDosServicesMapperUtilTest {

  NHSChoicesSearchMapperToDosServicesMapperUtil classUnderTest = new NHSChoicesSearchMapperToDosServicesMapperUtil();

  @Test
  void getTelephoneContact() {
    List<Contact> contacts = Collections.singletonList(new Contact("Primary", "Office hours", "Telephone", "01516399877"));
    String result = classUnderTest.getTelephoneContact(contacts);
    assertEquals("01516399877", result);
  }

  @Test
  void getTelephoneContact_shouldReturnEmptyStringWhenNoTelephoneContact() {
    List<Contact> contacts = Collections.emptyList();
    String result = classUnderTest.getTelephoneContact(contacts);
    assertEquals("", result);
  }

  @Test
  public void getTelephoneContactMultipleContacts() {
    List<Contact> contacts = Arrays.asList(
      new Contact("Primary", "Office hours", "Telephone", "044516399877"),
      new Contact("Secondary", "After hours", "Telephone", "07071234567")
    );

    String result = classUnderTest.getTelephoneContact(contacts);

    assertEquals("044516399877", result);
  }

  @Test
  void getWebsite() {
    List<Contact> contacts = Collections.singletonList(new Contact("Primary", "Office hours", "Website", "https://wehgroup.co.uk"));
    String result = classUnderTest.getWebsite(contacts);
    assertEquals("https://wehgroup.co.uk", result);
  }

  @Test
  public void getWebsiteMultipleContacts() {
    List<Contact> contacts = Arrays.asList(
      new Contact("Primary", "Office hours", "Website", "https://dental-provider.co.uk"),
      new Contact("Secondary", "After hours", "Website", "https://example.com")
    );

    String result = classUnderTest.getWebsite(contacts);

    assertEquals("https://dental-provider.co.uk", result);
  }

  @Test
  void getWebsite_shouldReturnEmptyStringWhenNoWebsiteContact() {
    List<Contact> contacts = Collections.emptyList();
    String result = classUnderTest.getWebsite(contacts);
    assertEquals("", result);
  }

  @Test
  void getEmail() {
    List<Contact> contacts = Collections.singletonList(new Contact("Primary", "Office hours", "Email", "pharmacy.fpg71@nhs.net"));
    String result = classUnderTest.getEmail(contacts);
    assertEquals("pharmacy.fpg71@nhs.net", result);
  }

  @Test
  public void getEmailMultipleContacts() {
    List<Contact> contacts = Arrays.asList(
      new Contact("Primary", "Office hours", "Email", "pharmacy.fpg71@nhs.net"),
      new Contact("Secondary", "After hours", "Email", "info@example.com")
    );

    String result = classUnderTest.getEmail(contacts);

    assertEquals("pharmacy.fpg71@nhs.net", result);
  }

  @Test
  void getEmail_shouldReturnEmptyStringWhenNoEmailContact() {
    List<Contact> contacts = Collections.emptyList();
    String result = classUnderTest.getEmail(contacts);
    assertEquals("", result);
  }

  @Test
  void getOpeningTimeDays() {
    List<OpeningTime> openingTimes = new ArrayList<>();
    openingTimes.add(new OpeningTime("Monday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Tuesday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Wednesday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Thursday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Friday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Saturday", null, null, null, 0, 0, "General", "", false,null,null));
    openingTimes.add(new OpeningTime("Sunday", null, null, null, 0, 0, "General", "", false,null,null));

    String result = classUnderTest.getOpeningTimeDays(openingTimes);

    assertEquals("Monday, Tuesday, Wednesday, Thursday, Friday", result);
  }

  @Test
  void getOpeningTimeDays_shouldReturnEmptyStringWhenNoOpeningTimes() {
    List<OpeningTime> openingTimes = Collections.emptyList();
    String result = classUnderTest.getOpeningTimeDays(openingTimes);
    assertEquals("", result);
  }

  @Test
  void getOpeningTime() {
    List<OpeningTime> openingTimes = new ArrayList<>();
    openingTimes.add(new OpeningTime("Monday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Tuesday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Wednesday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Thursday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Friday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Saturday", null, null, null, 0, 0, "General", "", false,null,null));
    openingTimes.add(new OpeningTime("Sunday", null, null, null, 0, 0, "General", "", false,null,null));
    ;

    String result = classUnderTest.getOpeningTime(openingTimes);

    assertEquals("08:30, 08:30, 08:30, 08:30, 08:30", result);
  }

  @Test
  void getClosingTime() {
    List<OpeningTime> openingTimes = new ArrayList<>();
    openingTimes.add(new OpeningTime("Monday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Tuesday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Wednesday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Thursday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Friday", "08:30", "18:30", null, 510, 1110, "General", "", true,null,null));
    openingTimes.add(new OpeningTime("Saturday", null, null, null, 0, 0, "General", "", false,null,null));
    openingTimes.add(new OpeningTime("Sunday", null, null, null, 0, 0, "General", "", false,null,null));

    String result = classUnderTest.getClosingTime(openingTimes);

    assertEquals("18:30, 18:30, 18:30, 18:30, 18:30", result);
  }

  @Test
  public void getGeoLocation() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setLatitude(40.7128);
    dataModel.setLongitude(-74.0060);

    GeoPoint result = classUnderTest.getGeoLocation(dataModel);

    assertEquals(40.7128, result.getLat(), 0.0001);
    assertEquals(-74.0060, result.getLon(), 0.0001);
  }

  @Test
  public void concatenateAddress() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setAddress1("123 Main St");
    dataModel.setAddress2("Apt 456");
    dataModel.setCity("City");

    String result = classUnderTest.concatenateAddress(dataModel);

    assertEquals("123 Main St, Apt 456, City", result);
  }

  @Test
  public void returnAddressListWithoutNullValues() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setAddress1("123 Main St");
    dataModel.setAddress2(null);
    dataModel.setCity("City");

    String result = classUnderTest.concatenateAddress(dataModel);

    assertEquals("123 Main St, City", result);
  }

  @Test
  void returnAddressListWithoutNullValues_allAddressesNull() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();

    String result = classUnderTest.concatenateAddress(dataModel);

    assertEquals("", result);
  }

  @Test
  public void correctlyCalculateDistance() {
    double distance = classUnderTest.distanceCalculator(51.5074, 0.1278, 51.486108, -0.159814);
    assertEquals(12.4, distance, 0.1);
  }

  @Test
  public void assumeDistanceIs999IfAnythingHasNoLocation() {
    double distance = classUnderTest.distanceCalculator(null, null, 51.486108, -0.159814);
    assertEquals(999.9, distance, 0.1);
    distance = classUnderTest.distanceCalculator(51.5074, 0.1278, null, null);
    assertEquals(999.9, distance, 0.1);
  }

}
