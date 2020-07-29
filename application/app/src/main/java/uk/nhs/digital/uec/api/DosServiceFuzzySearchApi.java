package uk.nhs.digital.uec.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DosServiceFuzzySearchApi {

  public static void main(String[] args) {
    System.setProperty("server.port", "9095");
    SpringApplication.run(DosServiceFuzzySearchApi.class, args);
  }
}
