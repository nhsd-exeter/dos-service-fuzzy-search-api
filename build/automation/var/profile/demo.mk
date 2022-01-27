include $(VAR_DIR)/platform-texas/v1/account-live-k8s-prod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/$(TEXAS_CERTIFICATE_ID)

PROFILE := demo
ENVIRONMENT := demo
SPRING_PROFILES_ACTIVE := demo
API_IMAGE_TAG := v0.0.3

CERTIFICATE_DOMAIN := certificate
ALLOWED_ORIGINS := *

SPLUNK_INDEX := eks_logs_service_finder_prod

APP_URL_PREFIX := $(K8S_APP_NAMESPACE)-$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)

# Elastic search datastore
DOMAIN := $(TF_VAR_service_prefix)-service
# ELASTICSEARCH_URL configured in make project-populate-application-variables

API_SERVICE_SEARCH_ENDPOINT := https://$(APP_URL_PREFIX)-service-search.$(TF_VAR_platform_zone)/dosapi/dosservices/v0.0.1/services/byfuzzysearch

SERVER_PORT := 8443
VERSION := v0.0.3

SERVICE_SEARCH_REPLICAS := 3

# Validation parameters
MIN_SEARCH_TERM_LENGTH := 3
MAX_SEARCH_CRITERIA := 5

# Search parameters
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH := 3000
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS := 100
MAX_NUM_SERVICES_TO_RETURN := 5
FUZZ_LEVEL := 2
NAME_PRIORITY := 4
ADDRESS_PRIORITY := 2
POSTCODE_PRIORITY := 0
NAME_PUBLIC_PRIORITY := 4


# ==============================================================================
# Infrastructure variables

DEPLOYMENT_STACKS = application
INFRASTRUCTURE_STACKS = elasticsearch,service_etl
INFRASTRUCTURE_STACKS_DESTROY = service_etl,elasticsearch
INFRASTRUCTURE_STACKS_AUTH = authentication
SERVICE_PREFIX := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)
TF_VAR_service_prefix := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)

TF_VAR_es_zone_awareness_enabled := false
TF_VAR_es_availability_zone_count := 2
TF_VAR_es_instance_count := 4
TF_VAR_es_instance_type := m6g.8xlarge.search
TF_VAR_es_snapshot_bucket := $(TF_VAR_service_prefix)-elastic-search-snapshots
TF_VAR_es_snapshot_role := $(TF_VAR_service_prefix)-elasticsearch-snapshot
TF_VAR_es_domain_name := $(TF_VAR_service_prefix)-service
TF_VAR_service_etl_logging_level := INFO
TF_VAR_service_etl_sns_logging_level := INFO
TF_VAR_service_etl_sns_email := service-etl-alerts-de-aaaafqkcxkkecdxfimtohq46zu@a2si.slack.com

# See : https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html
# Config for the cron job trigger for the service etl set to be:
# 6am Monday - Friday (This is because we dont want the service to be running every 4 minutes in non prod)
# For prod we need to set cron(0/4 * * * ? *) (Every 4 minutes)
TF_VAR_service_etl_cron_timer_minutes := 0/4
TF_VAR_service_etl_cron_timer_hours := *
TF_VAR_service_etl_cron_timer_day_of_month := *
TF_VAR_service_etl_cron_timer_month := *
TF_VAR_service_etl_cron_timer_day_of_week := ?
TF_VAR_service_etl_cron_timer_year := *

TF_VAR_service_etl_alarm_period := 86400
# Connection to DoS Read Replica for extraction Lambdas
TF_VAR_dos_sf_replica_db := uec-core-dos-put-db-replica-sf.dos-db-put
TF_VAR_service_finder_replica_sg := uec-core-dos-put-db-12-replica-sf-sg
TF_VAR_dos_read_replica_secret_name := core-dos-uet-database-upgrade/deployment
TF_VAR_dos_read_replica_secret_key := DB_SF_READONLY_PASSWORD
TF_VAR_service_etl_db_user := dos_sf_readonly
TF_VAR_service_etl_source_db := core-dos-uet-database-upgrade/deployment

#Cognito user pool details
COGNITO_USER_POOL := $(TF_VAR_service_prefix)-authentication
TF_VAR_cognito_user_pool := $(COGNITO_USER_POOL)
COGNITO_USER_POOL_CLIENT_SECRET := $(or $(COGNITO_USER_POOL_CLIENT_SECRET), )
COGNITO_USER_POOL_CLIENT_ID := $(or $(COGNITO_USER_POOL_CLIENT_ID), )
COGNITO_USER_POOL_ID := $(or $(COGNITO_USER_POOL_ID), )
ADD_DEFAULT_COGNITO_USERS := false

POSTCODE_MAPPING_SERVICE_URL := https://uec-dos-api-pca-$(PROFILE)-uec-dos-api-pc-ingress.$(TEXAS_HOSTED_ZONE)/api

#Authentication login endpoint is set for fuzzy search at the moment. This should be configured to point authentication service api
AUTH_LOGIN_URL := https://uec-dos-api-sfsa-$(PROFILE)-uec-dos-api-sfs-service.$(TEXAS_HOSTED_ZONE)
AUTH_LOGIN_URI := /authentication/login
AUTHENTICATION_ENDPOINT = $(AUTH_LOGIN_URL)$(AUTH_LOGIN_URI)
FUZZY_SEARCH_DOMAIN = $(PROJECT_ID)-$(PROFILE)-uec-dos-api-sfs-ingress.$(TEXAS_HOSTED_ZONE)
FUZZY_SEARCH_ENDPOINT = https://$(FUZZY_SEARCH_DOMAIN)


#admin access is set for now and this should be changed accordingly with user who has access to the api
POSTCODE_MAPPING_USER := fuzzy-search-api@nhs.net
POSTCODE_MAPPING_PASSWORD := $(COGNITO_ADMIN_AUTH_PASSWORD)

# Monitor deployment VARS
CHECK_DEPLOYMENT_TIME_LIMIT := 600
CHECK_DEPLOYMENT_POLL_INTERVAL := 10
