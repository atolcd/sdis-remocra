# Synchronisation des tables de REMOCRA vers le SIG
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/77/sig/remocra_vers_sig/synchroniser_remocra_vers_mssql.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"