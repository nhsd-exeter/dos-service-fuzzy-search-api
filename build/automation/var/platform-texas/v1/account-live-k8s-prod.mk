AWS_ACCOUNT_ID = $(AWS_ACCOUNT_ID_PROD)
AWS_ACCOUNT_NAME = prod

TEXAS_CERTIFICATE_ID = 8b67daa2-2b82-4287-b925-d74ab9fa68ce
# TEXAS_WAF_ACL_ID = ff530a4e-689d-4d90-b3ab-ac2160b5863d

TF_VAR_terraform_platform_state_store = nhsd-texasplatform-terraform-state-store-lk8s-$(AWS_ACCOUNT_NAME)
TF_VAR_project_id = $(PROJECT_ID)
TF_VAR_eks_terraform_state_key = eks/terraform.tfstate
TF_VAR_aws_account_id = $(AWS_ACCOUNT_ID_PROD)
TF_VAR_application_service_account_name = $(APPLICATION_SA_NAME)
# ==============================================================================


# TODO - Point to put for now, but this will need to be split into demo and live profiles
# Connection to DoS Read Replica for extraction Lambdas
TF_VAR_dos_sf_replica_db := uec-core-dos-live-db-12-replica-sf.dos-db-rds
# TF_VAR_dos_sf_replica_db := uec-core-dos-live-db-12-replica-sf.dos-db-rds
TF_VAR_service_finder_replica_sg := 	uec-core-dos-live-db-12-replica-sf-sg
# TF_VAR_service_finder_replica_sg := uec-core-dos-put-db-12-replica-sf-sg
TF_VAR_dos_read_replica_secret_name := core-dos/deployment
TF_VAR_dos_read_replica_secret_key := DB_SF_READONLY_PASSWORD
TF_VAR_service_etl_db_user := dos_sf_readonly
TF_VAR_service_etl_source_db := pathwaysdos
TF_VAR_core_dos_python_libs := core-dos-python-libs

# ==============================================================================

include $(VAR_DIR)/platform-texas/platform-texas-v1.mk
