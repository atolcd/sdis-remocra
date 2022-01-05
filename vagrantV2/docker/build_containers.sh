export REMOCRA_UID=2000
export REMOCRA_GID=2000

# Utilisateur et groupe
id -g ${REMOCRA_GID} || sudo groupadd -g ${REMOCRA_GID} remocra
id -u ${REMOCRA_UID} || sudo useradd remocra --uid ${REMOCRA_UID} --gid ${REMOCRA_GID} --groups ${REMOCRA_GID} --shell=/bin/nolog

# Propriétaire des fichiers
[ $(find /etc/docker_remocra/.docker/ -not -path "*.docker/db/postgresql_data*" ! -user ${REMOCRA_UID} 2>/dev/null | wc -l) -gt 0 ] && \
  echo "Changement propriétaire en ${REMOCRA_UID}:${REMOCRA_GID}" && \
  sudo find /etc/docker_remocra/.docker/ -not -path "*.docker/db/postgresql_data*" -exec chown ${REMOCRA_UID}:${REMOCRA_GID} {} \;

# Lancement des services (on retire les lignes "build" et on référence les images du registry)
cd /etc/docker_remocra/
export REMOCRA_VERSION="xxxxx"
cat docker-compose.yml | \
  sed '/build: \.\/.*/d' | \
  sed "s%image: remocra-geoserver%image: client-docker-registry.atolcd.com/atolcd/remocra-geoserver:${REMOCRA_VERSION}%g" | \
  sed "s%image: remocra-pdi-4.4-dkron%image: client-docker-registry.atolcd.com/atolcd/remocra-pdi-4.4-dkron:${REMOCRA_VERSION}%g" | \
  sed "s%image: remocra-pdi-dkron%image: client-docker-registry.atolcd.com/atolcd/remocra-pdi-dkron:${REMOCRA_VERSION}%g" | \
  sed "s%image: remocra%image: client-docker-registry.atolcd.com/atolcd/remocra:${REMOCRA_VERSION}%g" | \
  sed "s%image: remocra-api%image: client-docker-registry.atolcd.com/atolcd/remocra-api-remocra:${REMOCRA_VERSION}%g" | \
  docker-compose -f - up  --remove-orphans -d
