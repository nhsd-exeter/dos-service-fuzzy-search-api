package uk.nhs.digital.uec.api.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.exception.GoogleApiException;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.model.google.GeoLocationResponse;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;

@Component
@Slf4j
@Getter
@Setter
public class WebClientUtil {

  private WebClient authWebClient;
  private WebClient postCodeMappingWebClient;
  private WebClient googleApiWebClient;
  private WebClient nhsChoicesApiWebClient;
  private ObjectMapper objectMapper;

  @Autowired
  public WebClientUtil(
      @Qualifier("authWebClient") WebClient authWebClient,
      @Qualifier("postCodeMappingWebClient") WebClient postCodeMappingWebClient,
      @Qualifier("googleApiWebClient") WebClient googleApiWebClient,
      @Qualifier("nhsChoicesApiWebClient") WebClient nhsChoicesApiWebClient,
      @Qualifier("customerObjectMapper") ObjectMapper objectMapper) {
    this.authWebClient = authWebClient;
    this.postCodeMappingWebClient = postCodeMappingWebClient;
    this.googleApiWebClient = googleApiWebClient;
    this.nhsChoicesApiWebClient = nhsChoicesApiWebClient;
    this.objectMapper = objectMapper;
  }

  public CompletableFuture<List<NHSChoicesV2DataModel>> retrieveNHSChoicesServices(
      String searchLatitude, String searchLongitude, String searchTerms) {
    String filter =
        "OrganisationTypeId  eq 'OPT' or OrganisationTypeId  eq 'HOS' or OrganisationTypeId  eq"
            + " 'DEN' or OrganisationTypeId  eq 'CLI' or OrganisationTypeId  eq 'PHA' or"
            + " OrganisationTypeId  eq 'GPB'";
    return nhsChoicesApiWebClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/service-search")
                    .queryParam("api-version", "2")
                    .queryParam("search", searchTerms)
                    .queryParam("longitude", searchLongitude)
                    .queryParam("latitude", searchLatitude)
                    .queryParam("$filter", filter)
                    .build())
        .retrieve()
        .onStatus(
            HttpStatus::isError,
            response -> {
              log.error(
                  "HTTP error status code: {} {}",
                  response.statusCode().value(),
                  response.statusCode().getReasonPhrase());
              return Mono.empty();
            })
        .bodyToMono(String.class)
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
        .doOnNext(responseBody -> log.info("Response received"))
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
      log.error(
          "Error while connecting Postcode mapping location service from Fuzzy search service: "
              + e.getMessage());
      return Collections.emptyList();
    } catch (Exception e) {
      log.error("Error from Postcode Mapping API: " + e.getCause());
    }
    return postcodeMappingLocationList;
  }

  public GeoLocationResponse getGeoLocation(
      String address, String googleApikey, String googleApiUri) {
    String uri =
        String.format("%s?address=%s+UK&sensor=false&key=%s", googleApiUri, address, googleApikey);
    log.info("Geo location URI: {}", uri);

    return googleApiWebClient
        .get()
        .uri(uri)
        .retrieve()
        .bodyToMono(GeoLocationResponse.class)
        .onErrorResume(
            throwable -> {
              try {
                return handleException(throwable);
              } catch (InvalidParameterException e) {
                throw new GoogleApiException("Error handling Google API response", e);
              }
            })
        .block();
  }

  private Mono<GeoLocationResponse> handleException(Throwable throwable)
      throws InvalidParameterException {
    if (throwable instanceof WebClientResponseException webClientResponseException) {
      handleWebClientResponseException(webClientResponseException);
      log.error(
          "Error while connecting to Google API location service from Fuzzy search service: {}",
          webClientResponseException.getMessage());
    } else {
      log.error("Error from Google API: {}", throwable.getMessage());
    }

    // Return null in case of an error
    return Mono.justOrEmpty(null);
  }

  void handleWebClientResponseException(WebClientResponseException e)
      throws InvalidParameterException {
    HttpStatus statusCode = e.getStatusCode();
    String errorResponse = e.getResponseBodyAsString();
    log.error(
        "Error from Postcode Mapping API. Status Code: {}, Response: {}",
        statusCode,
        errorResponse);

    if (statusCode.value() == 400) {
      throw new InvalidParameterException(errorResponse);
    }
  }

  private List<NHSChoicesV2DataModel> parseNHSChoicesDataModel(String json) {
    log.info("parsing json from data model");
    List<NHSChoicesV2DataModel> nhsChoicesDataModels = new ArrayList<>();

    if (StringUtils.isBlank(json)) {
      log.info("Returning empty data model");
      return nhsChoicesDataModels;
    }

    try {
      Map<String, Object> dataMap = objectMapper.readValue(json, new TypeReference<>() {});
      List<Map<String, Object>> values = (List<Map<String, Object>>) dataMap.get("value");
      if (values != null && !values.isEmpty()) {
        for (Map<String, Object> value : values) {
          try {
            NHSChoicesV2DataModel nhsChoicesDataModel =
                objectMapper.convertValue(value, NHSChoicesV2DataModel.class);
            nhsChoicesDataModels.add(nhsChoicesDataModel);
          } catch (IllegalArgumentException e) {
            log.warn(
                "Skipping record due to unexpected property formats and null values: {}", value);
          }
        }
      } else {
        log.info("This search did not contain any NHS results");
      }
    } catch (JsonProcessingException e) {
      log.error("An unexpected error occurred whilst reading the response {}", e.getMessage());
    }

    return nhsChoicesDataModels;
  }

  @PostConstruct
  public void fortifyObjectMapper() {
    this.objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
