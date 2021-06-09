package uk.nhs.digital.uec.api.service;

import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.auth.exception.NoRolesException;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.Credentials;

@Service
public interface AccessTokenServiceInterface {

  public AuthTokens accessToken(Credentials credentials) throws NoRolesException;
}
