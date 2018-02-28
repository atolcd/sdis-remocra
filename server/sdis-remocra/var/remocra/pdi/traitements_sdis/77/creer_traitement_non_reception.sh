# Insertion automatiques des demandes de traitement des hydrants non réceptionnés
#  insertion d'une demande de traitement pour tous les CIS opérationnels qui ont au moins 1 nouvel hydrant non réceptionné

cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/77/non_reception_pei/creer_traitement_non_reception.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"