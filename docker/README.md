# SDIS Remocra - Docker

## Construction des images

Certains fichiers sont téléchargés lors du build. Les builds répétés peuvent être accélérés en récupérant les fichiers volumineux en amont dans les répertoires `ext`. Exemple :

```sh
# geoserver
(
  GS_VERSION=2.16.0
  cd ~/projets/atolcd/sdis-remocra/docker/geoserver/ext \
  && wget https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/geoserver-${GS_VERSION}-bin.zip \
  && wget https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/extensions/geoserver-${GS_VERSION}-querylayer-plugin.zip \
  && wget https://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib-linux-amd64.tar.gz \
  && wget https://download.java.net/media/jai-imageio/builds/release/1.1/jai_imageio-1_1-lib-linux-amd64.tar.gz
)
# pdi-4.4-dkron
(
  DKRON_VERSION=2.0.0-rc7 \
  cd ~/projets/atolcd/sdis-remocra/docker/pdi-4.4-dkron/ext \
  && wget https://sourceforge.net/projects/pentaho/files/Data%20Integration/4.4.0-stable/pdi-ce-4.4.0-stable.zip \
  && wget https://github.com/atolcd/sdis-remocra/releases/download/pentaho-4.4-remocra-deps-4da0f/pentaho-4.4-remocra-deps.zip \
  && wget https://github.com/distribworks/dkron/releases/download/v${DKRON_VERSION}/dkron_${DKRON_VERSION}_linux_amd64.tar.gz
)
# pdi-dkron
(
  DKRON_VERSION=2.0.0-rc7 \
  cd ~/projets/atolcd/sdis-remocra/docker/pdi-dkron/ext \
  && wget https://sourceforge.net/projects/pentaho/files/Data%20Integration/7.1/pdi-ce-7.1.0.0-12.zip \
  && wget https://github.com/atolcd/pentaho-gis-plugins/releases/download/v1.2.1/pentaho-gis-plugins-1.2.1-bin-7.zip \
  && wget https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/ojdbc14.jar \
  && wget https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/home/postgres/pdi/libext/JDBC/orai18n.jar \
  && wget https://github.com/distribworks/dkron/releases/download/v${DKRON_VERSION}/dkron_${DKRON_VERSION}_linux_amd64.tar.gz
)
```

Construction des images :
```sh
# Construction de remocra.war
cd ~/projets/atolcd/sdis-remocra/dist/package-update.sh \
 && cp remocra/target/remocra.war docker/remocra/ext

# Construction des images Docker
cd ~/projets/atolcd/sdis-remocra/docker \
 && docker-compose build
```



## Configuration des services

