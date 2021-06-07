_PROJECT_CONFIG_DEV_ENV_TIMESTAMP_FILE = $(TMP_DIR)/.project-config-dev-env.timestamp
#_PROJECT_CONFIG_DEV_ENV_TARGET =
#_PROJECT_CONFIG_DEV_ENV_TIMESTAMP =
#_PROJECT_CONFIG_DEV_ENV_FORCE =

# ==============================================================================

project-config: ### Configure project environment
	if [ ! -f $(PROJECT_DIR)/project.code-workspace ]; then
		cp -fv $(LIB_DIR)/project/template/project.code-workspace $(PROJECT_DIR)
	fi
	make \
		git-config \
		docker-config
	# Make sure project's SSL certificate is created
	if [ ! -f $(SSL_CERTIFICATE_DIR)/certificate.pem ]; then
		make ssl-generate-certificate-project
		[ $(PROJECT_NAME) != "make-devops" ] && rm -f $(SSL_CERTIFICATE_DIR)/.gitignore
	fi
	# Re-configure developer's environment on demand
	if [ -n "$(_PROJECT_CONFIG_DEV_ENV_TIMESTAMP)" ] && ([ ! -f $(_PROJECT_CONFIG_DEV_ENV_TIMESTAMP_FILE) ] || [ $(_PROJECT_CONFIG_DEV_ENV_TIMESTAMP) -gt $$(cat $(_PROJECT_CONFIG_DEV_ENV_TIMESTAMP_FILE)) ]) && [ $(BUILD_ID) -eq 0 ]; then
		if [[ ! "$(_PROJECT_CONFIG_DEV_ENV_FORCE)" =~ ^(true|yes|y|on|1|TRUE|YES|Y|ON)$$ ]]; then
			read -p "Your development environment needs to be re-configured, would you like to proceed? (yes or no) " answer
			if [[ ! "$$answer" =~ ^(yes|y|YES|Y)$$ ]]; then
				exit 1
			fi
		fi
		make $(_PROJECT_CONFIG_DEV_ENV_TARGET)
		echo $(BUILD_TIMESTAMP) > $(_PROJECT_CONFIG_DEV_ENV_TIMESTAMP_FILE)
	fi

project-start: ### Start Docker Compose
	make docker-compose-start

project-stop: ### Stop Docker Compose
	make docker-compose-stop

project-log: ### Print log from Docker Compose
	make docker-compose-log

project-deploy: ### Deploy application service stack to the Kubernetes cluster - mandatory: PROFILE=[profile name]
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make k8s-deploy STACK=$(or $(NAME), service)

project-document-infrastructure: ### Generate infrastructure diagram - optional: FIN=[Python file path, defaults to infrastructure/diagram.py],FOUT=[PNG file path, defaults to documentation/Infrastructure_Diagram]
	make docker-run-tools CMD="python \
		$(or $(FIN), $(INFRASTRUCTURE_DIR_REL)/diagram.py) \
		$(or $(FOUT), $(DOCUMENTATION_DIR_REL)/Infrastructure_Diagram) \
	"

project-populate-application-variables:
	export TTL=$$(make -s k8s-get-namespace-ttl)

	export COGNITO_USER_POOL_CLIENT_SECRET=$$(make -s project-aws-get-cognito-client-secret NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_CLIENT_ID=$$(make -s project-aws-get-cognito-client-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_ID=$$(make -s aws-cognito-get-userpool-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_JWT_VERIFICATION_URL=https://cognito-idp.eu-west-2.amazonaws.com/$${COGNITO_USER_POOL_ID}/.well-known/jwks.json

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
# ==============================================================================

project-tag-as-release-candidate: ### Tag release candidate - mandatory: ARTEFACT|ARTEFACTS=[comma-separated image names]; optional: COMMIT=[git commit hash, defaults to master]
	commit=$(or $(COMMIT), master)
	make git-tag-create-release-candidate COMMIT=$$commit
	tag=$$(make git-tag-get-release-candidate COMMIT=$$commit)
	for image in $$(echo $(or $(ARTEFACTS), $(ARTEFACT)) | tr "," "\n"); do
		make docker-image-find-and-tag-as \
			TAG=$$tag \
			IMAGE=$$image \
			COMMIT=$$commit
	done

project-tag-as-environment-deployment: ### Tag environment deployment - mandatory: ARTEFACT|ARTEFACTS=[comma-separated image names],PROFILE=[profile name]; optional: COMMIT=[git release candidate tag name, defaults to master]
	[ $(PROFILE) = local ] && (echo "ERROR: Please, specify the PROFILE"; exit 1)
	commit=$(or $(COMMIT), master)
	make git-tag-create-environment-deployment COMMIT=$$commit PROFILE=$(PROFILE)
	tag=$$(make git-tag-get-environment-deployment COMMIT=$$commit PROFILE=$(PROFILE) )
	for image in $$(echo $(or $(ARTEFACTS), $(ARTEFACT)) | tr "," "\n"); do
		make docker-image-find-and-tag-as \
			TAG=$$tag \
			IMAGE=$$image \
			COMMIT=$$commit
	done

# ==============================================================================

project-create-profile: ### Create profile file - mandatory: NAME=[profile name]
	mkdir -p $(VAR_DIR_REL)/profile
	cp -fv $(LIB_DIR_REL)/project/template/build/automation/var/profile/$(NAME).mk $(VAR_DIR_REL)/profile/$(NAME).mk

project-create-contract-test: ### Create contract test project structure from template
	rm -rf $(APPLICATION_TEST_DIR)/contract
	make -s test-create-contract

project-create-image: ### Create image from template - mandatory: NAME=[image name],TEMPLATE=[library template image name]
	make -s docker-create-from-template NAME=$(NAME) TEMPLATE=$(TEMPLATE)

project-create-deployment: ### Create deployment from template - mandatory: STACK=[deployment name],PROFILE=[profile name]
	rm -rf $(DEPLOYMENT_DIR)/stacks/$(STACK)
	make -s k8s-create-base-from-template STACK=$(STACK)
	make -s k8s-create-overlay-from-template STACK=$(STACK) PROFILE=$(PROFILE)
	make project-create-profile NAME=$(PROFILE)

project-create-infrastructure: ### Create infrastructure from template - mandatory: MODULE_TEMPLATE|MODULE_TEMPLATES=[library template infrastructure module name],STACK_TEMPLATE=[library template infrastructure module name]; optional: STACK=[new stack name],PROFILE=[profile name]
	for module in $$(echo $(or $(MODULE_TEMPLATE), $(MODULE_TEMPLATES)) | tr "," "\n"); do
		make -s terraform-create-module-from-template TEMPLATE=$$module
	done
	make -s terraform-create-stack-from-template NAME=$(or $(STACK), $(STACK_TEMPLATE)) TEMPLATE=$(STACK_TEMPLATE)

project-create-pipeline: ### Create pipeline
	make -s jenkins-create-pipeline-from-template

# ==============================================================================

project-check-if-tech-is-included-in-stack: ### Check if technology is included in the project's tech stack - optional NAME=[tech name, e.g. python] - return: true|false
	echo "$(PROJECT_TECH_STACK_LIST)" | grep -q $(NAME) && echo true || echo false

project-branch-deploy: ### Check if development branch can be deployed automatically - return: true|false
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_MAIN) ]] && echo true && exit 0
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_PREFIX)/$(GIT_BRANCH_PATTERN_SUFFIX) ]] && \
		[ $$(make project-message-contains KEYWORD=deploy) == true ] && \
			echo true && exit 0
	[ $$(make project-branch-test) == true ] && echo true && exit 0
	echo false

