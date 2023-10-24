package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class ConcurrentSearchConfig {

/*  @Bean
  public ExecutorService taskExecutor() {
    return Executors.newFixedThreadPool(15);
  }*/

  @Bean
  public ExecutorService fuzzyTaskExecutor() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    int maximumPoolSize = availableProcessors * 2; // factor of 2 is always a good starting point -  we can increase this factor after performance testing
    int queueCapacity = 2000; // this is assuming we have between 1000-1950 current users

    return new ThreadPoolExecutor(
      availableProcessors,
      maximumPoolSize,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(queueCapacity),
      Executors.defaultThreadFactory(),
      new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
}
