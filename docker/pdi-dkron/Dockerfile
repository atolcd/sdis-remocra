FROM openjdk:8-jdk-alpine

LABEL maintainer="ATOL Conseils & Développements"

# ---------------
# Non root user
# ---------------
ARG UID=1000
ARG GID=1000
RUN addgroup -g ${GID} -S pdi && \
    adduser -u ${UID} -S pdi -G pdi

COPY scripts/* /scripts/

#Variable d'environnement tz
RUN apk add tzdata

# ---------------
# Pentaho Data Integration
# ---------------

# ENV
ENV LANG                        fr_FR.UTF-8
ENV ENCODING                    UTF-8
ENV TZ                          Europe/Paris

ENV KETTLE_HOME                 /home/pdi
ENV PENTAHO_METASTORE_FOLDER    ${KETTLE_HOME}/.pentaho/metastore
ENV KETTLE_JNDI_ROOT            ${KETTLE_HOME}/simple-jndi

ENV \
    PDI_INITIAL_MEMORY="1G" \
    PDI_MAXIMUM_MEMORY="2G" \
    PDI_MAXIMUM_PERMSIZE="256m"

WORKDIR ${KETTLE_HOME}

# PDI
# https://sourceforge.net/projects/pentaho/files/Data%20Integration/7.1/pdi-ce-7.1.0.0-12.zip
RUN --mount=type=bind,source=ext/pdi-ce-7.1.0.0-12.zip,target=/tmp/pdi-ce-7.1.0.0-12.zip \
  unzip -qo /tmp/pdi-ce-7.1.0.0-12.zip -d ${KETTLE_HOME} \
  && mv ${KETTLE_HOME}/data-integration/* ${KETTLE_HOME} \
  && rmdir ${KETTLE_HOME}/data-integration \
  && chmod +x ${KETTLE_HOME}/*.sh \
  # Sera mis à jour juste après
  && rm -f ${KETTLE_HOME}/lib/postgresql-*

# Mise à jour de JDBC driver
# https://jdbc.postgresql.org/download/postgresql-42.2.10.jar
COPY ext/postgresql-42.2.10.jar ${KETTLE_HOME}/lib/

# Plugin GIS
# https://github.com/atolcd/pentaho-gis-plugins/releases/download/v1.2.1/pentaho-gis-plugins-1.2.1-bin-7.zip
RUN --mount=type=bind,source=ext/pentaho-gis-plugins-1.2.1-bin-7.zip,target=/tmp/pentaho-gis-plugins-1.2.1-bin-7.zip \
  unzip /tmp/pentaho-gis-plugins-1.2.1-bin-7.zip -d ${KETTLE_HOME}/plugins

# Plugin DOC
# https://github.com/atolcd/pentaho-doc-plugins/releases/download/v1.0.0/pentaho-doc-plugins-1.0.0-bin-7.zip
RUN --mount=type=bind,source=ext/pentaho-doc-plugins-1.0.0-bin-7.zip,target=/tmp/pentaho-doc-plugins-1.0.0-bin-7.zip \
  unzip /tmp/pentaho-doc-plugins-1.0.0-bin-7.zip -d ${KETTLE_HOME}/plugins

# Oracle
# https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/ojdbc8.jar
COPY ext/ojdbc8.jar ext/orai18n.jar ${KETTLE_HOME}/lib/

# MySql
# https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/mysql-connector-java-5.1.49.jar
COPY ext/mysql-connector-java-5.1.49.jar ${KETTLE_HOME}/lib/

# MSSql
# https://kumisystems.dl.sourceforge.net/project/jtds/jtds/1.3.1/jtds-1.3.1-dist.zip
COPY ext/jtds-1.3.1.jar ext/jtds-1.3.1.jar ${KETTLE_HOME}/lib/

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

ENV DKRON_VERSION 2.1.1
ENV DKRON_HOME /dkron

RUN set -x \
  && buildDeps='bash ca-certificates openssl curl gettext' \
  && apk add --update $buildDeps \
  && rm -rf /var/cache/apk/*

# https://github.com/distribworks/dkron/releases/download/v${DKRON_VERSION}/dkron_${DKRON_VERSION}_linux_amd64.tar.gz
RUN --mount=type=bind,source=ext/dkron_${DKRON_VERSION}_linux_amd64.tar.gz,target=/tmp/dkron_${DKRON_VERSION}_linux_amd64.tar.gz \
  mkdir -p ${DKRON_HOME} \
  && tar -xzf /tmp/dkron_${DKRON_VERSION}_linux_amd64.tar.gz -C ${DKRON_HOME}

# Droits
RUN mkdir -p ${KETTLE_HOME}/.kettle /var/remocra/pdi/log \
  && chmod a+rw ${KETTLE_HOME}/.kettle /var/remocra/pdi/log

USER pdi

VOLUME /var/remocra \
  ${KETTLE_HOME}/.kettle/kettle.properties ${KETTLE_HOME}/remocra.properties \
  ${DKRON_HOME}/dkron.data

ENV SHELL /bin/bash
WORKDIR ${DKRON_HOME}
ENTRYPOINT ["/scripts/entrypoint.sh"]
