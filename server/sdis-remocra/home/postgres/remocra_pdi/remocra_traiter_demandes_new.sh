# Tâche principale de récupération et d'execution des demandes de traitements effectuées via l'interface de lancement de l'applicatif.
# Périodicité : A DETERMINER

cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/modeles/processus_etl/commun/modeles_de_processus_et_executions/executer_les_processus_etl/executer_les_processus_etl.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

