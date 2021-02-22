#!/bin/bash

aws dynamodb create-table --table-name service-finder-nonprod-postcode-location-mapping --attribute-definitions AttributeName=postcode,AttributeType=S --key-schema AttributeName=postcode,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000

aws dynamodb put-item --table-name service-finder-nonprod-postcode-location-mapping --item "{\"postcode\":{\"S\":\"EX25SE\"},\"northing\":{\"N\":\"123332\"},\"easting\":{\"N\":\"123332\"}}" --endpoint-url http://localhost:8000
aws dynamodb put-item --table-name service-finder-nonprod-postcode-location-mapping --item "{\"postcode\":{\"S\":\"TW88DS\"},\"northing\":{\"N\":\"124332\"},\"easting\":{\"N\":\"123334\"}}" --endpoint-url http://localhost:8000
