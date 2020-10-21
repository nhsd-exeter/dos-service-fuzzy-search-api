output "elasticsearch_domain_arn" {
  value = aws_elasticsearch_domain.elasticsearch_service.arn
}

output "elasticsearch_endpoint" {
  value = aws_elasticsearch_domain.elasticsearch_service.endpoint
}
