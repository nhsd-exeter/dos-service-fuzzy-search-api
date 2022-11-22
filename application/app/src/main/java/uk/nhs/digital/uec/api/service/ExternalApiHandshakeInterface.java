package uk.nhs.digital.uec.api.service;

import java.util.List;
import org.springframework.util.MultiValueMap;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeLocation;

public interface ExternalApiHandshakeInterface {

  List<PostcodeLocation> getPostcodeMappings(
      List<String> postCodes, MultiValueMap<String, String> headers)
      throws InvalidParameterException;

  MultiValueMap<String, String> getAccessTokenHeader();
}
