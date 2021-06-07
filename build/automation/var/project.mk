PROGRAMME = uec
PROJECT_GROUP = uec/dos-api
PROJECT_GROUP_SHORT = uec-dos-api
PROJECT_NAME = service-fuzzy-search
PROJECT_NAME_SHORT = sfs

ROLE_PREFIX = UECAPISFS
SERVICE_TAG = $(PROJECT_GROUP_SHORT)
PROJECT_TAG = $(PROJECT_NAME)

# Java version for the API is 11 because this is the current version of Java that the AWS
# Lambda function supports.
JAVA_VERSION = 14

project-populate-application-variables:
	export TTL=$$(make -s k8s-get-namespace-ttl)

	export COGNITO_USER_POOL_CLIENT_SECRET=$$(make -s project-aws-get-cognito-client-secret NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_CLIENT_ID=$$(make -s project-aws-get-cognito-client-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_ID=$$(make -s aws-cognito-get-userpool-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_JWT_VERIFICATION_URL=https://cognito-idp.eu-west-2.amazonaws.com/$${COGNITO_USER_POOL_ID}/.well-known/jwks.json

# Talk to Dan
project-aws-get-cognito-client-id: # Get AWS cognito client id - mandatory: NAME
	aws cognito-idp list-user-pool-clients \
		--user-pool-id $$(make -s aws-cognito-get-userpool-id NAME=$(NAME)) \
		--region $(AWS_REGION) \
		--query 'UserPoolClients[].ClientId' \
		--output text

project-aws-get-cognito-client-secret: # Get AWS secret - mandatory: NAME
	aws cognito-idp describe-user-pool-client \
		--user-pool-id $$(make -s aws-cognito-get-userpool-id NAME=$(NAME)) \
		--client-id $$(make -s project-aws-get-cognito-client-id NAME=$(NAME)) \
		--region $(AWS_REGION) \
		--query 'UserPoolClient.ClientSecret' \
		--output text
