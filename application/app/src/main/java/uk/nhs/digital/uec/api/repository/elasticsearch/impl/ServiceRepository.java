package uk.nhs.digital.uec.api.repository.elasticsearch.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.ErrorMessageEnum;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;
import uk.nhs.digital.uec.api.util.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Repository
@Slf4j
public class ServiceRepository implements CustomServicesRepositoryInterface {

  @Autowired
  private ElasticsearchOperations operations;

  @Autowired
  private ApiRequestParams apiRequestParams;

  @Autowired
  private Environment environment;

  private static final DecimalFormat df = new DecimalFormat("0.00");

  /**
   * {@inheritDoc}
   */

  @Override
  public List<DosService> findAllServicesByGeoLocationWithSearchTerms(Double searchLatitude, Double searchLongitude,
      Double distanceRange, List<String> searchTerms) throws NotFoundException {
    log.info("Request Params: Lat: {}, Lng: {}", searchLatitude, searchLongitude);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    if (Objects.isNull(searchLatitude) || (Objects.isNull(searchLongitude))) {
      throw new NotFoundException(ErrorMessageEnum.INVALID_LOCATION.getMessage());
    }
    String searchCriteria = String.join(" ", searchTerms);
    // Adjust number of results to return from ES depending on how many words are in
    // the search
    int numOfSpaces = StringUtils.countMatches(searchCriteria, " ");

    int numberOfServicesToReturnFromElasticSearch = apiRequestParams.getMaxNumServicesToReturnFromElasticsearch();
    if (numOfSpaces == 2) {
      numberOfServicesToReturnFromElasticSearch = apiRequestParams
          .getMaxNumServicesToReturnFromElasticsearch3SearchTerms();
    }
    if (numOfSpaces > 2) {
      numberOfServicesToReturnFromElasticSearch = Math.floorDiv(
          apiRequestParams.getMaxNumServicesToReturnFromElasticsearch3SearchTerms(), 2);
    }
    log.info("Number of services to get from elasticsearch: {}", numberOfServicesToReturnFromElasticSearch);
    List<SearchHit<DosService>> services;

    Query searchQuery = buildElasticSearchQuery(searchLatitude, searchLongitude, distanceRange, searchCriteria,
        numberOfServicesToReturnFromElasticSearch);
    SearchHits<DosService> searchHitsDosServices = operations.search(searchQuery, DosService.class);
    services = searchHitsDosServices.getSearchHits();
    List<DosService> dosServices = services.stream()
        .map(searchHit -> {
          Double distance = (Double) searchHit.getSortValues().get(0);
          DosService service = searchHit.getContent();
          service.setDistance(Double.parseDouble(df.format(distance)));
          return service;
        }).collect(Collectors.toList());
    return getFilteredServices(dosServices);
  }

  @Override
  public List<DosService> findAllServicesByGeoLocation(
      Double searchLatitude, Double searchLongitude, Double distanceRange)
      throws NotFoundException {
    int numberOfServicesToReturnFromElasticSearch = apiRequestParams.getMaxNumServicesToReturnFromElasticsearch();
    log.info("Request Params: Lat: {}, Lng: {}", searchLatitude, searchLongitude);
    log.info("Fuzzy level: {}", apiRequestParams.getFuzzLevel());
    log.info("Number of services to get from elasticsearch: {}", numberOfServicesToReturnFromElasticSearch);
    log.info("Validate geo location points: {} {}", searchLatitude, searchLongitude);
    if (Objects.isNull(searchLatitude) || (Objects.isNull(searchLongitude))) {
      throw new NotFoundException(ErrorMessageEnum.INVALID_LOCATION.getMessage());
    }
    List<SearchHit<DosService>> services;
    Query searchQuery = buildElasticSearchQuery(searchLatitude, searchLongitude, distanceRange, null,
        numberOfServicesToReturnFromElasticSearch);
    services = operations.search(searchQuery, DosService.class).getSearchHits();

    List<DosService> dosServices = services.stream()
        .map(searchHit -> {
          Double distance = (Double) searchHit.getSortValues().get(0);
          DosService service = searchHit.getContent();
          service.setDistance(Double.parseDouble(df.format(distance)));
          return service;
        }).collect(Collectors.toList());
    return getFilteredServices(dosServices);
  }

  private List<DosService> getFilteredServices(List<DosService> services) {
    List<DosService> dosServices = new ArrayList<>();
    for (DosService serviceIterationItem : services) {
      dosServices.add(serviceIterationItem);
    }
    log.info("Number of services : {} ", dosServices.size());
    log.info("Active Profiles : {}", String.join(",", environment.getActiveProfiles()));
    if (Arrays.stream(environment.getActiveProfiles())
        .noneMatch(
            env -> env.equalsIgnoreCase("local")
                || env.equalsIgnoreCase("mock-auth")
                || env.equalsIgnoreCase("dev"))) {
      dosServices = dosServices.stream()
          .filter(
              ds -> Objects.nonNull(ds.getReferral_roles())
                  && (ds.getReferral_roles().contains(Constants.PROFESSIONAL_REFERRAL_FILTER)))
          .collect(Collectors.toList());
      log.info("Number of filtered services by Professional Referral {}", dosServices.size());
    }
    return dosServices;
  }

  private Query buildElasticSearchQuery(Double searchLatitude, Double searchLongitude, Double distanceRange,
      String searchCriteria, int numberOfServicesToReturnFromElasticSearch) {
    GeoPoint location = new GeoPoint(searchLatitude, searchLongitude);
    Sort sort = Sort.by(
        new GeoDistanceOrder("location", location)
            .withUnit("mi")
            .withIgnoreUnmapped(true));

    GeoDistanceQueryBuilder geoDistanceQueryBuilder = QueryBuilders
        .geoDistanceQuery("location")
        .geoDistance(GeoDistance.ARC)
        .point(searchLatitude, searchLongitude)
        .ignoreUnmapped(true)
        .distance(distanceRange, DistanceUnit.MILES);

    NativeSearchQuery searchQuery = null;
    if ((!StringUtils.isBlank(searchCriteria)) && (!StringUtils.isEmpty(searchCriteria))) {

      Map<String, Float> fields = new HashMap<>();
      fields.put("name", (float) apiRequestParams.getDefaultNamePriority());
      fields.put("public_name", (float) apiRequestParams.getDefaultPublicNamePriority());
      fields.put("address", (float) apiRequestParams.getDefaultAddressPriority());
      fields.put("postcode", (float) apiRequestParams.getDefaultPostcodePriority());
      BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
      boolQueryBuilder.must(multiMatchQuery(searchCriteria)
          .operator(Operator.AND)
          .type(Type.BEST_FIELDS)
          .fields(fields)
          .fuzziness(apiRequestParams.getFuzzLevel())
          .prefixLength(3));
      searchQuery = new NativeSearchQueryBuilder()
          .withQuery(boolQueryBuilder)
          .withFilter(geoDistanceQueryBuilder)
          .build();
      searchQuery.setPageable(PageRequest.of(
          0, numberOfServicesToReturnFromElasticSearch));
      searchQuery.addSort(sort);
    } else {
      searchQuery = new NativeSearchQueryBuilder()
          .withFilter(geoDistanceQueryBuilder)
          .build();
      searchQuery.setPageable(PageRequest.of(
          0, numberOfServicesToReturnFromElasticSearch));
      searchQuery.addSort(sort);
    }
    log.info("Query: {}, Filter: {}, Sort: {}, Paging:{} ", searchQuery.getQuery(), searchQuery.getFilter(),
        searchQuery.getSort(), searchQuery.getPageable());
    return searchQuery;
  }
}
