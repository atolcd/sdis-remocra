FROM openjdk:7-jdk-alpine

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
COPY Dockerfile ext/pdi-ce-4.4.0-stable.zip* ${KETTLE_HOME}/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/pdi-ce-4.4.0-stable.zip ] \
    || wget -P ${KETTLE_HOME} -q https://sourceforge.net/projects/pentaho/files/Data%20Integration/4.4.0-stable/pdi-ce-4.4.0-stable.zip) \
  && unzip -qo ${KETTLE_HOME}/pdi-ce-4.4.0-stable.zip -d ${KETTLE_HOME} \
  && mv ${KETTLE_HOME}/data-integration/* ${KETTLE_HOME} \
  && rmdir ${KETTLE_HOME}/data-integration \
  && rm -f ${KETTLE_HOME}/pdi-ce-4.4.0-stable.zip \
  && chmod +x ${KETTLE_HOME}/*.sh \
  && sed -i "s/-cp \$CLASSPATH/-cp \$CLASSPATH -Dfile\.encoding=utf8/g" ${KETTLE_HOME}/kitchen.sh \
  && rm -f Dockerfile # cleaning

# Dépendances PDI 4.4
COPY Dockerfile ext/pentaho-4.4-remocra-deps.zip* ${KETTLE_HOME}/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/pentaho-4.4-remocra-deps.zip ] \
    || wget -P ${KETTLE_HOME} -q https://github.com/atolcd/sdis-remocra/releases/download/pentaho-4.4-remocra-deps-4da0f/pentaho-4.4-remocra-deps.zip) \
  && unzip ${KETTLE_HOME}/pentaho-4.4-remocra-deps.zip -d ${KETTLE_HOME} \
  && rm -f ${KETTLE_HOME}/pentaho-4.4-remocra-deps.zip \
  && rm -f Dockerfile # cleaning


# Mise à jour de JDBC driver
RUN rm -f ${KETTLE_HOME}/libext/JDBC/postgresql-*
COPY Dockerfile ext/postgresql-42.2.10.jre7.jar* ${KETTLE_HOME}/libext/JDBC/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/libext/JDBC/postgresql-42.2.10.jre7.jar ] \
    || wget -P ${KETTLE_HOME}/libext/JDBC -q https://jdbc.postgresql.org/download/postgresql-42.2.10.jre7.jar) \
  && rm -f Dockerfile # cleaning

# MySql
COPY Dockerfile ext/mysql-connector-java-5.1.49.jar* ext/mysql-connector-java-5.1.49.jar* ${KETTLE_HOME}/lib/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/lib/mysql-connector-java-5.1.49.jar ] \
    || wget -P ${KETTLE_HOME}/lib -q https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/mysql-connector-java-5.1.49.jar) \
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

ENV DKRON_VERSION 2.1.1
ENV DKRON_HOME /dkron

RUN set -x \
  && buildDeps='bash ca-certificates openssl curl gettext' \
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
  && mkdir -p /var/remocra/pdi/log \
  && chown -R pdi:pdi /var/remocra \
  && chown -R pdi:pdi /scripts \
  && chown -R pdi:pdi ${KETTLE_HOME} \
  && chown -R pdi:pdi ${DKRON_HOME}

USER pdi

VOLUME /var/remocra \
  ${KETTLE_HOME}/.kettle/kettle.properties ${KETTLE_HOME}/remocra.properties ${KETTLE_HOME}/.kettle/repositories.xml \
  ${DKRON_HOME}/dkron.data

ENV SHELL /bin/bash
WORKDIR ${DKRON_HOME}
ENTRYPOINT ["/scripts/entrypoint.sh"]
