# Export des PEI
cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/bspp/sig/remocra_to_sig/export_remocra_vers_sig.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
