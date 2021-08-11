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

## Loading the full service data into Elasticsearch

The full service data set contains over 65,000 services. These services have been extracted from the test DoS database. To load or refresh these services into an Elasticsearch domain, invoke the following make target:

    make load-all-services PROFILE=local

Note that `PROFILE` is a mandatory and the setting of this parameter determines where to load the data. A setting of local will load the full service data set into a locally running containerised instance of Elasticsearch, while a setting of dev will load the data set into an AWS instance of Elasticsearch running within the Texas non-prod environment.

The service data files themselves are created by running the SQL code in the service_sql.txt file against a DoS database.

## Clearing out data from the service index

To clear out data from elasticsearch (service index), use the following command:

    curl -XDELETE http://localhost:9200/service

## Documentation

Please see the /documentation section for supplementary information, diagrams, and flows for this API.

## Pipelines

The following section describes the pipelines that are available in this project. All pipelines reside under the 'Fuzzy Search API' tab in Jenkins

### Development Pipeline

The development pipeline will be triggered with every branch push to the code repository. The pipeline can also be triggered manually. The pipeline will:

- Plan the infrastructure and report back on any changes
- Derive a Build tag
- Build the API and create a docker image
- Run contract tests against the API
- Push the docker image to the ECR
- Report back the Image tag

### Deployment Pipeline

The deployment pipeline will deploy a specified image tag into the nonprod environment. The pipeline is configured to be run against the master branch only. The pipeline has to be run manually, and the image to deploy into the nonprod environment will need to be specified. When run manually, the pipeline will:

- Plan and then provision any infrastructure changes
- Populate the SF datastore with test data
- Deploy the API into the nonprod environment
- Run smoke tests against the deployed API

### Release Tag Pipeline

The release tag pipeline will promote a 'development' image into the 'production' ECR and will tag it with a release tag. The pipeline is configured to run against the master branch only. The pipeline has to be manually run, and the development image tag and release image tag will need to be specified. The pipeline will:

- Promote the specified development image into the production ECR and tag it with the specified release tag
