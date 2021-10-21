#!/bin/bash
set -e

COGNITO_ADMIN_PASSWORD=$(
    aws secretsmanager get-secret-value \
        --secret-id $PROJECT_GROUP_SHORT-$PROJECT_NAME_SHORT-$ENVIRONMENT-cognito-admin-password \
        --region $AWS_REGION \
        --query 'SecretString' \
        --output text)

function updatePasswordInEnvFile {

  sed "s/admin_password/$COGNITO_ADMIN_PASSWORD/g" "$@" > "$APPLICATION_TEST_DIR"/contract/environments/non-prod-deploy_auth.postman_environment.json

}

updatePasswordInEnvFile "$1"
