package uk.nhs.digital.uec.api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents the credentials supplied by a user during login and registration */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {

  private String emailAddress;

  private String password;
}
