include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
PROFILE := local
SPRING_PROFILES_ACTIVE := $(PROFILE), mock-auth
CERTIFICATE_DOMAIN := localhost
ALLOWED_ORIGINS := *

# Datastore URLs
ELASTICSEARCH_URL := http://elasticsearch.sfs.local:9200
API_SERVICE_SEARCH_ENDPOINT := https://localhost:8443/dosapi/dosservices/v2/services/byfuzzysearch
# API_SERVICE_SEARCH_ENDPOINT := https://localhost:8443/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.3

# Validation parameters
MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 20

# Search parameters
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH := 100
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS := 50
MAX_NUM_SERVICES_TO_RETURN := 5
FUZZ_LEVEL := AUTO
NAME_PRIORITY := 100
ADDRESS_PRIORITY := 75
POSTCODE_PRIORITY := 75
NAME_PUBLIC_PRIORITY := 100

# Service Data files
SERVICE_DATA_FILE := create_all_services_local.sh
LOCATIONS_DATA_FILE := load_locations_test.sh

COGNITO_JWT_VERIFICATION_URL := http://testJwtVerificationUrl
COGNITO_USER_POOL_CLIENT_ID := testUserPoolClientId
COGNITO_USER_POOL_CLIENT_SECRET := testUserPoolClientSecret
COGNITO_USER_POOL_ID := testUserPoolId

POSTCODE_MAPPING_SERVICE_URL := http://mock.postcodeapi.sfs:8080/api

AUTH_LOGIN_URL := https://fuzzysearch.sfs.local:8443
AUTH_LOGIN_URI := /authentication/login
POSTCODE_MAPPING_USER := admin@nhs.net
POSTCODE_MAPPING_PASSWORD := password

GOOGLE_API_URL := https://maps.google.com/maps/api
GOOGLE_API_ADDRESS_URI := /geocode/json
GOOGLE_MAPS_API_KEY		   := gibarish

#NHS Choices
#NHS_CHOICES_API_URL := http://mock.postcodeapi.sfs:8080

#NHS_CHOICES_API_KEY := MOCK_NHS_API_KEY
NHS_CHOICES_API_KEY := 7cc220dc7d134f8c906c2f7a1782a61f

NHS_CHOICES_API_URL := https://api.nhs.uk

