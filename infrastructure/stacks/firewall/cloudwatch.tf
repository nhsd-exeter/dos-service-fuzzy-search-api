#  Cloudwatch dashboards

resource "aws_cloudwatch_dashboard" "wafv2_dashboard" {
  dashboard_name = var.waf_dashboard_name
  dashboard_body = <<EOF
    {
      "widgets": [
        {
            "height": 6,
            "width": 24,
            "y": 0,
            "x": 0,
            "type": "log",
            "properties": {
                "query": "SOURCE '${var.waf_log_group_name}' | fields httpRequest.uri, action, nonTerminatingMatchingRules.0.action, nonTerminatingMatchingRules.0.ruleId, ruleGroupList.0.excludedRules.0.exclusionType,ruleGroupList.0.excludedRules.0.ruleId | stats count(*) as requestCount by httpRequest.uri, action, nonTerminatingMatchingRules.0.action, nonTerminatingMatchingRules.0.ruleId,ruleGroupList.0.excludedRules.0.exclusionType,ruleGroupList.0.excludedRules.0.ruleId",
                "region": "eu-west-2",
                "stacked": false,
                "title": "Log group: ${var.waf_log_group_name}",
                "view": "table"
            }
        },
        {
            "height": 6,
            "width": 6,
            "y": 6,
            "x": 0,
            "type": "metric",
            "properties": {
                "metrics": [
                    [ "AWS/WAFV2", "CountedRequests", "WebACL", "${var.waf_name}", "Region", "eu-west-2", "Rule", "CrossSiteScripting_BODY" ],
                    [ "...", "${var.non_gb_rule_metric_name}" ],
                    [ "...", "${var.ip_reputation_list_metric_name}" ],
                    [ "...", "${var.common_rule_set_metric_name}" ],
                    [ "...", "${var.sql_injection_rules_metric}" ],
                    [ "...", "${var.bad_input_metric_name}" ],
                    [ ".", "AllowedRequests", ".", ".", ".", ".", ".", "ALL" ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "eu-west-2",
                "period": 10,
                "setPeriodToTimeRange": true,
                "stat": "Minimum"
            }
        }
      ]
    }
  EOF
}
