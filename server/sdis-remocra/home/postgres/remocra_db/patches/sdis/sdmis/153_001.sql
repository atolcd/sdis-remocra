BEGIN;

-- MAJ param conf
UPDATE remocra.param_conf
SET valeur = '3285'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_CTRL_PRIVE';

UPDATE remocra.param_conf
SET valeur = '3285'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC';

UPDATE remocra.param_conf
SET valeur = '1095'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_RECO_PRIVE';

UPDATE remocra.param_conf
SET valeur = '1095'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_RECO_PUBLIC';

UPDATE remocra.param_conf
SET valeur = '180'
WHERE cle = 'HYDRANT_LONGUE_INDISPONIBILITE_JOURS';

UPDATE remocra.param_conf
SET valeur = '7'
WHERE cle = 'HYDRANT_DELAI_RECO_URGENT';

UPDATE remocra.param_conf
SET valeur = '30'
WHERE cle = 'HYDRANT_DELAI_RECO_WARN';

UPDATE remocra.param_conf
SET valeur = '42'
WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';

UPDATE remocra.param_conf
SET valeur = '89'
WHERE cle = 'HYDRANT_NUMEROTATION_METHODE';

UPDATE remocra.param_conf
SET valeur = '72'
WHERE cle = 'PDI_DELTA_NOTIF_INDISPO_DEBUT';

UPDATE remocra.param_conf
SET valeur = '72'
WHERE cle = 'PDI_DELTA_NOTIF_INDISPO_FIN';

UPDATE remocra.param_conf
SET valeur = '69%'
WHERE cle = 'COMMUNES_INSEE_LIKE_FILTRE_SQL';

UPDATE remocra.param_conf
SET valeur = '69'
WHERE cle = 'PDI_CODE_SDIS';

UPDATE remocra.param_conf
SET valeur = 'REF_CIS,UTI_CIS,REF_MAIRIE,UTI_MAIRIE'
WHERE cle = 'PDI_UTILISATEUR_NOTIF_INDISPO';

UPDATE remocra.param_conf
SET valeur = 'nomCommune%adresse%numero%natureNom%dateReco%dateContr%dispoTerrestre%nomNatureDeci%nomTournee'
WHERE cle = 'HYDRANT_COLONNES';

UPDATE remocra.param_conf
SET valeur = 'utilisateur'
WHERE cle = 'NIVEAU_TRACABILITE';

-- Requête pour fiche résumé
DELETE FROM remocra.hydrant_resume
WHERE code IN ('RESUME_PIBI', 'RESUME_PENA');

