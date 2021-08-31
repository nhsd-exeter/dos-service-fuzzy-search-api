locals {

  service_etl_function_name = "uec-sf-${var.profile}-dos-replica-etl"
  service_etl_description   = "Service Finder function to extract data out of the DoS Read Replica and insert it into the elasticsearch datastore. Runs every 5 mins"
  service_etl_runtime       = "python3.8"
  //This is set to 3 mins and 59 seconds as the timer is set to run every 4 mins so it will timeout before the next job starts
  service_etl_timeout     = 239
  service_etl_memory_size = 10240

  service_etl_core_dos_python_libs_arn = data.aws_lambda_layer_version.dos_python_libs.arn
  dos_sf_replica_db_sg                 = data.aws_security_group.service_finder_replica_sg.id
  service_etl_db_user                  = var.service_etl_db_user
  service_etl_source_db                = var.service_etl_source_db
  service_etl_db_endpoint              = var.dos_sf_replica_db
  service_etl_db_port                  = "5432"
  service_etl_db_region                = "eu-west-2"
  service_etl_db_secret_name           = var.dos_read_replica_secret_name
  service_etl_db_secret_key            = var.dos_read_replica_secret_key
  service_etl_db_secret_arn            = data.aws_secretsmanager_secret.dos_read_replica_secret_name.arn

  es_domain_arn               = data.aws_elasticsearch_domain.elasticsearch.arn
  es_domain_endpoint          = data.aws_elasticsearch_domain.elasticsearch.endpoint
  es_domain_security_group_id = data.aws_security_group.elasticsearch_security_group.id

  service_etl_iam_name = "uec-sf-${var.profile}-dos-replica-etl-lambda"

  service_etl_policy_name = "uec-sf-${var.profile}-dos-replica-etl"

  rds_data_read_only_access_policy_arn = "arn:aws:iam::aws:policy/AmazonRDSReadOnlyAccess"

  service_etl_cloudwatch_event_name            = "service-finder-${var.profile}-dos-replica-etl-rule"
  service_etl_cloudwatch_event_description     = "Timer to run the dos-replica-etl every 4 minutes"
  service_etl_cloudwatch_event_cron_expression = "cron(0/4 * * * ? *)"
  service_etl_cloudwatch_event_target          = "lambda"
  service_etl_cloudwatch_event_statement       = "AllowExecutionFromCloudWatch"
  service_etl_cloudwatch_event_action          = "lambda:InvokeFunction"
  service_etl_cloudwatch_event_princinple      = "events.amazonaws.com"

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
  }

}