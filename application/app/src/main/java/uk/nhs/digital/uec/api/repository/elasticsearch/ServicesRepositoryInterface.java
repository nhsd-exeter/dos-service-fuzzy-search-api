package uk.nhs.digital.uec.api.repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.nhs.digital.uec.api.model.DosService;

public interface ServicesRepositoryInterface extends ElasticsearchRepository<DosService, String> {

  public Integer level = 1;

  // @Query(
  //     "{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\"],"
  //         + " \"fuzziness\": \"AUTO\"}}")
  // The hats make the match field more important.
  // "sort": { "_score": { "order": "desc" }}
  @Query(
      "{\"multi_match\": {\"query\": \"?0\", "
          + "\"fields\": [\"name^2\", \"public_name^3\", \"address\", \"postcode\"], "
          + "\"fuzziness\": \"?1\"}}")
  Iterable<DosService> findByName(String name, Integer fuzzLevel);
}
