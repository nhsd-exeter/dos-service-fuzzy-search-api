package uk.nhs.digital.uec.api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;


import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AuthServiceTest {

  @LocalServerPort
  private int port;

  @Autowired
  public TestRestTemplate restTemplate;

  @Value("${local.host}")
  private String host;

  @Value("${local.uri}")
  private String uri;

  @Value("${local.welcome.uri}")
  private String welcomeUri;

  @Test
  public void authorisedHeaderTest() throws Exception {
    MultiValueMap<String, String> authorizedHeader = getAuthorizedHeader();
    assertNotNull(authorizedHeader);
  }

  protected MultiValueMap<String, String> getAuthorizedHeader() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Credential cred = new Credential("admin@nhs.net", "password");
    HttpEntity<Credential> request = new HttpEntity<>(cred);
    ResponseEntity<String> loginResponse =
      restTemplate.postForEntity(
        new URL(host + ":" + port + "/" + uri).toString(), request, String.class);
    AuthToken authToken = mapper.readValue(loginResponse.getBody(), AuthToken.class);

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + authToken.getAccessToken());
    return headers;
  }
}
