-include $(VAR_DIR)/platform-texas/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
SPRING_PROFILES_ACTIVE := local

ELASTICSEARCH_URL := elasticsearch.sfs.local:9200
URL := http://localhost:9095/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 9095
VERSION := v0.0.1

MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 5
MAX_NUM_SERVICES_TO_RETURN := 5
FUZZ_LEVEL := 0
