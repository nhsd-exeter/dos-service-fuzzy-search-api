import requests
import time
import sys

maxAttempts = int(sys.argv[2].strip())
attemptCounter = 0
responseCode = 0
host = sys.argv[1].strip()
while responseCode != 200:
    if attemptCounter == maxAttempts:
        sys.exit("Maximum attempts reached unable to connect to deployed instance")
    print("Pinging deployed instance count: ", attemptCounter)
    try:
        x = requests.get(host)
        responseCode = x.status_code
        print("Status code is: ", responseCode)
    except requests.exceptions.ConnectionError:
        print("Connection refused")
    attemptCounter += 1
    print("Sleeping for 30 seconds")
    time.sleep(30)
