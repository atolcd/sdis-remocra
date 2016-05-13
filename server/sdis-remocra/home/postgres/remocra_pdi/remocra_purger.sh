# Purge les éléments générés par le système en fonction de paramètres d'ancienneté (nombre de jours)
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"maintenance/purge" -job:"purger" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

