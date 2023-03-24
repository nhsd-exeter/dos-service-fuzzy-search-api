include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/c0718115-4e22-4f48-a4aa-8c16ea86c5e6
ECR_TEXAS_URL_NONPROD = $(AWS_ECR_NON_PROD)/texas

PROFILE := dev
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE)
API_IMAGE_TAG := v0.0.3


SLEEP_AFTER_PLAN := 30s

CERTIFICATE_DOMAIN := localhost
ALLOWED_ORIGINS := *

SPLUNK_INDEX := eks_logs_service_finder_nonprod

APP_URL_PREFIX := $(K8S_APP_NAMESPACE)-$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)

# Elastic search datastore
DOMAIN := sf1-nonprod
# ELASTICSEARCH_URL configured in make project-populate-application-variables

API_SERVICE_SEARCH_ENDPOINT := https://$(APP_URL_PREFIX)-service-search.$(TF_VAR_platform_zone)/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.3
JMETER_MASTER_IMAGE := jmeter-master:5.4.1-log4j2-patch
JMETER_SLAVE_IMAGE := jmeter-slave:5.4.1-log4j2-patch

SERVICE_SEARCH_REPLICAS := 3

# Validation parameters
MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 20

# Search parameters
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH := 3000
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS := 100
MAX_NUM_SERVICES_TO_RETURN := 50
FUZZ_LEVEL := 2
NAME_PRIORITY := 100
ADDRESS_PRIORITY := 75
POSTCODE_PRIORITY := 75
NAME_PUBLIC_PRIORITY := 100

# Monitor deployment VARS
CHECK_DEPLOYMENT_TIME_LIMIT := 600
CHECK_DEPLOYMENT_POLL_INTERVAL := 10

# ==============================================================================
# Infrastructure variables

DEPLOYMENT_STACKS = application
INFRASTRUCTURE_STACKS =
INFRASTRUCTURE_STACKS_DESTROY = $(INFRASTRUCTURE_STACKS_BASE)
INFRASTRUCTURE_STACKS_BASE = elasticsearch
INFRASTRUCTURE_STACKS_ETL = service_etl
INFRASTRUCTURE_STACKS_AUTH = authentication
SERVICE_PREFIX := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)
TF_VAR_service_prefix := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)

TF_VAR_es_zone_awareness_enabled := true
TF_VAR_es_availability_zone_count := 2
TF_VAR_es_instance_count := 2
TF_VAR_es_instance_type := m5.large.elasticsearch
TF_VAR_es_snapshot_bucket := $(TF_VAR_service_prefix)-elastic-search-snapshots
TF_VAR_es_snapshot_role := $(TF_VAR_service_prefix)-elasticsearch-snapshot
TF_VAR_es_domain_name := $(TF_VAR_service_prefix)-service
TF_VAR_service_etl_logging_level := INFO
TF_VAR_service_etl_sns_logging_level := INFO
TF_VAR_service_etl_sns_email := service-etl-logs-aaaaepsnsym5hcy3wa6vxo4aya@a2si.slack.com

# See : https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html
# Config for the cron job trigger for the service etl set to be:
# 6am Monday - Friday (This is because we dont want the service to be running every 4 minutes in non prod)
# For prod we need to set cron(0/4 * * * ? *) (Every 4 minutes)
TF_VAR_service_etl_cron_timer_minutes := 0
TF_VAR_service_etl_cron_timer_hours := 6
TF_VAR_service_etl_cron_timer_day_of_month := ?
TF_VAR_service_etl_cron_timer_month := *
TF_VAR_service_etl_cron_timer_day_of_week := MON-FRI
TF_VAR_service_etl_cron_timer_year := *

TF_VAR_service_etl_alarm_period := 86400

#Cognito user pool details
COGNITO_USER_POOL := $(TF_VAR_service_prefix)-authentication
TF_VAR_cognito_user_pool := $(COGNITO_USER_POOL)
COGNITO_USER_POOL_CLIENT_SECRET := $(or $(COGNITO_USER_POOL_CLIENT_SECRET), )
COGNITO_USER_POOL_CLIENT_ID := $(or $(COGNITO_USER_POOL_CLIENT_ID), )
COGNITO_USER_POOL_ID := $(or $(COGNITO_USER_POOL_ID), )
ADD_DEFAULT_COGNITO_USERS := true

# Google API Key
GOOGLE_MAPS_API_KEY := $(or $(GOOGLE_MAPS_API_KEY), )


#Once wiremock is deployed to dev environment calls to postcode api will be mocked
POSTCODE_MAPPING_SERVICE_URL := https://uec-dos-api-sfsa-$(PROFILE)-uec-dos-api-sfs-mock-postcode-ingress.k8s-nonprod.texasplatform.uk/api
POSTCODE_MAPPING_USER := fuzzy-search-api@nhs.net

#Authentication login endpoint is set for fuzzy search at the moment. This should be configured to point authentication service api
AUTH_LOGIN_URL := https://uec-dos-api-sfsa-$(PROFILE)-uec-dos-api-sfs-service.$(TEXAS_HOSTED_ZONE)
AUTH_LOGIN_URI := /authentication/login

GOOGLE_API_URL := https://maps.google.com/maps/api
GOOGLE_API_ADDRESS_URI := /geocode/json


TF_VAR_texas_vpc_name = lk8s-$(AWS_ACCOUNT_NAME).texasplatform.uk
