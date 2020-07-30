# DoS Service Fuzzy Search API

This is the DoS Service Fuzzy Search API.  This is a RESTFUL API which will return a list of services
that match the search criteria passed to the API.  The API will perform fuzzy matching on the search
criteria specified, and will fuzzy match against the following service attributes:

  service name
  service public name
  service address
  service postcode

The API endpoint is called by passing the search criteria in as part of the URL.

The minimum number of characters in the search criteria is set by the PARAM.VALIDATION.MIN_SEARCH_STRING_LENGTH
environment variable. The maximum number of search criteria is set by the PARAM.VALIDATION.MAX_SEARCH_CRITERIA environment variable.

## Building the API

The API can be built by running the following command:

  make build

## Running the API unit tests

The unit tests for the API can be run by executing the following command:

  make test

## Running the API

The API can be run by running the following command:

  make start

When the API is running (it currently runs along HTTP on port 9095, but this will be changed to HTTPS shortly), the URL (home page) to the API is:
  http://127.0.0.1:9095/dosapi/dosservices/v0.0.1

The API has a single GET Rest endpoint:
  /byfuzzysearch/{searchString}

The searchString is the search criteria that you want the API to search with. Search strings should be separated by commas. i.e. an example call might be:

  http://127.0.0.1:9095/dosapi/dosservices/v0.0.1/byfuzzysearch/Head unit, Hexagon House

The API will return a list of matching services in JSON format. This is defined on Confluence:
https://nhsd-confluence.digital.nhs.uk/display/SFDEV/Known+Service+Search

## Stopping the API

The API can be stopped by running the following command:

  make stop




