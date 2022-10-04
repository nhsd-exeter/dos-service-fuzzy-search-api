#!/bin/bash
curl -XPUT "http://localhost:9200/service/_mapping" -H 'Content-Type: application/json' -d'{"properties": {"location": {"type": "geo_point"}}}'
curl -XPUT "http://localhost:9200/service/_doc/1" -H 'Content-Type: application/json' -d'{"id" : 1, "uIdentifier":1, "name":"service1", "publicName":"Public Service Name 1", "capacityStatus":"GREEN", "typeId":1, "type":"Type 1", "odsCode":"odscode1", "address": ["1 Service Street", "Service town", "Exmouth"], "postcode":"EX7 1PR", "referralRoles": ["Role 1", "Role 4"],"search_data": "service1 1 Service Street Service town Exmouth EX7 1PR"}'
curl -XPUT "http://localhost:9200/service/_doc/2" -H 'Content-Type: application/json' -d'{"id" : 2, "uIdentifier":2, "name":"service2", "publicName":"Public Service Name 2", "capacityStatus":"GREEN", "typeId":2, "type":"Type 2", "odsCode":"odscode2", "address": ["2 Service Street", "Service town", "Exmouth"], "postcode":"EX7 2PR", "referralRoles": ["Role 2", "Role 4"],"search_data": "service2 2 Service Street Service town Exmouth EX7 2PR"}'
curl -XPUT "http://localhost:9200/service/_doc/3" -H 'Content-Type: application/json' -d'{"id" : 3, "uIdentifier":3, "name":"service3", "publicName":"Public Service Name 3", "capacityStatus":"GREEN", "typeId":3, "type":"Type 3", "odsCode":"odscode3", "address": ["3 Service Street", "Service town", "Exmouth"], "postcode":"EX7 3PR", "referralRoles": ["Role 3", "Role 4"],"search_data": "service3 3 Service Street Service town Exmouth EX7 3PR"}'
curl -XPUT "http://localhost:9200/service/_doc/4" -H 'Content-Type: application/json' -d'{"id" : 4, "uIdentifier":4, "name":"service4", "publicName":"Public Service Name 4", "capacityStatus":"GREEN", "typeId":4, "type":"Type 4", "odsCode":"odscode4", "address": ["4 Service Street", "Service town", "Exmouth"], "postcode":"EX7 4PR", "referralRoles": ["Role 4", "Role 4"],"search_data": "service4 4 Service Street Service town Exmouth EX7 4PR"}'
curl -XPUT "http://localhost:9200/service/_doc/5" -H 'Content-Type: application/json' -d'{"id" : 5, "uIdentifier":5, "name":"service5", "publicName":"Public Service Name 5", "capacityStatus":"GREEN", "typeId":5, "type":"Type 5", "odsCode":"odscode5", "address": ["5 Service Street", "Service town", "Exmouth"], "postcode":"EX7 5PR", "referralRoles": ["Role 5", "Role 4"],"search_data": "service5 5 Service Street Service town Exmouth EX7 5PR"}'
curl -XPUT "http://localhost:9200/service/_doc/6" -H 'Content-Type: application/json' -d'{"id" : 6, "uIdentifier":6, "name":"service6", "publicName":"Public Service Name 6", "capacityStatus":"GREEN", "typeId":6, "type":"Type 6", "odsCode":"odscode6", "address": ["6 Service Street", "Service town", "Exmouth"], "postcode":"EX7 6PR", "referralRoles": ["Role 6", "Role 4"],"search_data": "service6 6 Service Street Service town Exmouth EX7 6PR"}'
curl -XPUT "http://localhost:9200/service/_doc/7" -H 'Content-Type: application/json' -d'{"id" : 7, "uIdentifier":7, "name":"service7", "publicName":"Public Service Name 7", "capacityStatus":"GREEN", "typeId":7, "type":"Type 7", "odsCode":"odscode7", "address": ["7 Service Street", "Service town", "Exmouth"], "postcode":"EX7 7PR", "referralRoles": ["Role 7", "Role 4"],"search_data": "service7 7 Service Street Service town Exmouth EX7 7PR"}'
curl -XPUT "http://localhost:9200/service/_doc/8" -H 'Content-Type: application/json' -d'{"id" : 8, "uIdentifier":8, "name":"service8", "publicName":"Public Service Name 8", "capacityStatus":"GREEN", "typeId":8, "type":"Type 8", "odsCode":"odscode8", "address": ["8 Service Street", "Service town", "Exmouth"], "postcode":"EX7 8PR", "referralRoles": ["Role 8", "Role 4"],"search_data": "service8 8 Service Street Service town Exmouth EX7 8PR"}'
curl -XPUT "http://localhost:9200/service/_doc/9" -H 'Content-Type: application/json' -d'{"id" : 9, "uIdentifier":9, "name":"service9", "publicName":"Public Service Name 9", "capacityStatus":"GREEN", "typeId":9, "type":"Type 9", "odsCode":"odscode9", "address": ["9 Service Street", "Service town", "Exmouth"], "postcode":"EX7 9PR", "referralRoles": ["Role 9", "Role 4"],"search_data": "service9 9 Service Street Service town Exmouth EX7 9PR"}'
curl -XPUT "http://localhost:9200/service/_doc/10" -H 'Content-Type: application/json' -d'{"id" : 10, "uIdentifier":10, "name":"service10", "publicName":"Public Service Name 10", "capacityStatus":"GREEN", "typeId":10, "type":"Type 1", "odsCode":"odscode10", "address": ["10 Service Street", "Service town", "Exmouth"], "postcode":"EX8 8PR", "referralRoles": ["Role 10", "Role 4"],"search_data": "service10 1 Service Street Service town Exmouth EX8 8PR"}'

