# Inserer une demande de référencement des modèle de processus ETL

psql -d remocra -c "INSERT INTO remocra.processus_etl (modele, statut, utilisateur, priorite, demande) SELECT m.id, s.id, u.id, 1, now() FROM remocra.processus_etl_modele m JOIN remocra.processus_etl_statut s ON s.code='A'  JOIN remocra.utilisateur u ON u.identifiant = 'sdis-adm-app' WHERE m.code = 'REFERENCER_PROCESSUS_ETL'"
