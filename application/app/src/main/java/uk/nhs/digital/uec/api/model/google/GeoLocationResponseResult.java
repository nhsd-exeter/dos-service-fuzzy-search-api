package uk.nhs.digital.uec.api.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GeoLocationResponseResult {
    @JsonProperty(value = "formatted_address")
    String formattedAddress;

    @JsonProperty("place_id")
    String placeId;

    @JsonProperty("geometry")
    Geometry geometry;

}
