# === Profile Specific =========================================================


variable "aws_account_id" { description = "Texas AWS account id" }

variable "aws_profile" { description = "Texas AWS profile name" }



# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "service_prefix" { description = "The prefix to be used for all infrastructure" }

variable "cognito_user_pool" { description = "Name of the Cognito pool" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "route53_terraform_state_key" { description = "The Route53 key in the terraform state bucket" }
