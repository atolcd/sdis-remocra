# T�che principale de cr�ation des notifications de d�but et de fin d'indisponibilit� temporaire d'hydrant
# P�riodicit� : A DETERMINER

cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/modeles/processus_etl/commun/maintenance/indisponibilite_temporaire/creer_notifications_indisponibilites.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

