package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Credential{

  private String emailAddress;
  private String password;

}
