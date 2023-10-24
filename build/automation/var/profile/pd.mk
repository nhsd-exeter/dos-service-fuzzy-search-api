include $(VAR_DIR)/platform-texas/v1/account-live-k8s-prod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/$(TEXAS_CERTIFICATE_ID)

PROFILE := pd
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE)
API_IMAGE_TAG := v0.0.3
SLEEP_AFTER_PLAN := 30s

CERTIFICATE_DOMAIN := certificate
ALLOWED_ORIGINS := *

SPLUNK_INDEX := eks_logs_service_finder_prod

APP_URL_PREFIX := $(K8S_APP_NAMESPACE)-$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)

# Elastic search datastore. N.B This name cannot be longer than 28 chars!
DOMAIN := sf1-prod
# DOMAIN := $(TF_VAR_service_prefix)-serviceservice
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
# INFRASTRUCTURE_STACKS_BASE = elasticsearch
# INFRASTRUCTURE_STACKS_ETL = service_etl
INFRASTRUCTURE_STACKS_AUTH = authentication
INFRASTRUCTURE_STACKS_FIREWALL = firewall
INFRASTRUCTURE_STACKS = $(INFRASTRUCTURE_STACKS_AUTH),$(INFRASTRUCTURE_STACKS_FIREWALL)
INFRASTRUCTURE_STACKS_DESTROY = $(INFRASTRUCTURE_STACKS_BASE)

SERVICE_PREFIX := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)

TF_VAR_service_prefix := $(SERVICE_PREFIX)
TF_VAR_es_zone_awareness_enabled := true
TF_VAR_es_availability_zone_count := 2
TF_VAR_es_instance_count := 2
TF_VAR_es_instance_type := m6g.8xlarge.elasticsearch
TF_VAR_es_snapshot_bucket := $(TF_VAR_service_prefix)-elastic-search-snapshots
TF_VAR_es_snapshot_role := $(TF_VAR_service_prefix)-elasticsearch-snapshot
TF_VAR_es_domain_name := $(DOMAIN)
TF_VAR_texas_vpc_name = lk8s-$(AWS_ACCOUNT_NAME).texasplatform.uk
TF_VAR_PROJECT_GROUP_SHORT = $(PROJECT_GROUP_SHORT)


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
TF_VAR_service_etl_sns_email := service-etl-alerts-de-aaaafqkcxkkecdxfimtohq46zu@a2si.slack.com
TF_VAR_service_etl_alarm_period := 240
# TF_VAR_service_etl_alarm_period := 86400 - every 24 hours
#Every 4 minutes
# TF_VAR_service_etl_alarm_period := 240



# Connection to DoS Read Replica for extraction Lambdas. For the Demo env we point to the live read replica
# TF_VAR_dos_sf_replica_db := uec-core-dos-put-db-12-replica-sf.crvqtzolulpo.eu-west-2.rds.amazonaws.com
# TF_VAR_service_finder_replica_sg := uec-core-dos-put-db-12-replica-sf-sg
# TF_VAR_dos_read_replica_secret_name := core-dos-uet-database-upgrade/deployment
# TF_VAR_dos_read_replica_secret_key := DB_SF_READONLY_PASSWORD
# TF_VAR_service_etl_db_user := dos_sf_readonly
# TF_VAR_service_etl_source_db := pathwaysdos_ut

# Connection to DoS Read Replica for extraction Lambdas. For the Demo env we point to the live read replica
TF_VAR_dos_sf_replica_db := uec-core-dos-live-db-replica-sf.dos-db-sync-rds
# TF_VAR_dos_sf_replica_db := uec-core-dos-live-db-12-replica-sf.crvqtzolulpo.eu-west-2.rds.amazonaws.com #uec-core-dos-live-db-12-replica-sf.dos-db-rds
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
ADD_DEFAULT_COGNITO_USERS := true

# Google API Key
GOOGLE_MAPS_API_KEY := $(or $(GOOGLE_MAPS_API_KEY), )

POSTCODE_MAPPING_SERVICE_URL := https://$(PROJECT_GROUP_SHORT)-pc-$(PROFILE)-$(PROJECT_GROUP_SHORT)-pc-ingress.$(TEXAS_HOSTED_ZONE)/api
POSTCODE_MAPPING_USER := fuzzy-search-api@nhs.net


#Authentication login endpoint is set for fuzzy search at the moment. This should be configured to point authentication service api
AUTH_LOGIN_URL := https://$(PROJECT_GROUP_SHORT)-sfsa-$(PROFILE)-$(PROJECT_GROUP_SHORT)-sfs-service.$(TEXAS_HOSTED_ZONE)
AUTH_LOGIN_URI := /authentication/login

GOOGLE_API_URL := https://maps.google.com/maps/api
GOOGLE_API_ADDRESS_URI := /geocode/json

# ===================== firewall
WAF_NAME = $(SERVICE_PREFIX)-waf-acl
TF_VAR_waf_dashboard_name = $(SERVICE_PREFIX)-wafv2-dashboard
TF_VAR_waf_name = $(WAF_NAME)
TF_VAR_waf_log_group_name = aws-waf-logs-$(SERVICE_PREFIX)
TF_VAR_non_gb_rule_metric_name = $(SERVICE_PREFIX)-waf-non-GB-geo-match-metric
TF_VAR_ip_reputation_list_metric_name = $(SERVICE_PREFIX)-waf-aws-ip-reputation-list-metric
TF_VAR_common_rule_set_metric_name = $(SERVICE_PREFIX)-waf-aws-common-rule-set-metric
TF_VAR_sql_injection_rules_metric = $(SERVICE_PREFIX)-waf-aws-bad-inputs-rule-set-metric
TF_VAR_bad_input_metric_name = $(SERVICE_PREFIX)-waf-aws-bad-inputs-rule-set-metric


#NHS Choices
NHS_CHOICES_API_URL := https://api.nhs.uk

