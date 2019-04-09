  CREATE TABLE remocra_referentiel.synchronisation_sig
  (
    schema_table_name character varying NOT NULL, -- Le nom complet de la table à synchroniser sous la forme "nom_schema.nom_table"
    date_heure_last_synchro timestamp without time zone, -- Date et heure de dernière synchronisation de la table
    statut_last_synchro character varying, -- Statut de la dernière synchronisation (succès ou échec)
    sql_query_after_synchro character varying, -- Requête SQL à jouer immédiatement après synchronisation des données
    CONSTRAINT synchronisation_sig_pkey PRIMARY KEY (schema_table_name)
  )
  WITH (
    OIDS=FALSE
  );
  ALTER TABLE remocra_referentiel.synchronisation_sig
    OWNER TO postgres;
  COMMENT ON TABLE remocra_referentiel.synchronisation_sig
    IS 'Table listant les tables à récupérer depuis la base postgis du SIG';
  COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.schema_table_name IS 'Le nom complet de la table à synchroniser sous la forme "nom_schema.nom_table"';
  COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.date_heure_last_synchro IS 'Date et heure de dernière synchronisation de la table';
  COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.statut_last_synchro IS 'Statut de la dernière synchronisation (succès ou échec)';
  COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.sql_query_after_synchro IS 'Requête SQL à jouer immédiatement après synchronisation des données';


  INSERT INTO remocra_referentiel.synchronisation_sig (schema_table_name, sql_query_after_synchro) VALUES ('CARTOSIG.ROUTE', 'TRUNCATE remocra.voie;
INSERT INTO remocra.voie (
    geometrie,
    mot_classant,
    nom,
    source,
    commune
) SELECT
    st_multi(fusion.geometrie),
    fusion.nom,
    fusion.nom,
    ''ROUTE''::character varying AS source,
    fusion.commune
FROM
(SELECT
    st_linemerge(st_union(r.geometrie)) AS geometrie,
    upper(translate(r.nom,
        ''âãäåÁÂÃÄÅèééêëÈÉÉÊËìíîïìÌÍÎÏÌóôõöÒÓÔÕÖùúûüÙÚÛÜ'',
        ''aaaaAAAAAeeeeeEEEEEiiiiiIIIIIooooOOOOOuuuuUUUU'')) AS nom,
    c.id AS commune
FROM
    remocra_referentiel.route r
    JOIN remocra.commune c ON(c.insee = r.ign_inseecom_g)
WHERE
    r.nom IS NOT NULL AND
    r.ign_inseecom_g IS NOT NULL AND
    r.geometrie IS NOT NULL
GROUP BY
    r.nom,
    c.id
UNION
SELECT
    st_linemerge(st_union(r.geometrie)) AS geometrie,
    upper(translate(r.nom,
        ''âãäåÁÂÃÄÅèééêëÈÉÉÊËìíîïìÌÍÎÏÌóôõöÒÓÔÕÖùúûüÙÚÛÜ'',
        ''aaaaAAAAAeeeeeEEEEEiiiiiIIIIIooooOOOOOuuuuUUUU'')) AS nom,
    c.id AS commune
FROM
    remocra_referentiel.route r
    JOIN remocra.commune c ON(c.insee = r.ign_inseecom_d)
WHERE
    r.nom IS NOT NULL AND
    r.ign_inseecom_d IS NOT NULL AND
    r.geometrie IS NOT NULL
GROUP BY
    r.nom,
    c.id) AS fusion;');
