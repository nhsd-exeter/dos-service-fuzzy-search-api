package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.service.ApiUtilsServiceInterface;

@Service
public class ApiUtilsService implements ApiUtilsServiceInterface {

  public List<String> createListFromString(final String sourceString, final String delimiter) {

    List<String> listFromString = new ArrayList<>();

    if (sourceString == null || sourceString.isEmpty()) {
      // debug that the source string is empty, but carry on TBC
      return listFromString;
    }

    if (delimiter == null || delimiter.isEmpty()) {
      // Do not split on anything
      return listFromString;
    }

    listFromString.addAll(List.of(sourceString.split(delimiter)));
    listFromString = listFromString.stream().map(String::trim).collect(Collectors.toList());

    return listFromString;
  }
}
