# Tâche principale de synchronisation des données métier vers OraclePar thématique :- Génération d'un script sql de mise à jour à partir de la date de la dernière synchronisation- Récupération (copie) des documents éventuelsPuis, toutes thématiques métier confondues- Génération d'un fichier ZIP englobant les sql et les documents- Création d'un fichier MD5 du ZIP- Dépôt du ZIP et du MD5 sur le serveur FTP

# Périodicité : A DETERMINER
cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"synchronisations/export_remocra/exporter_metier" -job:"exporter_donnees_metier" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

