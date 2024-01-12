package uk.nhs.digital.uec.api.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ConstantsTest {
  @Test
  void testProfessionalReferralFilter() {
    assertThat(Constants.PROFESSIONAL_REFERRAL_FILTER)
      .isNotNull()
      .isNotBlank()
      .isEqualTo("Professional Referral");
  }

  @Test
  void testDefaultDistanceRange() {
    assertThat(Constants.DEFAULT_DISTANCE_RANGE)
      .isNotNull()
      .isEqualTo(60.00);
  }

}
