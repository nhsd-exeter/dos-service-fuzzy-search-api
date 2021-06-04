resource "aws_secretsmanager_secret" "cognito_admin_password" {
  name                    = "${var.service_prefix}-cognito-admin-password"
  description             = "Password for Cognito admin"
  recovery_window_in_days = 0
  tags                    = var.tags
}

resource "aws_secretsmanager_secret_version" "cognito_admin_password" {
  secret_id     = aws_secretsmanager_secret.cognito_admin_password.id
  secret_string = random_password.cognito_admin_password.result
  lifecycle {
    ignore_changes = [secret_string]
  }
}

resource "random_password" "cognito_admin_password" {
  length      = 16
  min_upper   = 2
  min_lower   = 2
  min_numeric = 2
  special     = false
}
