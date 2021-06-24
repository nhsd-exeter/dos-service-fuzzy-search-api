
resource "aws_iam_role" "fuzzy-search-dynamodb_access" {
  name               = local.fuzzy-search-dynamodb_access_iam_name
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "dynamoDbFullAccessInsert" {
  role       = aws_iam_role.fuzzy-search-dynamodb_access.name
  policy_arn = local.dynamoDb_full_access_policy_arn
}
