package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NHSChoicesResponse {

  @JsonProperty("@data.context")
  private String context;
  @JsonProperty("@data.nextLink")
  private String nextLink;

  @JsonProperty("value")
  private List<NHSChoicesV2DataModel> value;
}
