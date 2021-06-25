resource "aws_cloudwatch_log_group" "index_elasticsearch_service" {
  name              = "/aws/${var.component}/${var.service_prefix}/INDEX_SLOW_LOGS"
  retention_in_days = 30
  tags              = var.tags
}

resource "aws_cloudwatch_log_group" "search_elasticsearch_service" {
  name              = "/aws/${var.component}/${var.service_prefix}/SEARCH_SLOW_LOGS"
  retention_in_days = 30
  tags              = var.tags
}

resource "aws_cloudwatch_log_group" "app_elasticsearch_service" {
  name              = "/aws/${var.component}/${var.service_prefix}/ES_APPLICATION_LOGS"
  retention_in_days = 30
  tags              = var.tags
}

resource "aws_cloudwatch_log_resource_policy" "cloudwatch_elasticsearch_policy" {
  policy_name     = "ElasticSearchPolicy"
  policy_document = data.aws_iam_policy_document.cloudwatch_elasticsearch.json
}
