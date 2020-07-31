package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.DosService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FuzzyServiceSearchServiceTest {

  @Autowired private FuzzyServiceSearchServiceInterface fuzzyServiceSearchService;

  @Test
  public void retrieveServicesByFuzzySearchTest() {

    List<String> searchCriteria = new ArrayList<>();
    searchCriteria.add("term1");
    searchCriteria.add("term2");

    List<DosService> services =
        fuzzyServiceSearchService.retrieveServicesByFuzzySearch(searchCriteria);

    assertEquals(2, services.size());
  }
}
