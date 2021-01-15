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

# Purger les anciens fichiers .TXT de PDI
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "purger-anciens-txt-pdi",
  "displayname": "Purger les anciens fichiers .TXT de PDI",
  "schedule": "0 50 23 * * *",
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
    "command": "find /var/remocra/pdi/log/ -mtime +7 -name \"*.TXT\" -print -delete"
  }
}'

# Notification des indisponibilités temporaires
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
