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
JAVA_VERSION = 11
