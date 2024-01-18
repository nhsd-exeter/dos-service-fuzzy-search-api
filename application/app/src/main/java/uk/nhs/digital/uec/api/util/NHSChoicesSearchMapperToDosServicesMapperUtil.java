package uk.nhs.digital.uec.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.model.nhschoices.Contact;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.model.nhschoices.OpeningTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class NHSChoicesSearchMapperToDosServicesMapperUtil {

  public int getSearchScore(double searchScore){
    try {
        return Math.toIntExact((long) searchScore);
    }catch (ArithmeticException exception){
      log.error("Error parsing searchSource: {}", exception);
    }
    return 0;
  }

  public String getTelephoneContact(List<Contact> contacts) {
    for (Contact contact : contacts) {
      if ("Telephone".equalsIgnoreCase(contact.getContactMethodType())) {
        return contact.getContactValue();
      }
    }
    return "";
  }

  public String getWebsite(List<Contact> contacts) {
    for (Contact contact : contacts) {
      if ("Website".equalsIgnoreCase(contact.getContactMethodType())) {
        return contact.getContactValue();
      }
    }
    return "";
  }

  public String getEmail(List<Contact> contacts) {
    for (Contact contact : contacts) {
      if ("Email".equalsIgnoreCase(contact.getContactMethodType())) {
        return contact.getContactValue();
      }
    }
    return "";
  }

  public String getOpeningTimeDays(List<OpeningTime> openingTimes) {
    if (openingTimes == null || openingTimes.isEmpty()) {
      return "";
    }

    List<String> days = new ArrayList<>();
    for (OpeningTime openingTime : openingTimes) {
      if (openingTime != null && openingTime.isOpen()) {
        String weekday = openingTime.getWeekday();
        if (weekday != null) {
          days.add(weekday);
        }
      }
    }
    return String.join(", ", days);
  }

  public String getOpeningTime(List<OpeningTime> openingTimes) {
    if (openingTimes == null || openingTimes.isEmpty()) {
      return "";
    }

    List<String> openingTimeList = new ArrayList<>();
    for (OpeningTime openingTime : openingTimes) {
      if (openingTime != null && openingTime.isOpen()) {
        String openTime = openingTime.getOpeningTime();
        if (openTime != null) {
          openingTimeList.add(openTime);
        }
      }
    }
    return String.join(", ", openingTimeList);
  }

  public String getClosingTime(List<OpeningTime> openingTimes) {
    if (openingTimes == null || openingTimes.isEmpty()) {
      return "";
    }

    List<String> closingTimeList = new ArrayList<>();
    for (OpeningTime openingTime : openingTimes) {
      if (openingTime != null && openingTime.isOpen()) {
        String closingTime = openingTime.getClosingTime();
        if (closingTime != null) {
          closingTimeList.add(closingTime);
        }
      }
    }
    return String.join(", ", closingTimeList);
  }

  public GeoPoint getGeoLocation(NHSChoicesV2DataModel nhsChoicesV2DataModel) {
    return new GeoPoint(nhsChoicesV2DataModel.getLatitude(), nhsChoicesV2DataModel.getLongitude());
  }

  public String concatenateAddress(NHSChoicesV2DataModel nhsChoicesV2DataModel) {
    List<String> addressList = returnAddressListWithoutNullValues(nhsChoicesV2DataModel);
    return String.join(", ", addressList);
  }

  private List<String> returnAddressListWithoutNullValues(NHSChoicesV2DataModel nhsChoicesV2DataModel) {
    return Stream.of(
        nhsChoicesV2DataModel.getAddress1(),
        nhsChoicesV2DataModel.getAddress2(),
        nhsChoicesV2DataModel.getAddress3(),
        nhsChoicesV2DataModel.getCity())
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

}
