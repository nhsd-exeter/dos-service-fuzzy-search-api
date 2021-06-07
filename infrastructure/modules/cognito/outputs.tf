output "pool_arn" {
  value       = aws_cognito_user_pool.pool.arn
  description = "Cognito pool ARN"
}

output "pool_id" {
  value       = aws_cognito_user_pool.pool.id
  description = "ID of the Cognito Pool."
}

output "pool_client_id" {
  value       = aws_cognito_user_pool_client.client.id
  description = "ID of the Cognito Pool Client."
}

output "pool_client_secret" {
  value       = aws_cognito_user_pool_client.client.client_secret
  description = "Secret for the Cognito User pool client."
}
