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

    return DosService.builder()
        .id(Integer.valueOf(identifier))
        .u_id(Integer.valueOf(identifier))
        .name("service" + identifier)
        .public_name("Public Service Name " + identifier)
        .type("Type 1")
        .type_id(Integer.parseInt(identifier))
        .ods_code("odscode" + identifier)
        .capacity_status("GREEN")
        .address(address)
        .postcode("EX7 8PR")
        .referral_roles(referralRoles)
        .build();
  }
}
