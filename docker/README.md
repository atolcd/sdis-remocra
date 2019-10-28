# SDIS Remocra - Docker

## Construction des images

Certains fichiers sont téléchargés lors du build. Les builds répétés peuvent être accélérés en récupérant les fichiers volumineux en amont dans les répertoires `ext`. Exemple :

```sh
# geoserver
(
  GS_VERSION=2.16.0
  cd ~/projets/atolcd/sdis-remocra/docker/geoserver/ext \
  && wget https://sourceforge.net/projects/geoserver/files/GeoServer/${GS_VERSION}/extensions/geoserver-${GS_VERSION}-querylayer-plugin.zip
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
### Connexion à la base de données : username / password

* `docker-compose.yml` :
  * services.db.environment : POSTGRES_USER / POSTGRES_PASSWORD
  * services.remocra.environment : JAVA_OPTS
* `.docker/jobs-common/kettle.properties` :
  * REMOCRA_POSTGIS_DATABASE_USER_NAME / REMOCRA_POSTGIS_DATABASE_USER_PASSWORD
* `.docker/jobs-4.4/.kettle/repositories.xml`
  * username / password
* `.docker/var_remocra/geoserver_data/workspaces/remocra_bspp/remocra_bspp_refererentiel/datastore.xml`
  * user / passwd
* `.docker/var_remocra/geoserver_data/workspaces/remocra/remocra/datastore.xml`
  * user / passwd

### Connexion à GeoServer

* `docker-compose.yml` :
  * services.geoserver.environment : GEOSERVER_ADMIN_USER / GEOSERVER_ADMIN_PASSWORD
* `.docker/var_remocra/geoserver_data/security/usergroup/default/users.xml`
  * password
  * (réalisé au lancement du service en cas d'absence du fichier  `.updatepassword.lock`)

Exemples de modification à réaliser avant de lancer les services :
```sh
# ------------------------------
# Paramètres
# ------------------------------
export POSTGRES_DB_HOSTNAME=postgis.sdisxx.fr
export POSTGRES_DB_USERNAME=remocra
export POSTGRES_DB_PASSWORD=sqDD43l8qds4d
export GEOSERVER_ADMIN_USERNAME=remocraadmin
export GEOSERVER_ADMIN_PASSWORD=65E8dapkf

cd ~/projets/atolcd/sdis-remocra/docker
# ------------------------------
# Accès à la base de données
# ------------------------------
# docker-compose.yml
sed -i "s/POSTGRES_USER:.*/POSTGRES_USER: ${POSTGRES_DB_USERNAME}/g" docker-compose.yml
sed -i "s/POSTGRES_PASSWORD:.*/POSTGRES_PASSWORD: ${POSTGRES_DB_PASSWORD}/g" docker-compose.yml
sed -i "s/-Ddatabase.username=[^ ]* /-Ddatabase.username=${POSTGRES_DB_USERNAME} /" docker-compose.yml
sed -i "s/-Ddatabase.password=[^ ]* /-Ddatabase.password=${POSTGRES_DB_PASSWORD} /" docker-compose.yml
sed -i "s/-Ddatabase.url=jdbc\\\:postgresql\\\:\/\/.*\\\:5432\/remocra[^ ]* /-Ddatabase.url=jdbc\\\:postgresql\\\:\/\/${POSTGRES_DB_HOSTNAME}\\\:5432\/remocra /" docker-compose.yml
# kettle.properties
sed -i "s/REMOCRA_POSTGIS_DATABASE_HOST.*/REMOCRA_POSTGIS_DATABASE_HOST = ${POSTGRES_DB_HOSTNAME}/g" .docker/jobs-common/kettle.properties
sed -i "s/REMOCRA_POSTGIS_DATABASE_USER_NAME.*/REMOCRA_POSTGIS_DATABASE_USER_NAME = ${POSTGRES_DB_USERNAME}/g" .docker/jobs-common/kettle.properties
sed -i "s/REMOCRA_POSTGIS_DATABASE_USER_PASSWORD.*/REMOCRA_POSTGIS_DATABASE_USER_PASSWORD = ${POSTGRES_DB_PASSWORD}/g" .docker/jobs-common/kettle.properties
# repositories.xml
sed -i "s/<server>.*<\/server>/<server>${POSTGRES_DB_HOSTNAME}<\/server>/g" .docker/jobs-4.4/.kettle/repositories.xml
sed -i "s/<username>.*<\/username>/<username>${POSTGRES_DB_USERNAME}<\/username>/g" .docker/jobs-4.4/.kettle/repositories.xml
sed -i "s/<password>.*<\/password>/<password>${POSTGRES_DB_PASSWORD}<\/password>/g" .docker/jobs-4.4/.kettle/repositories.xml
# datastore.xml
find .docker/var_remocra/geoserver_data -type f -name "datastore.xml" -exec sed -i "s/<entry key=\"host\">localhost<\/entry>/<entry key=\"host\">${POSTGRES_DB_HOSTNAME}<\/entry>/g" {} \;
find .docker/var_remocra/geoserver_data -type f -name "datastore.xml" -exec grep -l '<entry key="host">${POSTGRES_DB_HOSTNAME}<\/entry>' {} \; -exec sed -i "s/<entry key=\"user\">.*<\/entry>/<entry key=\"user\">${POSTGRES_DB_USERNAME}<\/entry>/g" {} \;
find .docker/var_remocra/geoserver_data -type f -name "datastore.xml" -exec grep -l '<entry key="host">${POSTGRES_DB_HOSTNAME}<\/entry>' {} \; -exec sed -i "s/<entry key=\"passwd\">.*<\/entry>/<entry key=\"passwd\">plain:${POSTGRES_DB_PASSWORD}<\/entry>/g" {} \;

