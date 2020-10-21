PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

# ==============================================================================
# Development workflow targets

prepare: ## Prepare environment
	make \
		git-config \
		docker-config

build: project-config # Build project
	make docker-run-mvn \
		DIR="application/app" \
		CMD="-Dmaven.test.skip=true clean install"
	mv \
		$(PROJECT_DIR)/application/app/target/dos-service-fuzzy-search-api-*.jar \
		$(PROJECT_DIR)/build/docker/dos-service-fuzzy-search-api/assets/application/dos-service-fuzzy-search-api.jar
	make docker-build NAME=dos-service-fuzzy-search-api

start: project-start # Start project

stop: project-stop # Stop project

restart: stop start

log: project-log # Show project logs

load-test-services: # Load test services into elasticsearch
	sh ./data/services/create_test_services.sh

load-all-services: # Load bulk service data into elasticsearch - mandatory: PROFILE=[name]
	sh ./data/services/$(SERVICE_DATA_FILE)

test: load-test-data # Test project
	make docker-run-mvn \
		DIR="application/app" \
		CMD="clean test" \
		PROFILE=local \
		VARS_FILE=$(VAR_DIR)/profile/local.mk

push: # Push project artefacts to the registry
	make docker-push NAME=api

deploy: # Deploy artefacts - mandatory: PROFILE=[name]
	make project-deploy STACK=application PROFILE=$(PROFILE)

provision: # Provision environment - mandatory: PROFILE=[name]
	make terraform-apply-auto-approve STACK=elasticsearch PROFILE=$(PROFILE)

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

# ==============================================================================

.SILENT:
