#!/bin/sh -e

SNAPSHOT_NAME=${SNAPSHOT_NAME}
REPO_NAME=snapshot-repo-${PROFILE}

function aws_register_elasticsearch_snapshot_repository {
    endpoint=$1
    python register-es-repo.py \
    $endpoint \
    ${AWS_ACCOUNT_ID} \
    ${REPO_NAME} \
    ${TF_VAR_es_snapshot_bucket} \
    ${TF_VAR_es_snapshot_role}
}

function aws_load_elasticsearch_snapshot {
    aws_register_elasticsearch_snapshot_repository ${ES_ENDPOINT}
    # Delete indexes
    curl -XDELETE "https://${ES_ENDPOINT}/_all"
    # Restore latest snapshot
    curl -XPOST "https://${ES_ENDPOINT}/_snapshot/${REPO_NAME}/${SNAPSHOT_NAME}/_restore"
    echo The snapshot ${SNAPSHOT_NAME} has been loaded
}

aws_load_elasticsearch_snapshot
