package uk.nhs.digital.uec.api.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBTable(tableName = "service-finder-nonprod-postcode-location-mapping")
public class PostcodeLocation {

  @DynamoDBHashKey
  @DynamoDBAttribute(attributeName = "postcode")
  private String postcode;

  @DynamoDBAttribute(attributeName = "easting")
  private Integer easting;

  @DynamoDBAttribute(attributeName = "northing")
  private Integer northing;
}
