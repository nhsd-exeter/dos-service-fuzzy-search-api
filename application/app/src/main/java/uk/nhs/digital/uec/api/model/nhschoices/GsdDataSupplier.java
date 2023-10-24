package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsdDataSupplier {
  @JsonProperty("ProvidedBy")
  private String providedBy;

  @JsonProperty("ProvidedByImage")
  private String providedByImage;

  @JsonProperty("ProvidedByUrl")
  private String providedByUrl;

  @JsonProperty("ProvidedOn")
  private String providedOn;
}
