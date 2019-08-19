# Tâche principale de création des notifications de début et de fin d'indisponibilité temporaire d'hydrant
# Périodicité : A DETERMINER

cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/modeles/processus_etl/commun/maintenance/indisponibilite_temporaire/creer_notifications_indisponibilites.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

