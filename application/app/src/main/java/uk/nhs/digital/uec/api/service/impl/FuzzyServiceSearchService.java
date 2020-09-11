package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;
import uk.nhs.digital.uec.api.utils.MockDosServicesUtil;

@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  /** {@inheritDoc} */
  @Override
  public List<DosService> retrieveServicesByFuzzySearch(final List<String> searchCriteria) {

    List<DosService> dosServices = new ArrayList<>();

    // If "Term0" is included in the search criteria, return no services, otherwise return the whole
    // set.
    if (!searchCriteria.contains("Term0")) {
      // Add the mock services we want to return:
      dosServices.add(MockDosServicesUtil.mockDosServices.get(1));
      dosServices.add(MockDosServicesUtil.mockDosServices.get(2));
    }

    return dosServices;
  }
}
