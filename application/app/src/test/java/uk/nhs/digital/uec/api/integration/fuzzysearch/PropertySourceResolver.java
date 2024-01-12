package uk.nhs.digital.uec.api.integration.fuzzysearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertySourceResolver {

  @Value("${api.endpoint.service_search}")
  public String endpointUrl;

  @Value("${configuration.search_parameters.max_num_services_to_return}")
  public int maxNumServicesToReturn;
}
