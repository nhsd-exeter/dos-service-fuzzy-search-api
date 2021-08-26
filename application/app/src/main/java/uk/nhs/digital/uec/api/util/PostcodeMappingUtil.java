package uk.nhs.digital.uec.api.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import uk.nhs.digital.uec.api.model.PostcodeLocation;

@Component
@Slf4j
public class PostcodeMappingUtil {

  @Value("${postcode.mapping.url}")
  private String psmUrl;

  @Value("${postcode.mapping.uri}")
  private String psmUri;

  public List<PostcodeLocation> getPostcodeMappings(List<String> postCodes) {
    List<PostcodeLocation> postcodeMappingLocationList = null;

    try {
      WebClient webClient = getWebClient(psmUrl);
      postcodeMappingLocationList =
          webClient
              .get()
              .uri(builder -> builder.path(psmUri).queryParam("postcodes", postCodes).build())
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

  private WebClient getWebClient(String baseUrl) throws SSLException {
    final SslContext context =
        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));
    return WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
