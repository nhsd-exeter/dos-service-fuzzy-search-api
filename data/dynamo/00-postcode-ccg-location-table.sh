aws dynamodb create-table --table-name ${DYNAMODB_POSTCODE_LOC_MAP_TABLE} \
                    --attribute-definitions AttributeName=postcode,AttributeType=S \
                    --key-schema AttributeName=postcode,KeyType=HASH \
                    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
                    --endpoint-url http://localhost:8000
