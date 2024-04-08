package uk.nhs.digital.uec.api.integration.authentication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.authentication.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;
import uk.nhs.digital.uec.api.authentication.util.JwtUtil;
import uk.nhs.digital.uec.api.filter.AccessTokenFilter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class EndpointAuthorisationIT {

  @LocalServerPort private int port;

  @Value("${local.host}")
  private String host;

  @Value("${local.uri}")
  private String uri;

  @Value("${local.welcome.uri}")
  private String welcomeUri;

  @Autowired private TestRestTemplate restTemplate;

  private String expiredToken;
  private String token;
  private String authorisationText;
  private String bearerText;

  @Mock private HttpServletRequest httpRequest;
  @Mock private JwtUtil jwtUtil;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;
  @Mock private HttpServletResponse httpResponse;
  @Mock private FilterChain filterChain;
  @InjectMocks private AccessTokenFilter filter = new AccessTokenFilter();

  @BeforeEach
  public void init() {
    expiredToken =
        "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYyNjc3NTgyMywic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYyNjc3OTQyMywiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.b1Q8Fc8lqiQuHO9tvjQW05MSOdvJ2hy33r-IO5VNUXP-zIwbA7yxl2WMvsoCxKn03CRoDhBQsJlaOhLCtsV_xwfKoLL3hKMZQ_CLQskuBrj4Xus9WmXyrqKyWAUoVAQ3O5NWtWuo8OYmEEcCLdFerv6lKRorIkb_U5ojB0xN1nkEZve0rAMXpDdhMpzNt6e3C_vrtasPAYuvx628gb9Bf9vixA-XMi4xFu2V5N9kKXFRA3w-iHDFUq3kOGtRLLpPRkOOoGo7bEeJGZ_JK2zc4uD-CIJVHIb8Ehf19c9NLk0fnuezWQkXxnbC-sjulJJqGUj_8uHrGM5w_xKWilatEg";
    token =
        "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJpZCIsImlhdCI6MTYyNjc3NTgyMywic3ViIjoiYWRtaW5AbmhzLm5ldCIsImlzcyI6Imlzc3VlciIsImV4cCI6MTYyNjc3OTQyMywiY29nbml0bzpncm91cHMiOlsiQVBJX1VTRVIiXX0.b1Q8Fc8lqiQuHO9tvjQW05MSOdvJ2hy33r-IO5VNUXP-zIwbA7yxl2WMvsoCxKn03CRoDhBQsJlaOhLCtsV_xwfKoLL3hKMZQ_CLQskuBrj4Xus9WmXyrqKyWAUoVAQ3O5NWtWuo8OYmEEcCLdFerv6lKRorIkb_U5ojB0xN1nkEZve0rAMXpDdhMpzNt6e3C_vrtasPAYuvx628gb9Bf9vixA-XMi4xFu2V5N9kKXFRA3w-iHDFUq3kOGtRLLpPRkOOoGo7bEeJGZ_JK2zc4uD-CIJVHIb8Ehf19c9NLk0fnuezWQkXxnbC-sjulJJqGUj_8uHrGM5w_xKWilatEg";
    authorisationText = "Authorization";
    bearerText = "Bearer ";
  }

  @Test
  public void authorisationFilterTest()
      throws ServletException, IOException, AccessTokenExpiredException {
    when(httpRequest.getHeader(authorisationText)).thenReturn(bearerText + token);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    filter.doFilterInternal(httpRequest, httpResponse, filterChain);
    verify(filterChain).doFilter(httpRequest, httpResponse);
  }

  @Test
  public void successfulAuthorisationForWelcomeApi()
      throws MalformedURLException, JsonMappingException, JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    Credential cred = new Credential("admin@nhs.net", "password");
    HttpEntity<Credential> request = new HttpEntity<>(cred);
    ResponseEntity<String> loginResponse =
        restTemplate.postForEntity(
            new URL(host + ":" + port + "/" + uri).toString(), request, String.class);
    AuthToken authToken = mapper.readValue(loginResponse.getBody(), AuthToken.class);

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add(authorisationText, bearerText + authToken.getAccessToken());
    ResponseEntity<String> response =
        restTemplate.exchange(
            host + ":" + port + welcomeUri,
            HttpMethod.GET,
            new HttpEntity<Object>(headers),
            String.class);
    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  public void unauthorisedAuthorisationLocalStubTest()
      throws MalformedURLException, JsonMappingException, JsonProcessingException {
    ResponseEntity<String> response =
        restTemplate.getForEntity(host + ":" + port + welcomeUri, String.class);
    assertEquals(401, response.getStatusCode().value());
  }

  @Test
  public void authorisationForWelcomeApiWithExpiredToken()
      throws MalformedURLException, JsonMappingException, JsonProcessingException {

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add(authorisationText, bearerText + expiredToken);
    ResponseEntity<String> response =
        restTemplate.exchange(
            host + ":" + port + welcomeUri,
            HttpMethod.GET,
            new HttpEntity<Object>(headers),
            String.class);
    assertEquals(401, response.getStatusCode().value());
  }
}
