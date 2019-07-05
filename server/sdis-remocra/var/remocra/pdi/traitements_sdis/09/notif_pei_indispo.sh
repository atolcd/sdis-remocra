# Notifier les changements d'état de diponibilité des PEI
cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/09/notif_pei_indispo/courrier_pei_indispo.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
