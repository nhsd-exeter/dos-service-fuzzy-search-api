variable "waf_dashboard_name" {
  description = "Name of cloudwatch dashboard for wafv2"
}

variable "waf_name" {
  description = "Name of the WAF ACL"
}

variable "waf_log_group_name" {
  description = "Name of the cloudwatch log group generated for WAF ACL"
}

variable "non_gb_rule_metric_name" {
  description = "Name of metric for the non gp geo rule"
}

variable "ip_reputation_list_metric_name" {
  description = "Name of metric for ip reputation list rule"
}

variable "common_rule_set_metric_name" {
  description = "Name for service team common rule set"
}

variable "sql_injection_rules_metric" {
  description = "Name for sql injections rules metric"
}

variable "bad_input_metric_name" {
  description = "Name for bad input metric rule"
}

variable "service_prefix" { description = "Used to identifier project resources" }

variable "profile" { description = "Indication of environment resource" }

variable "aws_region" { description = "Texas AWS deployment region" }
