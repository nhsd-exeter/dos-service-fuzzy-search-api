#!/bin/sh

URL=$1
INITIAL_SCRIPT=$2

echo "Waiting for $URL to be available..."
until curl -s $URL/_cluster/health | grep -q '"status":"green"'; do
  sleep 1
done

echo "Begin data import into OpenSearch..."
sh "$INITIAL_SCRIPT"
echo "Finish data import into OpenSearch..."
