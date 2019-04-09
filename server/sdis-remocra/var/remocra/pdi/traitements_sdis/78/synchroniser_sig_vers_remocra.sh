# Synchroniser les tables du SIG vers remocra
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/78/sig/sig_vers_remocra/synchroniser_tables_sig_vers_remocra.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