# ------------------------------
# GeoServer
# ------------------------------
rm -f .docker/var_remocra/geoserver_data/.updatepassword.lock
sed -i "s/GEOSERVER_ADMIN_USERNAME:.*/GEOSERVER_ADMIN_USERNAME: ${GEOSERVER_ADMIN_USERNAME}/g" docker-compose.yml
sed -i "s/GEOSERVER_ADMIN_PASSWORD:.*/GEOSERVER_ADMIN_PASSWORD: ${GEOSERVER_ADMIN_PASSWORD}/g" docker-compose.yml
```



## Base de données

```sh
cd ~/projets/atolcd/sdis-remocra/docker
docker-compose up db
```


### Bases de données

```sh
export DBACCESS="-U ${POSTGRES_DB_USERNAME:-remocra} -h localhost"
cd ~/projets/atolcd/sdis-remocra/docker
```

#### Référentiel PDI

```sh
REFERENTIEL_DUMP=~/projets/atolcd/sdis-remocra/server/sdis-remocra/home/postgres/pdi_db/000_remocra_pdi_all.sql
# Mise en place du référentiel des traitements
#docker exec -i remocra-db psql ${DBACCESS} -d remocra_ref_pdi -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'remocra_ref_pdi' AND pid <> pg_backend_pid();"
docker exec -it remocra-db dropdb remocra_ref_pdi ${DBACCESS}
docker exec -it remocra-db createdb ${DBACCESS} -E UTF8 remocra_ref_pdi
docker exec -i remocra-db psql ${DBACCESS} -d remocra_ref_pdi < ${REFERENTIEL_DUMP} 
```

#### Remocra

##### Cas 1 : vierge (à valider)
```sh
# Reprise des scripts SQL (à compléter si besoin)
find ../server/sdis-remocra/home/postgres/remocra_db -type f -name "*.sql" -exec sed -i "s/ndims/st_ndims/g" {} \;
find ../server/sdis-remocra/home/postgres/remocra_db -type f -name "*.sql" -exec sed -i "s/\.srid/\.st_srid/g" {} \;
find ../server/sdis-remocra/home/postgres/remocra_db -type f -name "*.sql" -exec sed -i "s/(srid(/(st_srid(/g" {} \;
sed -i '/\\i 030_acces.sql/s/^/--/g' ../server/sdis-remocra/home/postgres/remocra_db/000_remocra_all.sql
# Reset
../server/sdis-remocra/home/postgres/remocra_db/reset_db.sh
docker-compose restart remocra

# Mise à jour des paramètres
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.param_conf set valeur='/home/pdi/remocra.properties' where cle='PDI_FICHIER_PARAMETRAGE'"
docker exec -it remocra-db psql -U postgres remocra -c "update remocra.param_conf set valeur='http://geoserver.sdisxx.fr:8080/geoserver' where cle='WMS_BASE_URL'"
```

#####  Cas 2 : à partir d'un dump (migration)
```sh
# Migration postgis 1.5 vers postgis 2
# https://postgis.net/docs/postgis_installation.html#hard_upgrade

# ➔ Sur le serveur d'origine
# Récupération du dump
pg_dump -Fc -b -v -f "remocra-1.5.backup" remocra

# ➔ Sur l'hôte
# Préparation
sudo mv remocra-1.5.backup ~/projets/atolcd/sdis-remocra/docker/.docker/db/postgresql_data

