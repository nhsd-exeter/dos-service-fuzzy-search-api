package uk.nhs.digital.uec.api.utils;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.exception.CognitoIdpSecretHashException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_256;

@Slf4j
public class Utils {

  private Utils() {}

  public static String calculateSecretHash(
    String userName, String userPoolClientId, String userPoolClientSecret) {
    SecretKeySpec signingKey =
      new SecretKeySpec(
        userPoolClientSecret.getBytes(UTF_8),
        HMAC_SHA_256.toString());
    try {
      Mac mac = Mac.getInstance(HMAC_SHA_256.toString());
      mac.init(signingKey);
      mac.update(userName.getBytes(UTF_8));
      byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);

    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      log.error("Unable to calculate the cognito client secret hash for user: [{}]", userName);
      throw new CognitoIdpSecretHashException();
    }
  }
}
