#!/usr/local/bin/python3
import sys, os, base64, datetime, hashlib, hmac, json, re
import requests
from requests_aws4auth import AWS4Auth
from botocore.session import Session

def json_escape_string(text):
    return (text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\b", "\\b")
        .replace("\f", "\\f")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t"))

def escape_inline_script(match):
    text = match.group(0).replace("\"\"\"", "")
    return ("\"" + json_escape_string(text) + "\"")

# AWS service and region
service = 'es'
region = 'eu-west-2'

# Get script path, domain and AWS profile
if len(sys.argv) < 2:
    print('Syntax: ' + sys.argv[0] + ' <script> <es-domain> <aws-profile>')
    sys.exit()
script_path = sys.argv[1]
es_domain = sys.argv[2]
aws_endpoint = 'https://' + es_domain

# Build the update query
update_uri = '/service/_update_by_query'
es_endpoint = aws_endpoint + update_uri
with open(script_path, 'r') as fileObject:
    update_request = fileObject.read()
prog = re.compile("[\"]{3}.*?[\"]{3}", re.DOTALL)
escaped_update_request = re.sub(prog, escape_inline_script, update_request)

# Make the request
print('Request URL: %s\n' % es_endpoint)
credentials = Session().get_credentials()
aws_auth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)
response = requests.post(es_endpoint, auth=aws_auth, data=escaped_update_request, headers={"Content-Type":"application/json"})
if response.status_code != 200:
    print('Request returned with response code %d\n' % response.status_code)
    print('Response body: %s\n' % response.text)
    sys.exit()

# Output the response
print('Response: %s\n' % response.text)
