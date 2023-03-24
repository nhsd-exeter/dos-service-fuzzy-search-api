# data "terraform_remote_state" "vpc" {
#   backend = "s3"
#   config = {
#     bucket = var.terraform_platform_state_store
#     key    = var.vpc_terraform_state_key
#     region = var.aws_region
#   }
# }

# Texas-managed VPC info
data "aws_vpcs" "vpcs" {
  tags = {
    Name = var.texas_vpc_name
  }
}

data "aws_vpc" "vpc" {
  count = length(data.aws_vpcs.vpcs.ids)
  id    = tolist(data.aws_vpcs.vpcs.ids)[count.index]
}

data "aws_subnet_ids" "texas_subnet_ids" {
  vpc_id = data.aws_vpc.vpc[0].id
}

data "aws_subnet_ids" "texas_private_subnet_ids_filtered" {
  vpc_id = data.aws_vpc.vpc[0].id
  filter {
    name   = "tag:Name"
    values = ["*private*"]
  }
}

# Texas private subnets as a map of objects (keyed on subnet ID)
data "aws_subnet" "texas_private_subnet_ids_as_map_of_objects" {
  for_each = data.aws_subnet_ids.texas_private_subnet_ids_filtered.ids
  id       = each.value
}

# Texas private subnets as an array of objects (numerically keyed)
data "aws_subnet" "texas_private_subnet_ids_as_array_of_objects" {
  count = length(data.aws_subnet_ids.texas_private_subnet_ids_filtered.ids)
  id    = tolist(data.aws_subnet_ids.texas_private_subnet_ids_filtered.ids)[count.index]
}

data "terraform_remote_state" "security-groups-k8s" {
  backend = "s3"
  config = {
    bucket = var.terraform_platform_state_store
    key    = var.security_groups_k8s_terraform_state_key
    region = var.aws_region
  }
}
