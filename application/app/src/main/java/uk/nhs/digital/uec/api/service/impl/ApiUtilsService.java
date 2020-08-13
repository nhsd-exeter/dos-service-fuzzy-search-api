package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;

@Service
public class ApiUtilsService implements ApiUtilsServiceInterface {

  /** {@inheritDoc} */
  @Override
  public List<String> sanitiseSearchTerms(final List<String> searchCriteria) {

    List<String> listFromString = new ArrayList<>();
    listFromString = searchCriteria.stream().map(String::trim).collect(Collectors.toList());

    return listFromString;
  }
}
