# DoS Service Fuzzy Search API

## Introduction

This is the DoS Service Fuzzy Search API. This is a RESTFUL API which will return a list of services that match the search criteria passed to the API. The API will perform fuzzy matching on the search criteria specified, and will fuzzy match against the following service attributes:

- service name
- service public name
- service address
- service postcode

The API endpoint is called by passing the search criteria in as query parameters.

The minimum number of characters in the search criteria is set by the PARAM.VALIDATION.MIN_SEARCH_STRING_LENGTH environment variable. The maximum number of search criteria is set by the PARAM.VALIDATION.MAX_SEARCH_CRITERIA environment variable.

## Building the API

The API can be built by running the following command:

    make build

## Running the API unit tests

The unit tests for the API can be run by executing the following command:

    make test

Note that the unit tests rely on the project having being started. (make start)

## Running the API

The API and the supporting components can be started by running the following command:

    make start

When the API is running (it currently runs along HTTP on port 9095, but this will be changed to HTTPS shortly), the URL (home page) to the API is: http://127.0.0.1:9095/dosapi/dosservices/v0.0.1

The API has a single GET Rest endpoint:

- /byfuzzysearch

The API will return a list of matching services in JSON format. This is defined on Confluence: https://nhsd-confluence.digital.nhs.uk/display/SFDEV/Known+Service+Search

The supporting components are:

- Elasticsearch
- kibana
- Dos Test DoS DB

## Stopping the API

The API can be stopped by running the following command:

    make stop

## Loading test data into Elasticsearch

To load the test data into the locally running containerized version of elasticsearch, run the following command:

    make load-test-data

Note that test data can only be loaded into elasticsearch if the elasticsearch component is running. This component can be started by running the following command:

    make start

The test data is stored here:

    build/data/services/service_data.sh
