package uk.nhs.digital.uec.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponse;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class WebClientUtil {
  @Autowired
  private WebClient authWebClient;
  @Autowired
  private WebClient postCodeMappingWebClient;
  @Autowired
  private WebClient googleApiWebClient;
  @Autowired
  private WebClient nhsChoicesApiWebClient;
  @Autowired
  private ObjectMapper objectMapper;

  @Async("fuzzyTaskExecutor")
  public CompletableFuture<List<NHSChoicesV2DataModel>> retrieveNHSChoicesServices(String searchLatitude, String searchLongitude, String searchTerms) {
    return nhsChoicesApiWebClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/service-search")
        .queryParam("api-version", "2")
        .queryParam("search", searchTerms)
        .queryParam("longitude", searchLongitude)
        .queryParam("latitude", searchLatitude)
        .build())
      .retrieve()
      .onStatus(HttpStatus::isError, response -> {
        log.error("HTTP error status code: {}", response.statusCode().value());
        return Mono.empty();
      })
      .bodyToMono(String.class)
      .doOnNext((responseBody) -> {
        log.info("Response received");
      })
      .map(this::parseNHSChoicesDataModel)
      .toFuture();
  }

  public AuthToken getAuthenticationToken(Credential credential, String loginUri) {
    AuthToken authToken = null;
    try {
      authToken =
        authWebClient
          .post()
          .uri(builder -> builder.path(loginUri).build())
          .body(BodyInserters.fromValue(credential))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .retrieve()
          .bodyToMono(AuthToken.class)
          .block();
    } catch (Exception e) {
      log.error(
        "Error while connecting Authentication service from Fuzzy service: " + e.getMessage());
    }
    return authToken;
  }

  public List<PostcodeLocation> getPostcodeMappings(
    List<String> postCodes, MultiValueMap<String, String> headers, String psmUri)
    throws InvalidParameterException {
    List<PostcodeLocation> postcodeMappingLocationList = new ArrayList<>();
    try {
      postcodeMappingLocationList =
        postCodeMappingWebClient
          .get()
          .uri(builder -> builder.path(psmUri).queryParam("postcodes", postCodes).build())
          .headers(httpHeaders -> httpHeaders.putAll(headers))
          .retrieve()
          .bodyToFlux(PostcodeLocation.class)
          .collectList()
          .block();
    } catch (WebClientResponseException e) {
      handleWebClientResponseException(e);
      log.error("Error while connecting Postcode mapping location service from Fuzzy search service: " + e.getMessage());
      return Collections.emptyList();
    } catch (Exception e) {
      log.error("Error from Postcode Mapping API: " + e.getCause());
    }
    return postcodeMappingLocationList;
  }

  public GeoLocationResponse getGeoLocation(String address, String googleApikey, String googleApiUri)
    throws InvalidParameterException {
    GeoLocationResponse geoLocationResponse = null;
    String uri = String.format("%s?address=%s+UK&sensor=false&key=%s", googleApiUri, address, googleApikey);
    log.info(uri);
    try {
      geoLocationResponse = googleApiWebClient
        .get()
        .uri(uri)
        .retrieve()
        .bodyToMono(GeoLocationResponse.class)
        .block();

    } catch (WebClientResponseException e) {
      handleWebClientResponseException(e);
      log.error(
        "Error while connecting google api location service from Fuzzy search service: "
          + e.getMessage());
    } catch (Exception e) {
      log.error("Error from google Api: " + e);
    }
    return geoLocationResponse;
  }

  private void handleWebClientResponseException(WebClientResponseException e)
    throws InvalidParameterException {
    HttpStatus statusCode = e.getStatusCode();
    String errorResponse = e.getResponseBodyAsString();
    log.error("Error from Postcode Mapping API. Status Code: {}, Response: {}", statusCode, errorResponse);

    if (statusCode.value() == 400) {
      throw new InvalidParameterException(errorResponse);
    }
  }

  private List<NHSChoicesV2DataModel> parseNHSChoicesDataModel(String json) {
    List<NHSChoicesV2DataModel> nhsChoicesDataModels = new ArrayList<>();
    if (StringUtils.isBlank(json)) {
      return nhsChoicesDataModels;
    }
    try {
      Map<String, Object> dataMap = objectMapper.readValue(json, new TypeReference<>() {
      });
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
      log.error("An unexpected error occurred whilst reading the response {}", e.getMessage());
      return nhsChoicesDataModels;
    }
    return nhsChoicesDataModels;
  }

}
