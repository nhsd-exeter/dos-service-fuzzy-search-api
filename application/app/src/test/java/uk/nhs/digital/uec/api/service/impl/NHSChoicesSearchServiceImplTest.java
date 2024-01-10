package uk.nhs.digital.uec.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.util.NHSChoicesSearchMapperToDosServicesMapperUtil;
import uk.nhs.digital.uec.api.util.WebClientUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NHSChoicesSearchServiceImplTest {

  @Mock
  private WebClientUtil webClientUtil;

  @Mock
  private NHSChoicesSearchMapperToDosServicesMapperUtil servicesMapperUtil;

  @InjectMocks private NHSChoicesSearchServiceImpl service;


  @Test
  public void testRetrieveParsedNhsChoicesV2ModelWithEmptyResults() throws NotFoundException {
    // Mocking the web client to return an empty list
    when(webClientUtil.retrieveNHSChoicesServices(any(), any(), any()))
      .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

    CompletableFuture<List<DosService>> futureResult = service.retrieveParsedNhsChoicesV2Model("lat", "long", new ArrayList<>(), "postcode", 10);

    assertTrue(futureResult.join().isEmpty());
  }

  @Test
  public void testRetrieveParsedNhsChoicesV2ModelWithNonEmptyResults() throws NotFoundException {
    // Mocking a non-empty response from the web client
    NHSChoicesV2DataModel webclientResponse = new NHSChoicesV2DataModel();
    webclientResponse.setLatitude(0D);
    webclientResponse.setLongitude(0D);
    DosService dosService = mock(DosService.class);
    List<NHSChoicesV2DataModel> mockResponse = new ArrayList<>();
    mockResponse.add(webclientResponse);

    when(servicesMapperUtil.concatenateAddress(webclientResponse)).thenReturn("anyString()");
    when(webClientUtil.retrieveNHSChoicesServices(any(), any(), any()))
      .thenReturn(CompletableFuture.completedFuture(mockResponse));
    when(servicesMapperUtil.getGeoLocation(webclientResponse)).thenReturn(any());


    CompletableFuture<List<DosService>> futureResult = service.retrieveParsedNhsChoicesV2Model("lat", "long", new ArrayList<>(), "postcode", 10);

    // Assumptions: implement 'convertNHSChoicesToDosService' as needed in the test
    // Adjust the assertions as necessary based on the actual data and conversion logic
    assertEquals(1, futureResult.join().size());
  }

  @Test
  public void testRetrieveParsedNhsChoicesV2ModelWithException() throws NotFoundException {
    // Mocking the web client to throw an exception
    when(webClientUtil.retrieveNHSChoicesServices(any(), any(), any()))
      .thenReturn(CompletableFuture.supplyAsync(() -> {
        throw new RuntimeException("Error in web client");
      }));

    CompletableFuture<List<DosService>> futureResult = service.retrieveParsedNhsChoicesV2Model("lat", "long", new ArrayList<>(), "postcode", 10);

    assertTrue(futureResult.join().isEmpty());
  }

}
