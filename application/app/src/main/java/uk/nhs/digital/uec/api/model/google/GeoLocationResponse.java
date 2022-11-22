package uk.nhs.digital.uec.api.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GeoLocationResponse {

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "results")
    private GeoLocationResponseResult[] geoLocationResponseResults;

}
