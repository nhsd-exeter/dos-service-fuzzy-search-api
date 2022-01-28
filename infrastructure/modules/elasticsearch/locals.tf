locals {
  availability_zone_count_list = var.zone_awareness_enabled ? [var.availability_zone_count] : []

  subnet_ids = var.instance_count == "1" ? [var.private_subnets_ids[0]] : var.private_subnets_ids

}
