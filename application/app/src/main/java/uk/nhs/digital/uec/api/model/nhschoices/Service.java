package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {
  @JsonProperty("ServiceName")
  private String serviceName;

  @JsonProperty("ServiceCode")
  private String serviceCode;

  @JsonProperty("ServiceDescription")
  private String serviceDescription;

  @JsonProperty("Contacts")
  private List<Contact> contacts;

  @JsonProperty("ServiceProvider")
  private ServiceProvider serviceProvider;

  @JsonProperty("Treatments")
  private List<Object> treatments;

  @JsonProperty("OpeningTimes")
  private List<OpeningTime> openingTimes;

  @JsonProperty("AgeRange")
  private List<Object> ageRange;

  @JsonProperty("Metrics")
  private List<Object> metrics;

  @JsonProperty("KeyValueData")
  private List<Object> keyValueData;
}
