# Notification des SP DECI d'un changement d'état de dispobilité des PEI de leurs zone

cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/bspp/notifier_changement_etat/sp_deci/notifier_changement_etat_pei_public.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
