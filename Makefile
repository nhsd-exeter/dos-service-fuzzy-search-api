PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

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

load-test-services: # Load test services into elasticsearch
	sh ./data/services/create_test_services.sh

load-all-services: # Load bulk service data into elasticsearch - mandatory: PROFILE=[name]
	sh ./data/services/$(SERVICE_DATA_FILE)

load-test-postcode-locations:
	sh ./data/locations/$(LOCATIONS_DATA_FILE)

test: load-test-services # Test project
	make docker-run-mvn \
		DIR="application/app" \
		CMD="clean test" \
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
	export TTL=$$(make -s k8s-get-namespace-ttl)
	make project-deploy PROFILE=$(PROFILE) STACK=application

plan: # Plan environment - mandatory: PROFILE=[name]
	make terraform-plan STACK=elasticsearch PROFILE=$(PROFILE)

provision: # Provision environment - mandatory: PROFILE=[name]
	make terraform-apply-auto-approve STACK=elasticsearch PROFILE=$(PROFILE)

project-populate-cognito: ## Populate cognito - optional: PROFILE=nonprod|prod,AWS_ROLE=Developer
	eval "$$(make aws-assume-role-export-variables)"
	$(PROJECT_DIR)/infrastructure/scripts/cognito.sh

clean: # Clean up project

# ==============================================================================
# Supporting targets

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
	echo TODO: $(@)

run-smoke-test:
	echo TODO: $(@)

run-integration-test:
	echo TODO: $(@)

run-contract-test:
	echo TODO: $(@)

run-functional-test:
	[ $$(make project-branch-func-test) != true ] && exit 0
	echo TODO: $(@)

run-performance-test:
	[ $$(make project-branch-perf-test) != true ] && exit 0
	echo TODO: $(@)

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
# --------------------------------------
local-dynamodb-scripts:
	cd $(PROJECT_DIR)data/dynamo
	chmod +x *.sh
	./00-postcode-ccg-location-table.sh > /dev/null
	./01-postcode-ccg-location-data.sh
# ==============================================================================

.SILENT: \
	derive-build-tag
