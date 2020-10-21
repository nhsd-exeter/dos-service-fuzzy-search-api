#!/bin/bash

curl -XPOST 'https://vpc-sfs-dev-csfa7ah6cfpbllxcfqf6wmdcxi.eu-west-2.es.amazonaws.com/service/_bulk?pretty' --data-binary '@data/services/bulk_service_1.json' -H 'Content-Type: application/json'
curl -XPOST 'https://vpc-sfs-dev-csfa7ah6cfpbllxcfqf6wmdcxi.eu-west-2.es.amazonaws.com/service/_bulk?pretty' --data-binary '@data/services/bulk_service_2.json' -H 'Content-Type: application/json'
curl -XPOST 'https://vpc-sfs-dev-csfa7ah6cfpbllxcfqf6wmdcxi.eu-west-2.es.amazonaws.com/service/_bulk?pretty' --data-binary '@data/services/bulk_service_3.json' -H 'Content-Type: application/json'
curl -XPOST 'https://vpc-sfs-dev-csfa7ah6cfpbllxcfqf6wmdcxi.eu-west-2.es.amazonaws.com/service/_bulk?pretty' --data-binary '@data/services/bulk_service_4.json' -H 'Content-Type: application/json'
curl -XPOST 'https://vpc-sfs-dev-csfa7ah6cfpbllxcfqf6wmdcxi.eu-west-2.es.amazonaws.com/service/_bulk?pretty' --data-binary '@data/services/bulk_service_5.json' -H 'Content-Type: application/json'
