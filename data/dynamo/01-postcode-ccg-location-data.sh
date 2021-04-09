aws dynamodb put-item --table-name ${DYNAMODB_POSTCODE_LOC_MAP_TABLE} --item "{\"postcode\":{\"S\":\"PO305XW\"},\"easting\":{\"N\":\"74\"}, \"northing\":{\"N\":\"150\"}}" --endpoint-url http://localhost:8000
aws dynamodb put-item --table-name ${DYNAMODB_POSTCODE_LOC_MAP_TABLE} --item "{\"postcode\":{\"S\":\"TN49NH\"},\"easting\":{\"N\":\"558439\"}, \"northing\":{\"N\":\"140222\"}}" --endpoint-url http://localhost:8000
aws dynamodb put-item --table-name ${DYNAMODB_POSTCODE_LOC_MAP_TABLE} --item "{\"postcode\":{\"S\":\"EX88PR\"},\"easting\":{\"N\":\"265\"}, \"northing\":{\"N\":\"166\"}}" --endpoint-url http://localhost:8000
aws dynamodb put-item --table-name ${DYNAMODB_POSTCODE_LOC_MAP_TABLE} --item "{\"postcode\":{\"S\":\"EX71PR\"},\"easting\":{\"N\":\"66\"}, \"northing\":{\"N\":\"120\"}}" --endpoint-url http://localhost:8000
aws dynamodb put-item --table-name ${DYNAMODB_POSTCODE_LOC_MAP_TABLE} --item "{\"postcode\":{\"S\":\"LS166EB\"},\"easting\":{\"N\":\"51\"}, \"northing\":{\"N\":\"130\"}}" --endpoint-url http://localhost:8000
