package uk.nhs.digital.uec.api.model.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.uec.api.model.nhschoices.*;

import static org.junit.Assert.*;

public class NhsChoicesResponseTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testMapping() throws JsonProcessingException {
    String json = """
{
  "@data.context": "context",
  "@data.nextLink": "nextlink",
  "value": [
    {
      "@search.score": 1.23,
      "SearchKey": "ExampleKey",
      "ODSCode": "ODS123",
      "OrganisationName": "Example Organisation",
      "OrganisationTypeId": "1",
      "OrganisationType": "Type A",
      "OrganisationStatus": "Active",
      "SummaryText": "This is a summary.",
      "URL": "http://www.example.org",
      "Address1": "123 Example Street",
      "Address2": "Example District",
      "Address3": "Example Region",
      "City": "Example City",
      "County": "Example County",
      "Latitude": 51.5074,
      "Longitude": -0.1278,
      "Postcode": "EX4 MPL",
      "Geocode": {
        "type": "Point",
        "coordinates": [
          51.5074,
          -0.1278
        ],
        "crs": {
          "type": "name",
          "properties": {
            "name": "EPSG:4326"
          }
        }
      },
      "OrganisationSubType": "Subtype A",
      "OrganisationAliases": [
        "Alias A",
        "Alias B"
      ],
      "ParentOrganisation": {
        "ODSCode": "ODS456",
        "OrganisationName": "Parent Organisation"
      },
      "Services": [
        {
          "ServiceName": "General Practice",
          "ServiceCode": "GP123",
          "ServiceDescription": "General medical consultations and treatments.",
          "Contacts": [
            {
              "ContactType": "Phone",
              "ContactAvailabilityType": "24/7",
              "ContactMethodType": "Call",
              "ContactValue": "+441234567890"
            }
          ],
          "ServiceProvider": {
            "ODSCode": "ODS789",
            "OrganisationName": "Example Health Centre"
          },
          "Treatments": [
            "Routine check-up",
            "Vaccination"
          ],
          "OpeningTimes": [
            {
              "Weekday": "Monday",
              "OpeningTime": "09:00",
              "ClosingTime": "17:00",
              "Times": "09:00-17:00",
              "OffsetOpeningTime": 900,
              "OffsetClosingTime": 1700,
              "OpeningTimeType": "Standard",
              "AdditionalOpeningDate": "2023-01-01",
              "IsOpen": true,
              "FromAgeDays": "0",
              "ToAgeDays": "36500"
            }
          ],
          "AgeRange": [
            "All ages"
          ],
          "Metrics": [
            "Patient satisfaction: 95%"
          ],
          "KeyValueData": [
            "Parking: Yes"
          ]
        }
      ],
      "OpeningTimes": [
        {
          "Weekday": "Monday",
          "OpeningTime": "09:00",
          "ClosingTime": "17:00",
          "Times": "09:00-17:00",
          "OffsetOpeningTime": 900,
          "OffsetClosingTime": 1700,
          "OpeningTimeType": "Standard",
          "AdditionalOpeningDate": "2023-01-01",
          "IsOpen": true,
          "FromAgeDays": "0",
          "ToAgeDays": "36500"
        }
      ],
      "Contacts": [
        {
          "ContactType": "Phone",
          "ContactAvailabilityType": "24/7",
          "ContactMethodType": "Call",
          "ContactValue": "+441234567890"
        }
      ],
      "Facilities": [
        {
          "Id": 1,
          "Name": "Wheelchair Access",
          "Value": "Available",
          "FacilityGroupName": "Accessibility"
        }
      ],
      "Staff": [
        "Staff"
      ],
      "GSD": {
        "Metrics": [
          {
            "ElementTitle": "Patient Satisfaction",
            "ElementText": "95% satisfaction rate from patient feedback.",
            "ElementOrder": 1,
            "MetricId": "satisfaction_2023"
          },
          {
            "ElementTitle": "Average Waiting Time",
            "ElementText": "Average waiting time is 15 minutes.",
            "ElementOrder": 2,
            "MetricId": "waiting_time_2023"
          }
        ],
        "DataSupplier": [
          {
            "ProvidedBy": "National Health Service",
            "ProvidedByImage": "http://example.com/images/nhs_logo.png",
            "ProvidedByUrl": "http://www.nhs.uk",
            "ProvidedOn": "2023-01-01"
          }
        ],
        "GsdServices": [
          {
            "ServiceId": "GP123",
            "ServiceName": "General Practice"
          },
          {
            "ServiceId": "DEN456",
            "ServiceName": "Dental Care"
          }
        ]
      },
      "LastUpdatedDates": "2023-01-01",
      "AcceptingPatients": "Accepting patients",
      "GPRegistration": "GP Registration",
      "CCG": "South West CCG",
      "RelatedIAPTCCGs": [
        "South West CCG"
      ],
      "CCGLocalAuthority": [
        "South West CCG"
      ],
      "Trusts": [
        "Example Trust"
      ],
      "Metrics": [
        "Patient satisfaction: 95%"
      ]
    }
  ]
}
""";

