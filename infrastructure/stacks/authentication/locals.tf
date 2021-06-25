locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
  }

  sf_cognito = {
    cognito_pool_name = "${var.service_prefix}-pool"
  }

}
