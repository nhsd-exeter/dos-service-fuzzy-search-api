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

  @Value("${param.services.max_num_services_to_return}")
  private Integer defaultMaxNumServicesToReturn;

  @Value("${param.services.fuzz_level}")
  private Integer defaultFuzzLevel;

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
}
