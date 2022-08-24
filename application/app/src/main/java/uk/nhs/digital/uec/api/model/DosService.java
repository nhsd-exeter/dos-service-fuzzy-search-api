package uk.nhs.digital.uec.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.decimal4j.util.DoubleRounder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/** Defines the structure and attributes that are returned for each service. */
@Document(indexName = "service")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DosService implements Comparable<DosService> {

  private int _score; // NOSONAR

  @Id
  private int id;

  private int u_id; // NOSONAR

  private String name;

  private String public_name; // NOSONAR

  private String capacity_status; // NOSONAR

  private int type_id; // NOSONAR

  private String type;

  private String ods_code; // NOSONAR

  private List<String> address;

  private String postcode;

  private Integer easting;

  private Integer northing;

  private List<String> referral_roles; // NOSONAR

  private Double distance;

  private String public_phone_number;

  private String non_public_phone_number; // NOSONAR

  private String email;

  private String web;

  private String public_referral_instructions;

  private String referral_instructions;

  private String is_national;

  private String updated;

  private String openingtimedays;

  private String openingtime;

  private String closingtime;

  private List<String> specifiedopeningdates;

  private List<String> specifiedopeningtimes;

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