    NHSChoicesResponse response = objectMapper.readValue(json, NHSChoicesResponse.class);
    assertEquals("context", response.getContext());
    assertEquals("nextlink", response.getNextLink());

    NHSChoicesV2DataModel model = response.getValue().get(0);
    assertEquals(1.23, model.getSearchScore(), 0.1);
    assertEquals("ExampleKey", model.getSearchKey());
    assertEquals("ODS123", model.getOdsCode());
    assertEquals("Example Organisation", model.getOrganisationName());
    assertEquals("1", model.getOrganisationTypeId());
    assertEquals("Type A", model.getOrganisationType());
    assertEquals("Active", model.getOrganisationStatus());
    assertEquals("This is a summary.", model.getSummaryText());
    assertEquals("http://www.example.org", model.getUrl());
    assertEquals("123 Example Street", model.getAddress1());
    assertEquals("Example District", model.getAddress2());
    assertEquals("Example Region", model.getAddress3());
    assertEquals("Example City", model.getCity());
    assertEquals("Example County", model.getCounty());
    assertEquals(51.5074, model.getLatitude(), 0.1);
    assertEquals(-0.1278, model.getLongitude(), 0.1);
    assertEquals("EX4 MPL", model.getPostcode());

    assertEquals("Subtype A", model.getOrganisationSubType());
    assertFalse(model.getOrganisationAliases().isEmpty());
    assertEquals("Alias A", model.getOrganisationAliases().get(0));
    assertEquals("Alias B", model.getOrganisationAliases().get(1));
    assertNotNull(model.getParentOrganisation());
    assertEquals("ODS456", model.getParentOrganisation().getODSCode());
    assertEquals("Parent Organisation", model.getParentOrganisation().getOrganisationName());

    assertEquals("2023-01-01", model.getLastUpdatedDates());
    assertEquals("Accepting patients", model.getAcceptingPatients());
    assertEquals("GP Registration", model.getGPRegistration());
    assertEquals("South West CCG", model.getCCG());
    assertEquals(1, model.getRelatedIAPTCCGs().size());
    assertEquals("South West CCG", model.getRelatedIAPTCCGs().get(0));
    assertEquals(1, model.getCcgLocalAuthority().size());
    assertEquals("South West CCG", model.getCcgLocalAuthority().get(0));
    assertEquals(1, model.getTrusts().size());
    assertEquals("Example Trust", model.getTrusts().get(0));
    assertEquals(1, model.getMetrics().size());
    assertEquals("Patient satisfaction: 95%", model.getMetrics().get(0));

    // Geocode
    assertNotNull(model.getGeocode());
    assertEquals("Point", model.getGeocode().getType());
    assertEquals(51.5074, model.getGeocode().getCoordinates().get(0), 0.1);
    assertEquals(-0.1278, model.getGeocode().getCoordinates().get(1), 0.1);
    assertNotNull(model.getGeocode().getCrs());
    assertEquals("name", model.getGeocode().getCrs().getType());
    assertEquals("EPSG:4326", model.getGeocode().getCrs().getProperties().getName());

