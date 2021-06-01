-include $(VAR_DIR)/platform-texas/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
SPRING_PROFILES_ACTIVE := local
CERTIFICATE_DOMAIN := localhost
ALLOWED_ORIGINS := *

# Datastore URLs
ELASTICSEARCH_URL := elasticsearch.sfs.local:9200
POSTCODE_LOCATION_DYNAMO_URL := http://host.docker.internal:8000/
DYNAMODB_POSTCODE_LOC_MAP_TABLE := service-finder-nonprod-postcode-location-mapping
API_SERVICE_SEARCH_ENDPOINT := https://localhost:8443/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.3

# Validation parameters
MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 5

# Search parameters
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH := 100
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS := 50
MAX_NUM_SERVICES_TO_RETURN := 5
FUZZ_LEVEL := 0
NAME_PRIORITY := 1
ADDRESS_PRIORITY := 0
POSTCODE_PRIORITY := 0
NAME_PUBLIC_PRIORITY := 0

# Service Data files
SERVICE_DATA_FILE := create_all_services_local.sh
LOCATIONS_DATA_FILE := load_locations_test.sh
USER_MANAGEMENT_INTERNAL_URL := https://user-management.sf.test:8443
USER_MANAGEMENT_URL := https://localhost:18083
COOKIE_DOMAIN := localhost
ALLOWED_ORIGINS := https://localhost:8080,https://localhost:8081,https://localhost:18081,https://localhost:18082,https://localhost:18083,https://localhost:18080,https://localhost:18084,https://localhost

COGNITO_JWT_VERIFICATION_URL := http://testJwtVerificationUrl
COGNITO_USER_POOL_CLIENT_ID := testUserPoolClientId
COGNITO_USER_POOL_CLIENT_SECRET := testUserPoolClientSecret
COGNITO_USER_POOL_ID := testUserPoolId
