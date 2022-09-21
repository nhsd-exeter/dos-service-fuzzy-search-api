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
    @Query("{\"multi_match\": {\"query\": \"?0\", \"type\": \"best_fields\", \"fields\":"
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

    @Query("\"bool\": {\"must\": {\"match_all\": {}},"
            + " \"filter\": {\"geo_distance\": {\"distance\": \"?2\",\"location\": [?0,?1]}}},"
            + " \"fields\": [\"search_data^3\", \"name^?4\", \"public_name^?7\", \"address^?5\",\"postcode^?6\"]")
    Page<DosService> findByGeoLocation(
            String searchLatitude_0,
            String searchLongitude_1,
            String distanceRange_2,
            Object fuzzLevel_3,
            Integer namePriority_4,
            Integer addressPriority_5,
            Integer postcodePriority_6,
            Integer publicNamePriority_7,
            Pageable pageable);
}
