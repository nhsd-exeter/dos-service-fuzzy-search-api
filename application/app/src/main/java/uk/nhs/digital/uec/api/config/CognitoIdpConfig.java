package uk.nhs.digital.uec.api.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoIdpConfig {

  @Value(value = "${cognito.userPool.clientId}")
  private String userPoolAccessKey;

  @Value(value = "${cognito.userPool.clientSecret}")
  private String userPoolSecretKey;

  @Bean
  public AWSCognitoIdentityProvider cognitoClient() {

    BasicAWSCredentials awsCredentials =
        new BasicAWSCredentials(userPoolAccessKey, userPoolSecretKey);
    return AWSCognitoIdentityProviderClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(Regions.EU_WEST_2)
        .build();
  }
}
