package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Geocode {
  @JsonProperty("type")
  private String type;
  @JsonProperty("coordinates")
  private List<Double> coordinates;
  @JsonProperty("crs")
  private Crs crs;
}
