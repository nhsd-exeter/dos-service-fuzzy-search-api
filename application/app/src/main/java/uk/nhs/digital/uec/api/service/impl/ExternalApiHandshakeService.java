package uk.nhs.digital.uec.api.service.impl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLException;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import software.amazon.awssdk.http.Header;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.model.PostcodeLocation;
import uk.nhs.digital.uec.api.service.ExternalApiHandshakeInterface;

@Service
@Slf4j
public class ExternalApiHandshakeService implements ExternalApiHandshakeInterface {

  @Value("${postcode.mapping.url}")
  private String psmUrl;

  @Value("${postcode.mapping.uri}")
  private String psmUri;

  @Value("${auth.login.url}")
  private String loginUrl;

  @Value("${auth.login.uri}")
  private String loginUri;

  @Value("${postcode.mapping.user}")
  private String postcodeMappingUser;

  @Value("${postcode.mapping.password}")
  private String postcodeMappingPassword;

  public List<PostcodeLocation> getPostcodeMappings(
      List<String> postCodes, MultiValueMap<String, String> headers) {
    List<PostcodeLocation> postcodeMappingLocationList = null;

    try {
      WebClient webClient = getWebClient(psmUrl);
      postcodeMappingLocationList =
          webClient
              .get()
              .uri(builder -> builder.path(psmUri).queryParam("postcodes", postCodes).build())
              .headers(httpHeaders -> httpHeaders.putAll(headers))
              .retrieve()
              .bodyToFlux(PostcodeLocation.class)
              .collectList()
              .block();
    } catch (SSLException e) {
      log.info("SSL Error while handshake between Postcode mapping location: " + e.getMessage());
    } catch (Exception e) {
      log.info(
          "Error while connecting Postcode mapping location service from Fuzzy search service: "
              + e.getMessage());
      return Collections.emptyList();
    }
    return postcodeMappingLocationList;
  }

  @Override
  public MultiValueMap<String, String> getAccessTokenHeader() {
    Credential credential =
        Credential.builder()
            .emailAddress(postcodeMappingUser)
            .password(postcodeMappingPassword)
            .build();

    MultiValueMap<String, String> authRequestBody = new LinkedMultiValueMap<>();
    authRequestBody.add("emailAddress", postcodeMappingUser);
    authRequestBody.add("password", postcodeMappingPassword);

    AuthToken authToken = null;
    try {
      WebClient webClient = getWebClient(loginUrl);
      authToken =
          webClient
              .post()
              .uri(builder -> builder.path(loginUri).build())
              .body(BodyInserters.fromValue(credential))
              .header(Header.CONTENT_TYPE, MediaType.APPLICATION_JSON)
              .retrieve()
              .bodyToMono(AuthToken.class)
              .block();

    } catch (SSLException e) {
      log.info(
          "SSL Error while handshake between Fuzzy and Authentication service: " + e.getMessage());
    } catch (Exception e) {
      log.info(
          "Error while connecting Authentication service from Fuzzy service: " + e.getMessage());
      return new LinkedMultiValueMap<>();
    }
    return authToken != null ? createAuthenticationHeader(authToken) : null;
  }

  private WebClient getWebClient(String baseUrl) throws SSLException {
    final SslContext context =
        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));
    return WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  private MultiValueMap<String, String> createAuthenticationHeader(AuthToken authToken) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authToken.getAccessToken());
    return headers;
  }
}
