# General variables
variable "aws_account" {
  description = "AWS account identifier."
}

variable "tags" {
  type        = map(string)
  description = "A list of standard tags for any given resource."
}

variable "service_prefix" {
  description = "The prefix used to adhere to the naming conventions"
}

# Cognito variables
variable "cognito_pool_name" {
  description = "Name of the cognito user pool."
}

variable "profile" {
  description = "K8s deployment profile name that can be either 'nonprod' or 'prod'"
}
# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }
