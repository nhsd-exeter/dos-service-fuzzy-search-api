package uk.nhs.digital.uec.api.authentication.localstub;

import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.KEY_STORE;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.KEY_STORE_ALIAS;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.KEY_STORE_PASSWORD;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

public class LocalJwtFactory {

  public String createToken(
      String id, String issuer, String subject, long timeToLiveMs, Set<String> groupNames) {

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

    long nowMs = System.currentTimeMillis();
    Date nowDate = new Date(nowMs);

    KeyStoreKeyFactory keyStoreKeyFactory =
        new KeyStoreKeyFactory(new ClassPathResource(KEY_STORE), KEY_STORE_PASSWORD.toCharArray());

    PrivateKey privateKey = keyStoreKeyFactory.getKeyPair(KEY_STORE_ALIAS).getPrivate();

    JwtBuilder builder =
        Jwts.builder()
            .setId(id)
            .setIssuedAt(nowDate)
            .setSubject(subject)
            .setIssuer(issuer)
            .signWith(signatureAlgorithm, privateKey);

    if (timeToLiveMs >= 0) {
      long expiredMs = nowMs + timeToLiveMs;
      Date expirationDate = new Date(expiredMs);
      builder.setExpiration(expirationDate);
    }

    builder.claim("cognito:groups", groupNames.toArray());
    return builder.compact();
  }
}
