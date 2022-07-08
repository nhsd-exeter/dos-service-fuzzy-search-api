package uk.nhs.digital.uec.api.config;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.nhs.digital.uec.api.authentication.localstub.LocalAmazonCognitoIdentityClientStub;

@Configuration
@Profile({"local","mock-auth"})
public class LocalCognitoStubConfig {

  @Bean
  @Primary
  public AWSCognitoIdentityProvider integrationTestAmazonCognitoIdentityClient() {
    return new LocalAmazonCognitoIdentityClientStub();
  }
}
