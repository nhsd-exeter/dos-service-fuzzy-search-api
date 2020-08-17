package uk.nhs.digital.uec.api.service.mockimpl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.utils.TestDosServicesUtil;

@Profile("mock")
@Service
public class FuzzyServiceSearchServiceMock implements FuzzyServiceSearchServiceInterface {

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(final List<String> searchCriteria) {

    List<DosService> dosServices = new ArrayList<>();

    // If "Term0" is included in the search criteria, return no services, otherwise return the whole
    // set.
    if (!searchCriteria.contains("Term0")) {
      // Add the mock services we want to return:
      dosServices.add(TestDosServicesUtil.mockDosServices.get(1));
      dosServices.add(TestDosServicesUtil.mockDosServices.get(2));
    }

    return dosServices;
  }
}
