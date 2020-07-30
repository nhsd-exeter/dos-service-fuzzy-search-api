package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Defines the structure and attributes that are returned for each service. */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "u_identifier",
  "name",
  "public_name",
  "capacity_status",
  "type_id",
  "type",
  "address",
  "postcode",
  "referral_roles"
})
public class DosService {

  @JsonProperty("id")
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

  @JsonProperty("address")
  private List<String> address;

  @JsonProperty("postcode")
  private String postcode;

  @JsonProperty("referral_roles")
  private List<String> referralRoles;
}
