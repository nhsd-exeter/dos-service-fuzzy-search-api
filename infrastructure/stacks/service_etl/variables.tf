# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "vpc_terraform_state_key" { description = "The VPC key in the terraform state bucket" }

variable "es_domain_name" { description = "Elastic search domain name" }

variable "service_prefix" { description = "The prefix used to adhere to the naming conventions" }

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
