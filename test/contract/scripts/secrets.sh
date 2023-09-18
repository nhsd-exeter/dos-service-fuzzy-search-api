#!/bin/bash

set -e

COGNITO_ADMIN_PASSWORD=$(
    aws secretsmanager get-secret-value \
        --secret-id $PROJECT_GROUP_SHORT-$PROJECT_NAME_SHORT-$ENVIRONMENT-cognito-password \
        --region $AWS_REGION \
        --query 'SecretString' \
        --output text)


function getPassword {

  echo "$COGNITO_ADMIN_PASSWORD" | jq .AUTHENTICATION_PASSWORD

}

function getPasswordFromStore {

  COGNITO=$(getPassword)

  sed -i -e "s/\"REPLACE_PASS\"/$COGNITO/g" "$@"

}

getPasswordFromStore "$1"
