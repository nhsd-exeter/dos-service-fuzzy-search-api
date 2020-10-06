package uk.nhs.digital.uec.api.model;

import lombok.Getter;
import lombok.Setter;
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

  private Integer maxNumServicesToReturn;

  private Integer fuzzLevel;

  private Integer namePriority;

  private Integer addressPriority;

  @Value("${param.services.max_num_services_to_return}")
  private Integer defaultMaxNumServicesToReturn;

  @Value("${param.services.fuzz_level}")
  private Integer defaultFuzzLevel;

  @Value("${param.services.name_priority}")
  private Integer defaultNamePriority;

  @Value("${param.services.address_priority}")
  private Integer defaultAddressPriority;

  public Integer getMaxNumServicesToReturn() {
    if (this.maxNumServicesToReturn == null) {
      return defaultMaxNumServicesToReturn;
    }

    return maxNumServicesToReturn;
  }

  public Integer getFuzzLevel() {
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
}
