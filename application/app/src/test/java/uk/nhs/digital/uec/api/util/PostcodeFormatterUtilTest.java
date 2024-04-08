
package uk.nhs.digital.uec.api.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PostcodeFormatterUtilTest {

  @Test
  public void testPostcodeInIgnoreListIsEmptyString() {
    String postcode = "AA22";
    String formattedPostcode = PostcodeFormatterUtil.formatPostcode(postcode);
    assertEquals("", formattedPostcode);
  }

  @Test
  public void testPostcodeLessThanOrEqualToFourCharactersIsReturned() {
    String postcode = "AA2";
    String formattedPostcode = PostcodeFormatterUtil.formatPostcode(postcode);
    assertEquals("AA2", formattedPostcode);
  }

  @Test
  public void testStripsWhitespaceCorrectly() {
    //too many spaces in middle
    assertEquals("EH1 3PA", PostcodeFormatterUtil.formatPostcode("EH1  3PA"));
    //leading spaces
    assertEquals("EH1 3PA", PostcodeFormatterUtil.formatPostcode("  EH1  3PA"));
    //trailing spaces
    assertEquals("EH1 3PA", PostcodeFormatterUtil.formatPostcode("EH1  3PA  "));
  }

  @Test
  public void noMultipleZerosInFirstPartOfPostcode() {
    assertEquals("E0 3PA", PostcodeFormatterUtil.formatPostcode("E00 3PA"));
    assertEquals("EH0 3PA", PostcodeFormatterUtil.formatPostcode("EH00 3PA"));
  }

  @Test
  public void testSingleZeroStrippedOutOfLongerPostcodeFirstParts() {
    assertEquals("EAB 3PA", PostcodeFormatterUtil.formatPostcode("E0AB 3PA"));
    assertEquals("E0AB 3PA", PostcodeFormatterUtil.formatPostcode("E00AB 3PA"));
  }

}
