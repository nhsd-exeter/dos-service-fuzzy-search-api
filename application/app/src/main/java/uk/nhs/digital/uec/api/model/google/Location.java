package uk.nhs.digital.uec.api.model.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Location {
    @JsonProperty("lat")
    public double lat;
    @JsonProperty("lng")
    public double lng;
}
