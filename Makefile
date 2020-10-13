PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

# ==============================================================================
# Project targets: Dev workflow

# ==============================================================================
# Project targets: Ops workflow

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

load-test-data: # Load test services into elasticsearch
	sh ./data/services/service_data.sh

load-bulk-data: # Load bulk service data into elasticsearch
	sh ./data/services/bulk_service_data.sh

test: load-test-data # Test project
	make docker-run-mvn \
		DIR="application/app" \
		CMD="clean test" \
		PROFILE=local \
		VARS_FILE=$(VAR_DIR)/profile/local.mk

push: # Push project artefacts to the registry
	#make docker-push NAME=NAME_TEMPLATE_TO_REPLACE

deploy: project-deploy # Deploy project - mandatory: PROFILE=[name]

clean: # Clean up project

# ==============================================================================
# Supporting targets

create-artefact-repository: ## Create Docker repositories to store artefacts
	#make docker-create-repository NAME=NAME_TEMPLATE_TO_REPLACE

# ==============================================================================

.SILENT:
