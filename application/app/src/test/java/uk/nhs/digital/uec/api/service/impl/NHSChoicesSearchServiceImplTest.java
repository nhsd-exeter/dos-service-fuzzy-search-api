package uk.nhs.digital.uec.api.service.impl;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

class NHSChoicesSearchServiceImplTest {
  private static final Logger logger = LoggerFactory.getLogger(NHSChoicesSearchServiceImplTest.class);
  @Mock
  private WebClient nhsChoicesApiWebClient;
  @InjectMocks
  private NHSChoicesSearchServiceImpl nhsChoicesSearchService;
  private MockWebServer mockWebServer;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    try {
      mockWebServer = new MockWebServer();
      mockWebServer.start();

      nhsChoicesApiWebClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .exchangeStrategies(ExchangeStrategies.builder()
          .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
          .build())
        .build();

      Field field = NHSChoicesSearchServiceImpl.class.getDeclaredField("nhsChoicesApiWebClient");
      field.setAccessible(true);
      field.set(nhsChoicesSearchService, nhsChoicesApiWebClient);
    } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to start MockWebServer", e);
    }
  }

  @AfterEach
  public void tearDown() {
    try {
      if (mockWebServer != null) {
        mockWebServer.shutdown();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to shutdown MockWebServer", e);
    }
  }

  @Test
  @DisplayName("assert that when retrieveParsedNhsChoicesV2Model method is invoked, it can return json response from NhsChoices api as a list of NHSChoicesV2DataModel data class")
  void retrieveParsedNhsChoicesV2Model() throws IOException, NotFoundException, ExecutionException, InterruptedException {
    String jsonPayload = readJsonNHSChoicesApi2("nhsChoicesAPI2jsonFile.json");
    // Enqueue a mock response for the expected API call
    String expectedSearchLatitude = "53.4817";
    String expectedSearchLongitude = "2.2346";
    Double expectedDistanceRange = 1.0;
    List<String> expectedSearchTerms = List.of("term1", "term2");
    String expectedSearchPostcode = "M11EZ";

    mockWebServer.enqueue(new MockResponse()
      .setBody(jsonPayload).addHeader("Content-Type","application/json")
      .setResponseCode(200));

    List<NHSChoicesV2DataModel> response = nhsChoicesSearchService.retrieveParsedNhsChoicesV2Model(
      expectedSearchLatitude, expectedSearchLongitude, expectedDistanceRange,
      expectedSearchTerms, expectedSearchPostcode
    ).get();

    var recordedRequest = mockWebServer.takeRequest();

    assertEquals("/service-search/?api-version=2&search=" + expectedSearchPostcode +
      "&latitude=" + expectedSearchLatitude + "&longitude=" + expectedSearchLongitude, recordedRequest.getPath());
    assertNotNull(response);
    assertThat(response, hasSize(50));

  }

  @Test
  @DisplayName("assert that when retrieveParsedNhsChoicesV2Model method is invoked, it can return empty list, if NhsChoices api response returns no result")
  void retrieveParsedNhsChoicesV2Model_return_empty() throws IOException, NotFoundException, ExecutionException, InterruptedException {
    String jsonPayload = readJsonNHSChoicesApi2("nhsChoicesAPI2jsonFile_with_empty_value.json");
    // Enqueue a mock response for the expected API call
    String expectedSearchLatitude = "testLatitude";
    String expectedSearchLongitude = "testLongitude";
    Double expectedDistanceRange = 1.0;
    List<String> expectedSearchTerms = List.of("term1", "term2");
    String expectedSearchPostcode = "testPostcode";

    mockWebServer.enqueue(new MockResponse()
      .setBody(jsonPayload).addHeader("Content-Type","application/json")
      .setResponseCode(200));

    List<NHSChoicesV2DataModel> response = nhsChoicesSearchService.retrieveParsedNhsChoicesV2Model(
      expectedSearchLatitude, expectedSearchLongitude, expectedDistanceRange,
      expectedSearchTerms, expectedSearchPostcode
    ).get();

    var recordedRequest = mockWebServer.takeRequest();

    assertEquals("/service-search/?api-version=2&search=" + expectedSearchPostcode +
      "&latitude=" + expectedSearchLatitude + "&longitude=" + expectedSearchLongitude, recordedRequest.getPath());
    assertNotNull(response);
    assertTrue(response.isEmpty());

  }

  @Test
  @DisplayName("assert that parseNHSChoicesDataModel method can parse json response from NhsChoices api to NHSChoicesV2DataModel data class")
  void parseNHSChoicesDataModel() throws IOException {
    String json =  readJsonNHSChoicesApi2("nhsChoicesAPI2jsonFile.json");
    List<NHSChoicesV2DataModel> nhsChoicesV2DataModelList = nhsChoicesSearchService.parseNHSChoicesDataModel(json);

    assertThat(nhsChoicesV2DataModelList, not(IsEmptyCollection.empty()));
    assertThat(nhsChoicesV2DataModelList, hasSize(50));
  }

  @Test
  @DisplayName("assert that convertNHSChoicesToDosService method can parse NHSChoicesV2DataModel data class to DosService data class")
  void convertNHSChoicesToDosService() throws IOException {
    String json =  readJsonNHSChoicesApi2("nhsChoicesAPI2jsonFile.json");
    List<NHSChoicesV2DataModel> nhsChoicesV2DataModelList = nhsChoicesSearchService.parseNHSChoicesDataModel(json);
    logger.info(String.valueOf(nhsChoicesV2DataModelList.get(0)));

    DosService dosService = nhsChoicesSearchService.convertNHSChoicesToDosService(nhsChoicesV2DataModelList.get(0));
    assertNotNull(dosService);
    assertNotEquals(0, dosService.get_score());
    assertNotNull(dosService.getPostcode());
    assertNotNull(dosService.getName());
  }

  private static String readJsonNHSChoicesApi2(String fileName) throws IOException {
    File jsonFile = ResourceUtils.getFile("classpath:json/".concat(fileName));
    System.out.println(new String(FileCopyUtils.copyToByteArray(jsonFile), StandardCharsets.UTF_8));
    return new String(FileCopyUtils.copyToByteArray(jsonFile), StandardCharsets.UTF_8);
  }
}
