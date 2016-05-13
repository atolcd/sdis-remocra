# Tâche principale de récupération et de publication d'un fichier KML des risques
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"synchronisations/import_remocra" -job:"integrer_kml" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties" 

