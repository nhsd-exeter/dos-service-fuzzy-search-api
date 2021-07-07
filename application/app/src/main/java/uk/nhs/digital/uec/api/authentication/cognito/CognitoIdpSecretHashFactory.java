package uk.nhs.digital.uec.api.authentication.cognito;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.authentication.exception.CognitoIdpSecretHashException;

/** Create a secret hash to be sent with every request to Amazon Cognito identity client. */
@Slf4j
@Component
public class CognitoIdpSecretHashFactory {

  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

  @Value("${cognito.userPool.clientId}")
  private String userPoolClientId;

  @Value(value = "${cognito.userPool.clientSecret}")
  private String userPoolClientSecret;

  /**
   * Creates a secret hash string.
   *
   * @param username the username of the user
   * @return the secret hash string calculated based on the client secret and the username
   * @throws CognitoIdpSecretHashException if the generation of the secret hash failed due to an
   *     invalid key or an unknown cryptographic algorithm provided
   */
  public String create(String username) {
    SecretKeySpec signingKey =
        new SecretKeySpec(userPoolClientSecret.getBytes(UTF_8), HMAC_SHA256_ALGORITHM);
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);
      mac.update(username.getBytes(UTF_8));
      byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      log.error("Unable to calculate the cognito client secret hash for user: [{}]", username);
      throw new CognitoIdpSecretHashException();
    }
  }
}
