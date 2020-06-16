# Notifier les changements d état

```sh
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "notifier-changement-etat",
  "displayname": "Notifier les changements d état",
  "schedule": "0 0 5 * * *",
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
    "command": "/home/pdi/kitchen.sh -file:/var/remocra/pdi/traitements_sdis/66/notifier_changement_etat/notifier_changement_etat_pei_public.kjb -level:Error -param:PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties >> /var/remocra/pdi/log/remocra_notifier_changement_etat_pei_public.log 2>&1"
  }
}'
```