include $(VAR_DIR)/platform-texas/v1/account-live-k8s-prod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/$(TEXAS_CERTIFICATE_ID)

PROFILE := pd
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE)
API_IMAGE_TAG := v0.0.3
SLEEP_AFTER_PLAN := 120s

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
MAX_SEARCH_CRITERIA := 20

# Search parameters
MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH := 300
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
INFRASTRUCTURE_STACKS_BASE = elasticsearch
INFRASTRUCTURE_STACKS_ETL = service_etl
INFRASTRUCTURE_STACKS_AUTH = authentication

INFRASTRUCTURE_STACKS = $(INFRASTRUCTURE_STACKS_BASE),$(INFRASTRUCTURE_STACKS_ETL)
INFRASTRUCTURE_STACKS_DESTROY = $(INFRASTRUCTURE_STACKS_ETL),$(INFRASTRUCTURE_STACKS_BASE)


SERVICE_PREFIX := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)

TF_VAR_service_prefix := $(SERVICE_PREFIX)
TF_VAR_es_zone_awareness_enabled := true #false #Cross check reaosning with Jon P
TF_VAR_es_availability_zone_count := 2 #null
TF_VAR_es_instance_count := 4
TF_VAR_es_instance_type := m6g.8xlarge.elasticsearch
TF_VAR_es_snapshot_bucket := $(TF_VAR_service_prefix)-elastic-search-snapshots
TF_VAR_es_snapshot_role := $(TF_VAR_service_prefix)-elasticsearch-snapshot
#TF_VAR_es_domain_name := $(DOMAIN)
TF_VAR_es_domain_name := $(TF_VAR_service_prefix)-service
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
TF_VAR_service_etl_logging_level := INFO
TF_VAR_service_etl_sns_logging_level := INFO
TF_VAR_service_etl_sns_email := service-etl-alerts-de-aaaafqkcxkkecdxfimtohq46zu@a2si.slack.com #service-etl-logs-aaaaepsnsym5hcy3wa6vxo4aya@a2si.slack.com
#Every 4 minutes
TF_VAR_service_etl_alarm_period := 240

# Connection to DoS Read Replica for extraction Lambdas. For the Demo env we point to the live read replica
TF_VAR_dos_sf_replica_db := uec-core-dos-live-db-12-replica-sf.crvqtzolulpo.eu-west-2.rds.amazonaws.com #uec-core-dos-live-db-12-replica-sf.dos-db-rds
TF_VAR_service_finder_replica_sg := uec-core-dos-live-db-12-replica-sf-sg
TF_VAR_dos_read_replica_secret_name := core-dos/deployment
TF_VAR_dos_read_replica_secret_key := DB_SF_READONLY_PASSWORD
TF_VAR_service_etl_db_user := dos_sf_readonly
TF_VAR_service_etl_source_db := pathwaysdos

####Cognito user pool details
COGNITO_USER_POOL := $(TF_VAR_service_prefix)-authentication
TF_VAR_cognito_user_pool := $(COGNITO_USER_POOL)
COGNITO_USER_POOL_CLIENT_SECRET := $(or $(COGNITO_USER_POOL_CLIENT_SECRET), )
COGNITO_USER_POOL_CLIENT_ID := $(or $(COGNITO_USER_POOL_CLIENT_ID), )
COGNITO_USER_POOL_ID := $(or $(COGNITO_USER_POOL_ID), )
# Change to false after deployment
ADD_DEFAULT_COGNITO_USERS := false

POSTCODE_MAPPING_SERVICE_URL := https://uec-dos-api-pc-$(PROFILE)-uec-dos-api-pc-ingress.$(TEXAS_HOSTED_ZONE)/api
POSTCODE_MAPPING_USER := fuzzy-search-api@nhs.net
POSTCODE_MAPPING_PASSWORD := $(FUZZY_API_COGNIGTO_USER_PASSWORD)

##Authentication login endpoint is set for fuzzy search at the moment. This should be configured to point authentication service api
AUTH_LOGIN_URL := https://uec-dos-api-sfsa-$(PROFILE)-uec-dos-api-sfs-service.$(TEXAS_HOSTED_ZONE)
AUTH_LOGIN_URI := /authentication/login
