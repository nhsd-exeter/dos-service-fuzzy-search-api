resource "aws_iam_role" "snapshot" {
  name               = var.es_snapshot_role
  description        = "Role used for the Elasticsearch domain"
  assume_role_policy = data.aws_iam_policy_document.assume_policy_es.json
  tags               = var.tags
}

resource "aws_iam_policy" "snapshot_policy" {
  name        = var.es_snapshot_role
  description = "Policy allowing the Elasticsearch domain access to the snapshots S3 bucket"
  policy      = data.aws_iam_policy_document.snapshot_policy.json
}

resource "aws_iam_role_policy_attachment" "snapshot_policy_attachment" {
  role       = aws_iam_role.snapshot.id
  policy_arn = aws_iam_policy.snapshot_policy.arn
}