# Migration
export DBACCESS="-U ${POSTGRES_DB_USERNAME:-remocra} -h localhost"
docker exec -it remocra-db dropdb remocra ${DBACCESS}
docker exec -it remocra-db createdb remocra ${DBACCESS} -E UTF8 remocra
docker exec -it remocra-db psql ${DBACCESS} remocra -c "CREATE EXTENSION postgis;"
docker exec -it remocra-db psql ${DBACCESS} remocra -f /usr/local/share/postgresql/contrib/postgis-2.5/legacy.sql
docker exec -it remocra-db apk add perl
docker exec -it remocra-db bash -c "perl /usr/local/share/postgresql/contrib/postgis-2.5/postgis_restore.pl /var/lib/postgresql/data/remocra-1.5.backup | psql ${DBACCESS} remocra 2> /var/lib/postgresql/data/errors.txt"
docker exec -it remocra-db cat /var/lib/postgresql/data/errors.txt
docker exec -it remocra-db apk del perl

# Patches si nécessaire :
# docker exec -it remocra-db psql -U postgres remocra -c "select * from remocra.suivi_patches order by numero desc limit 1"
# remocra_patches '108 109 110 111 112 113 114 115 116 117 118'

docker exec -it remocra-db psql ${DBACCESS} remocra -f /usr/local/share/postgresql/contrib/postgis-2.5/uninstall_legacy.sql

# Mise à jour des paramètres
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.param_conf set valeur='/home/pdi/remocra.properties' where cle='PDI_FICHIER_PARAMETRAGE'"
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.param_conf set valeur='http://geoserver.sdisxx.fr:8080/geoserver' where cle='WMS_BASE_URL'"

docker up
```

##### Pour les tests

```sh
EMAIL_USERNAME=cva
EMAIL_DOMAIN=atolcd.com
SMTP_HOST=smtp.priv.atolcd.comm
# Redéfinition des mails par sécurité
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.utilisateur set email='${EMAIL_USERNAME}+'||lower(identifiant)||'@${EMAIL_DOMAIN}'"
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.organisme set email_contact='${EMAIL_USERNAME}+'||lower(code)||'@${EMAIL_DOMAIN}'"
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.param_conf set valeur='${EMAIL_USERNAME}@${EMAIL_DOMAIN}' where valeur like '%@%'"
# Serveur SMTP
docker exec -it remocra-db psql ${DBACCESS} remocra -c "update remocra.param_conf set valeur='${SMTP_HOST}' where cle='PDI_SMTP_URL'"
```



## Démarrage des services

### Exécution
```sh
cd ~/projets/atolcd/sdis-remocra/docker
docker-compose up
```
Accès :
* Remocra : http://localhost:8080/remocra
* GeoServer : http://localhost:8090/geoserver
* Planification : http://localhost:8070 / (http://localhost:8060)


### Commandes utiles

```sh
# Commande basique
docker exec -it remocra ls /var/remocra

# Requête
docker exec -it remocra-db psql ${DBACCESS} remocra -c "select * from remocra.utilisateur order by id asc limit 5"

# Shell
docker exec -it remocra /bin/bash
```



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
docker build -t remocra-pdi-4.4 .
```



## Conteneur PDI

### Construction

```sh
# Construction
cd ~/projets/atolcd/sdis-remocra/docker/pdi
docker build -t remocra-pdi .
```

```sh
# Mise en place des fichiers de traitements
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
  --entrypoint="/home/pdi/kitchen.sh" \
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




```sh

# Génération du fichier de propriétés au bon endroit
docker exec -it remocra-db psql -${DBACCESS} remocra -c "update remocra.param_conf set valeur='/home/pdi/remocra.properties' where cle='PDI_FICHIER_PARAMETRAGE'"

cd ~/projets/atolcd/sdis-remocra/docker/pdi-4.4-dkron
docker build -t remocra-pdi-4.4-dkron .


# Lancement du planificateur
docker run --rm \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/.docker/var_remocra":/var/remocra \
  -v "$(pwd)/.docker/jobs-common/kettle.properties":/home/pdi/.kettle/kettle.properties \
  -v "$(pwd)/.docker/jobs-common/remocra.properties":/home/pdi/remocra.properties \
  -v "$(pwd)/.docker/jobs-4.4/.kettle/repositories.xml":/home/pdi/.kettle/repositories.xml \
  -v "$(pwd)/.docker/jobs-4.4/dkron_data":/dkron/dkron.data \
  --network docker_remocra --link remocra-db:postgis.sdisxx.fr \
  --entrypoint="/home/pdi/kitchen.sh" \
  remocra-pdi-4.4-dkron \
  \
  -file:"/var/remocra/pdi/traitements_sdis/bspp/creer_demandes_indispo_carre9/creer_demandes_indispo_carre9.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties"


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