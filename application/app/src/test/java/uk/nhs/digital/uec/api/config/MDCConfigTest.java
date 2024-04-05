package uk.nhs.digital.uec.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.nhs.digital.uec.api.interceptor.MDCInterceptor;

import static org.mockito.Mockito.*;

public class MDCConfigTest {

  @Test
  public void testAddsMDCInterceptor() {
    InterceptorRegistry registry = mock(InterceptorRegistry.class);

    MDCConfig mdcConfig = new MDCConfig();
    mdcConfig.addInterceptors(registry);

    verify(registry).addInterceptor(isA(MDCInterceptor.class));
  }

}
