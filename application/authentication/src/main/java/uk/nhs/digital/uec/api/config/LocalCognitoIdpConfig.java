package uk.nhs.digital.uec.api.config;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.nhs.digital.uec.api.localstub.LocalAmazonCognitoIdentityClientStub;

@Configuration
@Profile({"local"})
public class LocalCognitoIdpConfig {

  @Bean
  @Primary
  public AWSCognitoIdentityProvider integrationTestAmazonCognitoIdentityClient() {
    return new LocalAmazonCognitoIdentityClientStub();
  }
}
