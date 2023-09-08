FROM openjdk:7-jdk-alpine

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
# https://sourceforge.net/projects/pentaho/files/Data%20Integration/4.4.0-stable/pdi-ce-4.4.0-stable.zip
RUN --mount=type=bind,source=ext/pdi-ce-4.4.0-stable.zip,target=/tmp/pdi-ce-4.4.0-stable.zip \
  unzip -qo /tmp/pdi-ce-4.4.0-stable.zip -d ${KETTLE_HOME} \
  && mv ${KETTLE_HOME}/data-integration/* ${KETTLE_HOME} \
  && rmdir ${KETTLE_HOME}/data-integration \
  && chmod +x ${KETTLE_HOME}/*.sh \
  && sed -i "s/-cp \$CLASSPATH/-cp \$CLASSPATH -Dfile\.encoding=utf8/g" ${KETTLE_HOME}/kitchen.sh \
  # Sera mis à jour juste après
  && rm -f ${KETTLE_HOME}/libext/JDBC/postgresql-*

# Mise à jour de JDBC driver
# https://jdbc.postgresql.org/download/postgresql-42.2.10.jre7.jar
COPY ext/postgresql-42.2.10.jre7.jar ${KETTLE_HOME}/libext/JDBC/

# Dépendances PDI 4.4
# https://github.com/atolcd/sdis-remocra/releases/download/pentaho-4.4-remocra-deps-4da0f/pentaho-4.4-remocra-deps.zip
RUN --mount=type=bind,source=ext/pentaho-4.4-remocra-deps.zip,target=/tmp/pentaho-4.4-remocra-deps.zip \
  unzip /tmp/pentaho-4.4-remocra-deps.zip -d ${KETTLE_HOME}

# MySql
# https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/mysql-connector-java-5.1.49.jar
COPY ext/mysql-connector-java-5.1.49.jar ${KETTLE_HOME}/lib/

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
  ${KETTLE_HOME}/.kettle/kettle.properties ${KETTLE_HOME}/remocra.properties ${KETTLE_HOME}/.kettle/repositories.xml \
  ${DKRON_HOME}/dkron.data

ENV SHELL /bin/bash
WORKDIR ${DKRON_HOME}
ENTRYPOINT ["/scripts/entrypoint.sh"]
