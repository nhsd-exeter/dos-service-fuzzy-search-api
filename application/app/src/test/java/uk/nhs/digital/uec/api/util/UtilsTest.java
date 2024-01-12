package uk.nhs.digital.uec.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
