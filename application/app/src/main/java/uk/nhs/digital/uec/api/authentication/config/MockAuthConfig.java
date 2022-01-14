package uk.nhs.digital.uec.api.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import uk.nhs.digital.uec.api.authentication.filter.TokenEntryPoint;
import uk.nhs.digital.uec.api.authentication.filter.MockAccessTokenFilter;


@Profile("mock-auth")
@Configuration
public class MockAuthConfig extends WebSecurityConfigurerAdapter {

  @Autowired private MockAccessTokenFilter mockAccessTokenFilter;
  @Autowired private TokenEntryPoint tokenEntrypoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.addFilterBefore(mockAccessTokenFilter, AbstractPreAuthenticatedProcessingFilter.class)
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(tokenEntrypoint)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
