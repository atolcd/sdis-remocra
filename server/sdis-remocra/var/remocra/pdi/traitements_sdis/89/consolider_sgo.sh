# Consolidation des informations des PEI en vue d'une exploitation par le SGO
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/89/sgo/consolider_pour_sgo.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
