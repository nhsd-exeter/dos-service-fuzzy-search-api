# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

variable "project_id" { description = "product id for either nonprod or prod" }

variable "aws_account_id" { description = "aws_account id for either nonprod or prod" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "eks_terraform_state_key" { description = "eks terraform state key defined in  env" }

variable "vpc_terraform_state_key" { description = "The VPC key in the terraform state bucket" }

variable "es_domain_name" { description = "Elastic search domain name" }

variable "service_prefix" { description = "The prefix used to adhere to the naming conventions" }

variable "service_account_role_name" { description = "Service account host IAM role for pod authentication" }

variable "application_service_account_name" { description = "application service account defined in the profile" }

# === Stack Specific ============================================================

variable "dos_sf_replica_db" { description = "The DOS read replica to get the postcodes from" }

variable "service_finder_replica_sg" { description = "The security group for the dos read replica" }

variable "dos_read_replica_secret_name" { description = "The dos read replica secret name" }

variable "dos_read_replica_secret_key" { description = "The dos read replica secret key" }

variable "service_etl_db_user" { description = "The dos read replica user name" }

variable "service_etl_source_db" { description = "The dos read replica source database" }

variable "core_dos_python_libs" { description = "core dos python libs for accessing dos databases" }

variable "service_etl_sns_email" { description = "email desitination for critical failures of etl process" }

variable "service_etl_sns_logging_level" { description = "Logging level for service_etl_sns lambda" }

variable "service_etl_logging_level" { description = "Logging level for service_etl lambda" }

variable "service_etl_cron_timer_minutes" { description = "cron timer for the minutes service etl should trigger" }
variable "service_etl_cron_timer_hours" { description = "cron timer for the hours service etl should trigger" }
variable "service_etl_cron_timer_day_of_month" { description = "cron timer for the day of the month service etl should trigger" }
variable "service_etl_cron_timer_month" { description = "cron timer for the month service etl should trigger" }
variable "service_etl_cron_timer_day_of_week" { description = "cron timer for the day of the week service etl should trigger" }
variable "service_etl_cron_timer_year" { description = "cron timer for the year service etl should trigger" }

variable "service_etl_alarm_period" { description = "The period in seconds that the alarm will check to make sure the etl process has been triggered" }

variable "service_account_role_name" { description = "Service account host IAM role for pod authentication" }

variable "application_service_account_name" { description = "application service account defined in the profile" }
