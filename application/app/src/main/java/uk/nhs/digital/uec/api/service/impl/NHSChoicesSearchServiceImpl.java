package uk.nhs.digital.uec.api.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.Contact;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.model.nhschoices.OpeningTime;
import uk.nhs.digital.uec.api.service.NHSChoicesSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class NHSChoicesSearchServiceImpl implements NHSChoicesSearchService {
  private static final String NHS_CHOICES = "NHS_CHOICES";
  private final Logger logger = LoggerFactory.getLogger(NHSChoicesSearchServiceImpl.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final WebClient nhsChoicesApiWebClient;


  @Autowired
  public NHSChoicesSearchServiceImpl(@Qualifier("nhsChoicesApiWebClient") WebClient nhsChoicesApiWebClient) {
    this.nhsChoicesApiWebClient = nhsChoicesApiWebClient;
  }

  @Override
  @Async("fuzzyTaskExecutor")
  public CompletableFuture<List<NHSChoicesV2DataModel>> retrieveParsedNhsChoicesV2Model(String searchLatitude, String searchLongitude, Double distanceRange, List<String> searchTerms, String searchPostcode) {
    return nhsChoicesApiWebClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/service-search/")
        .queryParam("api-version", "2")
        .queryParam("search", searchPostcode)
        .queryParam("latitude", searchLatitude)
        .queryParam("longitude", searchLongitude)
        .build())
      .retrieve()
      .onStatus(HttpStatus::isError, response -> {
        logger.error("HTTP error status code: {}", response.statusCode().value());
        return Mono.empty();
      })
      .bodyToMono(String.class)
      .doOnNext(responseBody -> logger.info("Response body: {}", responseBody))
      .map(this::parseNHSChoicesDataModel)
      .toFuture();
  }

  public List<NHSChoicesV2DataModel> parseNHSChoicesDataModel(String json) {
    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    List<NHSChoicesV2DataModel> nhsChoicesDataModels = new ArrayList<>();
    if (StringUtils.isBlank(json)) {
      return nhsChoicesDataModels;
    }

    try {
      Map<String, Object> dataMap = objectMapper.readValue(json, new TypeReference<>() {});
      List<Map<String, Object>> values = (List<Map<String, Object>>) dataMap.get("value");
      if (values != null && !values.isEmpty()) {
        for (Map<String, Object> value : values) {
          NHSChoicesV2DataModel nhsChoicesDataModel = objectMapper.convertValue(value, NHSChoicesV2DataModel.class);
          nhsChoicesDataModels.add(nhsChoicesDataModel);
        }
      } else {
        return nhsChoicesDataModels;
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return nhsChoicesDataModels;
  }

  @Override
  public DosService convertNHSChoicesToDosService(NHSChoicesV2DataModel nhsChoicesV2DataModel) {
    return DosService.builder()
      ._score(Double.valueOf(nhsChoicesV2DataModel.getSearchScore()).intValue())
      .name(nhsChoicesV2DataModel.getOrganisationName())
      .publicName(nhsChoicesV2DataModel.getOrganisationName())
      .odsCode(Objects.toString(nhsChoicesV2DataModel.getOdsCode(),""))
      .address(List.of(NHSChoicesSearchMapperToDosServicesMapperUtil.concatenateAddress(nhsChoicesV2DataModel)))
      .postcode(nhsChoicesV2DataModel.getPostcode())
      .publicPhoneNumber(NHSChoicesSearchMapperToDosServicesMapperUtil.getTelephoneContact(nhsChoicesV2DataModel.getContacts()))
      .email(NHSChoicesSearchMapperToDosServicesMapperUtil.getEmail(nhsChoicesV2DataModel.getContacts()))
      .web(NHSChoicesSearchMapperToDosServicesMapperUtil.getWebsite(nhsChoicesV2DataModel.getContacts()))
      .openingtimedays(NHSChoicesSearchMapperToDosServicesMapperUtil.getOpeningTimeDays(nhsChoicesV2DataModel.getOpeningTimes()))
      .openingtime(NHSChoicesSearchMapperToDosServicesMapperUtil.getOpeningTime(nhsChoicesV2DataModel.getOpeningTimes()))
      .closingtime(NHSChoicesSearchMapperToDosServicesMapperUtil.getClosingTime(nhsChoicesV2DataModel.getOpeningTimes()))
      .location(NHSChoicesSearchMapperToDosServicesMapperUtil.getGeoLocation(nhsChoicesV2DataModel))
      .datasource(NHS_CHOICES)
      .build();
  }

  public static class NHSChoicesSearchMapperToDosServicesMapperUtil {

    public static String getTelephoneContact(List<Contact> contacts) {
      for (Contact contact : contacts) {
        if ("Telephone".equalsIgnoreCase(contact.getContactMethodType())) {
          return contact.getContactValue();
        }
      }
      return "";
    }

    public static String getWebsite(List<Contact> contacts) {
      for (Contact contact : contacts) {
        if ("Website".equalsIgnoreCase(contact.getContactMethodType())) {
          return contact.getContactValue();
        }
      }
      return "";
    }

    public static String getEmail(List<Contact> contacts) {
      for (Contact contact : contacts) {
        if ("Email".equalsIgnoreCase(contact.getContactMethodType())) {
          return contact.getContactValue();
        }
      }
      return "";
    }

    public static String getOpeningTimeDays(List<OpeningTime> openingTimes) {
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

    public static String getOpeningTime(List<OpeningTime> openingTimes) {
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

    public static String getClosingTime(List<OpeningTime> openingTimes) {
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

    public static GeoPoint getGeoLocation(NHSChoicesV2DataModel nhsChoicesV2DataModel){
      return new GeoPoint(nhsChoicesV2DataModel.getLatitude(), nhsChoicesV2DataModel.getLongitude());
    }

    public static String concatenateAddress(NHSChoicesV2DataModel nhsChoicesV2DataModel) {
      List<String> addressList = returnAddressListWithoutNullValues(nhsChoicesV2DataModel);
      return String.join(", ", addressList);
    }

    private static List<String> returnAddressListWithoutNullValues(NHSChoicesV2DataModel nhsChoicesV2DataModel) {
      return Stream.of(
          nhsChoicesV2DataModel.getAddress1(),
          nhsChoicesV2DataModel.getAddress2(),
          nhsChoicesV2DataModel.getAddress3(),
          nhsChoicesV2DataModel.getCity())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    }

  }

}
