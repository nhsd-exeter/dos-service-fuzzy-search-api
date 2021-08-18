locals {

  dos_replica_etl_function_name = "uec-sf-${var.profile}-dos-replica-etl"
  dos_replica_etl_description   = "Service Finder function to extract data out of the DoS Read Replica and insert it into the elasticsearch datastore. Runs every 5 mins"
  dos_replica_etl_runtime       = "python3.8"
  //This is set to 3 mins and 59 seconds as the timer is set to run every 4 mins so it will timeout before the next job starts
  dos_replica_etl_timeout     = 239
  dos_replica_etl_memory_size = 1024

  dos_replica_etl_core_dos_python_libs_arn = data.aws_lambda_layer_version.dos_python_libs.arn
  dos_replica_etl_vpc_security_group       = data.aws_security_group.dos_application_security_group.id
  dos_replica_etl_db_user                  = var.dos_replica_etl_db_user
  dos_replica_etl_source_db                = var.dos_replica_etl_source_db
  dos_replica_etl_db_endpoint              = var.dos_replica_db
  dos_replica_etl_db_port                  = "5432"
  dos_replica_etl_db_region                = "eu-west-2"
  dos_replica_etl_db_secret_name           = var.dos_read_replica_secret_name
  dos_replica_etl_db_secret_key            = var.dos_read_replica_secret_key
  dos_replica_etl_db_secret_arn            = data.aws_secretsmanager_secret.dos_read_replica_secret_name.arn

  es_domain_arn                            = data.aws_elasticsearch_domain.elasticsearch.arn
  es_domain_endpoint                       = data.aws_elasticsearch_domain.elasticsearch.endpoint

  dos_replica_etl_iam_name = "uec-sf-${var.profile}-dos-replica-etl-lambda"

  dos_replica_etl_policy_name = "uec-sf-${var.profile}-dos-replica-etl"

  rds_data_read_only_access_policy_arn = "arn:aws:iam::aws:policy/AmazonRDSReadOnlyAccess"

  dos_replica_etl_cloudwatch_event_name            = "service-finder-${var.profile}-dos-replica-etl-rule"
  dos_replica_etl_cloudwatch_event_description     = "Timer to run the dos-replica-etl every 4 minutes"
  dos_replica_etl_cloudwatch_event_cron_expression = "cron(0/4 * * * ? *)"
  dos_replica_etl_cloudwatch_event_target          = "lambda"
  dos_replica_etl_cloudwatch_event_statement       = "AllowExecutionFromCloudWatch"
  dos_replica_etl_cloudwatch_event_action          = "lambda:InvokeFunction"
  dos_replica_etl_cloudwatch_event_princinple      = "events.amazonaws.com"

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
  }

}
