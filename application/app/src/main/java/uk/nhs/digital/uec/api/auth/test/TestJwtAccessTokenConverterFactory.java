package uk.nhs.digital.uec.api.auth.test;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Used only in apiTest and integrationTest environments to verify access tokens against a
 * pre-configured public key. Not used in production.
 */
public class TestJwtAccessTokenConverterFactory {

  private static final String INTEGRATION_TEST_PUBLIC_KEY = "integTest_public.txt";

  public JwtAccessTokenConverter getConverter() {

    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    Resource resource = new ClassPathResource(INTEGRATION_TEST_PUBLIC_KEY);
    String publicKey;
    try {
      publicKey = IOUtils.toString(resource.getInputStream());
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    converter.setVerifierKey(publicKey);

    return converter;
  }
}