curl -XPUT "http://localhost:9200/service/_doc/7001" -H 'Content-Type: application/json' -d'{"id" : 7001, "uIdentifier":7001, "name":"Head Clinic", "publicName":"Head Clinic", "capacityStatus":"GREEN", "typeId":1, "type":"Type 1", "odsCode":"odscode1", "address": ["1 Foot Street", "Service town", "Bristol"], "postcode":"EX7 1PR", "referralRoles": ["Role 1", "Role 4"], "search_data": "Head Clinic 1 Foot Street Service town Bristol EX7 1PR"}'
curl -XPUT "http://localhost:9200/service/_doc/7002" -H 'Content-Type: application/json' -d'{"id" : 7002, "uIdentifier":7002, "name":"Foot Clinic", "publicName":"Foot Clinic", "capacityStatus":"GREEN", "typeId":1, "type":"Type 1", "odsCode":"odscode1", "address": ["1 Head Street", "Service town", "Bristol"], "postcode":"EX7 1PR", "referralRoles": ["Role 1", "Role 4"], "search_data": "Foot Clinic 1 Head Street Service town Bristol EX7 1PR"}'
curl -XPUT "http://localhost:9200/service/_doc/7003" -H 'Content-Type: application/json' -d'{"id" : 7003, "uIdentifier":7003, "name":"Head Clinic", "publicName":"Head Clinic", "capacityStatus":"GREEN", "typeId":1, "type":"Type 1", "odsCode":"odscode1", "address": ["1 Foot Street", "Service town", "Exeter"], "postcode":"EX7 1PR", "referralRoles": ["Role 1", "Role 4"], "search_data": "Head Clinic 1 Foot Street Service town Exeter EX7 1PR"}'

curl -XPUT "http://localhost:9200/service/_doc/45082" -H 'Content-Type: application/json' -d'{"id" : 45082, "uIdentifier":1372850439, "name":"Totnes Hospital", "publicName":"Totnes Hospital", "capacityStatus":"AMBER", "typeId":28, "type":"Community Hospital", "odsCode":"ODS1", "address": ["Totnes Hospital","Coronation Road","Totnes","Devon"], "postcode":"TQ9 5GH", "referralRoles": ["Role 1", "Role 2"], "search_data": "Totnes Hospital Totnes Hospital Totnes Hospital Coronation Road Totnes Devon TQ9 5GH"}'

curl -XPUT "http://localhost:9200/service/_doc/120756" -H 'Content-Type: application/json' -d'{"id" : 120756, "uIdentifier":120756, "name":"GP - Woodbury Surgery - Woodbury - GPC", "publicName":"Woodbury Surgery", "capacityStatus":"GREEN", "typeId":28, "type":"GP Practice", "odsCode":"L83116", "address": ["Woodbury Surgery","Fulford Way","Woodbury","Devon"], "postcode":"EX5 1NZ", "easting":"12345", "northing":"12345", "referralRoles": ["Role 1", "Role 2"], "search_data": "GP - Woodbury Surgery - Woodbury - GPC Woodbury Surgery Woodbury Surgery Fulford Way Woodbury Devon EX5 1NZ"}'
curl -XPUT "http://localhost:9200/service/_doc/1362051578" -H 'Content-Type: application/json' -d'{"id" : 1362051578, "uIdentifier":1362051578, "name":"Dental Practice Woodbury Park Dental Practice Kent", "publicName":"", "capacityStatus":"GREEN", "typeId":13, "type":"Dental Practice", "odsCode":"1362051578", "address": ["1 Woodbury Park Road","Tunbridge Wells","Kent"], "postcode":"TN4 9NH","easting":"123332", "northing":"123332", "location": {"lat": 23.4, "lon": -2.56789}, "referralRoles": ["Role 1", "Role 2"], "search_data": "Dental Practice Woodbury Park Dental Practice Kent 1 Woodbury Park Road Tunbridge Wells Kent TN4 9NH"}'

