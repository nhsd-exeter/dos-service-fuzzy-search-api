locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
  }

  # Elasticsearch
  sfs_elasticsearch = {
    component               = "elasticsearch"
    availability_zone_count = var.es_availability_zone_count
    elasticsearch_version   = "OpenSearch_1.1"
    encrypt_at_rest         = true
    node_to_node_encryption = true
    instance_count          = var.es_instance_count
    instance_type           = var.es_instance_type
    volume_size_gb          = 10
    zone_awareness_enabled  = var.es_zone_awareness_enabled
  }

}
