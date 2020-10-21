resource "aws_s3_bucket" "es_snapshots" {
  bucket        = var.es_snapshot_bucket
  force_destroy = false
  acl           = "private"

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  versioning {
    enabled = true
  }

  tags = var.tags
}
