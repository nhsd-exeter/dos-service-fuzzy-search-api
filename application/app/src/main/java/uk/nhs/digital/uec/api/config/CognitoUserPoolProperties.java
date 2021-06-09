package uk.nhs.digital.uec.api.config;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/** Cognito User Pool properties */
@Component
@ConfigurationProperties(prefix = "cognito-user-pool-properties")
@Validated
@Getter
public class CognitoUserPoolProperties {

  @NotBlank
  @Value("${fuzzysearch.userPool.clientId}")
  private String clientId;

  @NotBlank
  @Value("${fuzzysearch.userPool.clientSecret}")
  private String clientSecret;

  @NotBlank
  @Value("${fuzzysearch.userPool.id}")
  private String poolId;
}
