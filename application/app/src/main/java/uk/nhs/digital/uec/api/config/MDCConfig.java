package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import uk.nhs.digital.uec.api.interceptor.MDCInterceptor;

@Configuration
public class MDCConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new MDCInterceptor());
    }
}
