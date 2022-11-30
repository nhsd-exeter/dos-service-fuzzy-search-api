package uk.nhs.digital.uec.api.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Geometry {
    @JsonProperty("location")
    private Location location;
    @JsonProperty("location_type")
    private String location_type;
}
