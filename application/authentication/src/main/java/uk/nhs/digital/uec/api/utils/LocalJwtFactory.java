package uk.nhs.digital.uec.api.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.PrivateKey;
import java.util.Date;
import java.util.Set;

import static uk.nhs.digital.uec.api.utils.Constants.COGNITO_GROUPS;
import static uk.nhs.digital.uec.api.utils.Constants.KEY_STORE;
import static uk.nhs.digital.uec.api.utils.Constants.KEY_STORE_ALIAS;
import static uk.nhs.digital.uec.api.utils.Constants.KEY_STORE_PASSWORD;


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

    builder.claim(COGNITO_GROUPS, groupNames.toArray());
    return builder.compact();
  }

}
