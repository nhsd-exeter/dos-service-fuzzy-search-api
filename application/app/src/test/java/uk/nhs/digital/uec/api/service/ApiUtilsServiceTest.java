package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ApiUtilsServiceTest{

  @Autowired
  private ApiUtilsServiceInterface apiUtilsService;

  @Test
  public void createListFromString1(){

    final String sourceString = "one,two,three";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(3, createdList.size());
    assertTrue(createdList.contains("one"));
    assertTrue(createdList.contains("two"));
    assertTrue(createdList.contains("three"));

  }

  public void createListFromString2(){

    final String sourceString = "one, two, three ";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(3, createdList.size());
    assertTrue(createdList.contains("one"));
    assertTrue(createdList.contains("two"));
    assertTrue(createdList.contains("three"));

  }

  public void createListFromString3(){

    final String sourceString = "";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(0, createdList.size());

  }

  public void createListFromString4(){

    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(null, delimiter);

    assertEquals(0, createdList.size());

  }

  public void createListFromString5(){

    final String sourceString = "one, two, three ";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, null);

    assertEquals(0, createdList.size());

  }

  public void createListFromString6(){

    final String sourceString = "one maple drive, 2 sortey street, EX8 6LR ";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(3, createdList.size());
    assertTrue(createdList.contains("one maple drive"));
    assertTrue(createdList.contains("2 sortey street"));
    assertTrue(createdList.contains("EX8 6LR"));

  }

  public void createListFromString7(){

    final String sourceString = ",one maple drive,, 2 sortey street,   , EX8 6LR ";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(3, createdList.size());
    assertTrue(createdList.contains("one maple drive"));
    assertTrue(createdList.contains("2 sortey street"));
    assertTrue(createdList.contains("EX8 6LR"));

  }

  public void createListFromString8(){

    final String sourceString = "    ";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(0, createdList.size());

  }

  public void createListFromString9(){

    final String sourceString = "  ,,  ";
    final String delimiter = ",";

    List<String> createdList = apiUtilsService.createListFromString(sourceString, delimiter);

    assertEquals(0, createdList.size());

  }

}
