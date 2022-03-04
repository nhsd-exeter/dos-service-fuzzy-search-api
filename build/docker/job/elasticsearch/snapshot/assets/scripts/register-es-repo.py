import sys
import boto3
import requests
from requests_aws4auth import AWS4Auth

host = sys.argv[1]
account = sys.argv[2]
repo = sys.argv[3]
bucket = sys.argv[4]
role = sys.argv[5]

region = 'eu-west-2'
service = 'es'

credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

#Register repository

path = '_snapshot/' + repo  # the Elasticsearch API endpoint
url = 'https://' + host + '/' + path

payload = {
    "type": "s3",
    "settings": {
        "bucket": bucket,
        "region": "eu-west-2",
        "role_arn": "arn:aws:iam::" + account + ":role/" + role
    }
}

print(payload)
print(url)

headers = {"Content-Type": "application/json"}

r = requests.put(url, auth=awsauth, json=payload, headers=headers)

print(r.status_code)
print(r.text)
