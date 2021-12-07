PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

DOCKER_REGISTRY_LIVE = $(DOCKER_REGISTRY)/prod
# ==============================================================================
# Development workflow targets

prepare: ## Prepare environment
	make \
		git-config \
		docker-config

derive-build-tag:
	dir=$$(make _docker-get-dir NAME=api)
	echo $$(cat $$dir/VERSION) | \
				sed "s/YYYY/$$(date --date=$(BUILD_DATE) -u +"%Y")/g" | \
				sed "s/mm/$$(date --date=$(BUILD_DATE) -u +"%m")/g" | \
				sed "s/dd/$$(date --date=$(BUILD_DATE) -u +"%d")/g" | \
				sed "s/HH/$$(date --date=$(BUILD_DATE) -u +"%H")/g" | \
				sed "s/MM/$$(date --date=$(BUILD_DATE) -u +"%M")/g" | \
				sed "s/ss/$$(date --date=$(BUILD_DATE) -u +"%S")/g" | \
				sed "s/SS/$$(date --date=$(BUILD_DATE) -u +"%S")/g" | \
				sed "s/hash/$$(git rev-parse --short HEAD)/g"

compile: # Compile the project to make the target class (binary) files
	make docker-run-mvn \
		DIR="application/app" \
		CMD="compile"

build: project-config # Build project
	make docker-run-mvn \
		DIR="application/app" \
		CMD="-Dmaven.test.skip=true clean install" \
		LIB_VOLUME_MOUNT="true"
	mv \
		$(PROJECT_DIR)/application/app/target/dos-service-fuzzy-search-api-*.jar \
		$(PROJECT_DIR)/build/docker/api/assets/application/dos-service-fuzzy-search-api.jar
	make docker-build NAME=api

start: project-start # Start project

stop: project-stop # Stop project

restart: stop start

log: project-log # Show project logs

unit-test: # Run project unit tests
	make docker-run-mvn \
		DIR="application/app" \
		CMD="test"

coverage-report: # Generate jacoco test coverage reports
	make unit-test
	make docker-run-mvn \
		DIR="application/app" \
		CMD="jacoco:report"

load-test-services: # Load test services into elasticsearch
	sh ./data/services/create_test_services.sh

load-all-services: # Load bulk service data into elasticsearch
	sh ./data/services/create_all_services_local.sh

load-test-postcode-locations:
	sh ./data/locations/$(LOCATIONS_DATA_FILE)

run-contract-tests:
	make start PROFILE=local VERSION=$(VERSION)
	sleep 60
	cd test/contract
	make run
	cd ../../
	make stop

test: load-test-services # Test project
	make docker-run-mvn \
		DIR="application/app" \
		CMD="clean test" \
		LIB_VOLUME_MOUNT="true" \
		PROFILE=local \
		VARS_FILE=$(VAR_DIR)/profile/local.mk

push: # Push project artefacts to the registry
	make docker-push NAME=api

tag-release: # Create the release tag - mandatory DEV_TAG RELEASE_TAG
	make docker-login
	docker pull $(DOCKER_REGISTRY)/api:$(DEV_TAG)
	docker tag $(DOCKER_REGISTRY)/api:$(DEV_TAG) $(DOCKER_REGISTRY)/api:$(RELEASE_TAG)
	docker tag $(DOCKER_REGISTRY)/api:$(DEV_TAG) $(DOCKER_REGISTRY_LIVE)/api:$(RELEASE_TAG)
	docker push $(DOCKER_REGISTRY)/api:$(RELEASE_TAG)
	docker push $(DOCKER_REGISTRY_LIVE)/api:$(RELEASE_TAG)

deploy: # Deploy artefacts - mandatory: PROFILE=[name]
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make project-deploy PROFILE=$(PROFILE) STACK=$(DEPLOYMENT_STACKS)

