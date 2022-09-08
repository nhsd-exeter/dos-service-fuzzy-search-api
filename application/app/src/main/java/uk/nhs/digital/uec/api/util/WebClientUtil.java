package uk.nhs.digital.uec.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;

@Component
@Slf4j
public class WebClientUtil {

  @Autowired private WebClient authWebClient;
  @Autowired private WebClient postCodeMappingWebClient;

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
      log.info(
          "Error while connecting Authentication service from Fuzzy service: " + e.getMessage());
      return null;
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
      log.info(
          "Error while connecting Postcode mapping location service from Fuzzy search service: "
              + e.getMessage());
      return Collections.emptyList();
    } catch (Exception e) {
      log.error("Error from Postcode Mapping API: " + e.getCause());
    }
    postcodeMappingLocationList.forEach(postcodeLocation -> log.info("Found locations for {}",postcodeLocation.getPostCode()));
    return postcodeMappingLocationList;
  }

  private void handleWebClientResponseException(WebClientResponseException e)
      throws InvalidParameterException {
    HttpStatus statusCode = e.getStatusCode();
    String errorResponse = e.getResponseBodyAsString();
    if (statusCode.value() == 400) {
      log.error("Error from Postcode Mapping API: " + errorResponse);
      throw new InvalidParameterException(errorResponse);
    }
  }
}
