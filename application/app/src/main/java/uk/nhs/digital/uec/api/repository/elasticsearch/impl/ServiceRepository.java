package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
@Slf4j
public class ServiceRepository implements CustomServicesRepositoryInterface {

  private static final String PROFESSIONAL_REFERRAL_FILTER = "Professional Referral";
  private static final String POSTCODE_REGEX =
    "([Gg][Ii][Rr]"
      + " 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";

  @Autowired
  private ServicesRepositoryInterface servicesRepo;
  @Autowired
  private ApiRequestParams apiRequestParams;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<DosService> findServiceBySearchTerms(List<String> searchTerms) {
    List<DosService> dosServices = new ArrayList<>();

    log.info("Request Params: {}", searchTerms);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info(
      "Number of services to get from elasticsearch: "
        + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    // Flattern search term list
    String searchCriteria = String.join(" ", searchTerms);

    // Adjust number of results to return from ES depending on how many words are in the search
    int numOfSpaces = StringUtils.countMatches(searchCriteria, " ");

    int numServicesToReturnFromEs = apiRequestParams.getMaxNumServicesToReturnFromElasticsearch();
    if (numOfSpaces == 2) {
      numServicesToReturnFromEs =
        apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms();
    }
    if (numOfSpaces > 2) {
      numServicesToReturnFromEs =
        Math.floorDiv(
          apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms(), 2);
    }

    dosServices = performSearch(searchCriteria, numServicesToReturnFromEs);

    return dosServices;
  }

  @Override
  public List<DosService> findServiceByLocation(String searchLocation) throws NotFoundException {
    List<DosService> dosServices = new ArrayList<>();

    log.info("Request Params: {}", searchLocation);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info(
      "Number of services to get from elasticsearch: "
        + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    //TODO:- Validate postcode entry
    log.info("Validate Postcode: {}", searchLocation);
    Pattern pattern = Pattern.compile(POSTCODE_REGEX);
    if (!pattern.matcher(searchLocation).matches()) {
      throw new NotFoundException(ErrorMessageEnum.INVALID_LOCATION.getMessage());
    }

    // Get the first part of the postcode
    String searchCriteria = searchLocation.substring(0, 4).trim();

    dosServices = performSearch(searchCriteria, null);

    return dosServices;
  }

  private List<DosService> performSearch(String searchCriteria, Integer numberOfServicesToReturnFromElasticSearch) {
    if (numberOfServicesToReturnFromElasticSearch == null) {
      return performSearch(searchCriteria);
    }
    final List<DosService> dosServices = new ArrayList<>();

    Long start = System.currentTimeMillis();
    Iterable<DosService> services =
      servicesRepo.findBySearchTerms(
        searchCriteria,
        apiRequestParams.getFuzzLevel(),
        apiRequestParams.getNamePriority(),
        apiRequestParams.getAddressPriority(),
        apiRequestParams.getPostcodePriority(),
        apiRequestParams.getPublicNamePriority(),
        PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));
    log.info("Search query duration {}ms", System.currentTimeMillis() - start);

    for (DosService serviceIterationItem : services) {
      if (serviceIterationItem.getReferral_roles().contains(PROFESSIONAL_REFERRAL_FILTER)) {
        dosServices.add(serviceIterationItem);
      }
    }

    return dosServices;
  }

  private List<DosService> performSearch(String searchCriteria) {
    final List<DosService> dosServices = new ArrayList<>();

    Long start = System.currentTimeMillis();
    Iterable<DosService> services =
      servicesRepo.findBySearchTerms(
        searchCriteria,
        apiRequestParams.getFuzzLevel(),
        apiRequestParams.getNamePriority(),
        apiRequestParams.getAddressPriority(),
        apiRequestParams.getPostcodePriority(),
        apiRequestParams.getPublicNamePriority(),
        PageRequest.of(0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()));
    log.info("Search query duration {}ms", System.currentTimeMillis() - start);

    for (DosService serviceIterationItem : services) {
      if (Objects.nonNull(serviceIterationItem.getReferral_roles())
        && !serviceIterationItem.getReferral_roles().isEmpty()){
        if(serviceIterationItem.getReferral_roles().contains(PROFESSIONAL_REFERRAL_FILTER)){
          dosServices.add(serviceIterationItem);
        }
      }
    }

    return dosServices;
  }
}