La configuration des services est réalisée à travers des variables d'environnement. Le fichier `.env` définit les valeurs par défaut.
Cf. [Environment variables in Compose](https://docs.docker.com/compose/environment-variables/).

Les informations d'accès à la base PostgreSQL et à l'administration de GeoServer sont mises à jour lors du démarrage des conteneurs.



## Traitement des bases de données / fichiers

```sh
cd ~/projets/atolcd/sdis-remocra/docker
docker-compose up db
```

Pour la suite, on définit les variables suivantes :
```sh
export PGUSER=${POSTGRES_DB_USERNAME:-remocra}
cd ~/projets/atolcd/sdis-remocra/docker
```

### Référentiel PDI

```sh
REFERENTIEL_DUMP=~/projets/atolcd/sdis-remocra/server/sdis-remocra/home/postgres/pdi_db/000_remocra_pdi_all.sql
# Mise en place du référentiel des traitements
#docker exec -i -e PGUSER=$PGUSER remocra-db psql -d remocra_ref_pdi -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'remocra_ref_pdi' AND pid <> pg_backend_pid();"
docker exec -it -e PGUSER=$PGUSER remocra-db dropdb remocra_ref_pdi
docker exec -it -e PGUSER=$PGUSER remocra-db createdb -E UTF8 remocra_ref_pdi
docker exec -i -e PGUSER=$PGUSER remocra-db psql -d remocra_ref_pdi < ${REFERENTIEL_DUMP} 
```

### Base de données Remocra

#### Cas 1 : vierge (à valider)
```sh
# Reprise des scripts SQL - préparation à réaliser une fois et à versionner (à compléter si besoin)
find ../server/sdis-remocra/home/postgres/remocra_db -type f -name "*.sql" -exec sed -i "s/ndims/st_ndims/g" {} \;
find ../server/sdis-remocra/home/postgres/remocra_db -type f -name "*.sql" -exec sed -i "s/\.srid/\.st_srid/g" {} \;
find ../server/sdis-remocra/home/postgres/remocra_db -type f -name "*.sql" -exec sed -i "s/(srid(/(st_srid(/g" {} \;
sed -i '/\\i 030_acces.sql/s/^/--/g' ../server/sdis-remocra/home/postgres/remocra_db/000_remocra_all.sql
# Reset
../server/sdis-remocra/home/postgres/remocra_db/reset_db.sh
docker-compose restart remocra

# Mise à jour des paramètres
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.param_conf set valeur='/home/pdi/remocra.properties' where cle='PDI_FICHIER_PARAMETRAGE'"
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.param_conf set valeur='http://geoserver.sdisxx.fr:8080/geoserver' where cle='WMS_BASE_URL'"
```

####  Cas 2 : à partir d'un dump (migration)

Cette partie concerne la migration de postgis 1.5 vers postgis 2.
Cf. [Hard upgrade PostGIS](https://postgis.net/docs/postgis_installation.html#hard_upgrade).

Sur le serveur d'origine :
```sh
# Récupération du dump
pg_dump -Fc -b -v -f "remocra-1.5.backup" remocra
```
Sur l'hôte :
```sh
# Préparation
sudo mv remocra-1.5.backup ~/projets/atolcd/sdis-remocra/docker/.docker/db/postgresql_data

# Migration
docker exec -it -e PGUSER=$PGUSER remocra-db dropdb remocra
docker exec -it -e PGUSER=$PGUSER remocra-db createdb remocra -E UTF8 remocra
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "CREATE EXTENSION postgis;"
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -f /usr/local/share/postgresql/contrib/postgis-2.5/legacy.sql
docker exec -it remocra-db apk add perl
docker exec -it -e PGUSER=$PGUSER remocra-db bash -c "perl /usr/local/share/postgresql/contrib/postgis-2.5/postgis_restore.pl /var/lib/postgresql/data/remocra-1.5.backup | psql remocra 2> /var/lib/postgresql/data/errors.txt"
docker exec -it remocra-db cat /var/lib/postgresql/data/errors.txt
docker exec -it remocra-db apk del perl

# Patches éventuels
# docker exec -it remocra-db psql -U postgres remocra -c "select * from remocra.suivi_patches order by numero desc limit 1"
# remocra_patches '108 109 110 111 112 113 114 115 116 117 118'

docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -f /usr/local/share/postgresql/contrib/postgis-2.5/uninstall_legacy.sql

# Mise à jour des paramètres
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.param_conf set valeur='/home/pdi/remocra.properties' where cle='PDI_FICHIER_PARAMETRAGE'"
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.param_conf set valeur='http://geoserver.sdisxx.fr:8080/geoserver' where cle='WMS_BASE_URL'"

docker up
```

#### Pour les tests

```sh
EMAIL_USERNAME=cva
EMAIL_DOMAIN=atolcd.com
SMTP_HOST=smtp.priv.atolcd.comm
# Redéfinition des mails par sécurité
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.utilisateur set email='${EMAIL_USERNAME}+'||lower(identifiant)||'@${EMAIL_DOMAIN}'"
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.organisme set email_contact='${EMAIL_USERNAME}+'||lower(code)||'@${EMAIL_DOMAIN}'"
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.param_conf set valeur='${EMAIL_USERNAME}@${EMAIL_DOMAIN}' where valeur like '%@%'"
# Serveur SMTP
docker exec -it -e PGUSER=$PGUSER remocra-db psql remocra -c "update remocra.param_conf set valeur='${SMTP_HOST}' where cle='PDI_SMTP_URL'"
```

### Fichiers de configuration

Sur le serveur d'origine :
```sh
# Sauvegarde (exemple)
cd /var/remocra && zip -rq /root/var_remocra.zip . -x \
  atlas/**\* \
  geoserver_data/logs/*.log \
  pdi/depot/**\* \
  pdi/export/**\* \
  pdi/log/**\* \
  pdi/synchro/**\* \
  pdi/tmp/**\*
```

Sur l'hôte :
```sh
# Extraction
unzip -q var_remocra.zip -d ~/projets/atolcd/sdis-remocra/docker/.docker/var_remocra
```




## Démarrage des autres services

### Exécution
```sh
cd ~/projets/atolcd/sdis-remocra/docker
docker-compose up
```
Accès :
* Remocra : http://localhost:8080/remocra
* GeoServer : http://localhost:8090/geoserver
* Planification : http://localhost:8070 / (http://localhost:8060)



## Planification des traitements

```sh
# Regénérer remocra.properties
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "fichier-proprietes",
  "displayname": "Fichier propriétés",
  "schedule": "@every 10m",
  "owner": "",
  "owner_email": "",
  "disabled": false,
  "tags": {
    "pdi": "4.4:1"
  },
  "metadata": { },
  "retries": 0,
  "concurrency": "forbid",
  "executor": "shell",
  "executor_config": {
    "command": "/home/pdi/kitchen.sh -rep:'ref_pdi_remocra' -dir:'maintenance' -job:'generer_fichier_proprietes' -user:admin -pass:admin -level:Error -param:'PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties' >> /var/remocra/pdi/log/remocra_generer_fichier_proprietes.log 2>&1"
  }
}'

# Traiter demandes
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "traiter-demandes",
  "displayname": "Traiter demandes",
  "schedule": "@every 2m",
  "owner": "",
  "owner_email": "",
  "disabled": false,
  "tags": {
    "pdi": "4.4:1"
  },
  "metadata": { },
  "retries": 0,
  "concurrency": "forbid",
  "executor": "shell",
  "executor_config": {
    "command": "/home/pdi/kitchen.sh -rep:'ref_pdi_remocra' -dir:'demandes' -job:'traiter_demandes' -user:admin -pass:admin -level:Error -param:'PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties' >> /var/remocra/pdi/log/remocra_traiter_demandes.log 2>&1"
  }
}'

# Notifier utilisateurs
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "notifier-utilisateurs",
  "displayname": "Notifier utilisateurs",
  "schedule": "@every 1m",
  "owner": "",
  "owner_email": "",
  "disabled": false,
  "tags": {
    "pdi": "4.4:1"
  },
  "metadata": { },
  "retries": 0,
  "concurrency": "forbid",
  "executor": "shell",
  "executor_config": {
    "command": "/home/pdi/kitchen.sh -rep:'ref_pdi_remocra' -dir:'maintenance/notification' -job:'notifier_utilisateurs' -user:admin -pass:admin -level:Error -param:'PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties' >> /var/remocra/pdi/log/remocra_notifier_utilisateurs.log 2>&1"
  }
}'

# Purger
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "purger",
  "displayname": "Purger",
  "schedule": "0 0 6 * * *",
  "owner": "",
  "owner_email": "",
  "disabled": false,
  "tags": {
    "pdi": "4.4:1"
  },
  "metadata": { },
  "retries": 0,
  "concurrency": "forbid",
  "executor": "shell",
  "executor_config": {
    "command": "/home/pdi/kitchen.sh -rep:'ref_pdi_remocra' -dir:'maintenance/purge' -job:'purger' -user:admin -pass:admin -level:Error -param:'PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties' >> /var/remocra/pdi/log/remocra_purger.log 2>&1"
  }
}'

# Regénérer remocra.properties
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "notifier-indispo",
  "displayname": "Notifier indispo",
  "schedule": "@every 5m",
  "owner": "",
  "owner_email": "",
  "disabled": false,
  "tags": {
    "pdi": "7.1:1"
  },
  "metadata": { },
  "retries": 0,
  "concurrency": "forbid",
  "executor": "shell",
  "executor_config": {
    "command": "/home/pdi/kitchen.sh -file:'/var/remocra/modeles/processus_etl/commun/maintenance/indisponibilite_temporaire/creer_notifications_indisponibilites.kjb' -level:Error -param:'PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties' >> /var/remocra/pdi/log/remocra_notifier_indispo_temporaires.log 2>&1"
  }
}'
```




## Conteneur PDI 4.4

### Construction
```sh
# Construction
cd ~/projets/atolcd/sdis-remocra/docker/pdi-4.4-dkron
docker build -t remocra-pdi-4.4-dkron .
```

Si besoin, mettre en place le référentiel de traitement (voir plus haut)

### Exécution
```sh
# Exécution d'un traitement
docker run --rm \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/.docker/var_remocra":/var/remocra \
  -v "$(pwd)/.docker/jobs-common/kettle.properties":/home/pdi/.kettle/kettle.properties \
  -v "$(pwd)/.docker/jobs-common/remocra.properties":/home/pdi/remocra.properties \
  -v "$(pwd)/.docker/jobs-4.4/.kettle/repositories.xml":/home/pdi/.kettle/repositories.xml \
  -v "$(pwd)/.docker/jobs-4.4/dkron_data":/dkron/dkron.data \
  --network docker_remocra --link remocra-db:postgis.sdisxx.fr \
  --entrypoint="/scripts/entrypoint-pdi.sh" \
  remocra-pdi-4.4-dkron \
  \
  -file:"/var/remocra/pdi/traitements_sdis/bspp/creer_demandes_indispo_carre9/creer_demandes_indispo_carre9.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties"

# Lancement du planificateur
docker run -it --rm \
  --name "jobs-4.4" \
  \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/.docker/var_remocra":/var/remocra \
  -v "$(pwd)/.docker/jobs-common/kettle.properties":/home/pdi/.kettle/kettle.properties \
  -v "$(pwd)/.docker/jobs-common/remocra.properties":/home/pdi/remocra.properties \
  -v "$(pwd)/.docker/jobs-4.4/.kettle/repositories.xml":/home/pdi/.kettle/repositories.xml \
  -v "$(pwd)/.docker/jobs-4.4/dkron_data":/dkron/dkron.data \
  --network docker_remocra --link remocra-db:postgis.sdisxx.fr \
  \
  -p 8060:8080 \
  remocra-pdi-4.4-dkron \
  agent --server --bootstrap-expect=1 --node-name=jobs-4.4

 # http://localhost:8060
```




## Conteneur PDI

### Construction

```sh
# Construction
cd ~/projets/atolcd/sdis-remocra/docker/pdi
docker build -t remocra-pdi .
```

Si besoin, mettre en place les fichiers de traitements :
```sh
cp -r ~/projets/atolcd/sdis-remocra/server/sdis-remocra/var/remocra/pdi ~/projets/atolcd/sdis-remocra/docker/.docker/var_remocra
```

### Exécution

```sh
# Exécution d'un traitement
cd ~/projets/atolcd/sdis-remocra/docker
docker run --rm \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/.docker/var_remocra":/var/remocra \
  -v "$(pwd)/.docker/jobs-common/kettle.properties":/home/pdi/.kettle/kettle.properties \
  -v "$(pwd)/.docker/jobs-common/remocra.properties":/home/pdi/remocra.properties \
  -v "$(pwd)/.docker/jobs/dkron_data":/dkron/dkron.data \
  --network docker_remocra --link remocra-db:postgis.sdisxx.fr \
  --entrypoint="/scripts/entrypoint-pdi.sh" \
  remocra-pdi-dkron \
  \
  -file:"/var/remocra/pdi/traitements_sdis/bspp/creer_demandes_indispo_carre9/creer_demandes_indispo_carre9.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties"

# Lancement du planificateur
docker run -it --rm \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/.docker/var_remocra":/var/remocra \
  -v "$(pwd)/.docker/jobs-common/kettle.properties":/home/pdi/.kettle/kettle.properties \
  -v "$(pwd)/.docker/jobs-common/remocra.properties":/home/pdi/remocra.properties \
  -v "$(pwd)/.docker/jobs/dkron_data":/dkron/dkron.data \
  --network docker_remocra --link remocra-db:postgis.sdisxx.fr \
  \
  -p 8070:8080 \
  remocra-pdi-dkron \
  agent --server --bootstrap-expect=1 --node-name=jobs

 # http://localhost:8070
```