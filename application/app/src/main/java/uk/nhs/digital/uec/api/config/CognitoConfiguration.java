package uk.nhs.digital.uec.api.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.uec.api.auth.filter.JwtDecoder;

/** The Amazon Cognito configuration */
@Configuration
public class CognitoConfiguration {

  private static final Regions DEFAULT_COGNITO_REGION = Regions.EU_WEST_2;

  @Bean
  public AWSCognitoIdentityProvider amazonCognitoIdentityClient() {
    return AWSCognitoIdentityProviderClientBuilder.standard()
        .withRegion(DEFAULT_COGNITO_REGION)
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return new JwtDecoder();
  }
}
