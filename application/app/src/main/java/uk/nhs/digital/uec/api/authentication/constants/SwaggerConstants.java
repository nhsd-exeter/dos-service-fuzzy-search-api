package uk.nhs.digital.uec.api.authentication.constants;

public class SwaggerConstants {

    private SwaggerConstants() {
    }

    public static final String SEARCH_CRITERIA_DESC = "Mandatory parameter representing any combination of name, public name, address or postcode of the service to search";

    public static final String SEARCH_POSTCODE_DESC = "Optional parameter representing the postcode of the location to search from. If provided the API will return a point to point distance of the services returned";

    public static final String MAX_NUM_ES_SERVICES_DESC = "Optional parameter that defines the maximum number of services from ES, that needs to be returned as response.";

    public static final String MAX_NUM_SERVICES_DESC = "Optional parameter that defines the maximum number of services that needs to be returned as response";

    public static final String FUZZ_LEVEL_DESC = "Optional parameter which represents the degree of fuzziness logic to apply to the search term(s). Values between 0..2.";

    public static final String NAME_PRIORITY_DESC = "Optional parameter that represents the priority to give the matching of the service name search term. Values between 0..100. A higher value will give more priority to the API matching on the name of the service";

    public static final String ADDRESS_PRIORITY_DESC = "Optional parameter that represents the priority to give the matching of the service address search term. Values between 0..100. A higher value will give more priority to the API matching on the address of the service";

    public static final String POSTCODE_PRIORITY_DESC = "Optional parameter that represents the priority to give the matching of the service postcode search term. Values between 0..100. A higher value will give more priority to the API matching on the postcode of the service";

    public static final String PUBLIC_NAME_PRIORITY_DESC = "Optional parameter that represents the priority to give the matching of the service public name search term. Values between 0..100. A higher value will give more priority to the API matching on the public name of the service";

    public static final String SEARCH_LATITUDE_DESC = "Optional parameter representing the latitude of the location to search from. If provided the API will return a point to point distance of the services returned";

    public static final String SEARCH_LONGITUDE_DESC = "Optional parameter representing the longitude of the location to search from. If provided the API will return a point to point distance of the services returned";

    public static final String DISTANCE_RANGE_DESC = "Optional parameter representing the distance range (e.g 20mi) of the current location to search from. If provided the API will return a point to point distance of the services returned";

    public static final String REFERRAL_ROLE_DESC = "Optional parameter representing the referral role. If provided the API will filter the data based the referral role";

}
