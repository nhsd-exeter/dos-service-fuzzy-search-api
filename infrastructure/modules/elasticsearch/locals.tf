locals {
  availability_zone_count_list = var.zone_awareness_enabled ? [var.availability_zone_count] : []

  #subnet_ids = var.instance_count == "1" ? [var.private_subnets_ids[0]] : var.private_subnets_id
  subnet_ids = var.availability_zone_count == "1" ? [var.private_subnets_ids[0]] : (var.availability_zone_count == "2" ? [var.private_subnets_ids[0], var.private_subnets_ids[1]] : (var.availability_zone_count == "3" ? [var.private_subnets_ids[0], var.private_subnets_ids[1], var.private_subnets_ids[3]] : null))

}
