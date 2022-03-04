FROM 461183108257.dkr.ecr.eu-west-2.amazonaws.com/uec/service-finder/docker-tools:latest

COPY assets/ /

RUN set -ex && \
    pip install requests-aws4auth && \
    find /scripts -type f -exec chmod 666 {} \; && \
    find /scripts -type d -exec chmod 777 {} \;

WORKDIR /scripts
ENTRYPOINT [ "/sbin/entrypoint.sh" ]
