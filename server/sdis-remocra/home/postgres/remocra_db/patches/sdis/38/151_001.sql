UPDATE remocra.param_conf
SET valeur = '30'
WHERE cle = 'HYDRANT_DELAI_CTRL_URGENT';

UPDATE remocra.param_conf
SET valeur = '90'
WHERE cle = 'HYDRANT_DELAI_CTRL_WARN';

UPDATE remocra.param_conf
SET valeur = '30'
WHERE cle = 'HYDRANT_DELAI_RECO_URGENT';

UPDATE remocra.param_conf
SET valeur = '90'
WHERE cle = 'HYDRANT_DELAI_RECO_WARN';

UPDATE remocra.param_conf
SET valeur = '60'
WHERE cle = 'HYDRANT_LONGUE_INDISPONIBILITE_JOURS';

UPDATE remocra.param_conf
SET valeur = '1095'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC';

UPDATE remocra.param_conf
SET valeur = '1095'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_CTRL_PRIVE';

UPDATE remocra.param_conf
SET valeur = '1095'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_RECO_PUBLIC';

UPDATE remocra.param_conf
SET valeur = '365'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_RECO_PRIVE';

UPDATE remocra.param_conf
SET valeur = '42'
WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';

UPDATE remocra.param_conf
SET valeur = '38'
WHERE cle = 'HYDRANT_NUMEROTATION_METHODE';

UPDATE remocra.param_conf
SET valeur = 'nomCommune%numero%adresse%nomNatureDeci%numeroInterne%natureNom%nomTournee%dateReco%dateContr%dispoTerrestre'
WHERE cle = 'HYDRANT_COLONNES';

INSERT INTO remocra.type_hydrant_anomalie(actif, code, nom, version) VALUES
(true, 'CDP_NON_REALISEE', 'Mesure débit pression non réalisée', 1);

UPDATE remocra.type_hydrant_anomalie
SET code = 'PRESSION_TROP_ELEVEE_NC'
WHERE code = 'PRESSION_TROP_ELEVEE';

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_38()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_rec record;
BEGIN
	p_rec = NEW;
	IF(p_rec.debit IS NULL) THEN
		SELECT p_rec.debit_max INTO p_rec.debit;
	END IF;
	RETURN p_rec;
END;
$function$
;

CREATE TRIGGER trig_calcul_debit_38 BEFORE
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_38();

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_visite_38()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_rec record;
	p_id bigint;
BEGIN
	p_rec = NEW;
	SELECT id INTO p_id FROM remocra.hydrant_pibi WHERE id = p_rec.hydrant;
	IF(p_rec.debit IS NULL AND p_id IS NOT NULL AND p_rec.ctrl_debit_pression) THEN
		p_rec.debit = p_rec.debit_max;
	END IF;
	RETURN p_rec;
END;
$function$
;

CREATE TRIGGER trig_calcul_debit_visite_38 BEFORE
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_visite FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_visite_38();


CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_38(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
	p_visiteCDP record;
	p_debit integer;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('CDP_NON_REALISEE','PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE_NC', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('DEBIT_INF_30', 'DEBIT_30_60_NC'));

	--Ajout des anomalies
	
	-- Si pas de visite de debit pression => anomalie
	SELECT * INTO p_visiteCDP
	FROM remocra.hydrant_visite hv
	WHERE hydrant = p_rec.id AND hv.ctrl_debit_pression
	ORDER BY date DESC LIMIT 1;
	
	IF(p_visiteCDP IS NULL) THEN
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'CDP_NON_REALISEE';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	ELSE 
		IF(p_rec.pression IS NULL OR p_rec.pression < 1) THEN
			select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
			insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
		END IF;
		p_debit = p_rec.debit;
		IF (p_debit < 15 OR p_debit IS NULL) THEN
			select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
			insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
		ELSIF (p_debit < 30) THEN
			select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
			insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
		END IF;
	END IF;
	perform remocra.calcul_indispo(p_rec.id);
END;
$function$
;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_38()
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
	perform remocra.calcul_debit_pression_38(p_rec.id);

    RETURN p_rec;
END;
$function$
;

ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;

CREATE TRIGGER trig_debit_pression_38 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_38();

CREATE SCHEMA IF NOT EXISTS remocra_sgo;

CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
	WITH last_cdp AS (
		SELECT DISTINCT ON (hv.hydrant) hv.hydrant, debit, pression, debit_max
		FROM remocra.hydrant_visite hv
		WHERE hv.ctrl_debit_pression
		ORDER BY hydrant, hv."date" DESC
	),
	date_it AS (
		SELECT hith.hydrant, MIN(hit.date_debut) AS date_deb, MAX(hit.date_fin) AS date_fin
		FROM remocra.hydrant_indispo_temporaire hit
			JOIN remocra.hydrant_indispo_temporaire_hydrant hith ON hith.indisponibilite = hit.id
			JOIN remocra.type_hydrant_indispo_statut this ON this.id = hit.statut
				AND this.code = 'EN_COURS'
		GROUP BY hith.hydrant
	),
	roi AS (
		SELECT hv.hydrant, hv.date
		FROM remocra.hydrant_visite hv
			JOIN remocra.type_hydrant_saisie ths ON ths.id = hv."type"
				AND ths.code = 'RECEP'
	),
	rop AS (
		SELECT DISTINCT ON (hv.hydrant) hv.hydrant, hv.date
		FROM remocra.hydrant_visite hv
			JOIN remocra.type_hydrant_saisie ths ON ths.id = hv."type"
				AND ths.code = 'RECO'
		ORDER BY hv.hydrant, hv.date DESC
	),
	vr AS (
		SELECT hv.hydrant, hv.date
		FROM remocra.hydrant_visite hv
			JOIN remocra.type_hydrant_saisie ths ON ths.id = hv."type"
				AND ths.code = 'CREA'
	),
	ctp AS (
		SELECT DISTINCT ON (hv.hydrant) hv.hydrant, hv.date
		FROM remocra.hydrant_visite hv
			JOIN remocra.type_hydrant_saisie ths ON ths.id = hv."type"
				AND ths.code = 'CTRL'
		ORDER BY hv.hydrant, hv.date DESC
	)
	SELECT h.id, h.numero_interne AS num_inte, h.numero AS num, thn.code AS nature, thd.code AS diametre, lc.pression, lc.debit_max, lc.debit,
		CASE
			WHEN h.dispo_terrestre = 'DISPO' THEN 'disponible'
			WHEN h.dispo_terrestre = 'INDISPO' THEN 'indisponible'
			WHEN h.dispo_terrestre = 'NON_CONFORME' THEN 'emploi restreint'
			ELSE 'indisponible'
		END AS etat_operationnel, h.geometrie, di.date_deb AS DH_DEB_INDISPO, di.date_fin AS DH_FIN_INDISPO, c.insee, roi.date AS date_roi, rop.date AS derniere_date_rop,
		vr.date AS date_reception, ctp.date AS derniere_date_ctp
	FROM remocra.hydrant h
		JOIN remocra.type_hydrant_nature thn ON thn.id = h.nature
		LEFT JOIN remocra.hydrant_pibi hpi ON hpi.id = h.id
		LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = hpi.diametre
		LEFT JOIN last_cdp lc ON lc.hydrant = h.id
		LEFT JOIN date_it di ON di.hydrant = h.id
		JOIN remocra.commune c ON c.id = h.commune
		LEFT JOIN roi ON roi.hydrant = h.id
		LEFT JOIN rop ON rop.hydrant = h.id
		LEFT JOIN vr ON vr.hydrant = h.id
		LEFT JOIN ctp ON ctp.hydrant = h.id;

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
