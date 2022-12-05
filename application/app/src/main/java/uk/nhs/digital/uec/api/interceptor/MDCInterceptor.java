package uk.nhs.digital.uec.api.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MDCInterceptor implements HandlerInterceptor {


  private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
  private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
          throws Exception {
            final String correlationId = getCorrelationIdFromHeaderOrParams(request);
            MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
            return true;
  }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {
              log.debug("Removing correlationId in fuzzy search {}", MDC.get(CORRELATION_ID_LOG_VAR_NAME));
              MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
    }


    private String generateUniqueCorrelationId() {
      String correlationId = UUID.randomUUID().toString();
      log.debug("Generated new correlationId in fuzzy search {}", correlationId);
      return correlationId;
    }

    private String getCorrelationIdFromHeaderOrParams(final HttpServletRequest request) {
      String correlationId = request.getParameter(CORRELATION_ID_HEADER_NAME);
      if (!StringUtils.isBlank(correlationId)) {
        log.debug("Received correlationId from service finder request params {}", correlationId);
        return correlationId;
      }
      correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
      if (!StringUtils.isBlank(correlationId)) {
        log.debug("Received correlationId from service finder request header {}", correlationId);
        return correlationId;

      }
      return generateUniqueCorrelationId();
    }
}
