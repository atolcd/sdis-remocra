FROM openjdk:11-jre

ENV LANG fr_FR.UTF-8
ENV TZ Europe/Paris
ENV INITIAL_MEMORY="500m"
ENV MAXIMUM_MEMORY="1g"
ENV MAXIMUM_RAM="2g"

ARG UID=1000
ARG GID=1000
RUN groupadd --system --non-unique --gid ${GID} remocra \
    && useradd --no-log-init --system --no-create-home --home /app --non-unique --uid ${UID} --gid remocra remocra \
    && mkdir -p /app/log /app/config \
    && chown remocra: /app/log /app/config
USER remocra
WORKDIR /app

COPY dist/entrypoint.sh .
COPY api/target/lib/*.jar lib/
COPY doc/target/documentation doc/documentation/
COPY api/target/api-remocra-api*.jar lib/

EXPOSE 8881

# VOLUME /app/log
# VOLUME /app/config

ENTRYPOINT ["/app/entrypoint.sh"]
