package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class LoginControllerTest {

  @LocalServerPort private int port;

  @Value("${local.host}")
  private String host;

  @Value("${local.uri}")
  private String uri;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void authorisedLoginLocalStubTest() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Credential cred = new Credential("admin@nhs.net", "password");
    HttpEntity<Credential> request = new HttpEntity<>(cred);
    ResponseEntity<String> response =
        restTemplate.postForEntity(
            new URL(host + ":" + port + "/" + uri).toString(), request, String.class);
    AuthToken authToken = mapper.readValue(response.getBody(), AuthToken.class);
    assertNotNull(authToken.getAccessToken());
    assertNotNull(authToken.getRefreshToken());
  }

  @Test
  public void unauthorisedLoginLocalStubTest() throws Exception {
    Credential cred = new Credential("wrongUser@nhs.net", "wrongPassword");
    HttpEntity<Credential> request = new HttpEntity<>(cred);
    ResponseEntity<String> response =
        restTemplate.postForEntity(
            new URL(host + ":" + port + "/" + uri).toString(), request, String.class);
    assertEquals("401 - Unauthorised", response.getBody());
  }
}
