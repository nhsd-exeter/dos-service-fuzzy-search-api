include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
SPRING_PROFILES_ACTIVE := local, mock-auth
CERTIFICATE_DOMAIN := localhost
ALLOWED_ORIGINS := *

# Datastore URLs
ELASTICSEARCH_URL := opensearch.sf.test:9200
API_SERVICE_SEARCH_ENDPOINT := https://localhost:8443/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.3

# Validation parameters
MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 20

# Search parameters
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH := 100
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS := 50
MAX_NUM_SERVICES_TO_RETURN := 5
FUZZ_LEVEL := 2
NAME_PRIORITY := 100
ADDRESS_PRIORITY := 75
POSTCODE_PRIORITY := 75
NAME_PUBLIC_PRIORITY := 100

# Service Data files
SERVICE_DATA_FILE := create_all_services_local.sh
LOCATIONS_DATA_FILE := load_locations_test.sh

# Monitor deployment VARS
CHECK_DEPLOYMENT_TIME_LIMIT := 600
CHECK_DEPLOYMENT_POLL_INTERVAL := 10
