# Synchroniser les tables du SIG vers remocra
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/42/sig/remocra_vers_sig/synchroniser_remocra_vers_sig.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
