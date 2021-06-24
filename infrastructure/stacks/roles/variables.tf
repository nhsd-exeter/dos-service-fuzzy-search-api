# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "aws_account_id" { description = "Texas AWS account id" }

variable "platform_zone" { description = "The hosted zone used for the platform" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "service_prefix" { description = "The prefix to be used for all infrastructure" }

variable "service_prefix_short" { description = "The prefix to be used for all infrastructure - this is a short version used when a name exceeds limits " }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "vpc_terraform_state_key" { description = "The VPC key in the terraform state bucket" }

variable "route53_terraform_state_key" { description = "The Route53 key in the terraform state bucket" }

variable "security_groups_terraform_state_key" { description = "The security groups key in the terraform state bucket" }

variable "security_groups_k8s_terraform_state_key" { description = "The k8s security groups key in the terraform state bucket" }

variable "programme" { description = "The programme tag" }

variable "service_tag" { description = "The service tag" }

variable "project_tag" { description = "The project tag" }
