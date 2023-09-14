resource "aws_wafv2_web_acl" "waf_acl" {
  name        = var.waf_name
  description = "SF Fuzzy Search Application WAF"
  scope       = "REGIONAL"

  default_action {
    allow {}
  }

  # Primary Web ACL metric
  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name                = "${var.service_prefix}-waf-acl-metric"
    sampled_requests_enabled   = true
  }

  tags = {
    Programme   = "uec"
    Service     = "service-finder"
    Product     = "service-finder"
    Environment = var.profile
  }

  # Common service team rules
  rule {
    name     = "${var.service_prefix}-aws-common-rule-set"
    priority = 10

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesCommonRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = var.common_rule_set_metric_name
      sampled_requests_enabled   = true
    }
  }

  #  Bad input rules
  rule {
    name     = "${var.service_prefix}-aws-bad-inputs-rule-set"
    priority = 20

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesKnownBadInputsRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = var.bad_input_metric_name
      sampled_requests_enabled   = true
    }
  }

  # Rule AWS ip reputation list
  rule {
    name     = "${var.service_prefix}-aws-ip-reputation-list"
    priority = 30

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesAmazonIpReputationList"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = var.ip_reputation_list_metric_name
      sampled_requests_enabled   = true
    }
  }

  # Service-team specfic rules
  rule {
    name     = "${var.service_prefix}-waf-non-GB-geo-match"
    priority = 1
    action {
      count {}
    }
    statement {
      not_statement {
        statement {
          geo_match_statement {
            country_codes = ["GB"]
          }
        }
      }
    }
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = var.non_gb_rule_metric_name
      sampled_requests_enabled   = true
    }
  }

  #  SQL Injection protection rules
  rule {
    name     = "${var.service_prefix}-aws-sql-injection-rules"
    priority = 40

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesSQLiRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = var.sql_injection_rules_metric
      sampled_requests_enabled   = true
    }
  }

  lifecycle {
    create_before_destroy = true
  }

}

resource "aws_cloudwatch_log_group" "waf_logs" {
  // Note CW log group name should begin aws-waf-logs
  name = var.waf_log_group_name
  tags = {
    Programme   = "uec"
    Service     = "service-finder"
    Product     = "service-finder"
    Environment = var.profile
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_wafv2_web_acl_logging_configuration" "waf_acl_lc" {
  log_destination_configs = [aws_cloudwatch_log_group.waf_logs.arn]
  resource_arn            = aws_wafv2_web_acl.waf_acl.arn
  lifecycle {
    create_before_destroy = true
  }
}

data "aws_caller_identity" "current" {}

locals {
  current_account_id = data.aws_caller_identity.current.account_id
}


# This subscription filter forwards WAF cloudwatch logs -> firehose -> service teams splunk index.
# The firehose will be set up by Texas by adding the service team's hec token to its secrets.
resource "aws_cloudwatch_log_subscription_filter" "subscr_filter" {
  name            = "${var.service_prefix}_subscr_filter"
  role_arn        = "arn:aws:iam::${local.current_account_id}:role/service-finder_cw_firehose_access_role"
  log_group_name  = "aws-waf-logs-${var.service_prefix}"
  filter_pattern  = ""
  destination_arn = "arn:aws:firehose:${var.aws_region}:${local.current_account_id}:deliverystream/service-finder-cw-logs-firehose"
  distribution    = "ByLogStream"

  depends_on = [
    aws_cloudwatch_log_group.waf_logs
  ]
}
