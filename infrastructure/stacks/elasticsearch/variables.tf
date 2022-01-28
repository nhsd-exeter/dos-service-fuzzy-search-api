# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "aws_account_id" { description = "Texas AWS account id" }

variable "platform_zone" { description = "The hosted zone used for the platform" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

variable "es_zone_awareness_enabled" { description = "Elastic search zone awareness enabled" }

variable "es_availability_zone_count" { description = "Elastic search availability zone count" }

variable "es_instance_count" { description = "Elastic search instance count" }

variable "es_instance_type" { description = "Elastic search instance type" }

variable "es_snapshot_bucket" { description = "Elastic search snapshot bucket name" }

variable "es_snapshot_role" { description = "Elastic search snapshot role" }

variable "es_domain_name" { description = "Elastic search domain name" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "service_prefix" { description = "The prefix to be used for all infrastructure" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "vpc_terraform_state_key" { description = "The VPC key in the terraform state bucket" }

variable "security_groups_k8s_terraform_state_key" { description = "The k8s security groups key in the terraform state bucket" }

variable "programme" { description = "The programme tag" }

variable "service_tag" { description = "The service tag" }

variable "project_tag" { description = "The project tag" }
