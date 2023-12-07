package uk.nhs.digital.uec.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/** Defines the structure and attributes that are returned for each service. */
@Document(indexName = "service")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DosService {

  private int _score; // NOSONAR

  @Id private int id;

  @Field(name = "u_id")
  private int uid;

  private String name;

  @Field(name = "public_name")
  private String publicName;

  @Field(name = "capacity_status")
  private String capacityStatus;

  @Field(name = "type_id")
  private int typeId;

  private String type;

  private String odsCode;

  private List<String> address;

  private String postcode;

  private Integer easting;

  private Integer northing;

  @Field(name = "referral_roles")
  private List<String> referralRoles;

  private Double distance;

  @Field(name = "public_phone_number")
  private String publicPhoneNumber;

  @Field(name = "non_public_phone_number")
  private String nonPublicPhoneNumber;

  private String email;

  private String web;

  @Field(name = "public_referral_instructions")
  private String publicReferralInstructions;

  @Field(name = "referral_instructions")
  private String referralInstructions;

  @Field(name = "is_national")
  private String isNational;

  private String updated;

  private String openingtimedays;

  private String openingtime;

  private String closingtime;

  private String specifieddates;

  private String specificopentimes;

  private String specificendtimes;

  @Field(name = "professional_referral_info")
  private String professionalReferralInfo;

  private GeoPoint location;

  private String datasource;

  public Double getDistance() {
    if (this.distance == null) {
      return Double.valueOf(999.9);
    }
    return this.distance;
  }
}
