#!/bin/sh -e

SNAPSHOT_NAME=${PROFILE}-$(date '+%Y%m%d%H%M%S')
REPO_NAME=snapshot-repo-${PROFILE}

aws_register_elasticsearch_snapshot_repository() {
    python register-es-repo.py \
    "${ES_ENDPOINT}" \
    "${AWS_ACCOUNT_ID}" \
    "${REPO_NAME}" \
    "${TF_VAR_es_snapshot_bucket}" \
    "${TF_VAR_es_snapshot_role}"
}

aws_create_elasticsearch_snapshot() {
    aws_register_elasticsearch_snapshot_repository
    curl -XPUT "https://${ES_ENDPOINT}/_snapshot/${REPO_NAME}/${SNAPSHOT_NAME}"
    echo The snapshot name is "${SNAPSHOT_NAME}"
}

aws_create_elasticsearch_snapshot
