-include $(VAR_DIR)/platform-texas/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
SPRING_PROFILES_ACTIVE := local
CERTIFICATE_DOMAIN := localhost

ELASTICSEARCH_URL := elasticsearch.sfs.local:9200
API_SERVICE_SEARCH_ENDPOINT := https://localhost:8443/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.1

# Validation parameters
MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 5

# Search parameters
MAX_NUM_SERVICES_TO_RETURN := 5
FUZZ_LEVEL := 0
NAME_PRIORITY := 1
ADDRESS_PRIORITY := 0
POSTCODE_PRIORITY := 0
PUBLIC_NAME_PRIORITY := 0

# Service Data files
SERVICE_DATA_FILE := create_all_services_local.sh
