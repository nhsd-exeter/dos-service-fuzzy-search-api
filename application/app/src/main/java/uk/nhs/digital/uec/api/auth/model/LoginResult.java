package uk.nhs.digital.uec.api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {

  private String accessToken;

  private String refreshToken;
}
