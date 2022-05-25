data "terraform_remote_state" "eks" {
  backend = "s3"
  config = {
    bucket = var.terraform_platform_state_store
    key    = var.eks_terraform_state_key
    region = var.aws_region
  }
}
resource "aws_iam_role" "iam_host_role" {
  path = "/"
  name = var.service_account_iam_role_name

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement" : [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated" : "arn:aws:iam::${var.aws_account_id}:oidc-provider/${trimprefix(data.terraform_remote_state.eks.outputs.eks_oidc_issuer_url, "https://")}"
        },
        "Action": "sts:AssumeRoleWithWebIdentity",
        "Condition": {
          "StringLike": {
            "${trimprefix(data.terraform_remote_state.eks.outputs.eks_oidc_issuer_url, "https://")}:sub": "system:serviceaccount:${var.project_id}*:${var.application_service_account_name}"
        }
      }
    }
  ]
}
EOF
}
