#!/bin/bash
#Script created to invoke jmeter test script with the slave POD IP addresses
#Script should be run like: ./load_test "path to the test script in jmx format"
/jmeter/apache-jmeter-*/bin/jmeter -n -t $1 -l jm_report.csv -Dserver.rmi.ssl.disable=true -R $(getent ahostsv4 jmeter-slaves-svc | cut -d' ' -f1 | sort -u | awk -v ORS=, '{print $1}' | sed 's/,$//')
