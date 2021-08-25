resource "aws_lambda_function" "service_etl_lambda" {
  filename         = data.archive_file.service_etl_function.output_path
  function_name    = local.service_etl_function_name
  description      = local.service_etl_description
  role             = aws_iam_role.service_etl_lambda_role.arn
  handler          = "service_etl.lambda_handler"
  source_code_hash = data.archive_file.service_etl_function.output_base64sha256
  runtime          = local.service_etl_runtime
  timeout          = local.service_etl_timeout
  memory_size      = local.service_etl_memory_size
  publish          = true
  tags             = local.standard_tags
  layers           = [local.service_etl_core_dos_python_libs_arn]
  environment {
    variables = {
      USR                = local.service_etl_db_user
      SOURCE_DB          = local.service_etl_source_db
      ENDPOINT           = local.service_etl_db_endpoint
      PORT               = local.service_etl_db_port
      REGION             = local.service_etl_db_region
      SECRET_NAME        = local.service_etl_db_secret_name
      SECRET_KEY         = local.service_etl_db_secret_key
      ES_DOMAIN_ENDPOINT = local.es_domain_endpoint
    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [
      local.dos_sf_replica_db_sg,
      aws_security_group.service_etl_security_group.id
    ]
  }
}

resource "aws_security_group" "service_etl_security_group" {
  name        = "uec-sf-${var.profile}-dos-replica-etl_sg"
  description = "Allows outbound traffic from ETL lambda"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  egress = [
    {
      from_port        = 0
      to_port          = 0
      protocol         = "-1"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = null
      description      = null
      prefix_list_ids  = null
      security_groups  = null
      self             = null
    }
  ]
}

resource "aws_security_group_rule" "service_etl_security_group_rule" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  description              = "allows lambda etl process to insert data into ES"
  security_group_id        = local.es_domain_security_group_id
  source_security_group_id = aws_security_group.service_etl_security_group.id
}


resource "aws_iam_role" "service_etl_lambda_role" {
  name               = local.service_etl_iam_name
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
  name   = local.service_etl_policy_name
  role   = aws_iam_role.service_etl_lambda_role.name
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
      "Resource": "${local.service_etl_db_secret_arn}"
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
  role       = aws_iam_role.service_etl_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "rdsDataReadOnlyAccessExtract" {
  role       = aws_iam_role.service_etl_lambda_role.name
  policy_arn = local.rds_data_read_only_access_policy_arn
}

resource "aws_cloudwatch_event_rule" "service_etl_cloudwatch_event" {
  name                = local.service_etl_cloudwatch_event_name
  description         = local.service_etl_cloudwatch_event_description
  schedule_expression = local.service_etl_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_service_etl_job" {
  rule      = aws_cloudwatch_event_rule.service_etl_cloudwatch_event.name
  target_id = local.service_etl_cloudwatch_event_target
  arn       = aws_lambda_function.service_etl_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_service_etl" {
  statement_id  = local.service_etl_cloudwatch_event_statement
  action        = local.service_etl_cloudwatch_event_action
  function_name = local.service_etl_function_name
  principal     = local.service_etl_cloudwatch_event_princinple
  source_arn    = aws_cloudwatch_event_rule.service_etl_cloudwatch_event.arn
}
