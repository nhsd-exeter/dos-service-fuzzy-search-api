package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility{
  @JsonProperty("Id")
  public int id;
  @JsonProperty("Name")
  public String name;
  @JsonProperty("Value")
  public String value;
  @JsonProperty("FacilityGroupName")
  public String facilityGroupName;
}
