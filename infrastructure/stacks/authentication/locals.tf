locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
    "tag"         = "uec-sf"
    "uc_name"     = "UECSF"
  }

  sf_cognito = {
    cognito_pool_name = var.cognito_user_pool
  }

}
