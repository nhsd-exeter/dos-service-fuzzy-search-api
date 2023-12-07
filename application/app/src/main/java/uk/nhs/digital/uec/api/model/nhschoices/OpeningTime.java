package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpeningTime {
  @JsonProperty("Weekday")
  private String weekday;

  @JsonProperty("OpeningTime")
  private String openingTime;

  @JsonProperty("ClosingTime")
  private String closingTime;

  @JsonProperty("Times")
  private Object times;

  @JsonProperty("OffsetOpeningTime")
  private int offsetOpeningTime;

  @JsonProperty("OffsetClosingTime")
  private int offsetClosingTime;

  @JsonProperty("OpeningTimeType")
  private String openingTimeType;

  @JsonProperty("AdditionalOpeningDate")
  private String additionalOpeningDate;

  @JsonProperty("IsOpen")
  private boolean isOpen;

  @JsonProperty("FromAgeDays")
  private String FromAgeDays;

  @JsonProperty("ToAgeDays")
  private String ToAgeDays;
}

