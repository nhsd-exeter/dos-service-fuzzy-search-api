package uk.nhs.digital.uec.api.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MDCInterceptorTest {

  MDCInterceptor mdcInterceptor = new MDCInterceptor();
  HttpServletRequest request;

  @BeforeEach
  public void setUp() {
    request = Mockito.mock(HttpServletRequest.class);
  }


  @Test
  public void testAddsCorrelationIdToMdc() throws Exception {
    addCorrelationIdToMdc(request);

    String correlationId = MDC.get("correlationId");
    assertEquals("test", correlationId);
  }

  @Test
  public void testRemovesCorrelationIdFromMdc() throws Exception {
    addCorrelationIdToMdc(request);

    mdcInterceptor.afterCompletion(request, null, null, null);
    assertNull(MDC.get("correlationId"));
  }

  @Test
  public void testGeneratesUniqueCorrelationId() throws Exception {
    mdcInterceptor.preHandle(request, null, null);
    assertNotNull(MDC.get("correlationId"));
  }

  private void addCorrelationIdToMdc(HttpServletRequest request) throws Exception {
    when(request.getParameter("X-Correlation-Id")).thenReturn("test");
    assertNull(MDC.get("correlationId"));
    mdcInterceptor.preHandle(request, null, null);
  }

}
