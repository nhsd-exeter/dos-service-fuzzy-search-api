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


resource "aws_s3_bucket_public_access_block" "es_snapshots_public_access" {
  bucket                  = aws_s3_bucket.es_snapshots.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