INSERT INTO remocra.hydrant_resume (libelle,code,source_sql) VALUES 
('Requête personnalisée PIBI', 'RESUME_PIBI', 'WITH last_change_dispo AS (
	SELECT DISTINCT ON (h1.id_hydrant) h1.date_operation
	FROM
    	(SELECT
	    	h2.id_hydrant,
	        h2.date_operation,
	        h2.dispo_terrestre,
	        lead(h2.dispo_terrestre) OVER (ORDER BY h2.numero, h2.date_operation DESC) as prev_dispo
     	FROM tracabilite.hydrant h2
     	ORDER BY h2.numero,h2.date_operation DESC) as h1
	WHERE h1.dispo_terrestre IS DISTINCT FROM h1.prev_dispo AND
    	h1.id_hydrant=${HYDRANT_ID} 
	GROUP BY h1.id_hydrant, h1.date_operation
	ORDER BY h1.id_hydrant, h1.date_operation DESC
),
nb_change_dispo AS (
 	SELECT CASE WHEN COUNT (*) <= 0 THEN 0 ELSE COUNT (*) - 1 END AS nb_change
	FROM (
		SELECT h1.id_hydrant, h1.date_operation, h1.dispo_terrestre
		FROM (
			SELECT
		    	h2.id_hydrant,
		        h2.date_operation,
		        h2.dispo_terrestre,
		        lead(h2.dispo_terrestre) OVER (ORDER BY h2.numero, h2.date_operation DESC) as prev_dispo
		     FROM
		        tracabilite.hydrant h2
		     WHERE h2.date_operation > now() - interval ''3 years''
		     ORDER BY
		        h2.numero,h2.date_operation DESC) as h1
		WHERE h1.dispo_terrestre IS DISTINCT FROM h1.prev_dispo AND
    		h1.id_hydrant=${HYDRANT_ID}
		ORDER BY h1.id_hydrant, h1.date_operation DESC
	) a
),
hydrant AS (
	SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "complement", h.complement),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "debit", h.debit),
			xmlelement(name "domaine", h.domaine),
			xmlelement(name "nature", h.nature),
			xmlelement(name "last_change_dispo", to_char(last_change_dispo.date_operation, ''DD/MM/YYYY HH:MI'')),
			xmlelement(name "nb_change_dispo", nb_change_dispo.nb_change)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie || CASE WHEN h.en_face = TRUE THEN '' (En face)'' else '''' end) AS adresse,
			c.nom AS commune,  
			h.complement, 
			h.dispo_terrestre,
			hp.debit,
			h.code AS nature,
			thd2.nom AS domaine
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.hydrant_pibi hp on hp.id=h.id
			LEFT JOIN remocra.type_hydrant_diametre thd on thd.id=hp.diametre
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			LEFT JOIN remocra.type_hydrant_domaine thd2 ON thd2.id = h.domaine
			-- Jumelage
			LEFT JOIN remocra.hydrant_pibi hp2 on hp2.id=hp.jumele
			LEFT JOIN remocra.hydrant h2 on h2.id=hp2.id
		WHERE 	h.id=${HYDRANT_ID}) AS h
		LEFT JOIN last_change_dispo ON 1=1
		LEFT JOIN nb_change_dispo ON 1=1
),
anomalies AS (
	SELECT
		XMLAGG(xmlelement(name "anomalie",
			xmlelement(name "nom", a.nom),
			xmlelement(name "indispo", a.indispo))
		)as anomalies_data
	FROM (
		SELECT tha.nom AS nom, 
			than.val_indispo_terrestre AS indispo
		FROM remocra.hydrant_anomalies ha
			JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
			JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
			JOIN remocra.hydrant h on h.id=ha.hydrant
		WHERE ha.hydrant=${HYDRANT_ID}
			AND than.nature = h.nature) 
		as a
),
cstc AS (
	SELECT
		XMLAGG(xmlelement(name "cstc",
			xmlelement(name "nom", c.nom),
			xmlelement(name "tournee", c.tournee))
		)as cstc_data
	FROM (
		SELECT o.nom AS nom, t.nom AS tournee
		FROM remocra.organisme o
			JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
			LEFT JOIN remocra.tournee t on t.affectation=o.id
			LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
		WHERE typeO.code IN (''CASERNE'', ''CIS'', ''CT'', ''CIE'', ''GROUPEMENT'') 
			AND ht.hydrant=${HYDRANT_ID}
		ORDER BY o.nom
	) as c
)
SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data,
		cstc.cstc_data
	)
) as TEXT) as xml 
FROM hydrant,
	anomalies,
	cstc;'),
('Requête personnalisée PENA', 'RESUME_PENA', 'WITH last_change_dispo AS (
	SELECT DISTINCT ON (h1.id_hydrant) h1.date_operation
	FROM
    	(SELECT
	    	h2.id_hydrant,
	        h2.date_operation,
	        h2.dispo_terrestre,
	        lead(h2.dispo_terrestre) OVER (ORDER BY h2.numero, h2.date_operation DESC) as prev_dispo
     	FROM tracabilite.hydrant h2
     	ORDER BY h2.numero,h2.date_operation DESC) as h1
	WHERE h1.dispo_terrestre IS DISTINCT FROM h1.prev_dispo AND
    	h1.id_hydrant=${HYDRANT_ID} 
	GROUP BY h1.id_hydrant, h1.date_operation
	ORDER BY h1.id_hydrant, h1.date_operation DESC
),
nb_change_dispo AS (
 	SELECT CASE WHEN COUNT (*) <= 0 THEN 0 ELSE COUNT (*) - 1 END AS nb_change
	FROM (
		SELECT h1.id_hydrant, h1.date_operation, h1.dispo_terrestre
		FROM (
			SELECT
		    	h2.id_hydrant,
		        h2.date_operation,
		        h2.dispo_terrestre,
		        lead(h2.dispo_terrestre) OVER (ORDER BY h2.numero, h2.date_operation DESC) as prev_dispo
		     FROM
		        tracabilite.hydrant h2
		     WHERE h2.date_operation > now() - interval ''3 years''
		     ORDER BY
		        h2.numero,h2.date_operation DESC) as h1
		WHERE h1.dispo_terrestre IS DISTINCT FROM h1.prev_dispo AND
    		h1.id_hydrant=${HYDRANT_ID}
		ORDER BY h1.id_hydrant, h1.date_operation DESC
	) a
),
hydrant AS (
	SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "complement", h.complement),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "domaine", h.domaine),
			xmlelement(name "nature", h.nature),
			xmlelement(name "last_change_dispo", to_char(last_change_dispo.date_operation, ''DD/MM/YYYY HH:MI'')),
			xmlelement(name "nb_change_dispo", nb_change_dispo.nb_change)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie || CASE WHEN h.en_face = TRUE THEN '' (En face)'' else '''' end) AS adresse,
			c.nom AS commune,  
			h.complement, 
			h.dispo_terrestre,
			h.code AS nature,
			thd2.nom AS domaine
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			LEFT JOIN remocra.type_hydrant_domaine thd2 ON thd2.id = h.domaine
		WHERE 	h.id=${HYDRANT_ID}) AS h
		LEFT JOIN last_change_dispo ON 1=1
		LEFT JOIN nb_change_dispo ON 1=1
),
anomalies AS (
	SELECT
		XMLAGG(xmlelement(name "anomalie",
			xmlelement(name "nom", a.nom),
			xmlelement(name "indispo", a.indispo))
		)as anomalies_data
	FROM (
		SELECT tha.nom AS nom, 
			than.val_indispo_terrestre AS indispo
		FROM remocra.hydrant_anomalies ha
			JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
			JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
			JOIN remocra.hydrant h on h.id=ha.hydrant
		WHERE ha.hydrant=${HYDRANT_ID}
			AND than.nature = h.nature) 
		as a
),
cstc AS (
	SELECT
		XMLAGG(xmlelement(name "cstc",
			xmlelement(name "nom", c.nom),
			xmlelement(name "tournee", c.tournee))
		)as cstc_data
	FROM (
		SELECT o.nom AS nom, t.nom AS tournee
		FROM remocra.organisme o
			JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
			LEFT JOIN remocra.tournee t on t.affectation=o.id
			LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
		WHERE typeO.code IN (''CASERNE'', ''CIS'', ''CT'', ''CIE'', ''GROUPEMENT'') 
			AND ht.hydrant=${HYDRANT_ID}
		ORDER BY o.nom
	) as c
)
SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data,
		cstc.cstc_data
	)
) as TEXT) as xml 
FROM hydrant,
	anomalies,
	cstc;');

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_69(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('DEBIT_INF_30', 'DEBIT_30_60_NC'));

	-- Ajout des anomalies
	if (p_rec.debit is null OR p_rec.debit >= 30) then -- cas de creation et reception sans anomalies
	    perform remocra.calcul_indispo(p_rec.id);
	elsif (p_rec.debit < 20) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	elsif (p_rec.debit < 30) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
