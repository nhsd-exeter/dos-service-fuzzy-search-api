# 001 API Authentication

* Date: 2021/07/01
* Status: Accepted
* Deciders: Jonathan Pearce, Daniel Stefaniuk

## Context

In order to protect the Fuzzy Search API an authentication mechanism is required so that only authorized users of the API can call the endpoints.

## Decision

We want to use a token based authentication mechanism since this is a more secure way to protect an API over other authentication mechanisms and this approach is widely adopted in the API world. We are looking for a solution whereby we do not have to manage the storage of users who have been granted authorization to use the API.

AWS Cognito is a good fit as it is a fully managed IDP and has clearly defined documentation and APIs that we can call to implement the token based authentication solution. Moreover, this solution gives us the option of being able to implement user roles. Given that we are embarking on a journey that is set to produce a suite of APIs, this IDP could be used to manage a users access across multiple APIs by means of using these roles.

Adopting the AWS Cognito solution, the Fuzzy Search capability will therefore now:

* include infrastructure code to plug up an AWS Cognito User Pool
* include an authentication endpoint where users can authenticate and receive access tokens in order to use the service endpoints provided by the Fuzzy Search capability
* protect service endpoints with token based authentication

The initial MVP will have no automatic user sign up to these services. Instead, users of the Fuzzy Search capability will be manually added to the AWS Cognito User Pool by the product team. The User Pool will initially contain the service-finder user, to allow calls from the service finder components to the Fuzzy Search service endpoints.

## Consequences

The following consequences are seen, but not envisaged as serious or blocking:

* additional users wishing to access the Fuzzy Search service endpoints need to be manually added to the AWS Cognito User Pool
* authentication login to retrieve access tokens is tied into the Fuzzy Search capability
