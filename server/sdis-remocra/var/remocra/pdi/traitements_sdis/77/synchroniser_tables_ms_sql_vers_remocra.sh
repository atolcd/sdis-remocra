# Synchronisation des tables du SIG vers REMOCRA
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/77/sig/sig_vers_remocra/synchroniser_tables_ms_sql_vers_remocra.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"