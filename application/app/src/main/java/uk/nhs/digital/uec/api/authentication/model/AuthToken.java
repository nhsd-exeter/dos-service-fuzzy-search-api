package uk.nhs.digital.uec.api.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

  private String accessToken;
  private String refreshToken;
}
