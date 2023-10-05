provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
  version = "4.20.0"
  # version = ">= 3.74.1, < 4.0.0"
}
