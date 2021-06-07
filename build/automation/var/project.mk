ORG_NAME = nhsd-exeter
PROGRAMME = uec
PROJECT_GROUP = uec/dos-api
PROJECT_GROUP_SHORT = uec-dos-api
PROJECT_NAME = service-fuzzy-search
PROJECT_NAME_SHORT = sfs
PROJECT_DISPLAY_NAME = DoS Service Fuzzy Search API
PROJECT_ID = $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)

ROLE_PREFIX = UECDoSAPI
PROJECT_TAG = $(PROJECT_NAME)
SERVICE_TAG = $(PROJECT_GROUP_SHORT)
SERVICE_TAG_COMMON = texas

PROJECT_TECH_STACK_LIST = java,terraform

DOCKER_REPOSITORIES =
SSL_DOMAINS_PROD =

# ==============================================================================

# Java version for the API is 11 because this is the current version of Java that the AWS
# Lambda function supports.
JAVA_VERSION = 14
