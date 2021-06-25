data "aws_dynamodb_table" "postcode_store" {
  name = local.postcode_store_name
}
