package uk.nhs.digital.uec.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FuzzySearchHomeController {

  @Value("${configuration.version}")
  private String apiVersion;

  /** Welcome/home page endpoint for the DoS Service Fuzzy Search API. */
  @GetMapping(value = "/api/home")
  public String home() {
    return "This is the DoS Service Fuzzy Search API. Version: " + apiVersion;
  }
}
