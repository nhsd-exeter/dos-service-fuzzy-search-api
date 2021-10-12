package uk.nhs.digital.uec.api.authentication.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import uk.nhs.digital.uec.api.authentication.filter.AccessTokenFilter;
import uk.nhs.digital.uec.api.authentication.filter.RefreshTokenFilter;
import uk.nhs.digital.uec.api.authentication.filter.TokenEntryPoint;

/** Configuration class to further secure all the APIs */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  public static final String LOGIN_URL = "/authentication/login";
  public static final String HEALTH_CHECK_READINESS_URL = "/actuator/health/readiness";
  public static final String HEALTH_CHECK_LIVENESS_URL = "/actuator/health/liveness";
  public static final String WELCOME_URL = "/dosapi/dosservices/v0.0.1/home";
  public static final String FUZZY_SEARCH_URL = "/dosapi/dosservices/v0.0.1/services/byfuzzysearch";

  @Autowired private AccessTokenFilter accessTokenFilter;
  @Autowired private RefreshTokenFilter refreshTokenFilter;
  @Autowired private TokenEntryPoint tokenEndpoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    List<String> permitAllEndpointList =
        Arrays.asList(LOGIN_URL, HEALTH_CHECK_READINESS_URL, HEALTH_CHECK_LIVENESS_URL);

    http.addFilterBefore(refreshTokenFilter, AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterAfter(accessTokenFilter, AbstractPreAuthenticatedProcessingFilter.class)
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
