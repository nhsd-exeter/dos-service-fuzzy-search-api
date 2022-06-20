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
import time
import logging

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
TIMESTAMP_VERSION = time.time()
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")


logging.basicConfig(level=LOGGING_LEVEL)
logger=logging.getLogger(__name__)

def get_secret():

    secret_name = SECRET_NAME
    region_name = "eu-west-2"

    # Create a Secrets Manager client
    session = boto3.session.Session()
    client = session.client(service_name="secretsmanager", region_name=region_name)

    try:
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    except ClientError as e:
            logger.error("Unable to retrieve secret due to {}".format(e))
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
        logger.error("Database connection failed due to {}".format(e))


# Method to get cursor from db
def getCursor(conn):
    try:
        cur = conn.cursor("service_etl_cursor")
        cur.itersize = BATCH_SIZE
        cur.arraysize = BATCH_SIZE
        return cur
    except Exception as e:
        logger.error("unable to retrieve cursor due to {}".format(e))
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
                            s.modifiedtime,
                            (select srr.referralroleid from
                            servicereferralroles srr
                            where srr.serviceid = s.id and
                            srr.referralroleid = (select urr.referralroleid
                            from userreferralroles urr where urr.userid = ?)) as canReferTo
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
                            and
                            statusid = 1
                        ) group by s.id, t.name, c.color"""

    logger.debug("Open connection")
    conn = connect()
    logger.debug("Connection opened")
    cur = getCursor(conn)
    logger.debug("got cursor")
    try:
        logger.debug("execute records")
        cur.execute(selectStatement)
        logger.debug("records executed")
        doc_list = []
        while True:
            records = cur.fetchmany()
            if not records:
                break

            doc_list = build_insert_dict(records, doc_list)
        count = len(doc_list)
        logger.debug("Successfully fetched " + str(count) + " records")
        return doc_list
    except Exception as e:
        logger.error("DoS Read Replica ETL extraction failed due to {}".format(e))
    finally:
        cur.close()
        conn.close()
        logger.debug("PostgreSQL connection is closed")


def build_insert_dict(records, doc_list):
    logger.debug("adding " + str(len(records)) + " records to doc_list")

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
            "updated": row[20],
            "timestamp_version": TIMESTAMP_VERSION
        }
        doc_list.append(document)
    return doc_list


def connect_to_elastic_search():
    try:
        logger.debug("ES DOMAIN: " + ES_DOMAIN_ENDPOINT)
        host = ES_DOMAIN_ENDPOINT
        region = REGION

        logger.debug("getting credentials")
        service = 'es'
        credentials = boto3.Session().get_credentials()
        awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

        logger.debug("connecting to es")
        es = Elasticsearch(
            hosts = [{'host': host, 'port': 443}],
            http_auth = awsauth,
            use_ssl = True,
            verify_certs = True,
            connection_class = RequestsHttpConnection
        )
        logger.debug(es.info())
        if not es.ping():
            raise ValueError("Connection failed")
        logger.debug("connected to es")
        return es;
    except Exception as e:
        logger.error("Unable to connect to ElasticSearch due to {}".format(e))


def remove_deleted_services(es):
    query_string = {"query": {"bool": {"must_not": {"term": {"timestamp_version": TIMESTAMP_VERSION}}}}}
    results = helpers.scan(es,
                    query=query_string,  # same as the search() body parameter
                    index=ES_INDEX,
                    doc_type="_doc",
                    _source=False,
                    track_scores=False,
                    scroll='5m')
    bulk_deletes = []
    for result in results:
        result['_op_type'] = 'delete'
        bulk_deletes.append(result)

    resp = helpers.bulk(es, bulk_deletes)
    return resp


def send_dos_changes_to_elasticsearch(doc_list):
    es = connect_to_elastic_search()
    logger.debug("size of import: " + str(len(doc_list)))
    try:
        logger.debug("Inserting into ES...")
        resp = helpers.bulk(es, doc_list, index = ES_INDEX, doc_type = "_doc")
        logger.info("Insert complete")
        logger.info(resp)
        logger.debug("starting to remove old services")
        delete_resp = remove_deleted_services(es)
        logger.info("removed old services:")
        logger.info(delete_resp)
        return resp
    except Exception as e:
        logger.error("Unable to insert records due to {}".format(e))

# This is the entry point for the Lambda function
def lambda_handler(event, context):
    logger.setLevel(LOGGING_LEVEL)
    logger.info("Starting Service ETL")
    records = extract_data_from_dos()
    resp = send_dos_changes_to_elasticsearch(records)
    logger.info("completed Service ETL")
    return resp
