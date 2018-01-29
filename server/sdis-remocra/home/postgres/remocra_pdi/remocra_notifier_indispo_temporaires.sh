# Tâche principale de création des notifications de début et de fin d'indisponibilité temporaire d'hydrant
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"maintenance/indisponibilites_temporaires" -job:"creer_notifications_indisponibilites" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