END;
$function$
;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_69()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_debit_pression_69(p_rec.id);

    RETURN p_rec;
END;
$function$
;

ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;

CREATE TRIGGER trig_debit_pression_69 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_69();

CREATE OR REPLACE FUNCTION remocra.calcul_volume_69(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pena%ROWTYPE;
BEGIN
	select * into p_rec from remocra.hydrant_pena where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('VOLUME_INSUFF', 'VOLUME_INSUFF_NC'));

	-- Ajout des anomalies
	if (p_rec.capacite is null OR p_rec.capacite = '' OR p_rec.capacite::int >= 30 OR p_rec.illimitee) then -- cas de creation et reception sans anomalies
	    perform remocra.calcul_indispo(p_rec.id);
	elsif (p_rec.capacite::int < 10) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'VOLUME_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	elsif (p_rec.capacite::int < 30) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'VOLUME_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
END;
$function$
;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_volume_69()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_volume_69(p_rec.id);

    RETURN p_rec;
END;
$function$
;

CREATE TRIGGER trig_volume_69 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_volume_69();

CREATE TABLE remocra_referentiel.synchronisation_sig
(
	schema_table_name character varying NOT NULL, -- Le nom complet de la table à synchroniser sous la forme "nom_schema.nom_table"
	date_heure_last_synchro timestamp without time zone, -- Date et heure de dernière synchronisation de la table
	statut_last_synchro character varying, -- Statut de la dernière synchronisation (succès ou échec)
	sql_query_after_synchro character varying, -- Requête SQL à jouer immédiatement après synchronisation des données
	synchroniser bool NULL DEFAULT false,
	CONSTRAINT synchronisation_sig_pkey PRIMARY KEY (schema_table_name)
)
WITH (
	OIDS=FALSE
);
ALTER TABLE remocra_referentiel.synchronisation_sig OWNER TO remocra;
COMMENT ON TABLE remocra_referentiel.synchronisation_sig IS 'Table listant les tables à récupérer depuis la base postgis du SIG';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.schema_table_name IS 'Le nom complet de la table à synchroniser sous la forme "nom_schema.nom_table"';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.date_heure_last_synchro IS 'Date et heure de dernière synchronisation de la table';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.statut_last_synchro IS 'Statut de la dernière synchronisation (succès ou échec)';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.sql_query_after_synchro IS 'Requête SQL à jouer immédiatement après synchronisation des données';
COMMENT ON COLUMN remocra_referentiel.synchronisation_sig.synchroniser IS 'Booléen pour gérer si l''on veut synchroniser la table';

COMMIT;
