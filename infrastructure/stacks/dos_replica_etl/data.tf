# Lambda
data "archive_file" "dos_replica_etl_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/dos_replica_etl"
  output_path = "${path.module}/functions_zip/${local.dos_replica_etl_function_name}.zip"
}

data "terraform_remote_state" "vpc" {
  backend = "s3"
  config = {
    bucket = var.terraform_platform_state_store
    key    = var.vpc_terraform_state_key
    region = var.aws_region
  }
}

data "aws_security_group" "dos_application_security_group" {
  name = var.dos_security_group
}

data "aws_security_group" "elasticsearch_security_group" {
  name = "${var.service_prefix}-elastic-search"
}

data "aws_lambda_layer_version" "dos_python_libs" {
  layer_name = var.core_dos_python_libs
}

data "aws_secretsmanager_secret" "dos_read_replica_secret_name" {
  name = var.dos_read_replica_secret_name
}

data "aws_elasticsearch_domain" "elasticsearch" {
  domain_name = var.es_domain_name
}
