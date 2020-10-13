# DoS Service Fuzzy Search API

## Introduction

This is the DoS Service Fuzzy Search API. This is a RESTFUL API which will return a list of services that match the search criteria passed to the API. The API will perform fuzzy matching on the search criteria specified, and will fuzzy match against the following service attributes:

- service name
- service public name
- service address
- service postcode

### Search Terms

The service search endpoint requires at least one search term to be passed through as a query parameter. The query parameter is as follows:

    search_term

The minimum number of characters allowed in a the search term is set by the `CONFIGURATION.VALIDATION.MIN_SEARCH_STRING_LENGTH` environment variable. The maximum number of search terms allowed is set by the `CONFIGURATION.VALIDATION.MAX_SEARCH_CRITERIA` environment variable.

### Search Prioritisation

The service search endpoint also allows for a number of search prioritisation parameters to be passed through so as to tailor the search to prioritise the matching of certain fields above others. For example, it may be the case that the matching of the service public name is more important than the matching of the service address. These variants can be catered for using the following search prioritisation query parameters:

    name_priority
    public_name_priority
    address_priority
    postcode_priority

The search prioritisation parameters are all optional, and default values will be applied if they are not passed through. The default values are set via correspondingly named environment variables. If values are provided they should be in the range of between 0 and 100.

### Fuzz Level

The service search endpoint will apply a zero level of fuzzy logic to the search terms by default. But this too can be configured by passing a value of between 0 and 2 into the following query parameter:

    fuzz_level

The value 0 means that the API will apply no fuzzy logic to the search terms, while a value of 2 will apply a maximum level of fuzziness.

### Number of matches returned

Also configurable is the number of matched services returned. This can be configured by supplying a value for the following query parameter:

    max_num_services_to_return

If set, this value should range from between 1 and 100.

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

The API will return a list of matching services in JSON format. This is defined on Confluence: [Known Service Search](https://nhsd-confluence.digital.nhs.uk/display/SFDEV/Known+Service+Search)

The supporting components are:

- Elasticsearch
- Kibana
- DoS Test Database

The supporting components are:

- Elasticsearch
- Kibana
- DoS Test Database

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
