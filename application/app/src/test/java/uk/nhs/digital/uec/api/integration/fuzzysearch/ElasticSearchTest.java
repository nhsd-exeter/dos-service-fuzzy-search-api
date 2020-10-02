package uk.nhs.digital.uec.api.integration.fuzzysearch;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;
import uk.nhs.digital.uec.api.util.PropertySourceResolver;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ElasticSearchTest {

  @Autowired private PropertySourceResolver propertySourceResolver;

  @Autowired private ServicesRepositoryInterface servicesRepository;

  private static String endpointUrl;

  TestRestTemplate restTemplate = new TestRestTemplate();
  HttpHeaders headers = new HttpHeaders();

  @BeforeEach
  public void configureProperties() {
    endpointUrl = propertySourceResolver.endpointUrl;
  }

  @Test
  public void postCallOnEndpoint() throws Exception {
    DosService dosService = new DosService();
    dosService.setId(1);
    dosService.setName("Service1");
    dosService.setUIdentifier(1);
    servicesRepository.save(dosService);

    HttpEntity<String> request = new HttpEntity<String>(null, headers);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(endpointUrl).queryParam("search_term", "Service1");
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String.class);

    assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);
  }
}
