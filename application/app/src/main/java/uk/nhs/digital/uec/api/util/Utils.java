package uk.nhs.digital.uec.api.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import uk.nhs.digital.uec.api.authentication.exception.CognitoIdpSecretHashException;

@Slf4j
public class Utils {

  private Utils() {}

  public static String calculateSecretHash(
      String userName, String userPoolClientId, String userPoolClientSecret) {
    SecretKeySpec signingKey =
        new SecretKeySpec(
            userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
            HmacAlgorithms.HMAC_SHA_256.toString());
    try {
      Mac mac = Mac.getInstance(HmacAlgorithms.HMAC_SHA_256.toString());
      mac.init(signingKey);
      mac.update(userName.getBytes(StandardCharsets.UTF_8));
      byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);

    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      log.error("Unable to calculate the cognito client secret hash for user: [{}]", userName);
      throw new CognitoIdpSecretHashException();
    }
  }
}
