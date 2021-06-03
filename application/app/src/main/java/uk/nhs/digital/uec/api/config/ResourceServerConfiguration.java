package uk.nhs.digital.uec.api.config;

import static org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.nhs.digital.uec.api.auth.CookieTokenExtractor;
import uk.nhs.digital.uec.api.auth.CorsConfig;
import uk.nhs.digital.uec.api.auth.factory.CookieFactory;
import uk.nhs.digital.uec.api.auth.factory.SslFactorySupplier;
import uk.nhs.digital.uec.api.auth.filter.AccessTokenChecker;
import uk.nhs.digital.uec.api.auth.filter.AccessTokenFilter;
import uk.nhs.digital.uec.api.auth.filter.JwtDecoder;
import uk.nhs.digital.uec.api.auth.filter.RefreshTokenFilter;
import uk.nhs.digital.uec.api.auth.filter.RefreshTokenService;

/** Spring Security configuration for the Fuzzy Search OAuth2 Resource Server */
@Configuration
@EnableResourceServer
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Value("${fuzzysearch.usermanagement.cookie.domain}")
  private String cookieDomain;

  @Value("${fuzzysearch.usermanagement.url}")
  private String userManagementUrl;

  @Value("${fuzzysearch.allowedorigins}")
  private String allowedOrigins;

  private final RestTemplateBuilder restTemplateBuilder;

  @Autowired
  public ResourceServerConfiguration(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
  }

  @Bean
  public CookieFactory cookieFactory() {
    return new CookieFactory(cookieDomain);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new CorsConfig(allowedOrigins);
  }

  @Bean
  public RefreshTokenFilter refreshTokenFilter() {
    RestTemplate restTemplate =
        restTemplateBuilder.requestFactory(new SslFactorySupplier()).build();
    return new RefreshTokenFilter(
        new RefreshTokenService(restTemplate, userManagementUrl),
        new AccessTokenChecker(new JwtDecoder()),
        cookieFactory());
  }

  @Bean
  public AccessTokenFilter accessTokenFilter() {
    return new AccessTokenFilter();
  }

  @Configuration
  public static class HttpHeadersSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.headers()
          .referrerPolicy(NO_REFERRER)
          .and()
          .httpStrictTransportSecurity()
          .includeSubDomains(true)
          .maxAgeInSeconds(Duration.ofDays(365).getSeconds())
          .and()
          .frameOptions()
          .sameOrigin()
          .cacheControl()
          .and()
          .contentTypeOptions()
          .and()
          .xssProtection()
          .block(true);
    }
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) {
    resources.tokenExtractor(new CookieTokenExtractor());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/dosapi/**")
        .addFilterBefore(refreshTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterAfter(accessTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class)
        .cors()
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable();
  }
}