    // Services
    assertEquals(1, model.getServices().size());
    Service service = model.getServices().get(0);
    assertEquals("General Practice", service.getServiceName());
    assertEquals("GP123", service.getServiceCode());
    assertEquals("General medical consultations and treatments.", service.getServiceDescription());
    assertEquals(2, service.getTreatments().size());
    assertEquals("Routine check-up", service.getTreatments().get(0));
    assertEquals("Vaccination", service.getTreatments().get(1));
    assertEquals(1, service.getAgeRange().size());
    assertEquals("All ages", service.getAgeRange().get(0));
    assertEquals(1, service.getMetrics().size());
    assertEquals("Patient satisfaction: 95%", service.getMetrics().get(0));
    assertEquals(1, service.getKeyValueData().size());
    assertEquals("Parking: Yes", service.getKeyValueData().get(0));

    // Contacts within service
    assertEquals(1, service.getContacts().size());
    Contact contact = service.getContacts().get(0);
    assertEquals("Phone", contact.getContactType());
    assertEquals("24/7", contact.getContactAvailabilityType());
    assertEquals("Call", contact.getContactMethodType());
    assertEquals("+441234567890", contact.getContactValue());

    // ServiceProvider within service
    ServiceProvider serviceProvider = service.getServiceProvider();
    assertEquals("ODS789", serviceProvider.getOdsCode());
    assertEquals("Example Health Centre", serviceProvider.getOrganisationName());

    // OpeningTimes within service
    assertEquals(1, service.getOpeningTimes().size());
    OpeningTime openingTime = service.getOpeningTimes().get(0);
    assertEquals("Monday", openingTime.getWeekday());
    assertEquals("09:00", openingTime.getOpeningTime());
    assertEquals("17:00", openingTime.getClosingTime());
    assertEquals("09:00-17:00", openingTime.getTimes());
    assertEquals(900, openingTime.getOffsetOpeningTime());
    assertEquals(1700, openingTime.getOffsetClosingTime());
    assertEquals("Standard", openingTime.getOpeningTimeType());
    assertEquals("2023-01-01", openingTime.getAdditionalOpeningDate());
    assertTrue(openingTime.isOpen());
    assertEquals("0", openingTime.getFromAgeDays());
    assertEquals("36500", openingTime.getToAgeDays());

    // Facilities
    assertEquals(1, model.getFacilities().size());
    Facility facility = model.getFacilities().get(0);
    assertEquals(1, facility.getId());
    assertEquals("Wheelchair Access", facility.getName());
    assertEquals("Available", facility.getValue());
    assertEquals("Accessibility", facility.getFacilityGroupName());

    // GSD
    assertNotNull(model.getGsd());
    assertEquals(2, model.getGsd().getMetrics().size());
    GsdMetrics metric = model.getGsd().getMetrics().get(0);
    assertEquals("Patient Satisfaction", metric.getElementTitle());
    assertEquals("95% satisfaction rate from patient feedback.", metric.getElementText());
    assertEquals(1, metric.getElementOrder());
    assertEquals("satisfaction_2023", metric.getMetricId());

    // DataSupplier within GSD
    assertEquals(1, model.getGsd().getDataSupplier().size());
    GsdDataSupplier dataSupplier = model.getGsd().getDataSupplier().get(0);
    assertEquals("National Health Service", dataSupplier.getProvidedBy());
    assertEquals("http://example.com/images/nhs_logo.png", dataSupplier.getProvidedByImage());
    assertEquals("http://www.nhs.uk", dataSupplier.getProvidedByUrl());
    assertEquals("2023-01-01", dataSupplier.getProvidedOn());

    // GsdServices within GSD
    assertEquals(2, model.getGsd().getGsdServices().size());
    GsdService gsdService = model.getGsd().getGsdServices().get(0);
    assertEquals("GP123", gsdService.getServiceId());
    assertEquals("General Practice", gsdService.getServiceName());


  }

}
