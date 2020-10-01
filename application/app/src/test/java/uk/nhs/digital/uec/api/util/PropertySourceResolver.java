package uk.nhs.digital.uec.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertySourceResolver {

  @Value("${api.endpoint}")
  public String endpointUrl;

  @Value("${param.services.max_num_services_to_return}")
  public int maxNumServicesToReturn;
}
