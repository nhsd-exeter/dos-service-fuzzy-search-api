package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
  @JsonProperty("ContactType")
  private String contactType;

  @JsonProperty("ContactAvailabilityType")
  private String contactAvailabilityType;

  @JsonProperty("ContactMethodType")
  private String contactMethodType;

  @JsonProperty("ContactValue")
  private String contactValue;
}
