package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import uk.nhs.digital.uec.api.util.DoubleSerializerUtil;

/** Defines the structure and attributes that are returned for each service. */
@Document(indexName = "service")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DosService {

  private int _score; // NOSONAR

  @Id private String id;

  @Field(name = "u_id")
  private String u_id; // NOSONAR

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

  @JsonSerialize(using = DoubleSerializerUtil.class)
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

  @Field(name = "professional_referral_info")
  private String professional_referral_info;

  private GeoPoint location;

  private String datasource;

  public Double getDistance() {
    if (this.distance == null) {
      return Double.valueOf(999.9);
    }
    return this.distance;
  }
}
