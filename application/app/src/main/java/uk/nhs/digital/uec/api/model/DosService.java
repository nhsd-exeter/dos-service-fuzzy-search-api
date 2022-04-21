package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.decimal4j.util.DoubleRounder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/** Defines the structure and attributes that are returned for each service. */
@Document(indexName = "service")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "_score",
  "id",
  "u_id",
  "u_identifier",
  "name",
  "public_name",
  "distance_in_miles",
  "capacity_status",
  "type_id",
  "type",
  "ods_code",
  "address",
  "postcode",
  "easting",
  "northing",
  "referral_roles",
  "public_referral_instructions",
  "public_phone_number",
  "non_public_phone_number",
  "email",
  "web",
  "is_national",
  "updated"
})
@AllArgsConstructor
@EqualsAndHashCode
@SuppressWarnings("squid:S00112")
@Builder
@NoArgsConstructor
public class DosService implements Comparable<DosService> {

  @JsonProperty("_score")
  private int _score; // NOSONAR

  @JsonProperty("id")
  @Id
  private int id;

  @JsonProperty("u_id")
  private int u_id; // NOSONAR

  @JsonProperty("name")
  private String name;

  @JsonProperty("public_name")
  private String public_name; // NOSONAR

  @JsonProperty("capacity_status")
  private String capacity_status; // NOSONAR

  @JsonProperty("type_id")
  private int type_id; // NOSONAR

  @JsonProperty("type")
  private String type;

  @JsonProperty("ods_code")
  private String ods_code; // NOSONAR

  @JsonProperty("address")
  private List<String> address;

  @JsonProperty("postcode")
  private String postcode;

  @JsonProperty("easting")
  private Integer easting;

  @JsonProperty("northing")
  private Integer northing;

  @JsonProperty("referral_roles")
  private List<String> referral_roles; // NOSONAR

  @JsonProperty("distance_in_miles")
  private Double distance;

  @JsonProperty("public_phone_number")
  private String public_phone_number;

  @JsonProperty("non_public_phone_number")
  private String non_public_phone_number; // NOSONAR

  @JsonProperty("email")
  private String email;

  @JsonProperty("web")
  private String web;

  @JsonProperty("public_referral_instructions")
  private String public_referral_instructions;

  @JsonProperty("referral_instructions")
  private String referral_instructions;

  @JsonProperty("is_national")
  private String is_national;

  @JsonProperty("updated")
  private String updated;

  public Double getDistance() {
    if (this.distance == null) {
      return Double.valueOf(999.9);
    }
    return DoubleRounder.round(this.distance, 1);
  }

  @Override
  public int compareTo(DosService ds) {
    return this.getDistance().compareTo(ds.getDistance());
  }
}
