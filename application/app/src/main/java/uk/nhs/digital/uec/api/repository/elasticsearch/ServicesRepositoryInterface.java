package uk.nhs.digital.uec.api.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.nhs.digital.uec.api.model.DosService;

public interface ServicesRepositoryInterface extends ElasticsearchRepository<DosService, String> {

  // @Query(
  //     "{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\"],"
  //         + " \"fuzziness\": \"AUTO\"}}")
  // The hats make the match field more important.
  // Fuzziness not allowed for multi-match type of cross_fields
  @Query(
      "{\"multi_match\": {\"query\": \"?0\", \"type\": \"best_fields\", \"fields\":"
          + " [\"search_data^1\", \"name^?2\", \"public_name^?5\", \"address^?3\","
          + " \"postcode^?4\"], \"fuzziness\": \"?1\", \"operator\":\"or\"}}")
  Page<DosService> findBySearchTerms(
      String searchTerms_0,
      Integer fuzzLevel_1,
      Integer namePriority_2,
      Integer addressPriority_3,
      Integer postcodePriority_4,
      Integer publicNamePriority_5,
      Pageable pageable);
}
