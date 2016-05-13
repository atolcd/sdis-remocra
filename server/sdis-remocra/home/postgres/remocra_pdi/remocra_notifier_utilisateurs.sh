# Tâche principale de notification des utilisateurs à partir de la lecture de la table contenant les emails à notifier
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"maintenance/notification" -job:"notifier_utilisateurs" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

