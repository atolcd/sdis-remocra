# Génère les tournées pur tout le départements (par commune et par tournées)
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/42/generation_tournee_departement/generer_tournees_departement.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
