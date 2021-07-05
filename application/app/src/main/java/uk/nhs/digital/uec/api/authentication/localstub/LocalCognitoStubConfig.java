package uk.nhs.digital.uec.api.authentication.localstub;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local"})
public class LocalCognitoStubConfig {

  @Bean
  @Primary
  public AWSCognitoIdentityProvider integrationTestAmazonCognitoIdentityClient() {
    return new LocalAmazonCognitoIdentityClientStub();
  }
}
