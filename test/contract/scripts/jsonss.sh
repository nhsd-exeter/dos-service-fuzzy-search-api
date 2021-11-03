#!/bin/bash

set -e

COGNITO_ADMIN_PASSWORD=$(
    aws secretsmanager get-secret-value \
        --secret-id $PROJECT_GROUP_SHORT-$PROJECT_NAME_SHORT-$ENVIRONMENT-cognito-passwords \
        --region $AWS_REGION \
        --query 'SecretString' \
        --output text)


function getPassword {

  echo "$COGNITO_ADMIN_PASSWORD" | jq .AUTHENTICATION_PASSWORD | tr -d '"'

}

getPassword
