package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsdMetrics {
  @JsonProperty("ElementTitle")
  private String elementTitle;

  @JsonProperty("ElementText")
  private String elementText;

  @JsonProperty("ElementOrder")
  private int elementOrder;

  @JsonProperty("MetricId")
  private String metricId;
}