project-populate-application-variables:
	export TTL=$$(make -s k8s-get-namespace-ttl)

	export COGNITO_USER_POOL_CLIENT_SECRET=$$(make -s project-aws-get-cognito-client-secret NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_CLIENT_ID=$$(make -s project-aws-get-cognito-client-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_ID=$$(make -s aws-cognito-get-userpool-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_JWT_VERIFICATION_URL=https://cognito-idp.eu-west-2.amazonaws.com/$${COGNITO_USER_POOL_ID}/.well-known/jwks.json
	export COGNITO_ADMIN_AUTH_PASSWORD=$$(make -s project-aws-get-admin-secret | jq .AUTHENTICATION_PASSWORD | tr -d '"')
	export ELASTICSEARCH_EP=$$(make aws-elasticsearch-get-endpoint DOMAIN=$(DOMAIN))
	export ELASTICSEARCH_URL=https://$${ELASTICSEARCH_EP}

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

project-aws-get-admin-secret: #Get AWS Pass
	aws secretsmanager get-secret-value \
		--secret-id $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(ENVIRONMENT)-cognito-passwords \
		--region $(AWS_REGION) \
		--query 'SecretString' \
		--output text

prepare-lambda-deployment: # Downloads the required libraries for the Lambda functions
	cd $(PROJECT_DIR)infrastructure/stacks/service_etl/functions/service_etl
	if [ $(BUILD_ID) -eq 0 ]; then
		pip install \
			-r requirements.txt \
			-t $(PROJECT_DIR)infrastructure/stacks/service_etl/functions/service_etl/deploy \
			--upgrade \
			--no-deps
	else
		pip install \
			-r requirements.txt \
			-t $(PROJECT_DIR)infrastructure/stacks/service_etl/functions/service_etl/deploy \
			--upgrade \
			--no-deps \
			--system
	fi
	cd $(PROJECT_DIR)infrastructure/stacks/service_etl/functions/service_etl/deploy
	rm -rf ./bin
	rm -rf ./*.dist-info
	rm -f LICENSE
	cp $(PROJECT_DIR)infrastructure/stacks/service_etl/functions/service_etl/service_etl.py \
		$(PROJECT_DIR)infrastructure/stacks/service_etl/functions/service_etl/deploy

plan: # Plan environment - mandatory: PROFILE=[name]
	make prepare-lambda-deployment
	make terraform-plan STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

provision: # Provision environment - mandatory: PROFILE=[name]
	make prepare-lambda-deployment
	make terraform-apply-auto-approve STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

project-populate-cognito: ## Populate cognito - optional: PROFILE=nonprod|prod,AWS_ROLE=Developer
	if $(ADD_DEFAULT_COGNITO_USERS); then \
		eval "$$(make aws-assume-role-export-variables)"
	$(PROJECT_DIR)/infrastructure/scripts/cognito.sh
	else
		echo 'Default users already added to pool';
	fi


clean: # Clean up project
	make stop
	docker network rm $(DOCKER_NETWORK) 2> /dev/null ||:

run-jmeter-performance-test:
	make run-jmeter JMETER_TEST_FOLDER_PATH=test/jmeter/tests/performance JMETER_TEST_FILE_PATH=test/jmeter/tests/performance/fuzzyPerformanceTest.jmx

run-jmeter-load-test:
	make run-jmeter JMETER_TEST_FOLDER_PATH=test/jmeter/tests/load JMETER_TEST_FILE_PATH=test/jmeter/tests/load/fuzzyLoadTest.jmx

run-jmeter-stress-test:
	make run-jmeter JMETER_TEST_FOLDER_PATH=test/jmeter/tests/stress JMETER_TEST_FILE_PATH=test/jmeter/tests/stress/fuzzyStressTest.jmx

deploy-jmeter-namespace:
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl create ns ${PROJECT_ID}-${PROFILE}-jmeter
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_slaves_deploy.yaml
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_slaves_svc.yaml
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_master_deploy.yaml
	make k8s-sts K8S_APP_NAMESPACE=${PROJECT_ID}-${PROFILE}-jmeter

destroy-jmeter-namespace:
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl delete ns ${PROJECT_ID}-${PROFILE}-jmeter


# ==============================================================================
# Supporting targets
get-auth-password:
	export AUTH_PASSWORD=$$(make -s project-aws-get-admin-secret | jq .ADMIN_PASSWORD | tr -d '"')


run-jmeter: # Run jmeter tests - mandatory: JMETER_TEST_FOLDER_PATH - test directory JMETER_TEST_FILE_PATH - the path of the jmeter tests to run
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	eval "$$(make get-auth-password)"
	sed -i 's|PASSWORD_TO_REPLACE|${COGNITO_ADMIN_AUTH_PASSWORD}|g' ${JMETER_TEST_FILE_PATH}
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl config set-context --current --namespace=${PROJECT_ID}-${PROFILE}-jmeter
	test/jmeter/scripts/jmeter_stop.sh
	test/jmeter/scripts/start_test.sh ${JMETER_TEST_FOLDER_PATH} ${JMETER_TEST_FILE_PATH}


trust-certificate: ssl-trust-certificate-project ## Trust the SSL development certificate

create-artefact-repositories: ## Create ECR repositories to store the artefacts
	make docker-create-repository NAME=api

# ==============================================================================
# Pipeline targets

build-artefact:
	echo TODO: $(@)

publish-artefact:
	echo TODO: $(@)

backup-data:
	echo TODO: $(@)

provision-infractructure:
	echo TODO: $(@)

deploy-artefact:
	echo TODO: $(@)

apply-data-changes:
	echo TODO: $(@)

# --------------------------------------

run-static-analisys:
	echo TODO: $(@)

run-unit-test:
	make unit-test

run-smoke-test:
	make start PROFILE=$(PROFILE) VERSION=$(API_IMAGE_TAG)
	sleep 60
	cd test/contract
	make run-smoke
	cd ../../
	make stop

run-integration-test:
	echo TODO: $(@)

run-functional-test:
	[ $$(make project-branch-func-test) != true ] && exit 0
	echo TODO: $(@)

run-performance-test:
	make test-performance NAME=fuzzyPerformanceTest.jmx

run-security-test:
	[ $$(make project-branch-sec-test) != true ] && exit 0
	echo TODO: $(@)

# --------------------------------------

remove-unused-environments:
	echo TODO: $(@)

remove-old-artefacts:
	echo TODO: $(@)

remove-old-backups:
	echo TODO: $(@)

# --------------------------------------

pipeline-send-notification:
	echo TODO: $(@)

pipeline-on-success:
	echo TODO: $(@)

pipeline-on-failure:
	echo TODO: $(@)

.SILENT: \
	derive-build-tag
