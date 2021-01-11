-include $(VAR_DIR)/platform-texas/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/c0718115-4e22-4f48-a4aa-8c16ea86c5e6

PROFILE := dev
SPRING_PROFILES_ACTIVE := dev
API_IMAGE_TAG := latest

CERTIFICATE_DOMAIN := localhost

APP_URL_PREFIX := $(K8S_APP_NAMESPACE)-$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)
ELASTICSEARCH_URL := https://vpc-sfs-dev-csfa7ah6cfpbllxcfqf6wmdcxi.eu-west-2.es.amazonaws.com
API_SERVICE_SEARCH_ENDPOINT := https://$(APP_URL_PREFIX)-service-search.$(TF_VAR_platform_zone)/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.1

SERVICE_SEARCH_REPLICAS := 3

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

# ==============================================================================
# Infrastructure variables

DEPLOYMENT_STACKS = application
INFRASTRUCTURE_STACKS = elasticsearch

TF_VAR_service_prefix := service-fuzzy-search-$(PROFILE)
TF_VAR_service_prefix_short := sfs-$(PROFILE)

TF_VAR_es_zone_awareness_enabled  := false
TF_VAR_es_availability_zone_count := null
TF_VAR_es_instance_count := 1
TF_VAR_es_instance_type := t3.small.elasticsearch
TF_VAR_es_snapshot_bucket := $(TF_VAR_service_prefix)-elastic-search-snapshots
TF_VAR_es_snapshot_role := $(TF_VAR_service_prefix)-elasticsearch-snapshot
TF_VAR_es_domain_name := sfs-$(PROFILE)

# Service Data files
SERVICE_DATA_FILE := create_all_services_dev.sh
