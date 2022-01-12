FROM tomcat:7.0.109-jdk8-openjdk-slim

LABEL maintainer="ATOL Conseils & Développements"

# curl
RUN set -e \
    && apt-get -y update \
    && apt-get install -y curl unzip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY scripts/* /scripts/

# ENV
ENV LANG fr_FR.UTF-8
ENV TZ Europe/Paris

RUN rm -rf /usr/local/tomcat/webapps/*

# Gestion des URIs avec accents (exemple : GET avec QueryParam)
RUN sed -i "s/<Connector port=\"8080\"/<Connector URIEncoding=\"utf-8\" relaxedQueryChars=\"\[\]\" port=\"8080\"/" /usr/local/tomcat/conf/server.xml

# TODO voir si nécessaire pour 443 via passerelle (redirection à la connexion)
# Eventuellement mapper ce fichier en volume
# RUN sed -i "s/<ConnectorURIEncoding=\"utf-8\"/<Connector secure=\"true\" scheme=\"https\" proxyPort=\"443\" URIEncoding=\"utf-8\"/" /usr/local/tomcat/conf/server.xml

COPY ext/remocra.war /usr/local/tomcat/webapps

RUN cd /usr/local/tomcat/webapps && unzip remocra.war -d remocra && rm remocra.war


ENV \
    REMOCRA_INITIAL_MEMORY="1G" \
    REMOCRA_MAXIMUM_MEMORY="2G"

# ---------------
# Non root user
# ---------------
ARG UID=1000
ARG GID=1000
RUN groupadd -g ${GID} remocra \
    && useradd -u ${UID} -g remocra remocra \
    && chown -R remocra /usr/local/tomcat \
    && chown -R remocra /scripts

# Droits
RUN mkdir -p /var/remocra/log \
  && chown -R remocra:remocra /var/remocra/log

USER remocra

VOLUME /var/remocra

ENTRYPOINT ["/scripts/entrypoint.sh"]
