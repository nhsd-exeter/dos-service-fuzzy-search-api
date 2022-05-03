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

run-contract:
	make docker-run-postman \
		DIR="$(APPLICATION_TEST_DIR)/contract" \
		CMD=" \
			run collections/FuzzySearchApiContractTests_collection.json -e environments/Fuzzy_Contract_Tests_Env.postman_environment.json --insecure \
		"

run-smoke: ### Run Postman test suite against deployed application ###

	sed -i -e "s/REPLACE_AUTH_HOST/$(FUZZY_DOMAIN)/g" $(APPLICATION_TEST_DIR)/contract/environments/Fuzzy_Smoke_Tests_Env.postman_environment.json
	sed -i -e "s/REPLACE_HOST/$(FUZZY_DOMAIN)/g" $(APPLICATION_TEST_DIR)/contract/environments/Fuzzy_Smoke_Tests_Env.postman_environment.json

	case $(PROFILE) in
		dev|pt|test) #pt = peformance testing
			echo Profile: $(PROFILE) Executing mock token test
			sed -i -e "s/REPLACE_ACCESS_TOKEN/MOCK_FUZZY_API_ACCESS_TOKEN/g" $(APPLICATION_TEST_DIR)/contract/environments/Fuzzy_Smoke_Tests_Env.postman_environment.json
			make docker-run-postman DIR="$(APPLICATION_TEST_DIR)/contract" CMD="run collections/FuzzySearchApiSmokeTests_nonAuth.postman_collection.json -e environments/Fuzzy_Smoke_Tests_Env.postman_environment.json"
			;;
		pd|dm|sg) #PD = Prod, dm = Demo, sg = staging
			echo Profile: $(PROFILE) Executing real token test
			eval "$$(make aws-assume-role-export-variables)"
			$(APPLICATION_TEST_DIR)/contract/scripts/secrets.sh $(APPLICATION_TEST_DIR)/contract/environments/Fuzzy_Smoke_Tests_Env.postman_environment.json
			make docker-run-postman DIR="$(APPLICATION_TEST_DIR)/contract" CMD="run collections/FuzzySearchSmokeTest.postman_collection.json -e environments/Fuzzy_Smoke_Tests_Env.postman_environment.json"
			;;
			*)
			echo "Profile: $(PROFILE) Env unsupported"
		esac

	make stop

reload: ### Reload the Wiremock mappings
	curl -d '' http://localhost:8080/__admin/mappings/reset
