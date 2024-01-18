package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentOrganisation{
  @JsonProperty("ODSCode")
  public String oDSCode;
  @JsonProperty("OrganisationName")
  public String organisationName;
}
