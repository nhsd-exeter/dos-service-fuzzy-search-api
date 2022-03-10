FROM wiremock/wiremock:2.32.0-alpine

COPY assets/wiremock_mappings/ /home/wiremock/mappings/

EXPOSE 8080
EXPOSE 8443

ENTRYPOINT ["/docker-entrypoint.sh"]
