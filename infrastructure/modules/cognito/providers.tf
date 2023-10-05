provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
  version = ">= 3.74.1, < 4.59.0"
}

provider "random" {
  version = "3.3.1"
}
