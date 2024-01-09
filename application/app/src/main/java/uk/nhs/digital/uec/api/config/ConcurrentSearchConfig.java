package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class ConcurrentSearchConfig {

  @Bean
  public ExecutorService executorService() {
    return Executors.newFixedThreadPool(15);
  }
}
