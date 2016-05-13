# Tâche principale de récupération et d'execution des demandes de traitements effectuées via l'interface de lancement de l'applicatif.
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"demandes" -job:"traiter_demandes" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

