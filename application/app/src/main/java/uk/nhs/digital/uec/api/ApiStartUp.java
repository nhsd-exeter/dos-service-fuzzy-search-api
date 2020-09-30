package uk.nhs.digital.uec.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.repository.elasticsearch.CustomServicesRepositoryInterface;

@Component
public class ApiStartUp implements ApplicationListener<ApplicationReadyEvent> {

  @Autowired private CustomServicesRepositoryInterface elasticsearch;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {

    elasticsearch.saveMockServices();
  }
}
