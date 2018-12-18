begin;


-- Création du schéma et de la structure d'échange de données avec le SGO
CREATE TABLE remocra_referentiel.synchronisation_sig (
    schema_table_name character varying NOT NULL,
	date_heure_last_synchro timestamp without time zone,
	statut_last_synchro character varying,
	sql_query_after_synchro character varying,
	CONSTRAINT synchronisation_sig_pkey PRIMARY KEY (schema_table_name)
);
COMMENT ON TABLE remocra_referentiel.synchronisation_sig IS 'Table listant les tables à récupérer depuis la base postgis du SIG';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.schema_table_name IS 'Le nom complet de la table à synchroniser sous la forme "nom_schema.nom_table"';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.date_heure_last_synchro IS 'Date et heure de dernière synchronisation de la table';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.statut_last_synchro IS 'Statut de la dernière synchronisation (succès ou échec)';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.sql_query_after_synchro IS 'Requête SQL à jouer immédiatement après synchronisation des données';

-- Création de l'index sur la table tracabilite.hydrant
CREATE INDEX hydrant_id_hydrant_idx  ON tracabilite.hydrant  USING btree (id_hydrant);

commit;
