package uk.nhs.digital.uec.api.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;
import uk.nhs.digital.uec.api.service.NHSChoicesSearchService;
import uk.nhs.digital.uec.api.util.Constants;
import uk.nhs.digital.uec.api.util.NHSChoicesSearchMapperToDosServicesMapperUtil;
import uk.nhs.digital.uec.api.util.WebClientUtil;

@Service
@Slf4j(topic = "NHS_CHOICES_IMPL")
public class NHSChoicesSearchServiceImpl implements NHSChoicesSearchService {

  private static final String NHS_CHOICES_DATASOURCE = "NHS_CHOICES";
  private final WebClientUtil webClientUtil;
  private final NHSChoicesSearchMapperToDosServicesMapperUtil servicesMapperUtil;

  @Autowired
  public NHSChoicesSearchServiceImpl(
      WebClientUtil webClientUtil,
      NHSChoicesSearchMapperToDosServicesMapperUtil servicesMapperUtil) {
    this.webClientUtil = webClientUtil;
    this.servicesMapperUtil = servicesMapperUtil;
  }

  @Override
  public CompletableFuture<List<DosService>> retrieveParsedNhsChoicesV2Model(
      String searchLatitude,
      String searchLongitude,
      List<String> searchTerms,
      String searchPostcode,
      Integer maxNumServicesToReturn) {
    // Validate search terms
    log.info("Validating search terms");
    String terms = this.validateSearchTerms(searchTerms, searchPostcode);
    log.info("Max number of services to return {}", maxNumServicesToReturn / 2);

    return webClientUtil
        .retrieveNHSChoicesServices(searchLatitude, searchLongitude, terms)
        .thenApply(
            nhscs -> {
              List<DosService> dosServices;
              if (nhscs.isEmpty()) {
                log.info("No services found");
                dosServices = new ArrayList<>();
              } else {
                log.info("Converting NHS choices services for service finder");
                dosServices =
                    nhscs.stream()
                        .map(nhsChoicesV2DataModel -> convertNHSChoicesToDosService(
                          Double.parseDouble(searchLatitude),
                          Double.parseDouble(searchLongitude),
                          nhsChoicesV2DataModel))
                        .filter(dosService -> dosService.getDistance() <= Constants.DEFAULT_DISTANCE_RANGE)
                        .collect(Collectors.toList());
              }

              log.info(
                  "NHS Choices services search successful. Found {} NHS Services(s).",
                  dosServices.size());
              return dosServices;
            })
        .exceptionally(
            ex -> {
              log.error("Error in NHS Choices services search", ex);
              return Collections.emptyList();
            });
  }

  private String validateSearchTerms(List<String> searchTerms, String searchPostcode) {
    if (Objects.isNull(searchTerms)) {
      searchTerms = new ArrayList<>();
      try {
        searchTerms.add(URLDecoder.decode(searchPostcode, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    } else if (searchTerms.isEmpty()) {
      try {
        searchTerms.add(URLDecoder.decode(searchPostcode, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    StringBuilder stringBuilder = new StringBuilder();
    searchTerms.forEach(stringBuilder::append);
    return stringBuilder.toString();
  }

  private DosService convertNHSChoicesToDosService(double searchLatitude, double searchLongitude,NHSChoicesV2DataModel nhsChoicesV2DataModel) {
    return DosService.builder()
        ._score(servicesMapperUtil.getSearchScore(nhsChoicesV2DataModel.getSearchScore()))
        .name(nhsChoicesV2DataModel.getOrganisationName())
        .public_name(nhsChoicesV2DataModel.getOrganisationName())
        .ods_code(Objects.toString(nhsChoicesV2DataModel.getOdsCode(), ""))
        .address(Collections.singletonList(servicesMapperUtil.concatenateAddress(nhsChoicesV2DataModel)))
        .postcode(nhsChoicesV2DataModel.getPostcode())
        .distance(servicesMapperUtil.distanceCalculator(searchLatitude, searchLongitude,nhsChoicesV2DataModel.getLatitude(), nhsChoicesV2DataModel.getLongitude()))
        .public_phone_number(
            servicesMapperUtil.getTelephoneContact(nhsChoicesV2DataModel.getContacts()))
        .email(servicesMapperUtil.getEmail(nhsChoicesV2DataModel.getContacts()))
        .web(servicesMapperUtil.getWebsite(nhsChoicesV2DataModel.getContacts()))
        .openingtimedays(
            servicesMapperUtil.getOpeningTimeDays(nhsChoicesV2DataModel.getOpeningTimes()))
        .openingtime(servicesMapperUtil.getOpeningTime(nhsChoicesV2DataModel.getOpeningTimes()))
        .closingtime(servicesMapperUtil.getClosingTime(nhsChoicesV2DataModel.getOpeningTimes()))
        .location(servicesMapperUtil.getGeoLocation(nhsChoicesV2DataModel))
        .datasource(NHS_CHOICES_DATASOURCE)
        .build();
  }
}
