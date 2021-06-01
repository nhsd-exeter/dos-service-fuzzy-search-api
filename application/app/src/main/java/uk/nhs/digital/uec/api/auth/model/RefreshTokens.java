package uk.nhs.digital.uec.api.auth.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokens {

  @NotBlank(message = "refreshToken must not be blank")
  private String refreshToken;

  @NotBlank(message = "identityProviderId must not be blank")
  private String identityProviderId;
}
