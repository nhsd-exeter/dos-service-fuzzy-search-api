package uk.nhs.digital.uec.api.util;

import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;


public class PostcodeFormatterUtil {
  private static String inCode = "";
  private static String outCode = "";
  private static final String[] IGNORELIST = new String[] {"AA22", "AA88", "AA89", "AA90", "AA91", "AA99", "WALES", "BT00"};

  public  static String formatPostcode(String postcode) {
    String str = StringUtils.deleteWhitespace(postcode).toUpperCase();
    str = str.replace("*", "");

    if (StringUtils.equalsAnyIgnoreCase(str, IGNORELIST)) {
      return "";
    }

    if (str.length() <= 4) {
      return str;
    }

    int outEnd = str.length() - 3;
    outCode = str.substring(0, outEnd);
    inCode = str.substring(outEnd);

    System.out.println("Outcode length: "+outCode.length());
    System.out.println("Is this numeric: "+StringUtils.mid(outCode, 4, 1));
    System.out.println("Is this equal to 0: "+StringUtils.mid(outCode, 2, 1));

    if (outCode.length() == 3 && StringUtils.isNumeric(outCode.substring(1)) && StringUtils.mid(outCode, 1, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 1) + StringUtils.mid(outCode, 2, 1);
    }

    if (outCode.length() == 4 && StringUtils.isNumeric(outCode.substring(2)) && StringUtils.mid(outCode, 2, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 2) + StringUtils.mid(outCode, 3, 1);
    }

    if (outCode.length() == 4 && !StringUtils.isNumeric(StringUtils.mid(outCode, 1, 3)) && StringUtils.mid(outCode, 1, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 1) + StringUtils.mid(outCode, 2, 2);
    }

    if (outCode.length() == 5 && !StringUtils.isNumeric(StringUtils.mid(outCode, 4, 1)) && StringUtils.mid(outCode, 2, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 2) + StringUtils.mid(outCode, 3, 2);
    }

    return outCode + " " + inCode;
  }

}
