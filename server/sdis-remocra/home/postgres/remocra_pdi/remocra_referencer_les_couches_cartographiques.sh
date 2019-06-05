# Référencer les couches OGC
cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/modeles/processus_etl/commun/maintenance/referencer_les_couches_cartographiques/referencer_les_couches_cartographiques.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
