# Insertion automatiques des demandes de traitement des PEI indisponibles
#  insertion d'une demande de traitement pour tous les organismes qui ont au moins 1 PEI indisponibles sur les dernières 24h

cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/42/pei_indisponibles/creer_traitement_pei_indisponibles.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
