FROM openjdk:8

LABEL maintainer="ATOL Conseils & Développements"

ARG GS_VERSION=2.16.2

# Trick copy or download (Docker must exists). Pattern :
# COPY Dockerfile <resource>* <dest>/
# RUN ([ -f <dest>/<resource> ] || wget -P <dest> -q <>/<resource-part-url><resource>) && rm -f Dockerfile

ENV GEOSERVER_HOME=/var/lib/geoserver
ENV GEOSERVER_DATA_DIR=/var/remocra/geoserver_data

ENV TZ=Europe/Paris

COPY scripts/* /scripts/

# unzip
RUN set -e \
    && apt-get -y update \
    && apt-get install -y unzip htop \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && chmod +x /scripts/*.sh

# GeoServer
COPY Dockerfile ext/geoserver-${GS_VERSION}-bin.zip* /tmp/
RUN set -x \
  && ([ -f /tmp/geoserver-${GS_VERSION}-bin.zip ] \
    || wget -P /tmp -q https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/geoserver-${GS_VERSION}-bin.zip) \
  && unzip -qn /tmp/geoserver-${GS_VERSION}-bin.zip -d $(dirname "$GEOSERVER_HOME") \
  && mv -f $(dirname "$GEOSERVER_HOME")/geoserver-${GS_VERSION} $GEOSERVER_HOME \
  && rm -f /tmp/geoserver-*-bin.zip \
  && rm -f Dockerfile # cleaning

# Plugin querylayer
COPY Dockerfile ext/geoserver-${GS_VERSION}-querylayer-plugin.zip* /tmp/
RUN set -x \
  && ([ -f /tmp/geoserver-${GS_VERSION}-querylayer-plugin.zip ] \
    || wget -P /tmp -q https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/extensions/geoserver-${GS_VERSION}-querylayer-plugin.zip) \
  && unzip -qn /tmp/geoserver-*-querylayer-plugin.zip -d ${GEOSERVER_HOME}/webapps/geoserver/WEB-INF/lib/ \
  && rm -f /tmp/geoserver-*-querylayer-plugin.zip \
  && rm -f Dockerfile # cleaning

# https://geoserver.geo-solutions.it/edu/en/install_run/jai_io_install.html

# JAI
COPY Dockerfile ext/jai-1_1_3-lib-linux-amd64.tar.gz* /tmp/
RUN set -x \
  && ([ -f /tmp/jai-1_1_3-lib-linux-amd64.tar.gz ] \
    || wget -P /tmp -q https://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib-linux-amd64.tar.gz) \
  && tar -xzf /tmp/jai-1_1_3-lib-linux-amd64.tar.gz -C /tmp \
  && mv /tmp/jai-1_1_3/lib/*.jar $JAVA_HOME/jre/lib/ext/ \
  && mv /tmp/jai-1_1_3/lib/*.so $JAVA_HOME/jre/lib/amd64/ \
  && rm -f /tmp/jai-1_1_3-lib-linux-amd64.tar.gz \
  && rm -r /tmp/jai-1_1_3 \
  && rm -f Dockerfile # cleaning

# JAI Image I/O
COPY Dockerfile ext/jai_imageio-1_1-lib-linux-amd64.tar.gz* /tmp/
RUN set -x \
  && ([ -f /tmp/jai_imageio-1_1-lib-linux-amd64.tar.gz ] \
    || wget -P /tmp -q https://download.java.net/media/jai-imageio/builds/release/1.1/jai_imageio-1_1-lib-linux-amd64.tar.gz) \
  && tar -xzf /tmp/jai_imageio-1_1-lib-linux-amd64.tar.gz -C /tmp \
  && mv /tmp/jai_imageio-1_1/lib/*.jar $JAVA_HOME/jre/lib/ext/ \
  && mv /tmp/jai_imageio-1_1/lib/*.so $JAVA_HOME/jre/lib/amd64/ \
  && rm -f /tmp/jai_imageio-1_1-lib-linux-amd64.tar.gz \
  && rm -r /tmp/jai_imageio-1_1 \
  && rm -f Dockerfile # cleaning

# Cleaning GeoServer
RUN rm -f ${GEOSERVER_HOME}/webapps/geoserver/WEB-INF/lib/jai*.jar

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