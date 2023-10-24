package uk.nhs.digital.uec.api.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import uk.nhs.digital.uec.api.model.nhschoices.Contact;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.model.nhschoices.OpeningTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NHSChoicesSearchMapperToDosServicesMapperUtilTest {

  @Test
  void getTelephoneContact_shouldReturnEmptyStringWhenNoTelephoneContact() {
    List<Contact> contacts = Collections.emptyList();
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getTelephoneContact(contacts);
    assertEquals("", result);
  }

  @Test
  void getTelephoneContact_shouldReturnTelephoneContactValue() {
    List<Contact> contacts = Collections.singletonList(new Contact("Primary", "Office hours", "Telephone", "01516399877"));
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getTelephoneContact(contacts);
    assertEquals("01516399877", result);
  }

  @Test
  void getWebsite_shouldReturnEmptyStringWhenNoWebsiteContact() {
    List<Contact> contacts = Collections.emptyList();
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getWebsite(contacts);
    assertEquals("", result);
  }

  @Test
  void getWebsite_shouldReturnWebsiteContactValue() {
    List<Contact> contacts = Collections.singletonList(new Contact("Primary", "Office hours", "Website", "https://wehgroup.co.uk"));
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getWebsite(contacts);
    assertEquals("https://wehgroup.co.uk", result);
  }

  @Test
  void getEmail_shouldReturnEmptyStringWhenNoEmailContact() {
    List<Contact> contacts = Collections.emptyList();
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getEmail(contacts);
    assertEquals("", result);
  }

  @Test
  void getEmail_shouldReturnEmailContactValue() {
    List<Contact> contacts = Collections.singletonList(new Contact("Primary", "Office hours", "Email", "pharmacy.fpg71@nhs.net"));
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getEmail(contacts);
    assertEquals("pharmacy.fpg71@nhs.net", result);
  }

  @Test
  void getOpeningTimeDays_shouldReturnEmptyStringWhenNoOpeningTimes() {
    List<OpeningTime> openingTimes = Collections.emptyList();
    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getOpeningTimeDays(openingTimes);
    assertEquals("", result);
  }

  @Test
  void getOpeningTimeDays() {
    List<OpeningTime> openingTimes = new ArrayList<>();
    openingTimes.add(new OpeningTime("Monday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Tuesday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Wednesday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Thursday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Friday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Saturday", null, null, null, 0, 0, "General", "", false));
    openingTimes.add(new OpeningTime("Sunday", null, null, null, 0, 0, "General", "", false));

    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getOpeningTimeDays(openingTimes);

    assertEquals("Monday, Tuesday, Wednesday, Thursday, Friday", result);
  }

  @Test
  void getOpeningTime() {
    List<OpeningTime> openingTimes = new ArrayList<>();
    openingTimes.add(new OpeningTime("Monday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Tuesday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Wednesday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Thursday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Friday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Saturday", null, null, null, 0, 0, "General", "", false));
    openingTimes.add(new OpeningTime("Sunday", null, null, null, 0, 0, "General", "", false));

    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getOpeningTime(openingTimes);

    assertEquals("08:30, 08:30, 08:30, 08:30, 08:30", result);
  }

  @Test
  void getClosingTime() {
    List<OpeningTime> openingTimes = new ArrayList<>();
    openingTimes.add(new OpeningTime("Monday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Tuesday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Wednesday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Thursday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Friday", "08:30", "18:30", null, 510, 1110, "General", "", true));
    openingTimes.add(new OpeningTime("Saturday", null, null, null, 0, 0, "General", "", false));
    openingTimes.add(new OpeningTime("Sunday", null, null, null, 0, 0, "General", "", false));

    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getClosingTime(openingTimes);

    assertEquals("18:30, 18:30, 18:30, 18:30, 18:30", result);
  }

  @Test
  public void testGetGeoLocation() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setLatitude(40.7128);
    dataModel.setLongitude(-74.0060);

    GeoPoint result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.getGeoLocation(dataModel);

    assertEquals(40.7128, result.getLat(), 0.0001);
    assertEquals(-74.0060, result.getLon(), 0.0001);
  }

  @Test
  public void testConcatenateAddress() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setAddress1("123 Main St");
    dataModel.setAddress2("Apt 456");
    dataModel.setCity("City");

    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.concatenateAddress(dataModel);

    assertEquals("123 Main St, Apt 456, City", result);
  }

  @Test
  public void testReturnAddressListWithoutNullValues() {
    NHSChoicesV2DataModel dataModel = new NHSChoicesV2DataModel();
    dataModel.setAddress1("123 Main St");
    dataModel.setAddress2(null);
    dataModel.setCity("City");

    String result = NHSChoicesSearchServiceImpl.NHSChoicesSearchMapperToDosServicesMapperUtil.concatenateAddress(dataModel);

    assertEquals("123 Main St, City", result);
  }

}
