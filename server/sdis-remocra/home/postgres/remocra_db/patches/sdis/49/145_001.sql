BEGIN;

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_49(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
	p_diametre_nom varchar;
	p_nature_code varchar;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('DEBIT_INF_30', 'DEBIT_30_60_NC'));

    -- on récupère le diamètre de l'hydrant
	select thd.nom into p_diametre_nom from remocra.type_hydrant_diametre thd where thd.id = p_rec.diametre;

    -- On récupère la nature de l'hydrant
	select thn.code into p_nature_code
	from remocra.type_hydrant_nature thn
	join hydrant h on h.nature = thn.id
	where p_rec.id = h.id;

	-- Ajout des anomalies
    if(p_diametre_nom = '80' and (p_nature_code = 'PI' or p_nature_code = 'BI')) then
	    if (p_rec.debit is null OR p_rec.debit >= 30) then -- cas de creation et reception sans anomalies
            perform remocra.calcul_indispo(p_rec.id);
        elsif (p_rec.debit < 15) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        elsif (p_rec.debit < 30) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
    elsif(p_diametre_nom = '100' and (p_nature_code = 'PI' or p_nature_code = 'BI')) then
        if (p_rec.debit is null OR p_rec.debit >= 60) then -- cas de creation et reception sans anomalies
            perform remocra.calcul_indispo(p_rec.id);
        elsif (p_rec.debit < 30) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        elsif (p_rec.debit < 60) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
    elsif(p_diametre_nom = '150' and (p_nature_code = 'PI' or p_nature_code = 'BI')) then
        if (p_rec.debit is null OR p_rec.debit >= 120) then -- cas de creation et reception sans anomalies
            perform remocra.calcul_indispo(p_rec.id);
        elsif (p_rec.debit < 30) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        elsif (p_rec.debit < 120) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
	end if;

END;
$function$
;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_49()
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
	perform remocra.calcul_debit_pression_49(p_rec.id);

    RETURN p_rec;
END;
$function$
;

ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;

CREATE TRIGGER trig_debit_pression_49 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_49();

-- Gestion des PEI
UPDATE remocra.param_conf
SET valeur = '90'
WHERE cle = 'HYDRANT_DELAI_CTRL_URGENT';

UPDATE remocra.param_conf
SET valeur = '180'
WHERE cle = 'HYDRANT_DELAI_CTRL_WARN';

UPDATE remocra.param_conf
SET valeur = '30'
WHERE cle = 'HYDRANT_DELAI_RECO_URGENT';

UPDATE remocra.param_conf
SET valeur = '90'
WHERE cle = 'HYDRANT_DELAI_RECO_WARN';

UPDATE remocra.param_conf
SET valeur = '180'
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
SET valeur = '1095'
WHERE cle = 'HYDRANT_RENOUVELLEMENT_RECO_PRIVE';

UPDATE remocra.param_conf
SET valeur = 'numero%natureNom%nomTournee%dateReco%dateContr%dispoTerrestre%nomNature%nomNatureDeci'
WHERE cle = 'HYDRANT_COLONNES';

UPDATE remocra.param_conf
SET valeur = 'utilisateur'
WHERE cle = 'NIVEAU_TRACABILITE';

UPDATE remocra.param_conf
SET valeur = '49'
WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';

UPDATE remocra.param_conf
SET valeur = '49'
WHERE cle = 'HYDRANT_NUMEROTATION_METHODE';

--param_conf indispos temporaire
UPDATE remocra.param_conf
SET valeur = '36'
WHERE cle = 'PDI_DELTA_NOTIF_INDISPO_DEBUT';

UPDATE remocra.param_conf
SET valeur = '24'
WHERE cle = 'PDI_DELTA_NOTIF_INDISPO_FIN';

-- Insertions des requêtes personnalisées pour les PIBI et les PENA
INSERT INTO remocra.hydrant_resume(libelle, code, source_sql) VALUES

