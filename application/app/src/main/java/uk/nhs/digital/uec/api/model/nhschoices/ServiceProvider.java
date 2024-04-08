package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProvider {
  @JsonProperty("ODSCode")
  private String odsCode;

  @JsonProperty("OrganisationName")
  private String organisationName;
}
