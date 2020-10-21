resource "aws_security_group" "elasticsearch" {
  vpc_id      = var.vpc_id
  name        = "${var.service_prefix}-elastic-search"
  description = "Elastic Search Security Group"
  tags = merge(
    var.tags,
    {
      "Name" = "${var.service_prefix}-elastic-search"
    },
  )
}

resource "aws_security_group_rule" "allow_all_out" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.elasticsearch.id
}

resource "aws_security_group_rule" "allow_in_from_eks_worker" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  source_security_group_id = var.eks_security_group_id
  security_group_id        = aws_security_group.elasticsearch.id
  description              = "Allow access in from Eks-worker to elasticsearch"
}

resource "aws_security_group_rule" "allow_in_from_vpn" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  source_security_group_id = var.vpn_security_group_id
  security_group_id        = aws_security_group.elasticsearch.id
  description              = "Allow access in from VPN to elasticsearch"
}