project-branch-test: ### Check if development branch can be tested automatically - optional KEYWORDS=[keywords,comma,separated] - return: true|false
	keywords=$(or $(KEYWORDS) || test,func-test,perf-test,sec-test)
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_MAIN) ]] && echo true && exit 0
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_PREFIX)/$(GIT_BRANCH_PATTERN_SUFFIX) ]] && \
		[ $$(make project-message-contains KEYWORD=$$keywords) == true ] && echo true && exit 0
	echo false

project-branch-func-test: ### Check if development branch can be tested (functional) automatically - return: true|false
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_MAIN) ]] && echo true && exit 0
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_PREFIX)/$(GIT_BRANCH_PATTERN_SUFFIX) ]] && \
		[ $$(make project-message-contains KEYWORD=test,func-test) == true ] && \
			echo true && exit 0
	echo false

project-branch-perf-test: ### Check if development branch can be tested (performance) automatically - return: true|false
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_MAIN) ]] && echo true && exit 0
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_PREFIX)/$(GIT_BRANCH_PATTERN_SUFFIX) ]] && \
		[ $$(make project-message-contains KEYWORD=test,perf-test) == true ] && \
			echo true && exit 0
	echo false

project-branch-sec-test: ### Check if development branch can be tested (security) automatically - return: true|false
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_MAIN) ]] && echo true && exit 0
	[[ $(BUILD_BRANCH) =~ $(GIT_BRANCH_PATTERN_PREFIX)/$(GIT_BRANCH_PATTERN_SUFFIX) ]] && \
		[ $$(make project-message-contains KEYWORD=test,sec-test) == true ] && \
			echo true && exit 0
	echo false

project-message-contains: ### Check if git commit message contains any give keyword, format: '[ci keyword-one,keyword-two,...]' - mandatory KEYWORD=[comma-separated keywords]
	msg=$$(make git-commit-get-message)
	for str in $$(echo $(KEYWORD) | sed "s/,/ /g"); do
		echo "$$msg" | grep -E '\[ci .*\]' | grep -Eoq "\[ci .*[^-]$${str}[^-].*" && echo true && exit 0
	done
	echo false

project-get-build-tag: ### Return the default build tag
	echo $(BUILD_TAG)

project-list-profiles: ### List all the profiles
	for profile in $$(cd $(VAR_DIR)/profile; ls *.mk 2> /dev/null | sed 's/.mk//'); do
		[ $$profile != local ] && echo $$profile ||:
	done

project-tag-as-environment-deployment: ### Tag environment deployment - mandatory: ARTEFACT|ARTEFACTS=[comma-separated image names],PROFILE=[profile name]; optional: COMMIT=[git release candidate tag name, defaults to master]
	[ $(PROFILE) = local ] && (echo "ERROR: Please, specify the PROFILE"; exit 1)
	commit=$(or $(COMMIT), master)
	git_tag=$$(make git-tag-get-environment-deployment COMMIT=$$commit ENVIRONMENT=$(ENVIRONMENT))
	for image in $$(echo $(or $(ARTEFACTS), $(ARTEFACT)) | tr "," "\n"); do
		make docker-image-find-and-version-as \
			VERSION=$$git_tag \
			IMAGE=$$image \
			COMMIT=$$commit
	done

# ==============================================================================

.SILENT: \
	project-branch-deploy \
	project-branch-func-test \
	project-branch-perf-test \
	project-branch-sec-test \
	project-branch-test \
	project-create-contract-test \
	project-create-deployment \
	project-create-image \
	project-create-infrastructure \
	project-create-pipeline \
	project-create-profile \
	project-get-build-tag \
	project-check-if-tech-is-included-in-stack \
	project-list-profiles \
	project-message-contains
