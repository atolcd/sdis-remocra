# Création et alimentation du schéma remocra_referentiel
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"synchronisations/import_remocra" -job:"recuperer_et_importer_base_distante" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

