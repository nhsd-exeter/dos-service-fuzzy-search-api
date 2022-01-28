PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)
include $(abspath $(PROJECT_DIR)/test/jmeter/jMeter.mk)

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
	cp \
		$(PROJECT_DIR)/build/automation/etc/certificate/* \
		$(PROJECT_DIR)/application/src/main/resources/certificate
	make docker-run-mvn \
		DIR="application/app" \
		CMD="-Dmaven.test.skip=true clean install" \
		LIB_VOLUME_MOUNT="true"
	mv \
		$(PROJECT_DIR)/application/app/target/dos-service-fuzzy-search-api-*.jar \
		$(PROJECT_DIR)/build/docker/api/assets/application/dos-service-fuzzy-search-api.jar
	make docker-build NAME=api

	cp $(PROJECT_DIR)/test/wiremock_mappings/*.json \
	$(PROJECT_DIR)build/docker/mock-postcode-api/assets/wiremock_mappings/
	make docker-build NAME=mock-postcode-api


quick-start: project-start # Start project

start: # Start project and load data in to elastic search
	make project-start
	echo "Waiting for elastic search to be fully initalised"
	sleep 30
	make load-all-services
	make load-test-services

stop: project-stop # Stop project

restart: stop start

quick-restart: stop quick-start

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
	make quick-start PROFILE=local VERSION=$(VERSION)
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

debug:
	make start 2> /dev/null ||:
	docker rm --force fuzzysearch 2> /dev/null ||:
	make docker-run-mvn-lib-mount \
		NAME=fuzzysearch \
		DIR=application/app \
		CMD="spring-boot:run \
			-Dspring-boot.run.jvmArguments=' \
			-Xdebug \
			-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:9999 \
			' \
		" \
		ARGS=" \
		--env SPRING_PROFILES_ACTIVE='$(SPRING_PROFILES_ACTIVE)' \
		--env  CERTIFICATE_DOMAIN='$(CERTIFICATE_DOMAIN)' \
		--env  ALLOWED_ORIGINS='$(ALLOWED_ORIGINS)' \
		--env  API_SERVICE_SEARCH_ENDPOINT='$(API_SERVICE_SEARCH_ENDPOINT)' \
		--env  SERVER_PORT='$(SERVER_PORT)' \
		--env  VERSION='$(VERSION)' \
		--env  ELASTICSEARCH_URL='$(ELASTICSEARCH_URL)' \
		--env  MIN_SEARCH_TERM_LENGTH='$(MIN_SEARCH_TERM_LENGTH)' \
		--env  MAX_SEARCH_CRITERIA='$(MAX_SEARCH_CRITERIA)' \
		--env  MAX_NUM_SERVICES_TO_RETURN='$(MAX_NUM_SERVICES_TO_RETURN)' \
		--env  MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH='$(MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH)' \
		--env  MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS='$(MAX_NUM_SERVICES_TO_RETURN_FROM_ELASTICSEARCH_3_SEARCH_TERMS)' \
		--env  FUZZ_LEVEL='$(FUZZ_LEVEL)' \
		--env  NAME_PRIORITY='$(NAME_PRIORITY)' \
		--env  ADDRESS_PRIORITY='$(ADDRESS_PRIORITY)' \
		--env  POSTCODE_PRIORITY='$(POSTCODE_PRIORITY)' \
		--env  PUBLIC_NAME_PRIORITY='$(NAME_PUBLIC_PRIORITY)' \
		--env  AWS_ACCESS_KEY_ID='dummy' \
		--env  AWS_SECRET_ACCESS_KEY='dummy'\
		--env  COGNITO_USER_POOL_CLIENT_ID='$(COGNITO_USER_POOL_CLIENT_ID)' \
		--env  COGNITO_USER_POOL_CLIENT_SECRET='$(COGNITO_USER_POOL_CLIENT_SECRET)' \
		--env  COGNITO_USER_POOL_ID='$(COGNITO_USER_POOL_ID)' \
		--env  POSTCODE_MAPPING_SERVICE_URL='$(POSTCODE_MAPPING_SERVICE_URL)' \
		--env  AUTH_LOGIN_URL='$(AUTH_LOGIN_URL)' \
		--env  AUTH_LOGIN_URI='$(AUTH_LOGIN_URI)' \
		--env  POSTCODE_MAPPING_USER='$(POSTCODE_MAPPING_USER)' \
		--env  POSTCODE_MAPPING_PASSWORD='$(POSTCODE_MAPPING_PASSWORD)' \
		--publish 9999:9999 \
		--publish 8443:8443 \
		"
		make start

docker-run-mvn-lib-mount: ### Build Docker image mounting library volume - mandatory: DIR, CMD
	make docker-run-mvn LIB_VOLUME_MOUNT=true \
		DIR="$(DIR)" \
		CMD="$(CMD)"

push: # Push project artefacts to the registry
	make docker-push NAME=api
	make docker-push NAME=mock-postcode-api


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
	make plan-base
	make plan-etl
	sleep $(SLEEP_AFTER_PLAN)

provision: # Provision environment - mandatory: PROFILE=[name]
	make provision-base
	make provision-etl

plan-base: # Plan environment - mandatory: PROFILE=[name]
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make terraform-plan STACK=$(INFRASTRUCTURE_STACKS_BASE) PROFILE=$(PROFILE)
	sleep $(SLEEP_AFTER_PLAN)

provision-base: # Provision environment - mandatory: PROFILE=[name]
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make terraform-apply-auto-approve STACK=$(INFRASTRUCTURE_STACKS_BASE) PROFILE=$(PROFILE)

plan-etl: # Plan environment - mandatory: PROFILE=[name]
	make prepare-lambda-deployment
	make terraform-plan STACK=$(INFRASTRUCTURE_STACKS_ETL) PROFILE=$(PROFILE)
	sleep $(SLEEP_AFTER_PLAN)

provision-etl: # Provision environment - mandatory: PROFILE=[name]
	make prepare-lambda-deployment
	make terraform-apply-auto-approve STACK=$(INFRASTRUCTURE_STACKS_ETL) PROFILE=$(PROFILE)

plan_auth: # Plan environment - mandatory: PROFILE=[name]
	make terraform-plan STACK=$(INFRASTRUCTURE_STACKS_AUTH) PROFILE=$(PROFILE)
	sleep $(SLEEP_AFTER_PLAN)

provision_auth: # Provision environment - mandatory: PROFILE=[name]
	make terraform-apply-auto-approve STACK=$(INFRASTRUCTURE_STACKS_AUTH) PROFILE=$(PROFILE)

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

# ==============================================================================
# Supporting targets

trust-certificate: ssl-trust-certificate-project ## Trust the SSL development certificate

create-artefact-repositories: ## Create ECR repositories to store the artefacts
	make docker-create-repository NAME=api

k8s-get-replica-sets-not-yet-updated:
	echo -e
	kubectl get deployments -n $(K8S_APP_NAMESPACE) \
	-o=jsonpath='{range .items[?(@.spec.replicas!=@.status.updatedReplicas)]}{.metadata.name}{"("}{.status.updatedReplicas}{"/"}{.spec.replicas}{")"}{" "}{end}'

k8s-get-pod-status:
	echo -e
	kubectl get pods -n $(K8S_APP_NAMESPACE)

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
	eval "$$(make aws-assume-role-export-variables)"
	http_result=$$(aws lambda invoke --function-name $(PROJECT_ID)-$(PROFILE)-service-etl out.json --log-type Tail | jq .StatusCode)
	if [[ ! $$http_result -eq 200 ]]; then
		cat out.json
		rm -r out.json
		exit 1
	fi
	echo $$http_result
	rm -r out.json

monitor-r53-connection:
	attempt_counter=0
	max_attempts=5
	http_status_code=0

	until [[ $$http_status_code -eq 200 ]]; do
		if [[ $$attempt_counter -eq $$max_attempts ]]; then
			echo "Maximum attempts reached unable to connect to deployed instance"
			exit 0
		fi

		echo 'Pinging deployed instance'
		attempt_counter=$$(($$attempt_counter+1))
		http_status_code=$$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 $(FUZZY_ENDPOINT)/api/home)
		echo Status code is: $$http_status_code
		sleep 10
	done

k8s-check-deployment-of-replica-sets:
	eval "$$(make aws-assume-role-export-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	sleep 10
	elaspedtime=10
	until [ $$elaspedtime -gt $(CHECK_DEPLOYMENT_TIME_LIMIT) ]; do
		replicasNotYetUpdated=$$(make -s k8s-get-replica-sets-not-yet-updated)
		if [ -z "$$replicasNotYetUpdated" ]
		then
			echo "Success - all replica sets in the deployment have been updated."
			exit 0
		else
			echo "Waiting for all replicas to be updated: " $$replicasNotYetUpdated

			echo "----------------------"
			echo "Pod status: "
			make k8s-get-pod-status
			podStatus=$$(make -s k8s-get-pod-status)
			echo "-------"

			#Check failure conditions
			if [[ $$podStatus = *"ErrImagePull"*
					|| $$podStatus = *"ImagePullBackOff"* ]]; then
				echo "Failure: Error pulling Image"
				exit 1
			elif [[ $$podStatus = *"Error"*
								|| $$podStatus = *"error"*
								|| $$podStatus = *"ERROR"* ]]; then
				echo "Failure: Error with deployment"
				exit 1
			fi

		fi
		sleep 10
		((elaspedtime=elaspedtime+$(CHECK_DEPLOYMENT_POLL_INTERVAL)))
		echo "Elapsed time: " $$elaspedtime
	done

	echo "Conditional Success: The deployment has not completed within the timescales, but carrying on anyway"
	exit 0

# --------------------------------------

run-static-analisys:
	echo TODO: $(@)

run-unit-test:
	make unit-test

run-smoke-test:
	make stop
	make quick-start PROFILE=$(PROFILE) VERSION=$(API_IMAGE_TAG)
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
