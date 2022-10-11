package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;

@Repository
@Slf4j
public class ServiceRepository implements CustomServicesRepositoryInterface {

  private static final String POSTCODE_REGEX =
      "([Gg][Ii][Rr]"
          + " 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";
  private static final String PROFESSIONAL_REFERRAL_FILTER = "Professional Referral";

  @Autowired private ServicesRepositoryInterface servicesRepo;

  @Autowired private ApiRequestParams apiRequestParams;

  /** {@inheritDoc} */
  @Override
  public List<DosService> findServiceBySearchTerms(List<String> searchTerms) {
    log.info("Request Params: {}", searchTerms);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info(
        "Number of services to get from elasticsearch: "
            + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    // Flattern search term list
    String searchCriteria = String.join(" ", searchTerms);

    // Adjust number of results to return from ES depending on how many words are in
    // the search
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

    return performSearch(searchCriteria, numServicesToReturnFromEs);
  }

  @Override
  public List<DosService> findServicesByGeoLocation(
      List<String> searchTerms, String searchLatitude, String searchLongitude, Double distanceRange)
      throws NotFoundException {
    log.info("Request Params: Lat: {}, Lng: {}", searchLatitude, searchLongitude);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info(
        "Number of services to get from elasticsearch: "
            + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    log.info("Validate geo location points: {} {}", searchLatitude, searchLongitude);
    if (!NumberUtils.isCreatable(searchLatitude) || (!NumberUtils.isCreatable(searchLongitude))) {
      throw new NotFoundException(ErrorMessageEnum.INVALID_LOCATION.getMessage());
    }
    String searchCriteria = String.join(" ", searchTerms);

    // Adjust number of results to return from ES depending on how many words are in
    // the search
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
    return performSearch(
        searchCriteria, searchLatitude, searchLongitude, distanceRange, numServicesToReturnFromEs);
  }

  @Override
  public List<DosService> findAllServicesByGeoLocation(
      String searchLatitude, String searchLongitude, Double distanceRange)
      throws NotFoundException {
    log.info("Request Params: Lat: {}, Lng: {}", searchLatitude, searchLongitude);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info(
        "Number of services to get from elasticsearch: "
            + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    log.info("Validate geo location points: {} {}", searchLatitude, searchLongitude);
    if (!NumberUtils.isCreatable(searchLatitude) || (!NumberUtils.isCreatable(searchLongitude))) {
      throw new NotFoundException(ErrorMessageEnum.INVALID_LOCATION.getMessage());
    }
    Integer numberOfServicesToReturnFromElasticSearch = null;
    return performSearch(
        searchLatitude, searchLongitude, distanceRange, numberOfServicesToReturnFromElasticSearch);
  }

  private List<DosService> performSearch(
      String searchCriteria, Integer numberOfServicesToReturnFromElasticSearch) {
    Long start = System.currentTimeMillis();
    if (numberOfServicesToReturnFromElasticSearch == null) {
      return performSearch(searchCriteria);
    }

    log.info("Search Params {} {} {} {} {} {} {}",searchCriteria,
      apiRequestParams.getFuzzLevel(),
      apiRequestParams.getNamePriority(),
      apiRequestParams.getAddressPriority(),
      apiRequestParams.getPostcodePriority(),
      apiRequestParams.getPublicNamePriority(),
      PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));

    Page<DosService> services =
        servicesRepo.findBySearchTerms(
            searchCriteria,
            apiRequestParams.getFuzzLevel(),
            apiRequestParams.getNamePriority(),
            apiRequestParams.getAddressPriority(),
            apiRequestParams.getPostcodePriority(),
            apiRequestParams.getPublicNamePriority(),
            PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));
    log.info(
        "Search query duration {}ms. Number of services found {}",
        System.currentTimeMillis() - start,
        services.getTotalElements());
    return getFilteredServices(services);
  }

  private List<DosService> performSearch(
      String searchCriteria,
      String searchLatitude,
      String searchLongitude,
      Double distanceRange,
      Integer numberOfServicesToReturnFromElasticSearch) {
    Long start = System.currentTimeMillis();
    if (numberOfServicesToReturnFromElasticSearch == null) {
      return performSearch(searchCriteria, searchLatitude, searchLongitude, distanceRange);
    }
    log.info("Search Params {} {} {} {} {} {} {} {} {} {}",searchCriteria,
      searchLatitude,
      searchLongitude,
      distanceRange,
      apiRequestParams.getFuzzLevel(),
      apiRequestParams.getNamePriority(),
      apiRequestParams.getAddressPriority(),
      apiRequestParams.getPostcodePriority(),
      apiRequestParams.getPublicNamePriority(),
      PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));

    Page<DosService> services =
        servicesRepo.findSearchTermsByGeoLocation(
            searchCriteria,
            searchLatitude,
            searchLongitude,
            distanceRange,
            apiRequestParams.getFuzzLevel(),
            apiRequestParams.getNamePriority(),
            apiRequestParams.getAddressPriority(),
            apiRequestParams.getPostcodePriority(),
            apiRequestParams.getPublicNamePriority(),
            PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));
    log.info(
        "Search query duration {}ms. Number of services found {}",
        System.currentTimeMillis() - start,
        services.getTotalElements());
    return getFilteredServices(services);
  }

  private List<DosService> performSearch(
      String searchLatitude,
      String searchLongitude,
      Double distanceRange,
      Integer numberOfServicesToReturnFromElasticSearch) {
    Long start = System.currentTimeMillis();
    if (numberOfServicesToReturnFromElasticSearch == null) {
      return performSearch(null, searchLatitude, searchLongitude, distanceRange);
    }
    log.info("Search Params {} {} {} {}",searchLatitude,
      searchLongitude,
      distanceRange,
      PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));

    Page<DosService> services =
        servicesRepo.findAllByGeoLocation(
            searchLatitude,
            searchLongitude,
            distanceRange,
            PageRequest.of(0, numberOfServicesToReturnFromElasticSearch));
    log.info(
        "Search query duration {}ms. Number of services found {}",
        System.currentTimeMillis() - start,
        services.getTotalElements());
    return getFilteredServices(services);
  }

  private List<DosService> performSearch(String searchCriteria) {
    Long start = System.currentTimeMillis();
    log.info("Search params {} {} {} {} {} {} {}", searchCriteria,
      apiRequestParams.getFuzzLevel(),
      apiRequestParams.getNamePriority(),
      apiRequestParams.getAddressPriority(),
      apiRequestParams.getPostcodePriority(),
      apiRequestParams.getPublicNamePriority(),
      PageRequest.of(
        0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()));

    Page<DosService> services =
        servicesRepo.findBySearchTerms(
            searchCriteria,
            apiRequestParams.getFuzzLevel(),
            apiRequestParams.getNamePriority(),
            apiRequestParams.getAddressPriority(),
            apiRequestParams.getPostcodePriority(),
            apiRequestParams.getPublicNamePriority(),
            PageRequest.of(
                0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()));
    log.info(
        "Search query duration {}ms. Number of services found {}",
        System.currentTimeMillis() - start,
        services.getTotalElements());
    return getFilteredServices(services);
  }

  private List<DosService> performSearch(
      String searchCriteria, String searchLatitude, String searchLongitude, Double distanceRange) {
    Long start = System.currentTimeMillis();

    log.info("Search params {} {} {} {} {} {} {} {} {} {} ", searchCriteria,
      searchLatitude,
      searchLongitude,
      distanceRange,
      apiRequestParams.getFuzzLevel(),
      apiRequestParams.getNamePriority(),
      apiRequestParams.getAddressPriority(),
      apiRequestParams.getPostcodePriority(),
      apiRequestParams.getPublicNamePriority(),
      PageRequest.of(
        0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()));

    Page<DosService> services =
        (searchCriteria == null || searchCriteria.isEmpty())
            ? servicesRepo.findAllByGeoLocation(
                searchLatitude,
                searchLongitude,
                distanceRange,
                PageRequest.of(
                    0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()))
            : servicesRepo.findSearchTermsByGeoLocation(
                searchCriteria,
                searchLatitude,
                searchLongitude,
                distanceRange,
                apiRequestParams.getFuzzLevel(),
                apiRequestParams.getNamePriority(),
                apiRequestParams.getAddressPriority(),
                apiRequestParams.getPostcodePriority(),
                apiRequestParams.getPublicNamePriority(),
                PageRequest.of(
                    0, apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms()));
    log.info(
        "Search query duration {}ms. Number of services found {}",
        System.currentTimeMillis() - start,
        services.getTotalElements());
    return getFilteredServices(services);
  }

  private List<DosService> getFilteredServices(Page<DosService> services) {
    final List<DosService> dosServices = new ArrayList<>();
    for (DosService serviceIterationItem : services) {
      dosServices.add(serviceIterationItem);
    }
    log.info("Number of services : {} ", dosServices.size());
    return dosServices;
  }
}
