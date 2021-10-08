#!/bin/bash
set -e

[ -z "$PROFILE" ] && PROFILE="nonprod"

COGNITO_GROUPS=(API_USER)
COGNITO_ADMIN_USER="service-finder-admin@nhs.net"

COGNITO_ADMIN_PASSWORD=$(
    aws secretsmanager get-secret-value \
        --secret-id $PROJECT_GROUP_SHORT-$PROJECT_NAME_SHORT-$ENVIRONMENT-cognito-admin-password \
        --region $AWS_REGION \
        --query 'SecretString' \
        --output text)

USER_POOL_ID=$(
    aws cognito-idp list-user-pools \
        --query "UserPools[?Name=='$COGNITO_USER_POOL'].Id" \
    --region $AWS_REGION \
        --max-results 60 \
        --output text)

USER_POOL_CLIENT_ID=$(
    aws cognito-idp list-user-pool-clients \
        --user-pool-id $USER_POOL_ID \
    --region $AWS_REGION \
        --query 'UserPoolClients[].ClientId' \
        --output text)

USER_POOL_CLIENT_SECRET=$(
    aws cognito-idp describe-user-pool-client \
        --user-pool-id $USER_POOL_ID \
        --client-id $USER_POOL_CLIENT_ID \
    --region $AWS_REGION \
        --query 'UserPoolClient.ClientSecret' \
        --output text)

function cognito_group_exists {

    COGNITO_GROUP=$1

    FOUND=$(
        aws cognito-idp get-group \
        --region $AWS_REGION \
            --group-name $COGNITO_GROUP \
            --user-pool-id $USER_POOL_ID 2> /dev/null | grep -c "\"GroupName\": \"$COGNITO_GROUP\"")
    [ ${FOUND} -eq 1 ] && return 0 || return 1
}

function cognito_user_exists {

    COGNITO_USER=$1

    FOUND=$(
        aws cognito-idp admin-get-user \
            --region $AWS_REGION \
            --user-pool-id $USER_POOL_ID \
            --username $COGNITO_USER 2> /dev/null | grep -c CONFIRMED)
    [ ${FOUND} -eq 1 ] && return 0 || return 1
}

function cognito_setup_groups {

    for COGNITO_GROUP in "${COGNITO_GROUPS[@]}"; do
        if ! cognito_group_exists $COGNITO_GROUP; then
            aws cognito-idp create-group \
            --region $AWS_REGION \
                --group-name $COGNITO_GROUP \
                --user-pool-id $USER_POOL_ID
        else
            >&2 echo "Group '$COGNITO_GROUP' already exists"
        fi
    done
}

function cognito_add_user_to_group {

    COGNITO_USER=$1
    COGNITO_GROUP=$2

    if cognito_group_exists $COGNITO_GROUP; then
        aws cognito-idp admin-add-user-to-group \
        --region $AWS_REGION \
            --group-name $COGNITO_GROUP \
            --user-pool-id $USER_POOL_ID \
            --username $COGNITO_USER \
        || >&2 echo "Could not add user '$COGNITO_USER' to the '$COGNITO_GROUP' group"
    else
        >&2 echo "Group '$COGNITO_GROUP' does not exist"
    fi
}

function cognito_setup_user {

    COGNITO_USER=$1; shift
    COGNITO_GROUPS_SUBSET=("$@")

    if ! cognito_user_exists $COGNITO_USER; then
        SECRET_HASH=$(calculate_secret_hash $COGNITO_USER)
        aws cognito-idp sign-up \
        --region $AWS_REGION \
            --client-id $USER_POOL_CLIENT_ID \
            --username $COGNITO_USER \
            --password $COGNITO_ADMIN_PASSWORD \
            --secret-hash $SECRET_HASH && \
        aws cognito-idp admin-confirm-sign-up \
        --region $AWS_REGION \
            --user-pool-id $USER_POOL_ID \
            --username $COGNITO_USER
    else
        >&2 echo "User '$COGNITO_USER' already exists"
    fi

    for COGNITO_GROUP in "${COGNITO_GROUPS_SUBSET[@]}"; do
        cognito_add_user_to_group $COGNITO_USER $COGNITO_GROUP
    done
}

function calculate_secret_hash {

    COGNITO_USER=$1

    echo -n "${COGNITO_USER}${USER_POOL_CLIENT_ID}" | openssl dgst -sha256 -hmac "$USER_POOL_CLIENT_SECRET" -binary | base64
}

function cognito_setup_users_and_groups {

    cognito_setup_groups
    cognito_setup_user $COGNITO_ADMIN_USER "${COGNITO_GROUPS[@]}"
}

function main {
    cognito_setup_users_and_groups
}

main
