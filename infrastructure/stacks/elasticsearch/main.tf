# Elasticsearch
module "sfs_elasticsearch" {
  source                = "../../modules/elasticsearch"
  service_prefix        = var.service_prefix
  service_prefix_short  = var.service_prefix
  es_domain_name        = var.es_domain_name
  es_snapshot_bucket    = var.es_snapshot_bucket
  es_snapshot_role      = var.es_snapshot_role
  vpc_id                = data.terraform_remote_state.vpc.outputs.vpc_id
  private_subnets_ids   = data.terraform_remote_state.vpc.outputs.private_subnets
  eks_security_group_id = data.terraform_remote_state.security-groups-k8s.outputs.eks_worker_additional_sg_id
  availability_zone_count = local.sfs_elasticsearch["availability_zone_count"]
  component               = local.sfs_elasticsearch["component"]
  elasticsearch_version   = local.sfs_elasticsearch["elasticsearch_version"]
  encrypt_at_rest         = local.sfs_elasticsearch["encrypt_at_rest"]
  node_to_node_encryption = local.sfs_elasticsearch["node_to_node_encryption"]
  instance_type           = local.sfs_elasticsearch["instance_type"]
  instance_count          = local.sfs_elasticsearch["instance_count"]
  volume_size_gb          = local.sfs_elasticsearch["volume_size_gb"]
  zone_awareness_enabled  = local.sfs_elasticsearch["zone_awareness_enabled"]
  tags                    = local.standard_tags
}
