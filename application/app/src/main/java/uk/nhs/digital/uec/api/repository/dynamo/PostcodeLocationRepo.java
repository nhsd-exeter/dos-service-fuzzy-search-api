package uk.nhs.digital.uec.api.repository.dynamo;

import java.util.List;
import java.util.Optional;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import uk.nhs.digital.uec.api.model.dynamo.PostcodeLocation;

@EnableScan
public interface PostcodeLocationRepo extends CrudRepository<PostcodeLocation, String> {
  List<Optional<PostcodeLocation>> findByPostcode(String postcode);

  List<Optional<PostcodeLocation>> findByPostcodeIn(List<String> postcodes);
}
