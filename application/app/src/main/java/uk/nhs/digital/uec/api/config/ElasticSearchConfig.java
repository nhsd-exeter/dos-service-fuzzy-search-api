package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uk.nhs.digital.uec.api.repository.elasticsearch")
public class ElasticSearchConfig {}
