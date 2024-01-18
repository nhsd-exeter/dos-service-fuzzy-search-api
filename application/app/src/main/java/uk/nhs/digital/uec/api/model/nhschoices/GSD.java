package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GSD {
  @JsonProperty("Metrics")
  private List<GsdMetrics> metrics;

  @JsonProperty("DataSupplier")
  private List<GsdDataSupplier> dataSupplier;

  @JsonProperty("GsdServices")
  private List<GsdService> gsdServices;
}
