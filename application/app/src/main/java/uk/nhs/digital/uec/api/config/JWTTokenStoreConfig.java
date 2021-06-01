package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import uk.nhs.digital.uec.api.auth.test.TestJwtAccessTokenConverterFactory;

/* Only required when running locally to mimic the JWT Key store (which is in Cognito in AWS environments) */
@Configuration
@Profile({"local"})
public class JWTTokenStoreConfig {

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    return new TestJwtAccessTokenConverterFactory().getConverter();
  }
}
