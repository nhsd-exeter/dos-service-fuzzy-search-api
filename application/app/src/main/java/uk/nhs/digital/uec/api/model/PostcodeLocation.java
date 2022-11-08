package uk.nhs.digital.uec.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostcodeLocation {

  private String postcode;

  private Integer easting;

  private Integer northing;
}
