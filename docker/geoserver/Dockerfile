ARG GS_VERSION=2.16.0

# TODO remplacer par 2.16.0 lorsque l'image sera taguée
FROM kartoza/geoserver:latest

LABEL maintainer="ATOL Conseils & Développements"

# Trick copy or download (Docker must exists). Pattern :
# COPY Dockerfile <resource>* <dest>/
# RUN ([ -f <dest>/<resource> ] || wget -P <dest> -q <>/<resource-part-url><resource>) && rm -f Dockerfile

COPY scripts/* /scripts/

ENV GS_VERSION ${GS_VERSION:-2.16.0}

USER root

# Installation unzip
RUN set -e \
    && apt-get -y update \
    && apt-get install -y unzip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Plugin querylayer
COPY Dockerfile ext/geoserver-${GS_VERSION}-querylayer-plugin.zip* /tmp/
RUN set -x \
  && ([ -f /tmp/geoserver-${GS_VERSION}-querylayer-plugin.zip ] \
    || wget -P /tmp -q https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/extensions/geoserver-${GS_VERSION}-querylayer-plugin.zip) \
  && unzip -qn /tmp/geoserver-*-querylayer-plugin.zip -d /usr/local/tomcat/webapps/geoserver/WEB-INF/lib/ \
  && rm -f /tmp/geoserver-*-querylayer-plugin.zip \
  && rm -f Dockerfile # cleaning

# ---------------
# Non root user
# ---------------
ARG UID=1000
ARG GID=1000
RUN usermod -u ${UID} geoserver \
  && groupmod -g ${GID} geoserver \
  && chown -R geoserver /usr/local/tomcat

ENTRYPOINT ["/scripts/entrypoint-remocra.sh"]