package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.ApiRequestParams;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;

@Service
public class ApiUtilsService implements ApiUtilsServiceInterface {

  @Autowired private ApiRequestParams apiRequestParams;

  public void configureApiRequestParams(HttpServletRequest request) {
    apiRequestParams.setFuzzLevel(Integer.parseInt(request.getParameter("fuzz_level")));
    apiRequestParams.setFilterReferralRole(request.getParameter("filter_referral_role"));
    apiRequestParams.setMaxNumServicesToReturn(
        Integer.parseInt(request.getParameter("max_number_of_services_to_return")));
  }

  /** {@inheritDoc} */
  @Override
  public List<String> sanitiseSearchTerms(final List<String> searchCriteria) {

    List<String> listFromString = new ArrayList<>();
    listFromString = searchCriteria.stream().map(String::trim).collect(Collectors.toList());

    return listFromString;
  }
}
