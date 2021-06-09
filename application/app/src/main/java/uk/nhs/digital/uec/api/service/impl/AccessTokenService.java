package uk.nhs.digital.uec.api.service.impl;

import static uk.nhs.digital.uec.api.auth.AuthConstants.COGNITO_GROUPS;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.auth.cognito.CognitoService;
import uk.nhs.digital.uec.api.auth.exception.InvalidAccessTokenException;
import uk.nhs.digital.uec.api.auth.exception.NoRolesException;
import uk.nhs.digital.uec.api.auth.filter.JwtDecoder;
import uk.nhs.digital.uec.api.auth.model.AuthTokens;
import uk.nhs.digital.uec.api.auth.model.Credentials;
import uk.nhs.digital.uec.api.auth.util.CheckArgument;
import uk.nhs.digital.uec.api.service.AccessTokenServiceInterface;

@Service
@Slf4j
public class AccessTokenService implements AccessTokenServiceInterface {

  @Autowired private CognitoService cognitoService;
  @Autowired private JwtDecoder jwtDecoder;

  @Override
  public AuthTokens accessToken(Credentials credentials) throws NoRolesException {
    CheckArgument.isNotNull(credentials, "credentials must not be null");
    String emailAddress = credentials.getEmailAddress();
    try {
      AuthTokens authTokenResults = cognitoService.authenticate(credentials);
      checkAccessTokenGroups(authTokenResults);
      return authTokenResults;
    } catch (InvalidAccessTokenException e) {
      throw new InvalidAccessTokenException(
          String.format("Invalid credentials for [%s]", emailAddress));
    }
  }

  private void checkAccessTokenGroups(AuthTokens authTokens) throws NoRolesException {
    DecodedJWT jwt = jwtDecoder.decode(authTokens.getAccessToken());
    Claim groupsClaim = jwt.getClaim(COGNITO_GROUPS);
    if (groupsClaim == null || groupsClaim.asList(String.class) == null) {
      log.info("NO GROUPS, NO ACCESS");
      throw new NoRolesException();
    }
  }
}
