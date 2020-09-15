package uk.nhs.digital.uec.api.service.mock;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.util.MockDosServicesUtil;

@Profile("test")
@Service
public class FuzzyServiceSearchServiceMock implements FuzzyServiceSearchServiceInterface {

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(final List<String> searchCriteria) {

    List<DosService> dosServices = new ArrayList<>();

    if (!searchCriteria.contains("Term0")) {
      dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
      dosServices.add(MockDosServicesUtil.mockDosServices.get(2));
    }

    return dosServices;
  }
}
