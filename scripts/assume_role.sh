#!/usr/bin/env bash

# Fail on error
set -e
aws --version
ENV=${1?You must pass in the env you are deploying to i.e live-lk8s-nonprod or live-lk8s-prod for service teams}
SERVICETEAM=${2?You must pass in your service team e.g. dspt - this must match the prefix for the role you are going to assume e.g. dspt-jenkins-nonprod-assume-role}

ACCOUNT='aws_account_ids'
AWS_ACCOUNT_ID=$(aws secretsmanager get-secret-value --secret-id ${ACCOUNT} --region eu-west-2| \
            jq --raw-output '.SecretString' | jq -r --arg envName "${ENV}" '.[$envName]')

echo ${AWS_ACCOUNT_ID}

if [ ${#AWS_ACCOUNT_ID} -ne '12' ];
  then echo "Invalid env name ${ENV}"
  exit 1
fi

AWS_ROLE="${SERVICETEAM}-jenkins-assume-role"
AWS_ROLE_SESSION="${SERVICETEAM}-jenkins-mom"

# call assume role
assume_role_result=$(aws sts assume-role --role-arn arn:aws:iam::${AWS_ACCOUNT_ID}:role/${AWS_ROLE} --role-session-name ${AWS_ROLE_SESSION} --duration-seconds 900)

# parse response
export AWS_ACCESS_KEY_ID=$(echo ${assume_role_result} | jq --raw-output '.Credentials.AccessKeyId')
export AWS_SECRET_ACCESS_KEY=$(echo ${assume_role_result} | jq --raw-output '.Credentials.SecretAccessKey')
export AWS_SESSION_TOKEN=$(echo ${assume_role_result} | jq --raw-output '.Credentials.SessionToken')
