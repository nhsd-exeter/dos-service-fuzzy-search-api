data "aws_iam_policy_document" "cloudwatch_elasticsearch" {
  statement {
    sid = "AllowLogAccesstoES"

    principals {
      type        = "Service"
      identifiers = ["es.amazonaws.com"]
    }

    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:DescribeLogGroups",
      "logs:DescribeLogStreams",
      "logs:PutLogEvents",
      "logs:GetLogEvents",
      "logs:FilterLogEvents",
    ]

    resources = ["*"]
  }
}

# S3 bucket snapshots
data "aws_iam_policy_document" "snapshot_policy" {

  statement {
    actions = [
      "s3:ListBucket",
      "s3:GetObject",
      "s3:PutObject",
      "s3:DeleteObject",
      "iam:PassRole",
    ]

    resources = [
      aws_s3_bucket.es_snapshots.arn,
      "${aws_s3_bucket.es_snapshots.arn}/*",
    ]
  }
}
data "aws_iam_policy_document" "assume_policy_es" {
  statement {
    actions = [
      "sts:AssumeRole",
    ]

    principals {
      identifiers = ["es.amazonaws.com"]
      type        = "Service"
    }
  }
}
