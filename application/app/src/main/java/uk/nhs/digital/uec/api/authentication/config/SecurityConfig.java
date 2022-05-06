package uk.nhs.digital.uec.api.authentication.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import uk.nhs.digital.uec.api.filter.AccessTokenFilter;
import uk.nhs.digital.uec.api.filter.TokenEntryPoint;

/** Configuration class to further secure all the APIs */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!mock-auth")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  public static final String LOGIN_URL = "/authentication/login";
  public static final String REFRESH_URL = "/authentication/refresh";
  public static final String HEALTH_CHECK_READINESS_URL = "/actuator/health/readiness";
  public static final String HEALTH_CHECK_LIVENESS_URL = "/actuator/health/liveness";
  public static final String WELCOME_URL = "/api/home";
  public static final String FUZZY_SEARCH_URL = "/dosapi/dosservices/v0.0.1/services/byfuzzysearch";

  public static final String SWAGGER_URL = "/swagger-ui.html";
  public static final String SWAGGER_API_DOCS = "/v2/api-docs";
  public static final String SWAGGER_RESOURCES_DIR = "/swagger-resources/**";
  public static final String SWAGGER_WEBJARS = "/webjars/**";
  public static final String SWAGGER_API_DOCS_DIR = "/v3/api-docs/**";
  public static final String SWAGGER_UI_DIR = "/swagger-ui/**";

  @Autowired private AccessTokenFilter accessTokenFilter;
  @Autowired private TokenEntryPoint tokenEndpoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    List<String> permitAllEndpointList =
        Arrays.asList(
            WELCOME_URL,
            FUZZY_SEARCH_URL,
            LOGIN_URL,
            HEALTH_CHECK_READINESS_URL,
            HEALTH_CHECK_LIVENESS_URL,
            SWAGGER_URL,
            SWAGGER_API_DOCS,
            SWAGGER_RESOURCES_DIR,
            SWAGGER_WEBJARS,
            SWAGGER_API_DOCS_DIR,
            SWAGGER_UI_DIR,
            REFRESH_URL);

    http.addFilterBefore(accessTokenFilter, AbstractPreAuthenticatedProcessingFilter.class)
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()]))
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(tokenEndpoint)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
