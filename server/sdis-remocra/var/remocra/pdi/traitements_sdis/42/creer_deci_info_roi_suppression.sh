# Créer une demande d'envoie du courrier d'information de ROI et de suppression de PEI
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/42/deci_info_roi_suppression/generer_info_roi_suppression.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
