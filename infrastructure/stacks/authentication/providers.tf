provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.20.0"
      # or if you want to use a range
      # version = ">= 3.74.1, < 4.0.0"
    }
  }
}
