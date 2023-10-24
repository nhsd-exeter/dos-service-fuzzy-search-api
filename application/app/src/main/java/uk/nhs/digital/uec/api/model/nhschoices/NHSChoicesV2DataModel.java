package uk.nhs.digital.uec.api.model.nhschoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NHSChoicesV2DataModel {

  @JsonProperty("@search.score")
  private double searchScore;
  @JsonProperty("SearchKey")
  private String searchKey;
  @JsonProperty("ODSCode")
  private String odsCode;
  @JsonProperty("OrganisationName")
  private String organisationName;
  @JsonProperty("OrganisationTypeId")
  private String organisationTypeId;
  @JsonProperty("OrganisationType")
  private String organisationType;
  @JsonProperty("OrganisationStatus")
  private String organisationStatus;
  @JsonProperty("SummaryText")
  private Object summaryText;
  @JsonProperty("URL")
  private String url;
  @JsonProperty("Address1")
  private String address1;
  @JsonProperty("Address2")
  private String address2;
  @JsonProperty("Address3")
  private String address3;
  @JsonProperty("City")
  private String city;
  @JsonProperty("County")
  private String county;
  @JsonProperty("Latitude")
  private double latitude;
  @JsonProperty("Longitude")
  private double longitude;
  @JsonProperty("Postcode")
  private String Postcode;
  @JsonProperty("Geocode")
  private Geocode Geocode;
  @JsonProperty("OrganisationSubType")
  private Object organisationSubType;
  @JsonProperty("OrganisationAliases")
  private List<Object> organisationAliases;
  @JsonProperty("ParentOrganisation")
  private Object parentOrganisation;
  @JsonProperty("Services")
  private List<Service> services;
  @JsonProperty("OpeningTimes")
  private List<OpeningTime> openingTimes;
  @JsonProperty("Contacts")
  private List<Contact> contacts;
  @JsonProperty("Facilities")
  private List<Object> facilities;
  @JsonProperty("Staff")
  private List<Object> staff;
  @JsonProperty("GSD")
  private GSD gsd;
  @JsonProperty("LastUpdatedDates")
  private Object lastUpdatedDates;
  @JsonProperty("AcceptingPatients")
  private Object AcceptingPatients;
  @JsonProperty("GPRegistration")
  private Object GPRegistration;
  @JsonProperty("CCG")
  private Object CCG;
  @JsonProperty("RelatedIAPTCCGs")
  private List<Object> relatedIAPTCCGs;
  @JsonProperty("CCGLocalAuthority")
  private List<Object> ccgLocalAuthority;
  @JsonProperty("Trusts")
  private List<Object> trusts;
  @JsonProperty("Metrics")
  private List<Object> metrics;
}
