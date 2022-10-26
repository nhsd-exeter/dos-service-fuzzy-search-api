package uk.nhs.digital.uec.api.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostcodeLocation {

  private String postcode;

  private Integer easting;

  private Integer northing;

  private String name;

  private String region;

  private String subRegion;

  private String ccg;

  private String organisationCode;

  private String nhs_region;

  private String icb;

  private String email;
}
