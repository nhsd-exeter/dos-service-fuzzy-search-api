package uk.nhs.digital.uec.api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.nhs.digital.uec.api.model.DosService;

/** Class to encapsulate testing service data. */
public class TestDosServicesUtil {

  public static Map<Integer, DosService> mockDosServices = new HashMap<>();

  static {
    // Mock service 1
    DosService dosService = new DosService();
    dosService.setId(1);
    dosService.setUIdentifier(1);
    dosService.setName("service1");
    dosService.setPublicName("Public Service Name 1");
    dosService.setType("Type 1");
    dosService.setTypeId(1);
    dosService.setOdsCode("odscode1");
    dosService.setCapacityStatus("GREEN");

    List<String> address = new ArrayList<>();
    address.add("1 Service Street");
    address.add("Service town");
    address.add("Exmouth");
    dosService.setAddress(address);

    dosService.setPostcode("EX7 8PR");

    List<String> referralRoles = new ArrayList<>();
    referralRoles.add("Role 1");
    referralRoles.add("Role 2");
    dosService.setReferralRoles(referralRoles);

    // Mock service 2
    DosService dosService2 = new DosService();
    dosService2.setId(2);
    dosService2.setUIdentifier(23);
    dosService2.setName("service2");
    dosService2.setPublicName("Public Service Name 2");
    dosService2.setType("Type 2");
    dosService2.setTypeId(2);
    dosService2.setOdsCode("odscode2");
    dosService2.setCapacityStatus("AMBER");

    List<String> address2 = new ArrayList<>();
    address2.add("2 Service Street");
    address2.add("Service town");
    address2.add("Exmouth");
    dosService2.setAddress(address2);
    dosService2.setPostcode("EX7 8PR");

    List<String> referralRoles2 = new ArrayList<>();
    referralRoles2.add("Role 1");
    referralRoles2.add("Role 4");
    dosService2.setReferralRoles(referralRoles2);

    mockDosServices.put(1, dosService);
    mockDosServices.put(2, dosService2);
  }
}
