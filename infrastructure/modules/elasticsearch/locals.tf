locals {
  availability_zone_count_list = var.zone_awareness_enabled ? [var.availability_zone_count] : []

  subnet_ids  = var.instance_count == "1" ? [var.public_subnets_ids[0]] : var.public_subnets_ids

}