-- PIBI
('Requête personnalisée PIBI', 'RESUME_PIBI', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data,
		cstc.cstc_data
	)
) as TEXT) as xml
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "complement", h.complement),
			xmlelement(name "debit_renforce", h.debit_renforce),
			xmlelement(name "grosDebit", h.grosDebit),
			xmlelement(name "debit", h.debit),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "jumele", h.jumele),
			xmlelement(name "nature", h.nature)
	) as hydrant_data
	FROM
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie || CASE WHEN h.en_face = TRUE THEN '' (En face)'' else '''' end) AS adresse,
			c.nom AS commune,
			h.complement,
			hp.debit_renforce,
			h.dispo_terrestre,
			th.code AS nature,
			CASE WHEN thn.code = ''PI'' AND hp.diametre IS NOT NULL AND thd.code = ''DIAM150'' THEN TRUE
				WHEN thn.code = ''BI'' AND hp.jumele >= 0 THEN TRUE
				ELSE FALSE
			END AS grosDebit,
			CASE WHEN hp.jumele >= 0 THEN h2.numero
				ELSE NULL
			END as jumele,
			hp.debit||'' m3/h'' AS debit
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.hydrant_pibi hp on hp.id=h.id
			LEFT JOIN remocra.type_hydrant_diametre thd on thd.id=hp.diametre
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			-- Jumelage
			LEFT JOIN remocra.hydrant_pibi hp2 on hp2.id=hp.jumele
			LEFT JOIN remocra.hydrant h2 on h2.id=hp2.id
		WHERE 	h.id=${HYDRANT_ID})
		AS h) as hydrant,

	(SELECT
		XMLAGG(xmlelement(name "anomalie",
			xmlelement(name "nom", a.nom),
			xmlelement(name "indispo", a.indispo))
		)as anomalies_data
	FROM
		(SELECT tha.nom AS nom,
			than.val_indispo_terrestre AS indispo
		FROM 	remocra.hydrant_anomalies ha
			JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
			JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
			JOIN remocra.hydrant h on h.id=ha.hydrant
		WHERE 	ha.hydrant=${HYDRANT_ID}
			AND than.nature = h.nature)
		as a) as anomalies,

	(SELECT
		XMLAGG(xmlelement(name "cstc",
			xmlelement(name "nom", c.nom),
			xmlelement(name "tournee", c.tournee))
		)as cstc_data
	FROM
		(SELECT o.nom AS nom, t.nom AS tournee
		FROM remocra.organisme o
			JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
			LEFT JOIN remocra.tournee t on t.affectation=o.id
			LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
		WHERE typeO.code = ''CIS''
			AND ht.hydrant=${HYDRANT_ID}
		ORDER BY o.nom) as c) as cstc;'),
-- PENA
('Requête personnalisée PENA', 'RESUME_PENA', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data,
		cstc.cstc_data
	)
) as TEXT) as xml
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "complement", h.complement),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "nature", h.nature),
			xmlelement(name "capacite", h.capacite),
			xmlelement(name "aspirations", h.aspirations)
	) as hydrant_data
	FROM
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie || CASE WHEN h.en_face = TRUE THEN '' (En face)'' else '''' end) AS adresse,
			c.nom AS commune,
			h.complement,
			h.dispo_terrestre,
			th.code AS nature,
			CASE WHEN hp.illimitee THEN ''Illimitée''
				WHEN ((NOT hp.illimitee OR hp.illimitee IS NULL) AND hp.capacite IS NOT NULL AND CAST(NULLIF(TRIM(hp.capacite), '''') AS Integer) IS NOT NULL AND CAST(NULLIF(TRIM(hp.capacite), '''') AS Integer) > -1 ) then hp.capacite|| ''m3''
				ELSE NULL
			END as capacite,

			(SELECT COUNT(ha)
				FROM remocra.hydrant h
				JOIN remocra.hydrant_pena hp on hp.id=h.id
				JOIN remocra.hydrant_aspiration ha on h.id=ha.pena
				WHERE h.id=${HYDRANT_ID}) AS aspirations

		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			-- PENA
			JOIN remocra.hydrant_pena hp on hp.id=h.id
		WHERE 	h.id=${HYDRANT_ID})
		AS h) as hydrant,

	(SELECT
		XMLAGG(xmlelement(name "anomalie",
			xmlelement(name "nom", a.nom),
			xmlelement(name "indispo", a.indispo))
		)as anomalies_data
	FROM
		(SELECT tha.nom AS nom,
			than.val_indispo_terrestre AS indispo
		FROM 	remocra.hydrant_anomalies ha
			JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
			JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
			JOIN remocra.hydrant h on h.id=ha.hydrant
		WHERE 	ha.hydrant=${HYDRANT_ID}
			AND than.nature = h.nature)
		as a) as anomalies,

	(SELECT
		XMLAGG(xmlelement(name "cstc",
			xmlelement(name "nom", c.nom),
			xmlelement(name "tournee", c.tournee))
		)as cstc_data
	FROM
		(SELECT o.nom AS nom, t.nom AS tournee
		FROM remocra.organisme o
			JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
			LEFT JOIN remocra.tournee t on t.affectation=o.id
			LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
		WHERE typeO.code = ''CIS''
			AND ht.hydrant=${HYDRANT_ID}
		ORDER BY o.nom) as c) as cstc;');

COMMIT;
