PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))/../..))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

# ==============================================================================

config: ### Configure the contract test environment
	make project-config

test: start run stop ### Execute the contract test suite

start: config ### Start the contract test project
	make docker-compose-start YML=$(DOCKER_DIR)/mockservice.docker-compose.yml

stop: ### Stop the contract test project
	make docker-compose-stop YML=$(DOCKER_DIR)/mockservice.docker-compose.yml

run: ### Run Postman test suite against the Wiremock mappings
	make docker-run-postman \
		DIR="$(APPLICATION_TEST_DIR)/contract" \
		CMD=" \
			run FuzzySearchApiContractTests_collection.json -e environments/environment.json --insecure \
		"

run-smoke: ### Run Postman test suite against deployed application ###
	eval "$$(make aws-assume-role-export-variables)"
	$(APPLICATION_TEST_DIR)/contract/scripts/secrets.sh $(APPLICATION_TEST_DIR)/contract/environments/non-prod-deploy.postman_environment.json
	make docker-run-postman \
		DIR="$(APPLICATION_TEST_DIR)/contract" \
		CMD=" \
			run FuzzySearchApiSmokeTests_collection.json -e environments/non-prod-deploy.postman_environment.json \
		"
	make stop

reload: ### Reload the Wiremock mappings
	curl -d '' http://localhost:8080/__admin/mappings/reset
