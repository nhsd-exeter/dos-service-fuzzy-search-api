package uk.nhs.digital.uec.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Data
@AllArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

  private String allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowCredentials(true)
        .allowedHeaders("Accept", "Authorization", "Content-Type", "Origin")
        .allowedMethods("DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH")
        .allowedOrigins(allowedOrigins.split(","));
  }
}
