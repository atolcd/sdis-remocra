# Importer les utilisateurs

```sh
curl -v http://localhost:8070/v1/jobs -XPOST -d '{
  "name": "importer-utilisateurs",
  "displayname": "Importer les utilisateurs",
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
    "command": "/home/pdi/kitchen.sh -file:/var/remocra/pdi/traitements_sdis/66/utilisateur/synchroniser_ldap_remocra.kjb -level:Error -param:PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties >> /var/remocra/pdi/log/remocra_importer_utilisateurs.log 2>&1"
  }
}'
```