curl -XPUT "http://localhost:9200/service/_doc/12598411" -H 'Content-Type: application/json' -d'{"id": 12598411,"uIdentifier": 12598411,"name": "Pharmacy: East Riding Pharmacy (St Augustines Gate, Hedon)","publicName": "East Riding Pharmacy","typeId": 13,"type": "Pharmacy","address": ["16-20 ST AUGUSTINE''S GATE$HEDON"],"postcode": "HU12 8EX","referralRoles": ["Professional Referral"],"capacityStatus": "GREEN","easting": 518904,   "northing": 428544,   "odsCode": "FDC68", "location":{"lat": 53.7397502,"lon": -0.1987208},"search_data": "Pharmacy: East Riding Pharmacy (St Augustines Gate, Hedon)"}'
curl -XPUT "http://localhost:9200/service/_doc/12599111" -H 'Content-Type: application/json' -d'{"id":12599111,"uIdentifier":12599111,"name":"Pharmacy: Flamborough Pharmacy (High Street, Flamborough)","publicName":"Flamborough Pharmacy","typeId":13,"type":"Pharmacy","address":["High Street $Flamborough"],"postcode":"YO15 1JX","referralRoles":["Professional Referral"],"capacityStatus":"GREEN","easting":522747,"northing":470710,"odsCode":"FTR61","location":{"lat":54.1176359,"lon":-0.1235775},"search_data":"Pharmacy: Flamborough Pharmacy (High Street, Flamborough)"}'
curl -XPUT "http://localhost:9200/service/_doc/12600311" -H 'Content-Type: application/json' -d'{"id":12600311,"uIdentifier":12600311,"name":"Pharmacy: N R Drummond Pharmacy (Swinefleet Road, Goole)","publicName":"Drummond Pharmacy","typeId":13,"type":"Pharmacy","address":["Alfreds Place$Swinefleet Road$Goole"],"postcode":"DN14 5RL","referralRoles":["Professional Referral"],"capacityStatus":"GREEN","easting":474525,"northing":422738,"odsCode":"FF647","location":{"lat":53.695801,"lon":-0.872594},"search_data":"Pharmacy: N R Drummond Pharmacy (Swinefleet Road, Goole)"}'
curl -XPUT "http://localhost:9200/service/_doc/12600411" -H 'Content-Type: application/json' -d'{"id":12600411,"uIdentifier":12600411,"name":"Pharmacy: Patrington Pharmacy (Market Place, Partrington)","publicName":"Patrington Pharmacy","typeId":13,"type":"Pharmacy","address":["13 MARKET PLACE$PATRINGTON$NORTH HUMBERSIDE"],"postcode":"HU12 0RA","referralRoles":["Professional Referral"],"capacityStatus":"GREEN","easting":531364,"northing":422633,"odsCode":"FNX46","location":{"lat":53.6836691,"lon":-0.0124323},"search_data":"Pharmacy: Patrington Pharmacy (Market Place, Partrington)"}'
curl -XPUT "http://localhost:9200/service/_doc/13150611" -H 'Content-Type: application/json' -d'{"id":13150611,"uIdentifier":13150611,"name":"Pharmacy: Lloyds Pharmacy (Elmbridge Parade, Greatfield)","publicName":"Lloyds Pharmacy","typeId":13,"type":"Pharmacy","address":["44 Elmbridge Parade$Greatfield$Hull$Yorkshire"],"postcode":"HU9 4JU","referralRoles":["Professional Referral"],"capacityStatus":"GREEN","easting":515186,"northing":430339,"odsCode":"FVX20","location":{"lat":53.7567095,"lon":-0.2543932},"search_data":"Pharmacy: Lloyds Pharmacy (Elmbridge Parade, Greatfield)"}'
