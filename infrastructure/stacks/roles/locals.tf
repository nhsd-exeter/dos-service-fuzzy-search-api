locals {
  service_account_role_iam_name = "${var.service_prefix}-service-account-role"
  service_account_policy_name   = "${var.service_prefix}-dynamodb_postcode_store_read_only_access"

  postcode_store_name = "${var.dynamo_db_postcode_store_table_name}"
  postcode_store_arn  = data.aws_dynamodb_table.postcode_store.arn

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
  }

}
