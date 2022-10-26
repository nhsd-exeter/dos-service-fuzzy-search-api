package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;

@Repository
@Slf4j
public class CustomServicesRepository implements CustomServicesRepositoryInterface {

  private static final String POSTCODE_REGEX =
      "([Gg][Ii][Rr]"
          + " 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";
  private static final String PROFESSIONAL_REFERRAL_FILTER = "Professional Referral";

  @Autowired private ServicesRepositoryInterface servicesRepo;

  @Autowired private ApiRequestParams apiRequestParams;

  @Autowired private Environment environment;

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
  public List<DosService> findAllServicesByGeoLocation(
      Double searchLatitude, Double searchLongitude, Double distanceRange,List<String> searchTerms)
      throws NotFoundException {
    log.info("Request Params: Lat: {}, Lng: {}", searchLatitude, searchLongitude);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info(
        "Number of services to get from elasticsearch: "
            + apiRequestParams.getMaxNumServicesToReturnFromElasticsearch());

    log.info("Validate geo location points: {} {}", searchLatitude, searchLongitude);
    if (Objects.isNull(searchLatitude) || (Objects.isNull(searchLongitude))) {
      throw new NotFoundException(ErrorMessageEnum.INVALID_LOCATION.getMessage());
    }

    int numServicesToReturnFromEs = apiRequestParams.getMaxNumServicesToReturnFromElasticsearch();

    return !Objects.isNull(searchTerms) ? performSearch( String.join(" ", searchTerms),searchLatitude, searchLongitude, distanceRange,numServicesToReturnFromEs):
      performSearch(
        searchLatitude, searchLongitude, distanceRange, numServicesToReturnFromEs);
  }


  private List<DosService> performSearch(
      String searchCriteria, Integer numberOfServicesToReturnFromElasticSearch) {
    Long start = System.currentTimeMillis();
    if (numberOfServicesToReturnFromElasticSearch == null) {
      return performSearch(searchCriteria);
    }

    log.info(
        "Search Params {} {} {} {} {} {} {}",
        searchCriteria,
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
      Double searchLatitude,
      Double searchLongitude,
      Double distanceRange,
      Integer numberOfServicesToReturnFromElasticSearch) {
    Long start = System.currentTimeMillis();
    if (numberOfServicesToReturnFromElasticSearch == null) {
      return performSearch(null, searchLatitude, searchLongitude, distanceRange,numberOfServicesToReturnFromElasticSearch);
    }
    log.info(
        "Search Params {} {} {} {}",
        searchLatitude,
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
    log.info(
        "Search params {} {} {} {} {} {} {}",
        searchCriteria,
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
      String searchCriteria, Double searchLatitude, Double searchLongitude, Double distanceRange,Integer numberOfServicesToReturnFromElasticSearch) {
    Long start = System.currentTimeMillis();

    log.info(
        "Search params {} {} {} {} {} {} {} {} {} {} ",
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
            0, numberOfServicesToReturnFromElasticSearch));

    Page<DosService> services =
        (searchCriteria == null || searchCriteria.isEmpty())
            ? servicesRepo.findAllByGeoLocation(
                searchLatitude,
                searchLongitude,
                distanceRange,
                PageRequest.of(
                    0, numberOfServicesToReturnFromElasticSearch))
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
                    0, numberOfServicesToReturnFromElasticSearch));
    log.info(
        "Search query duration {}ms. Number of services found {}",
        System.currentTimeMillis() - start,
        services.getTotalElements());
    return getFilteredServices(services);
  }

  private List<DosService> getFilteredServices(Page<DosService> services) {
    List<DosService> dosServices = new ArrayList<>();
    for (DosService serviceIterationItem : services) {
      dosServices.add(serviceIterationItem);
    }
    log.info("Number of services : {} ", dosServices.size());

    if (Arrays.stream(environment.getActiveProfiles())
        .noneMatch(
            env ->
                env.equalsIgnoreCase("local")
                    || env.equalsIgnoreCase("mock-auth")
                    || env.equalsIgnoreCase("dev"))) {
      dosServices =
          dosServices.stream()
              .filter(
                  ds ->
                      Objects.nonNull(ds.getReferral_roles())
                          && (ds.getReferral_roles().contains(PROFESSIONAL_REFERRAL_FILTER)))
              .collect(Collectors.toList());
      log.info("Number of filtered services by Professional Referral {}", dosServices.size());
    }
    return dosServices;
  }


}
