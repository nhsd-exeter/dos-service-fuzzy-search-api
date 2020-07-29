package uk.nhs.digital.uec.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

  @Value("${param.version}")
  private String apiVersion;

  @RequestMapping(value="/")
  public String home()
  {
    return "This is the DoS Service Fuzzy Search API. Version: " + apiVersion;
  }
}
