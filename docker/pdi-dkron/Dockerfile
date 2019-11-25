FROM openjdk:8-jdk-alpine

LABEL maintainer="ATOL Conseils & Développements"

# Trick copy or download (Docker must exists). Pattern :
# COPY Dockerfile <resource>* <dest>/
# RUN ([ -f <dest>/<resource> ] || wget -P <dest> -q <>/<resource-part-url><resource>) && rm -f Dockerfile

# ---------------
# Non root user
# ---------------
ARG UID=1000
ARG GID=1000
RUN addgroup -g ${GID} -S pdi && \
    adduser -u ${UID} -S pdi -G pdi

COPY scripts/* /scripts/

# ---------------
# Pentaho Data Integration
# ---------------

# ENV
ENV LANG                        fr_FR.UTF-8
ENV ENCODING                    UTF-8

ENV KETTLE_HOME                 /home/pdi
ENV PENTAHO_METASTORE_FOLDER    /home/pdi/.pentaho/metastore
ENV KETTLE_JNDI_ROOT            /home/pdi/simple-jndi

# ENV PENTAHO_DI_JAVA_OPTIONS   "-DPENTAHO_METASTORE_FOLDER=/home/pdi/.pentaho"
ENV PENTAHO_DI_JAVA_OPTIONS     "-Xms1024m -Xmx2048m -XX:MaxPermSize=256m -DPENTAHO_METASTORE_FOLDER=/home/pdi/.pentaho"

WORKDIR ${KETTLE_HOME}

# PDI
COPY Dockerfile ext/pdi-ce-7.1.0.0-12.zip* ${KETTLE_HOME}/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/pdi-ce-7.1.0.0-12.zip ] \
    || wget -P ${KETTLE_HOME} -q https://sourceforge.net/projects/pentaho/files/Data%20Integration/7.1/pdi-ce-7.1.0.0-12.zip) \
  && unzip -qo ${KETTLE_HOME}/pdi-ce-7.1.0.0-12.zip -d ${KETTLE_HOME} \
  && mv ${KETTLE_HOME}/data-integration/* ${KETTLE_HOME} \
  && rmdir ${KETTLE_HOME}/data-integration \
  && rm -f ${KETTLE_HOME}/pdi-ce-7.1.0.0-12.zip \
  && chmod +x ${KETTLE_HOME}/*.sh \
  && rm -f Dockerfile # cleaning

# Plugin GIS
COPY Dockerfile ext/pentaho-gis-plugins-1.2.1-bin-7.zip* ${KETTLE_HOME}/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/pentaho-gis-plugins-1.2.1-bin-7.zip ] \
    || wget -P ${KETTLE_HOME} -q https://github.com/atolcd/pentaho-gis-plugins/releases/download/v1.2.1/pentaho-gis-plugins-1.2.1-bin-7.zip) \
  && unzip ${KETTLE_HOME}/pentaho-gis-plugins-1.2.1-bin-7.zip -d ${KETTLE_HOME}/plugins \
  && rm -f ${KETTLE_HOME}/pentaho-gis-plugins-1.2.1-bin-7.zip \
  && rm -f Dockerfile # cleaning

# Oracle
COPY Dockerfile ext/ojdbc14.jar* ext/orai18n.jar* ${KETTLE_HOME}/lib/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/lib/ojdbc14.jar ] \
    || wget -P ${KETTLE_HOME} -q https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/ojdbc14.jar) \
  && ([ -f ${KETTLE_HOME}/lib/orai18n.jar ] \
    || wget -P ${KETTLE_HOME} -q https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/orai18n.jar) \
  && rm -f Dockerfile # cleaning

ENV PATH ${KETTLE_HOME}/kitchen.sh:$PATH

# Pour exécution standalone
#ENTRYPOINT ["/scripts/entrypoint-pdi.sh"]


# ---------------
# Planification
# https://hub.docker.com/r/dkron/dkron/dockerfile
# Deux adaptations :
# - récupération du tar.gz en amont ou téléchargement
# - exécution non root
# ---------------

ENV DKRON_VERSION 2.0.0-rc7
ENV DKRON_HOME /dkron

RUN set -x \
  && buildDeps='bash ca-certificates openssl curl' \
  && apk add --update $buildDeps \
  && rm -rf /var/cache/apk/*

COPY Dockerfile ext/dkron_${DKRON_VERSION}_linux_amd64.tar.gz* ${DKRON_HOME}/
RUN set -x \
  && ([ -f ${DKRON_HOME}/dkron_${DKRON_VERSION}_linux_amd64.tar.gz ] \
    || wget -P ${DKRON_HOME} -q https://github.com/distribworks/dkron/releases/download/v${DKRON_VERSION}/dkron_${DKRON_VERSION}_linux_amd64.tar.gz) \
  && cd ${DKRON_HOME} \
  && tar -xzf dkron_${DKRON_VERSION}_linux_amd64.tar.gz \
  && rm ${DKRON_HOME}/dkron_${DKRON_VERSION}_linux_amd64.tar.gz \
  && rm -f Dockerfile # cleaning


# Droits
RUN mkdir -p ${KETTLE_HOME}/.kettle \
  && chown -R pdi:pdi ${KETTLE_HOME} \
  && chown -R pdi:pdi /scripts \
  && chown -R pdi:pdi ${DKRON_HOME}

USER pdi

EXPOSE 8080

ENV SHELL /bin/bash
WORKDIR ${DKRON_HOME}
ENTRYPOINT ["/scripts/entrypoint.sh"]
