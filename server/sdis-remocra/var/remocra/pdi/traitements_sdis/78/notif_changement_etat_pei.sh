# Notifier les changements d'état de diponibilité des PEI
cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/78/notif_changement_etat_pei/courrier_changement_etat.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
