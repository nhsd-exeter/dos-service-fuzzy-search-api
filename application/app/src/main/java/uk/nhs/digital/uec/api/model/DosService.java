package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.decimal4j.util.DoubleRounder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.Objects;

/**
 * Defines the structure and attributes that are returned for each service.
 */
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
public class DosService implements Comparable<DosService> {

  @JsonProperty("_score")
  private Float _score;

  @JsonProperty("id")
  @Id
  private int id;

  @JsonProperty("u_id")
  private int uid;

  @JsonProperty("u_identifier")
  private int uIdentifier;

  @JsonProperty("name")
  private String name;

  @JsonProperty("public_name")
  private String publicName;

  @JsonProperty("capacity_status")
  private String capacityStatus;

  @JsonProperty("type_id")
  private int typeId;

  @JsonProperty("type")
  private String type;

  @JsonProperty("ods_code")
  private String odsCode;

  @JsonProperty("address")
  private List<String> address;

  @JsonProperty("postcode")
  private String postcode;

  @JsonProperty("easting")
  private Integer easting;

  @JsonProperty("northing")
  private Integer northing;

  @JsonProperty("referral_roles")
  private List<String> referralRoles;

  @JsonProperty("distance_in_miles")
  private Double distance;

  @JsonProperty("public_phone_number")
  private int publicPhoneNumber;

  @JsonProperty("non_public_phone_number")
  private int nonPublicPhoneNumber;

  @JsonProperty("email")
  private String email;

  @JsonProperty("web")
  private String website;

  @JsonProperty("is_national")
  private String isNational;

  @JsonProperty("updated")
  private String updated;

  @JsonProperty("public_referral_instructions")
  private String publicReferralInstructions;

  private DosService(DosServiceBuilder builder) {
    this.id = builder.id;
    this.uIdentifier = builder.uIdentifier;
    this.name = builder.name;
    this.publicName = builder.publicName;
    this.capacityStatus = builder.capacityStatus;
    this.typeId = builder.typeId;
    this.type = builder.type;
    this.odsCode = builder.odsCode;
    this.address = builder.address;
    this.postcode = builder.postcode;
    this.easting = builder.easting;
    this.northing = builder.northing;
    this.referralRoles = builder.referralRoles;
    this.uid = builder.uid;
    this.publicReferralInstructions = builder.publicReferralInstructions;
    this.updated = builder.updated;
    this.isNational = builder.isNational;
    this.website = builder.website;
    this.email = builder.email;
    this.nonPublicPhoneNumber = builder.nonPublicPhoneNumber;
    this.publicPhoneNumber = builder.publicPhoneNumber;
  }

  public DosService() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DosService)) return false;
    DosService that = (DosService) o;
    return id == that.id && uid == that.uid && uIdentifier == that.uIdentifier && typeId == that.typeId && publicPhoneNumber == that.publicPhoneNumber && nonPublicPhoneNumber == that.nonPublicPhoneNumber && Objects.equals(_score, that._score) && Objects.equals(name, that.name) && Objects.equals(publicName, that.publicName) && Objects.equals(capacityStatus, that.capacityStatus) && Objects.equals(type, that.type) && Objects.equals(odsCode, that.odsCode) && Objects.equals(address, that.address) && Objects.equals(postcode, that.postcode) && Objects.equals(easting, that.easting) && Objects.equals(northing, that.northing) && Objects.equals(referralRoles, that.referralRoles) && Objects.equals(distance, that.distance) && Objects.equals(email, that.email) && Objects.equals(website, that.website) && Objects.equals(isNational, that.isNational) && Objects.equals(updated, that.updated) && Objects.equals(publicReferralInstructions, that.publicReferralInstructions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_score, id, uid, uIdentifier, name, publicName, capacityStatus, typeId, type, odsCode, address, postcode, easting, northing, referralRoles, distance, publicPhoneNumber, nonPublicPhoneNumber, email, website, isNational, updated, publicReferralInstructions);
  }

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

  public static class DosServiceBuilder {

    private int id;
    private int uIdentifier;
    private String name;
    private String publicName;
    private String capacityStatus;
    private int typeId;
    private String type;
    private String odsCode;
    private List<String> address;
    private String postcode;
    private Integer easting;
    private Integer northing;
    private List<String> referralRoles;

    private int uid;
    private String publicReferralInstructions;
    private String updated;
    private String isNational;
    private String website;
    private String email;
    private int nonPublicPhoneNumber;
    private int publicPhoneNumber;

    public DosServiceBuilder updated(String updated) {
      this.updated = updated;
      return this;
    }

    public DosServiceBuilder isNational(String isNational) {
      this.isNational = isNational;
      return this;
    }
    public DosServiceBuilder website(String website) {
      this.website = website;
      return this;
    }
    public DosServiceBuilder email(String email) {
      this.email = email;
      return this;
    }

    public DosServiceBuilder nonPublicPhoneNumber(int nonPublicPhoneNumber) {
      this.nonPublicPhoneNumber = nonPublicPhoneNumber;
      return this;
    }

    public DosServiceBuilder publicPhoneNumber(int publicPhoneNumber) {
      this.publicPhoneNumber = publicPhoneNumber;
      return this;
    }
    public DosServiceBuilder uid(int uid) {
      this.uid = uid;
      return this;
    }

    public DosServiceBuilder publicReferralInstructions(String publicReferralInstructions) {
      this.publicReferralInstructions = publicReferralInstructions;
      return this;
    }

    public DosServiceBuilder id(int id) {
      this.id = id;
      return this;
    }

    public DosServiceBuilder uIdentifier(int uIdentifier) {
      this.uIdentifier = uIdentifier;
      return this;
    }

    public DosServiceBuilder name(String name) {
      this.name = name;
      return this;
    }

    public DosServiceBuilder publicName(String publicName) {
      this.publicName = publicName;
      return this;
    }

    public DosServiceBuilder capacityStatus(String capacityStatus) {
      this.capacityStatus = capacityStatus;
      return this;
    }

    public DosServiceBuilder typeId(int typeId) {
      this.typeId = typeId;
      return this;
    }

    public DosServiceBuilder type(String type) {
      this.type = type;
      return this;
    }

    public DosServiceBuilder odsCode(String odsCode) {
      this.odsCode = odsCode;
      return this;
    }

    public DosServiceBuilder address(List<String> address) {
      this.address = address;
      return this;
    }

    public DosServiceBuilder postcode(String postcode) {
      this.postcode = postcode;
      return this;
    }

    public DosServiceBuilder easting(Integer easting) {
      this.easting = easting;
      return this;
    }

    public DosServiceBuilder northing(Integer northing) {
      this.northing = northing;
      return this;
    }

    public DosServiceBuilder referralRoles(List<String> referralRoles) {
      this.referralRoles = referralRoles;
      return this;
    }

    public DosService build() {
      return new DosService(this);
    }
  }
}
