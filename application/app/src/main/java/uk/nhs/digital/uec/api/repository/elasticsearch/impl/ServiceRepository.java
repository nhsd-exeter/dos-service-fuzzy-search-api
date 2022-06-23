package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class ServiceRepository implements CustomServicesRepositoryInterface {

  @Autowired
  private ServicesRepositoryInterface servicesRepo;

  @Autowired
  private ApiRequestParams apiRequestParams;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<DosService> findServiceBySearchTerms(List<String> searchTerms) {
    final List<DosService> dosServices = new ArrayList<>();

    log.info("Request Params: " + apiRequestParams.getFuzzLevel());
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

    Long start = System.currentTimeMillis();
    Iterable<DosService> services =
      servicesRepo.findBySearchTerms(
        searchCriteria,
        apiRequestParams.getFuzzLevel(),
        apiRequestParams.getNamePriority(),
        apiRequestParams.getAddressPriority(),
        apiRequestParams.getPostcodePriority(),
        apiRequestParams.getPublicNamePriority(),
        PageRequest.of(0, numServicesToReturnFromEs));
    log.info("Search query duration {}ms", System.currentTimeMillis() - start);

    Iterator<DosService> serviceit = services.iterator();
    while (serviceit.hasNext()) {
      DosService serviceIterationItem = serviceit.next();
      if (Objects.nonNull(serviceIterationItem.getReferral_roles()) && !serviceIterationItem.getReferral_roles().isEmpty()) {
        dosServices.add(serviceIterationItem);
      }
    }

    return dosServices;
  }
}
