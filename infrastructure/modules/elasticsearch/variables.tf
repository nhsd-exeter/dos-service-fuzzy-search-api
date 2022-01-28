# General variables
variable "component" {
  description = "Name of the component to deploy."
}

variable "tags" {
  type        = map(string)
  description = "A list of standard tags for any given resource."
}

variable "service_prefix" {
  description = "The prefix used to adhere to the naming conventions"
}

variable "service_prefix_short" {
  description = "The prefix used to adhere to the naming conventions"
}

variable "vpc_id" {
  description = "VPC identifier."
}

variable "private_subnets_ids" {
  default     = []
  description = "List of private subnet ids for the VPC."
}

variable "eks_security_group_id" {
  description = "Id of the EKS security group identifier."
}

# Elasticsearch variables
variable "availability_zone_count" {
  description = "Number of Availability Zones for the domain to use with zone_awareness_enabled."
}

variable "elasticsearch_version" {
  description = "The version of ElasticSearch to deploy."
}

variable "encrypt_at_rest" {
  description = "Whether to enable encryption at rest. Can't be true for t2.small."
}

variable "node_to_node_encryption" {
  description = "Whether to enable node_to_node encryption. Can't be true for t2.small."
}

variable "instance_count" {
  description = "Number of instances in the ElasticSearch cluster."
}

variable "instance_type" {
  description = "Instance type of data nodes in the ElasticSearch cluster."
}

variable "volume_size_gb" {
  description = "The size of EBS volumes attached to data nodes in GB."
}

variable "zone_awareness_enabled" {
  description = "Indicates whether zone awareness is enabled."
}

variable "es_snapshot_bucket" {
  description = "Name of the elastic search snapshot bucket."
}

variable "es_snapshot_role" {
  description = "Name of the elastic search snapshot role."
}

variable "es_domain_name" {
  description = "Name of the elastic search domain"
}
