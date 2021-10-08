AWS_ACCOUNT_ID = $(AWS_ACCOUNT_ID_NONPROD)
AWS_ACCOUNT_NAME = nonprod

TEXAS_CERTIFICATE_ID = c0718115-4e22-4f48-a4aa-8c16ea86c5e6
TEXAS_WAF_ACL_ID = dfae6ec3-aa05-428f-a022-5fd85f646009

TF_VAR_terraform_platform_state_store = nhsd-texasplatform-terraform-state-store-live-lk8s-$(AWS_ACCOUNT_NAME)

# ==============================================================================

# Connection to DoS Read Replica for extraction Lambdas
TF_VAR_dos_sf_replica_db := core-dos-regression-sf-replica.dos-db-rds
TF_VAR_service_finder_replica_sg := live-lk8s-nonprod-uec-sf-core-dos-sf-replica-sg
TF_VAR_dos_read_replica_secret_name := core-dos-dev/deployment
TF_VAR_dos_read_replica_secret_key := DB_SF_READONLY_PASSWORD
TF_VAR_service_etl_db_user := dos_sf_readonly
TF_VAR_service_etl_source_db := pathwaysdos_regression
TF_VAR_core_dos_python_libs := core-dos-python-libs

# ==============================================================================

include $(VAR_DIR)/platform-texas/platform-texas-v1.mk
