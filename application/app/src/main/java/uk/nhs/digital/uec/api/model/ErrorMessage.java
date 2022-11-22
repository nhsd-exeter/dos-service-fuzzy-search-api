package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * This class is used to wrap the error response which will be sent as API response
 */
@Getter
@AllArgsConstructor
public class ErrorMessage {

    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("message")
    private String message;

}
