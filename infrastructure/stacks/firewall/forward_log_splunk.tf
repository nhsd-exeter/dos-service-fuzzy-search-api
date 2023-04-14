# Create IAM role necessary for cross-account log subscriptions
resource "aws_iam_role" "cw_to_subscription_filter_role" {
  name               = "${var.service_prefix}_CWLtoSubscriptionFilterRole"
  assume_role_policy = data.aws_iam_policy_document.central_logs_assume_role.json
  lifecycle {
    create_before_destroy = true
  }
}

data "aws_iam_policy_document" "central_logs_assume_role" {
  statement {
    sid     = "centralLogsAssumeRole"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["logs.${var.aws_region}.amazonaws.com"]
    }
  }
}

# Permissions policy to define actions cloudwatch logs can perform
resource "aws_iam_policy" "central_cw_subscription_iam_policy" {
  name   = "${var.service_prefix}_central_cw_subscription"
  policy = data.aws_iam_policy_document.central_cw_subscription_doc_policy.json
  lifecycle {
    create_before_destroy = true
  }
}

data aws_iam_policy_document "central_cw_subscription_doc_policy" {
  statement {
    actions = [
      "logs:PutLogEvents"
    ]
    resources = [
      "arn:aws:logs:${var.aws_region}:${local.current_account_id}:log-group:aws-waf-logs-${var.service_prefix}:*"
    ]
  }
}

resource "aws_iam_role_policy_attachment" "central_logging_att" {
  policy_arn = aws_iam_policy.central_cw_subscription_iam_policy.arn
  role       = aws_iam_role.cw_to_subscription_filter_role.id
}

data "aws_secretsmanager_secret" "cloudwatch-cross-accounts" {
  name = "cloudwatch-cross-account-logging"
}

data "aws_secretsmanager_secret_version" "cloudwatch-cross-accounts" {
  secret_id = data.aws_secretsmanager_secret.cloudwatch-cross-accounts.id
}

locals {
  cross_account_id = jsondecode(data.aws_secretsmanager_secret_version.cloudwatch-cross-accounts.secret_string)["central-logging"]
}

resource "time_sleep" "wait_30_seconds" {
  depends_on      = [aws_iam_role.cw_to_subscription_filter_role]
  create_duration = "30s"
}
# The subscription filter to send to the central logging
resource "aws_cloudwatch_log_subscription_filter" "central_logging_subscr_filter" {
  name            = "${var.service_prefix}_central_logging_subscr_filter"
  role_arn        = "arn:aws:iam::${local.current_account_id}:role/${var.service_prefix}_CWLtoSubscriptionFilterRole"
  log_group_name  = var.waf_log_group_name
  filter_pattern  = ""
  destination_arn = "arn:aws:logs:${var.aws_region}:${local.cross_account_id}:destination:waf_log_destination"
  distribution    = "ByLogStream"

  depends_on = [
    aws_cloudwatch_log_group.waf_logs,
    time_sleep.wait_30_seconds
  ]
  lifecycle {
    create_before_destroy = true
  }
}
