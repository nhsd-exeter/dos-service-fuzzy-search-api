package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
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
  "u_identifier",
  "name",
  "public_name",
  "capacity_status",
  "type_id",
  "type",
  "ods_code",
  "address",
  "postcode",
  "referral_roles"
})
public class DosService {

  @JsonProperty("_score")
  private Float _score;

  @JsonProperty("id")
  @Id
  private int id;

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

  @JsonProperty("referral_roles")
  private List<String> referralRoles;

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
    this.referralRoles = builder.referralRoles;
  }

  public DosService() {}

  @Override
  public boolean equals(Object obj) {

    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    DosService that = (DosService) obj;

    if (this.id == that.id
        && this.name.equals(that.name)
        && this.odsCode.equals(that.odsCode)
        && this.address.containsAll(that.address)
        && this.address.size() == that.address.size()
        && this.capacityStatus.equals(that.capacityStatus)
        && this.postcode.equals(that.postcode)
        && this.publicName.equals(that.publicName)
        && this.referralRoles.size() == that.referralRoles.size()
        && this.referralRoles.containsAll(that.referralRoles)
        && this.type.equals(that.type)
        && this.typeId == that.typeId
        && this.uIdentifier == that.uIdentifier) {
      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {

    int hash = 7;
    hash = 31 * hash + this.id;
    hash = 31 * hash + this.typeId;
    hash = 31 * hash + this.uIdentifier;
    hash = 31 * hash + (null == this.address ? 0 : this.address.hashCode());
    hash = 31 * hash + (null == this.capacityStatus ? 0 : this.capacityStatus.hashCode());
    hash = 31 * hash + (null == this.name ? 0 : this.name.hashCode());
    hash = 31 * hash + (null == this.odsCode ? 0 : this.odsCode.hashCode());
    hash = 31 * hash + (null == this.postcode ? 0 : this.postcode.hashCode());
    hash = 31 * hash + (null == this.publicName ? 0 : this.publicName.hashCode());
    hash = 31 * hash + (null == this.referralRoles ? 0 : this.referralRoles.hashCode());
    hash = 31 * hash + (null == this.type ? 0 : this.type.hashCode());

    return hash;
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
    private List<String> referralRoles;

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

    public DosServiceBuilder referralRoles(List<String> referralRoles) {
      this.referralRoles = referralRoles;
      return this;
    }

    public DosService build() {
      return new DosService(this);
    }
  }
}
