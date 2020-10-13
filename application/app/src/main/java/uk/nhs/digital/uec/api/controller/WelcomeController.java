package uk.nhs.digital.uec.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Simple welcome/home page controller for the DoS Service Fuzzy Search API. */
@RestController
public class WelcomeController {

  @Value("${configuration.version}")
  private String apiVersion;

  @RequestMapping(value = "/dosapi/dosservices/v0.0.1/home")
  public String home() {
    return "This is the DoS Service Fuzzy Search API. Version: " + apiVersion;
  }
}
