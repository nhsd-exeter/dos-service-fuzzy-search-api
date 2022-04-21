package uk.nhs.digital.uec.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.nhs.digital.uec.api.model.DosService;

/** Class to encapsulate testing service data. */
public class MockDosServicesUtil {

  private MockDosServicesUtil() {}

  public static Map<Integer, DosService> mockDosServices = new HashMap<>();

  static {
//    mockDosServices.put(1, buildMockService1("1"));
//    mockDosServices.put(2, buildMockService1("2"));
    addMockServices(20);
  }

  private static void addMockServices(int numberToAdd) {
    for (int i = mockDosServices.size() + 1; i <= numberToAdd; i++) {
      mockDosServices.put(i, buildMockService1(String.valueOf(i)));
    }
  }

  private static DosService buildMockService1(String identifier) {

    List<String> address = new ArrayList<>();
    address.add(identifier + " Service Street");
    address.add("Service town");
    address.add("Exmouth");

    List<String> referralRoles = new ArrayList<>();
    referralRoles.add("Role 1");
    referralRoles.add("Role 2");

    return new DosService.DosServiceBuilder()
        .id(Integer.valueOf(identifier))
        .uIdentifier(identifier)
        .name("service" + identifier)
        .publicName("Public Service Name " + identifier)
        .type("Type 1")
        .typeId(Integer.parseInt(identifier))
        .odsCode("odscode" + identifier)
        .capacityStatus("GREEN")
        .address(address)
        .postcode("EX7 8PR")
        .referralRoles(referralRoles)
        .build();
  }
}
