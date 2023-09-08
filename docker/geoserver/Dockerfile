FROM openjdk:8

LABEL maintainer="ATOL Conseils & Développements"

ARG GS_VERSION=2.16.2

ENV GEOSERVER_HOME=/var/lib/geoserver
ENV GEOSERVER_DATA_DIR=/var/remocra/geoserver_data

ENV TZ=Europe/Paris

COPY scripts/* /scripts/

# unzip
RUN set -e \
    && apt-get -y update \
    && apt-get install -y unzip htop \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# GeoServer
# https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/geoserver-${GS_VERSION}-bin.zip
RUN --mount=type=bind,source=ext/geoserver-${GS_VERSION}-bin.zip,target=/tmp/geoserver-${GS_VERSION}-bin.zip \
  set -x \
  && unzip -qn /tmp/geoserver-${GS_VERSION}-bin.zip -d $(dirname "$GEOSERVER_HOME") \
  && mv -f $(dirname "$GEOSERVER_HOME")/geoserver-${GS_VERSION} $GEOSERVER_HOME

# Plugin querylayer
# https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/extensions/geoserver-${GS_VERSION}-querylayer-plugin.zip
RUN --mount=type=bind,source=ext/geoserver-${GS_VERSION}-querylayer-plugin.zip,target=/tmp/geoserver-${GS_VERSION}-querylayer-plugin.zip \
  set -x \
  && unzip -qn /tmp/geoserver-*-querylayer-plugin.zip -d ${GEOSERVER_HOME}/webapps/geoserver/WEB-INF/lib/ \
  && rm -f ${GEOSERVER_HOME}/webapps/geoserver/WEB-INF/lib/jai*.jar

# https://geoserver.geo-solutions.it/edu/en/install_run/jai_io_install.html

# JAI
# https://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib-linux-amd64.tar.gz
RUN --mount=type=bind,source=ext/jai-1_1_3-lib-linux-amd64.tar.gz,target=/tmp/jai-1_1_3-lib-linux-amd64.tar.gz \
  set -x \
  && tar -xzf /tmp/jai-1_1_3-lib-linux-amd64.tar.gz --strip-components=2 -C "$JAVA_HOME/jre/lib/ext/" --wildcards '*/lib/*.jar' \
  && tar -xzf /tmp/jai-1_1_3-lib-linux-amd64.tar.gz --strip-components=2 -C "$JAVA_HOME/jre/lib/amd64/" --wildcards '*/lib/*.so'

# JAI Image I/O
# https://download.java.net/media/jai-imageio/builds/release/1.1/jai_imageio-1_1-lib-linux-amd64.tar.gz
RUN --mount=type=bind,source=ext/jai_imageio-1_1-lib-linux-amd64.tar.gz,target=/tmp/jai_imageio-1_1-lib-linux-amd64.tar.gz \
  set -x \
  && tar -xzf /tmp/jai_imageio-1_1-lib-linux-amd64.tar.gz --strip-components=2 -C "$JAVA_HOME/jre/lib/ext/" --wildcards '*/lib/*.jar' \
  && tar -xzf /tmp/jai_imageio-1_1-lib-linux-amd64.tar.gz --strip-components=2 -C "$JAVA_HOME/jre/lib/amd64/" --wildcards '*/lib/*.so'

ENV \
    GEOSERVER_INITIAL_MEMORY="1G" \
    GEOSERVER_MAXIMUM_MEMORY="2G"

# ---------------
# Non root user
# ---------------
ARG UID=1000
ARG GID=1000
RUN groupadd -g ${GID} geoserver \
  && useradd -u ${UID} -g geoserver geoserver \
  && chown -R geoserver ${GEOSERVER_HOME} \
  && mkdir -p ${GEOSERVER_DATA_DIR} \
  && chown -R geoserver ${GEOSERVER_DATA_DIR}
USER geoserver

VOLUME ${GEOSERVER_DATA_DIR}

ENTRYPOINT ["/scripts/entrypoint.sh"]
