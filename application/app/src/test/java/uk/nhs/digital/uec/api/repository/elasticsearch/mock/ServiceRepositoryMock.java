package uk.nhs.digital.uec.api.repository.elasticsearch.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.repository.elasticsearch.ServicesRepositoryInterface;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@Repository
@Profile("test")
public class ServiceRepositoryMock implements ServicesRepositoryInterface {

  /** {@inheritDoc} */
  @Override
  public List<DosService> findServiceBySearchTerms(List<String> searchTerms) {
    final List<DosService> dosServices = new ArrayList<>();

    if (!searchTerms.contains("Term0")) {
      dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
      dosServices.add(MockDosServicesUtil.mockDosServices.get(2));
    }

    if (searchTerms.contains("All")) {

      for (Map.Entry<Integer, DosService> entry : MockDosServicesUtil.mockDosServices.entrySet()) {
        dosServices.add(entry.getValue());
      }
    }

    return dosServices;
  }
}
