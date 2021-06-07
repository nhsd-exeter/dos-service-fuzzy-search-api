module "cognito" {
  source              = "../../modules/cognito"
  aws_account         = var.aws_account_id
  profile             = var.profile
  service_prefix      = var.service_prefix
  cognito_pool_name   = local.sf_cognito["cognito_pool_name"]
  tags                = local.standard_tags
}
