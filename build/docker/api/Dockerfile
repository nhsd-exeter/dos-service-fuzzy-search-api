FROM openjdk:latest AS builder
ENV \
    GOSU_VERSION="1.12"

RUN set -eux && \
    \
    # Get base packages
    apk add --no-cache \
        ca-certificates \
        dpkg \
        gnupg \
        wget \
    && \
    \
    # Get gosu
    dpkgArch="$(dpkg --print-architecture | awk -F- '{ print $NF }')" && \
    wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch" && \
    wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch.asc" && \
    export GNUPGHOME="$(mktemp -d)" && \
    gpg --batch --keyserver hkps://keys.openpgp.org --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 && \
    gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu && \
    gpgconf --kill all && \
    rm -rf "$GNUPGHOME" /usr/local/bin/gosu.asc && \
    chmod +x /usr/local/bin/gosu && \
    \
    # Assemble and optimise a set of Java modules
    jlink \
        --no-header-files \
        --no-man-pages \
        --strip-java-debug-attributes \
        --module-path /opt/java/openjdk/jmods \
        --add-modules java.base,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.security.jgss,jdk.crypto.ec,jdk.unsupported,java.sql \
        --verbose \
        --compress 2 \
        --output /jdk

# ==============================================================================

FROM alpine:latest
ENV \
    LANG="C.UTF-8" \
    LC_ALL="C.UTF-8" \
    SYSTEM_USER_GID="999" \
    SYSTEM_USER_UID="999" \
    SYSTEM_USER="openjdk" \
    TZ="Europe/London"

COPY --from=builder /usr/local/bin/gosu /usr/local/bin/gosu
COPY --from=builder /jdk /opt/jdk/
ENV PATH "/opt/jdk/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
RUN set -eux && \
    \
    # Install base packages
    apk add --no-cache \
        bash \
        curl \
    && \
    \
    # Configure system user
    ( deluser $SYSTEM_USER ||: ) && ( delgroup $SYSTEM_USER ||: ) && ( delgroup $(getent group $SYSTEM_USER_GID | sed -e "s/:x:$SYSTEM_USER_GID//g") ||: ) && \
    addgroup -S -g $SYSTEM_USER_GID $SYSTEM_USER && \
    adduser -H -D -s /bin/false -g "" -h /tmp -G $SYSTEM_USER -u $SYSTEM_USER_UID $SYSTEM_USER && \
    gosu nobody true


COPY assets/application/dos-service-fuzzy-search-api.jar /application/
CMD ["java", "-jar", "/application/dos-service-fuzzy-search-api.jar"]
