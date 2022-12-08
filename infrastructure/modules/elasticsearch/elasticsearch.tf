resource "aws_elasticsearch_domain" "elasticsearch_service" {
  domain_name           = var.es_domain_name
  elasticsearch_version = var.elasticsearch_version

  vpc_options {
    subnet_ids         = local.subnet_ids
    security_group_ids = [aws_security_group.elasticsearch.id]
  }

  cluster_config {
    instance_type          = var.instance_type
    instance_count         = var.instance_count
    zone_awareness_enabled = var.zone_awareness_enabled

    dynamic zone_awareness_config {
      for_each = local.availability_zone_count_list
      content {
        availability_zone_count = zone_awareness_config.value
      }
    }
  }

  domain_endpoint_options {
    enforce_https       = true
    tls_security_policy = "Policy-Min-TLS-1-2-2019-07"
  }

  ebs_options {
    ebs_enabled = true
    volume_size = var.volume_size_gb
  }

  encrypt_at_rest {
    enabled = var.encrypt_at_rest
  }

  node_to_node_encryption {
    enabled = var.node_to_node_encryption
  }

  advanced_options = {
    # "override_main_response_version"         = "false"
    "rest.action.multi.allow_explicit_index" = "true"
  }

  log_publishing_options {
    cloudwatch_log_group_arn = aws_cloudwatch_log_group.index_elasticsearch_service.arn
    log_type                 = "INDEX_SLOW_LOGS"
  }

  log_publishing_options {
    cloudwatch_log_group_arn = aws_cloudwatch_log_group.search_elasticsearch_service.arn
    log_type                 = "SEARCH_SLOW_LOGS"
  }

  log_publishing_options {
    cloudwatch_log_group_arn = aws_cloudwatch_log_group.app_elasticsearch_service.arn
    log_type                 = "ES_APPLICATION_LOGS"
  }

  tags = var.tags
}

resource "aws_elasticsearch_domain_policy" "elasticsearch_domain_policy" {
  domain_name = aws_elasticsearch_domain.elasticsearch_service.domain_name

  access_policies = <<POLICIES
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "es:*",
            "Principal": "*",
            "Effect": "Allow",
            "Resource": "${aws_elasticsearch_domain.elasticsearch_service.arn}/*"
        }
    ]
}
POLICIES
}
