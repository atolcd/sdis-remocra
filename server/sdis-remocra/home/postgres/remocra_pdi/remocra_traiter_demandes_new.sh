# T�che principale de r�cup�ration et d'execution des demandes de traitements effectu�es via l'interface de lancement de l'applicatif.
# P�riodicit� : A DETERMINER

cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/modeles/processus_etl/commun/modeles_de_processus_et_executions/executer_les_processus_etl/executer_les_processus_etl.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

