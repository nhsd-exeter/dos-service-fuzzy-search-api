package uk.nhs.digital.uec.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.service.FuzzyServiceSearchServiceInterface;

@Profile("prod")
@Service
public class FuzzyServiceSearchService implements FuzzyServiceSearchServiceInterface {

  public List<DosService> retrieveServicesByFuzzySearch(final List<String> searchCriteria) {

    List<DosService> dosServices = new ArrayList<>();

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

    dosServices.add(dosService);

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

    dosServices.add(dosService2);

    return dosServices;
  }
}
