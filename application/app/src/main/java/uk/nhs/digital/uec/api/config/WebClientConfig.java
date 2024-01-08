package uk.nhs.digital.uec.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {

  @Value("${nhs.choices.apikey}")
  private String SUBSCRIPTION_KEY;
  @Value("${auth.login.url}")
  private String loginUrl;

  @Value("${postcode.mapping.url}")
  private String psmUrl;

  @Value("${google.api.url}")
  private String googleApiUrl;

  @Value("${nhs.choices.url}")
  private String nhsChoicesUrl;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  @Bean
  public WebClient authWebClient() {
    return WebClient.builder()
      .baseUrl(loginUrl)
      .clientConnector(new ReactorClientHttpConnector(getSecureHttpClient()))
      .build();
  }

  @Bean
  public WebClient postCodeMappingWebClient() {
    return WebClient.builder()
      .baseUrl(psmUrl)
      .clientConnector(new ReactorClientHttpConnector(getSecureHttpClient()))
      .build();
  }

  @Bean
  public WebClient googleApiWebClient() {
    return WebClient.builder()
      .baseUrl(googleApiUrl)
      .clientConnector(new ReactorClientHttpConnector(getSecureHttpClient()))
      .build();
  }

  @Bean
  public WebClient nhsChoicesApiWebClient() {
    return WebClient.builder()
      .codecs(configure -> configure
        .defaultCodecs()
        .maxInMemorySize(16 * 1024 * 1024))
      .defaultHeader("subscription-key", SUBSCRIPTION_KEY)
      .defaultHeader("Accept-Charset", "application/json")
      .defaultHeader("Content-Type", "application/json")
      .baseUrl(nhsChoicesUrl)
      .clientConnector(new ReactorClientHttpConnector(getSecureHttpClient()))
      .build();
  }

  private HttpClient getSecureHttpClient() {
    SslContext context;
    try {
      context =
        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    } catch (SSLException e) {
      log.info("SSL Error while handshake between Fuzzy and external service: " + e.getMessage());
      return HttpClient.create().wiretap(true);
    }
    return HttpClient.create()
      .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Set the connection timeout
      .secure(t -> t.sslContext(context));
  }
}
