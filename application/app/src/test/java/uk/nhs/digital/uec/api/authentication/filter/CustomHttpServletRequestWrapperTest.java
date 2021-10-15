package uk.nhs.digital.uec.api.authentication.filter;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class CustomHttpServletRequestWrapperTest {

  private CustomHttpServletRequestWrapper customHttpServletRequestWrapper;

  @BeforeEach
  public void init() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper(request);
    customHttpServletRequestWrapper.addHeader("Authorization", "Bearer xyz-AccessToken-tO-TeSt");
    customHttpServletRequestWrapper.addHeader("REFRESH_TOKEN", "Bearer xyz-Refresh-ToKen-tO-TeSt");
  }

  @Test
  public void getHeader() {
    String header = customHttpServletRequestWrapper.getHeader("Authorization");
    assertSame("Bearer xyz-AccessToken-tO-TeSt", header);
  }

  @Test
  public void getHeaderNamesTest() {
    Enumeration<String> headerNames = customHttpServletRequestWrapper.getHeaderNames();
    List<String> headerNameList = Collections.list(headerNames).stream().toList();
    assertSame("Authorization", headerNameList.get(0));
    assertSame("REFRESH_TOKEN", headerNameList.get(1));
  }
}
