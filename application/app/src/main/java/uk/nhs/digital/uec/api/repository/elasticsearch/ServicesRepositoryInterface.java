package uk.nhs.digital.uec.api.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.nhs.digital.uec.api.model.DosService;

public interface ServicesRepositoryInterface extends ElasticsearchRepository<DosService, String> {

  // @Query(
  // "{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\"],"
  // + " \"fuzziness\": \"AUTO\"}}")
  // The hats make the match field more important.
  // Fuzziness not allowed for multi-match type of cross_fields
  @Query(
      "{\"multi_match\": {\"query\": \"?0\", \"type\": \"best_fields\", \"fields\":"
          + " [\"search_data^1\", \"name^?2\", \"public_name^?5\", \"address^?3\","
          + " \"postcode^?4\"], \"fuzziness\": \"?1\", \"operator\":\"and\"}}")
  Page<DosService> findBySearchTerms(
      String searchTerms_0,
      Object fuzzLevel_1,
      Integer namePriority_2,
      Integer addressPriority_3,
      Integer postcodePriority_4,
      Integer publicNamePriority_5,
      Pageable pageable);

  @Query(
      "{"
          + "    \"bool\": {"
          + "      \"should\": {"
          + "        \"multi_match\": {"
          + "          \"query\": \"?0\","
          + "          \"type\": \"best_fields\","
          + "          \"fields\": ["
          + "            \"search_data^4\","
          + "            \"name^?5\","
          + "            \"public_name^?8\","
          + "            \"address^?6\","
          + "            \"postcode^?7\""
          + "          ],"
          + "          \"fuzziness\": \"?4\","
          + "          \"operator\": \"and\""
          + "        }"
          + "      },"
          + "      \"filter\": {"
          + "        \"geo_distance\": {"
          + "          \"distance\": ?3,"
          + "          \"unit\": \"km\","
          + "          \"location.lon\" : ?2,"
          + "          \"location.lat\": ?1"
          + "        }"
          + "      }"
          + "    }"
          + "  }")
  Page<DosService> findSearchTermsByGeoLocation(
      String searchTerms_0,
      Double searchLatitude_1,
      Double searchLongitude_2,
      Double distanceRangeKm_3,
      Object fuzzLevel_4,
      Integer namePriority_5,
      Integer addressPriority_6,
      Integer postcodePriority_7,
      Integer publicNamePriority_8,
      Pageable pageable);

  @Query(
    "{\"bool\": { \"must\" : { \"match_all\" :{}}, \"filter\" : {\"geo_distance\" :{ \"distance\""
      + " : ?2, \"unit\":\"km\",\"location\" : { \"lat\": ?0, \"lon\":?1}}}}}")
  Page<DosService> findAllByGeoLocation(
    Double searchLatitude_0, Double searchLongitude_1, Double distanceRange_2, Pageable pageable);
}
