package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;


/**
 * Defines the structure and attributes that are returned for each service.
 */
@Document(indexName = "service")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DosService implements Comparable<DosService> {

  private int _score; // NOSONAR

  @Id
  private int id;

  @Field(name = "u_id")
  private int u_id; // NOSONAR

  private String name;

  @Field(name = "public_name")
  private String public_name; // NOSONAR

  @Field(name = "capacity_status")
  private String capacity_status; // NOSONAR

  @Field(name = "type_id")
  private int type_id; // NOSONAR

  private String type;

  private String ods_code; // NOSONAR

  private List<String> address;

  private String postcode;

  private Integer easting;

  private Integer northing;

  @Field(name = "referral_roles")
  private List<String> referral_roles; // NOSONAR

  private Double distance;

  @Field(name = "public_phone_number")
  private String public_phone_number;

  @Field(name = "non_public_phone_number")
  private String non_public_phone_number; // NOSONAR

  private String email;

  private String web;

  @Field(name = "public_referral_instructions")
  private String public_referral_instructions;

  @Field(name = "referral_instructions")
  private String referral_instructions;

  @Field(name = "is_national")
  private String is_national;

  private String updated;

  private String openingtimedays;

  private String openingtime;

  private String closingtime;

  private String specifieddates;

  private String specificopentimes;

  private String specificendtimes;

  private GeoPoint location;

  public Double getDistance() {
    if (this.distance == null) {
      return Double.valueOf(999.9);
    }
    return this.distance;
  }

  @Override
  public int compareTo(DosService ds) {
    return this.getDistance().compareTo(ds.getDistance());
  }
}
