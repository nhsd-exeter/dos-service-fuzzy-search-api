package uk.nhs.digital.uec.api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class to encapsulate the authentication tokens that are issued by Cognito and are used to
 * authenticate communication to the APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationToken {

  private String accessToken;

  private String refreshToken;
}
