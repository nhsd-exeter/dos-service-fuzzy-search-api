locals {


  fuzzy-search-dynamodb_access_iam_name = "${var.service_tag}-${var.profile}-${var.project_tag}-dynamodb-access"

  dynamoDb_full_access_policy_arn      = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

  standard_tags = {
    "Programme"   = var.programme
    "Service"     = var.service_tag
    "Product"     = var.project_tag
    "Environment" = var.profile
  }

}
