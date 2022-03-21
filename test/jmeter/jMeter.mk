# ==============================================================================
# Jmeter workflow targets

run-jmeter-nominal-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter DURATION=1500 THROUGHPUT=2.4 JMETER_TEST_FOLDER_PATH=test/jmeter/tests/nominal JMETER_TEST_FILE_PATH=test/jmeter/tests/nominal/performance.jmx

run-jmeter-peak-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter DURATION=1500 THROUGHPUT=16.7 JMETER_TEST_FOLDER_PATH=test/jmeter/tests/peak JMETER_TEST_FILE_PATH=test/jmeter/tests/peak/performance.jmx

run-jmeter-double-peak-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter DURATION=1500 THROUGHPUT=33.3 JMETER_TEST_FOLDER_PATH=test/jmeter/tests/doublepeak JMETER_TEST_FILE_PATH=test/jmeter/tests/doublepeak/performance.jmx

run-jmeter-burst-nominal-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter DURATION=60 THROUGHPUT=180 JMETER_TEST_FOLDER_PATH=test/jmeter/tests/burstnominal JMETER_TEST_FILE_PATH=test/jmeter/tests/burstnominal/performance.jmx

run-jmeter-burst-peak-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter DURATION=60 THROUGHPUT=540 JMETER_TEST_FOLDER_PATH=test/jmeter/tests/burstpeak JMETER_TEST_FILE_PATH=test/jmeter/tests/burstpeak/performance.jmx

run-jmeter-burst-double-peak-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter DURATION=60 THROUGHPUT=1080 JMETER_TEST_FOLDER_PATH=test/jmeter/tests/burstdoublepeak JMETER_TEST_FILE_PATH=test/jmeter/tests/burstdoublepeak/performance.jmx

deploy-jmeter-namespace:
	eval "$$(make aws-assume-role-export-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	sed -i 's|ECR_TEXAS_URL_NONPROD_TO_REPLACE|$(ECR_TEXAS_URL_NONPROD)|g' deployment/jmeter/jmeter_slaves_deploy.yaml
	sed -i 's|JMETER_SLAVE_IMAGE_TO_REPLACE|$(JMETER_SLAVE_IMAGE)|g' deployment/jmeter/jmeter_slaves_deploy.yaml
	sed -i 's|ECR_TEXAS_URL_NONPROD_TO_REPLACE|$(ECR_TEXAS_URL_NONPROD)|g' deployment/jmeter/jmeter_master_deploy.yaml
	sed -i 's|JMETER_MASTER_IMAGE_TO_REPLACE|$(JMETER_MASTER_IMAGE)|g' deployment/jmeter/jmeter_master_deploy.yaml
	kubectl create ns ${PROJECT_ID}-${PROFILE}-jmeter
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_slaves_deploy.yaml
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_slaves_svc.yaml
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_master_deploy.yaml
	make k8s-sts K8S_APP_NAMESPACE=${PROJECT_ID}-${PROFILE}-jmeter

destroy-jmeter-namespace:
	eval "$$(make aws-assume-role-export-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl delete ns ${PROJECT_ID}-${PROFILE}-jmeter

# ==============================================================================
# Supporting targets
project-aws-get-authentication-secret: #Get AWS Pass
	aws secretsmanager get-secret-value \
		--secret-id $(PROJECT_GROUP_SHORT)-sfsa-$(ENVIRONMENT)-cognito-passwords \
		--region $(AWS_REGION) \
		--query 'SecretString' \
		--output text

extract-access-token:
	make -s get-authentication-access-token ADMIN_PASSWORD=$$(make -s project-aws-get-authentication-secret | jq .ADMIN_PASSWORD | tr -d '"') | jq .accessToken | tr -d '"'

get-authentication-access-token:
		curl --request POST ${AUTHENTICATION_ENDPOINT} \
			--header 'Content-Type: application/json' \
			--data-raw '{"emailAddress": "service-finder-admin@nhs.net","password": "${ADMIN_PASSWORD}"}'

run-jmeter: # Run jmeter tests - mandatory: JMETER_TEST_FOLDER_PATH - test directory JMETER_TEST_FILE_PATH - the path of the jmeter tests to run
	sed -i 's|FUZZY_SEARCH_DOMAIN_TO_REPLACE|$(FUZZY_DOMAIN)|g' ${JMETER_TEST_FILE_PATH}
	sed -i 's|THROUGHPUT_TO_REPLACE|$(THROUGHPUT)|g' ${JMETER_TEST_FILE_PATH}
	sed -i 's|DURATION_TO_REPLACE|$(DURATION)|g' ${JMETER_TEST_FILE_PATH}
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl config set-context --current --namespace=${PROJECT_ID}-${PROFILE}-jmeter
	test/jmeter/scripts/jmeter_stop.sh
	test/jmeter/scripts/start_test.sh ${JMETER_TEST_FOLDER_PATH} ${JMETER_TEST_FILE_PATH} test/jmeter/properties/placeholder.user.properties
