resource "aws_lambda_function" "dos_replica_etl_lambda" {
  filename         = data.archive_file.dos_replica_etl_function.output_path
  function_name    = local.dos_replica_etl_function_name
  description      = local.dos_replica_etl_description
  role             = aws_iam_role.dos_replica_etl_lambda_role.arn
  handler          = "dos_replica_etl.lambda_handler"
  source_code_hash = data.archive_file.dos_replica_etl_function.output_base64sha256
  runtime          = local.dos_replica_etl_runtime
  timeout          = local.dos_replica_etl_timeout
  memory_size      = local.dos_replica_etl_memory_size
  publish          = true
  tags             = local.standard_tags
  layers           = [local.dos_replica_etl_core_dos_python_libs_arn]
  environment {
    variables = {
      USR                = local.dos_replica_etl_db_user
      SOURCE_DB          = local.dos_replica_etl_source_db
      ENDPOINT           = local.dos_replica_etl_db_endpoint
      PORT               = local.dos_replica_etl_db_port
      REGION             = local.dos_replica_etl_db_region
      SECRET_NAME        = local.dos_replica_etl_db_secret_name
      SECRET_KEY         = local.dos_replica_etl_db_secret_key
      ES_DOMAIN_ENDPOINT = local.es_domain_endpoint
    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [local.dos_replica_etl_vpc_security_group]
  }
}
resource "aws_iam_role" "dos_replica_etl_lambda_role" {
  name               = local.dos_replica_etl_iam_name
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

resource "aws_iam_role_policy" "uec-sf-dos-extract" {
  name   = local.dos_replica_etl_policy_name
  role   = aws_iam_role.dos_replica_etl_lambda_role.name
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:Describe*",
        "secretsmanager:Get*",
        "secretsmanager:List*"
      ],
      "Resource": "${local.dos_replica_etl_db_secret_arn}"
    },
    {
      "Effect": "Allow",
      "Action": [
        "es:*"
      ],
      "Resource": "${local.es_domain_arn}"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "AWSLambdaVPCAccessExecutionRole" {
  role       = aws_iam_role.dos_replica_etl_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "rdsDataReadOnlyAccessExtract" {
  role       = aws_iam_role.dos_replica_etl_lambda_role.name
  policy_arn = local.rds_data_read_only_access_policy_arn
}

resource "aws_cloudwatch_event_rule" "dos_replica_etl_cloudwatch_event" {
  name                = local.dos_replica_etl_cloudwatch_event_name
  description         = local.dos_replica_etl_cloudwatch_event_description
  schedule_expression = local.dos_replica_etl_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_dos_replica_etl_job" {
  rule      = aws_cloudwatch_event_rule.dos_replica_etl_cloudwatch_event.name
  target_id = local.dos_replica_etl_cloudwatch_event_target
  arn       = aws_lambda_function.dos_replica_etl_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_dos_replica_etl" {
  statement_id  = local.dos_replica_etl_cloudwatch_event_statement
  action        = local.dos_replica_etl_cloudwatch_event_action
  function_name = local.dos_replica_etl_function_name
  principal     = local.dos_replica_etl_cloudwatch_event_princinple
  source_arn    = aws_cloudwatch_event_rule.dos_replica_etl_cloudwatch_event.arn
}
