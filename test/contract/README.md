# Contract Test

## Table of contents

- [Contract Test](#contract-test)
  - [Table of contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Run the test suite from the command-line](#run-the-test-suite-from-the-command-line)
  - [Run the test suite using Postman](#run-the-test-suite-using-postman)
  - [Run the smoke tests](#run-the-smoke-tests)

## Introduction

Initially, the contract test suite should consists of two parts

1. Wiremock mappings (service API interface definitions) in the `test/contract/mappings` directory
2. Postman test collection in the `test/contract` directory

## Run the test suite from the command-line

From within the `test/contract` directory which is the sub-project directory we can do the following to execute the test suite

    make start
    make run-contract
    # Update the mappings
    make reload
    make run
    # ...

or simply run it as a single command

    make -s test

## Run the test suite using Postman

- Start up the Wiremock service by running `make start`
- [Import](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/) the Postman collection from the `test/contract` directory
- [Select](https://learning.postman.com/docs/sending-requests/managing-environments/#selecting-an-active-environment) the `Postman` environment (the `Docker` one is only used from the command-line)
- [Run](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/) the test collection

## Run the smoke tests

The smoke tests are configured to run in different ways dependent on the environment they are run in.

Environments:
  - dev (Non-Production)
  - pt (Performance Tests)
  - sg (Staging)
  - prod (Production)
  - test
  - demo

From within the project directory we can do the following to execute the test suite

  make stop
	make quick-start PROFILE=$(PROFILE) VERSION=$(API_IMAGE_TAG)
	sleep 20
	cd test/contract
	make run-smoke
	cd ../../
	make stop

Alternatively the pipeline calls the `make run smoke-tests`

In the dev, test and pt environments the application is configured to accept mock tokens. Before the postman tests are executed a script is run to configure the environment to load the mock token.

In the prod, demo and staging environments the postman collections uses a real token obtained from the respective cognito instances.

  `$(APPLICATION_TEST_DIR)/contract/scripts/secrets.sh`

This is to ensure that all deployed instances of fuzzy search are communicating with the intended components that make up the API.
