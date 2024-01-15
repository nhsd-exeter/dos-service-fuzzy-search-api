package uk.nhs.digital.uec.api.util;

import org.junit.jupiter.api.Test;
import uk.nhs.digital.uec.api.authentication.exception.CognitoIdpSecretHashException;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

  @Test
  public void testCalculateSecretHash2() {
    String userName = "testUser";
    String userPoolClientId = "testUserPoolClientId";
    String userPoolClientSecret = "testUserPoolClientSecret";

    String secretHash = Utils.calculateSecretHash(userName, userPoolClientId, userPoolClientSecret);

    assertFalse(secretHash.isEmpty());

    assertEquals(44, secretHash.length());
  }

  @Test
  void testCalculateSecretHashValidInputs() {
    String userName = "testUser";
    String userPoolClientId = "testUserPoolClientId";
    String userPoolClientSecret = "testUserPoolClientSecret";

    String secretHash = Utils.calculateSecretHash(userName, userPoolClientId, userPoolClientSecret);

    assertFalse(secretHash.isEmpty());
    assertEquals(44, secretHash.length());
  }

}
