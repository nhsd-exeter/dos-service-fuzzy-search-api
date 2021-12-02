package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.nhs.digital.uec.api.model.DosService;

/** Class to encapsulate testing service data. */
public class MockDosServicesUtil {

  private MockDosServicesUtil() {}

  public static final Map<Integer, DosService> mockDosServices = new HashMap<>();

  static {
    mockDosServices.put(1, buildMockService1(1));
    mockDosServices.put(2, buildMockService2());
    addMockServices(20);
  }

  public static void addMockServices(int numberToAdd) {
    for (int i = mockDosServices.size() + 1; i <= numberToAdd; i++) {
      mockDosServices.put(i, buildMockService1(i));
    }
  }

  private static DosService buildMockService1(int identifier) {

    List<String> address = new ArrayList<>();
    address.add(identifier + " Service Street");
    address.add("Service town");
    address.add("Exmouth");

    List<String> referralRoles = new ArrayList<>();
    referralRoles.add("Role 1");
    referralRoles.add("Role 2");

    return new DosService.DosServiceBuilder()
        .id(identifier)
        .uIdentifier(identifier)
        .name("service" + identifier)
        .publicName("Public Service Name " + identifier)
        .type("Type 1")
        .typeId(1)
        .odsCode("odscode" + identifier)
        .capacityStatus("GREEN")
        .address(address)
        .postcode("EX7 8PR")
        .referralRoles(referralRoles)
        .build();
  }

  private static DosService buildMockService2() {

    List<String> address2 = new ArrayList<>();
    address2.add("2 Service Street");
    address2.add("Service town");
    address2.add("Exmouth");

    List<String> referralRoles2 = new ArrayList<>();
    referralRoles2.add("Role 1");
    referralRoles2.add("Role 4");

    return new DosService.DosServiceBuilder()
        .id(2)
        .uIdentifier(23)
        .name("service2")
        .publicName("Public Service Name 2")
        .type("Type 2")
        .typeId(2)
        .odsCode("odscode2")
        .capacityStatus("AMBER")
        .address(address2)
        .postcode("EX7 8PR")
        .referralRoles(referralRoles2)
        .build();
  }
}
