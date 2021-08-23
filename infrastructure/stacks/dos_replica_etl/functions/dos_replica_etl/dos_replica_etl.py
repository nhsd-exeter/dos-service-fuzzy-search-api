from __future__ import print_function
import boto3
import base64
from botocore.exceptions import ClientError
import psycopg2
import psycopg2.extras
import os
import json
from requests_aws4auth import AWS4Auth
from elasticsearch import Elasticsearch, RequestsHttpConnection, helpers

USR = os.environ.get("USR")
SOURCE_DB = os.environ.get("SOURCE_DB")
ENDPOINT = os.environ.get("ENDPOINT").split(":")[0]
PORT = os.environ.get("PORT")
REGION = os.environ.get("REGION")
SECRET_NAME = os.environ.get("SECRET_NAME")
SECRET_KEY = os.environ.get("SECRET_KEY")
ES_DOMAIN_ENDPOINT = os.environ.get("ES_DOMAIN_ENDPOINT")
BATCH_SIZE = 100000
ES_INDEX = "service"



def get_secret():

    secret_name = SECRET_NAME
    region_name = "eu-west-2"

    # Create a Secrets Manager client
    session = boto3.session.Session()
    client = session.client(service_name="secretsmanager", region_name=region_name)

    try:
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    except ClientError as e:
            print("Unable to retrieve secret due to {}".format(e))
            raise e
    else:
        # Decrypts secret using the associated KMS CMK.
        # Depending on whether the secret is a string or binary, one of these fields will be populated.
        if "SecretString" in get_secret_value_response:
            return get_secret_value_response["SecretString"]
        else:
            return base64.b64decode(get_secret_value_response["SecretBinary"])


# Method to connect to database
def connect():
    try:
        secret_dict = json.loads(get_secret())
        conn = psycopg2.connect(
            host=ENDPOINT,
            port=PORT,
            user=USR,
            database=SOURCE_DB,
            password=secret_dict[SECRET_KEY],
        )
        return conn
    except Exception as e:
        print("Database connection failed due to {}".format(e))


# Method to get cursor from db
def getCursor(conn):
    try:
        cur = conn.cursor("dos_replica_etl_cursor")
        cur.itersize = BATCH_SIZE
        cur.arraysize = BATCH_SIZE
        return cur
    except Exception as e:
        print("unable to retrieve cursor due to {}".format(e))
        conn.close()


def extract_data_from_dos():
    selectStatement = """select
                            s.id,
                            s.uid,
                            s.name,
                            s.publicname,
                            s.typeid,
                            t.name as servicetype,
                            s.address,
                            s.postcode,
                            STRING_AGG (r.name, ',') as referralrole,
                            c.color as capacitystatus,
                            s.easting,
                            s.northing,
                            s.publicphone,
                            s.nonpublicphone,
                            s.email,
                            s.web,
                            s.publicreferralinstructions,
                            s.telephonetriagereferralinstructions,
                            s.odscode,
                            s.isnational,
                            s.modifiedtime
                        from
                            pathwaysdos.services s,
                            pathwaysdos.servicecapacities sc,
                            pathwaysdos.capacitystatuses c,
                            pathwaysdos.servicereferralroles sr,
                            pathwaysdos.referralroles r,
                            pathwaysdos.servicetypes t
                        where (
                            s.id = sc.serviceid
                            and
                            sc.capacitystatusid = c.capacitystatusid
                            and
                            sr.serviceid = s.id
                            and
                            sr.referralroleid = r.id
                            and
                            s.typeid = t.id
                        ) group by s.id, t.name, c.color"""

    print("Open connection")
    conn = connect()
    print("Connection opened")
    cur = getCursor(conn)
    print("got cursor")
    try:
        print("execute records")
        cur.execute(selectStatement)
        print("records executed")
        doc_list = []
        while True:
            records = cur.fetchmany()
            if not records:
                break

            doc_list = build_insert_dict(records, doc_list)
        count = len(doc_list)
        print("Successfully fetched " + str(count) + " records")
        return doc_list
    except Exception as e:
        print("DoS Read Replica ETL extraction failed due to {}".format(e))
    finally:
        cur.close()
        conn.close()
        print("PostgreSQL connection is closed")


def build_insert_dict(records, doc_list):
    print("adding " + str(len(records)) + " records to doc_list")

    for row in records:
        document = {
            "_id": row[0],
            "id": row[0],
            "u_id": row[1],
            "name": row[2],
            "public_name": row[3],
            "type_id": row[4],
            "type": row[5],
            "address": row[6],
            "postcode": row[7],
            "referral_roles": row[8],
            "capacity_status": row[9],
            "easting": row[10],
            "northing": row[11],
            "public_phone_number": row[12],
            "non_public_phone_number": row[13],
            "email": row[14],
            "web": row[15],
            "public_referral_instructions": row[16],
            "referral_instructions": row[17],
            "ods_code": row[18],
            "is_national": row[19],
            "updated": row[20]
        }
        doc_list.append(document)
    return doc_list


def connect_to_elastic_search():
    try:
        print("ES DOMAIN: " + ES_DOMAIN_ENDPOINT)
        host = ES_DOMAIN_ENDPOINT
        region = REGION

        print("getting credentials")
        service = 'es'
        credentials = boto3.Session().get_credentials()
        awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

        print("connecting to es")
        es = Elasticsearch(
            hosts = [{'host': host, 'port': 443}],
            http_auth = awsauth,
            use_ssl = True,
            verify_certs = True,
            connection_class = RequestsHttpConnection
        )
        print(es.info())
        if not es.ping():
            raise ValueError("Connection failed")
        print("connected to es")
        return es;
    except Exception as e:
        print("Unable to connect to ElasticSearch due to {}".format(e))


def insert_records_to_elasticsearch(doc_list):

    es = connect_to_elastic_search()
    print("size of import: " + str(len(doc_list)))
    try:
        print("Inserting into ES...")
        resp = helpers.bulk(es, doc_list, index = ES_INDEX, doc_type = "_doc")
        print("Insert complete")
        return resp
    except Exception as e:
        print("Unable to insert records due to {}".format(e))

# This is the entry point for the Lambda function
def lambda_handler(event, context):

    print("Starting DoS ETL")
    records = extract_data_from_dos()
    resp = insert_records_to_elasticsearch(records)
    return resp
