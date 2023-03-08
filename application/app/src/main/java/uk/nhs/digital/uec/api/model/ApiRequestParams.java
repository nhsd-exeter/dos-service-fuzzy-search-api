package uk.nhs.digital.uec.api.model;

import lombok.Getter;
import lombok.Setter;
import uk.nhs.digital.uec.api.util.Constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class ApiRequestParams {

  private String filterReferralRole;

  private Integer maxNumServicesToReturnFromElasticsearch;

  private Integer maxNumServicesToReturnFromElasticsearch3SearchTerms;

  private Integer maxNumServicesToReturn;

  private Integer fuzzLevel;

  private Integer namePriority;

  private Integer addressPriority;

  private Integer postcodePriority;

  private Integer publicNamePriority;

  @Value("${configuration.search_parameters.max_num_services_to_return_from_elasticsearch}")
  private Integer defaultMaxNumServicesToReturnFromElasticsearch;

  @Value(
      "${configuration.search_parameters.max_num_services_to_return_from_elasticsearch_3_search_terms}")
  private Integer defaultMaxNumServicesToReturnFromElasticsearch3SearchTerms;

  @Value("${configuration.search_parameters.max_num_services_to_return}")
  private Integer defaultMaxNumServicesToReturn;

  @Value("${configuration.search_parameters.fuzz_level}")
  private Object defaultFuzzLevel;

  @Value("${configuration.search_parameters.name_priority}")
  private Integer defaultNamePriority;

  @Value("${configuration.search_parameters.address_priority}")
  private Integer defaultAddressPriority;

  @Value("${configuration.search_parameters.postcode_priority}")
  private Integer defaultPostcodePriority;

  @Value("${configuration.search_parameters.public_name_priority}")
  private Integer defaultPublicNamePriority;

  public Integer getMaxNumServicesToReturnFromElasticsearch() {
    if (this.maxNumServicesToReturnFromElasticsearch == null) {
      return defaultMaxNumServicesToReturnFromElasticsearch;
    }

    return maxNumServicesToReturnFromElasticsearch;
  }

  public Integer getMaxNumServicesToReturnFromElasticsearch3SearchTerms() {
    if (this.maxNumServicesToReturnFromElasticsearch3SearchTerms == null) {
      return defaultMaxNumServicesToReturnFromElasticsearch3SearchTerms;
    }

    return maxNumServicesToReturnFromElasticsearch3SearchTerms;
  }

  public Integer getMaxNumServicesToReturn() {
    if (this.maxNumServicesToReturn == null) {
      return defaultMaxNumServicesToReturn;
    }

    return maxNumServicesToReturn;
  }

  public Object getFuzzLevel() {
    if (this.fuzzLevel == null) {
      return defaultFuzzLevel;
    }

    return fuzzLevel;
  }

  public void setNamePriority(Integer namePriority) {
    if (namePriority == null) {
      namePriority = defaultNamePriority;
    }

    this.namePriority = namePriority;
  }

  public void setAddressPriority(Integer addressPriority) {
    if (addressPriority == null) {
      addressPriority = defaultAddressPriority;
    }

    this.addressPriority = addressPriority;
  }

  public void setPostcodePriority(Integer postcodePriority) {
    if (postcodePriority == null) {
      postcodePriority = defaultPostcodePriority;
    }

    this.postcodePriority = postcodePriority;
  }

  public void setPublicNamePriority(Integer publicNamePriority) {
    if (publicNamePriority == null) {
      publicNamePriority = defaultPublicNamePriority;
    }

    this.publicNamePriority = publicNamePriority;
  }

  public String getFilterReferralRole() {
    if (this.filterReferralRole == null) {
      return Constants.PROFESSIONAL_REFERRAL_FILTER;
    } else {
      return filterReferralRole;
    }
  }
}
