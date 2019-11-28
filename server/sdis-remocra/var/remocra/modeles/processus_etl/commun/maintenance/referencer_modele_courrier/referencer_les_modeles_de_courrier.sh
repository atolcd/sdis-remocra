# Synchroniser les données de REMOcRA dans le SIG
cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/modeles/processus_etl/commun/maintenance/referencer_modele_courrier/referencer_les_modeles_de_courrier.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
