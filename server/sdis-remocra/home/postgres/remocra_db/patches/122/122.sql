begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 122;
    description_patch := 'Fiche PEI: requêtes onglet Résumé; Renommage et ajout de droit des visites';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début

SELECT pg_catalog.setval('remocra.type_droit_id_seq', (select max(id) from remocra.type_droit));

INSERT INTO remocra.type_droit (code, description, nom, version, categorie)
VALUES('HYDRANTS_CREATION_C', 'Réaliser des visites de réception', 'hydrants.creation_C', 1, 'Module PEI');

UPDATE remocra.type_droit set description = 'Réaliser des reconnaissances opérationnelles initiales' where code = 'HYDRANTS_RECEPTION_C';
UPDATE remocra.type_droit set description = 'Réaliser des reconnaissances opérationnelles périodiques' where code = 'HYDRANTS_RECONNAISSANCE_C';

---- Partie Onglet

DROP TABLE IF EXISTS remocra.requete_fiche CASCADE;
CREATE TABLE remocra.requete_fiche
(
  id BIGSERIAL NOT NULL,
  libelle CHARACTER VARYING NOT NULL,
  code CHARACTER VARYING NOT NULL,
  source_sql CHARACTER VARYING NOT NULL,
  CONSTRAINT requete_fiche_pkey PRIMARY KEY (id)
);

/**
  * Ajout des requêtes par défaut
  * Pour jouer des requêtes personnalisées:
  * 	La requête doit avoir le code RESUME_PIBI pour les PIBI, RESUME_PENA pour les pena
  * 	Le fichier resume.html doit se trouver au bon emplacement dans le dossier /var/
  */

-- Requête par défaut des PIBI
INSERT INTO remocra.requete_fiche(libelle, code, source_sql) VALUES
('Requête par défaut PIBI', 'RESUME_PIBI_DEFAUT', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data
	)
) as TEXT) as xml 
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "complement", h.complement),
			xmlelement(name "debit_renforce", h.debit_renforce),
			xmlelement(name "grosDebit", h.grosDebit),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "jumele", h.jumele),
			xmlelement(name "tournee", h.tournee),
			xmlelement(name "nature", h.nature),
			xmlelement(name "cstc", h.cstc)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie) AS adresse,
			c.nom AS commune,  
			h.complement, 
			hp.debit_renforce,
			h.dispo_terrestre,
			th.code AS nature,
			CASE WHEN thn.code = ''PI'' AND hp.diametre IS NOT NULL AND CAST(thd.nom AS INTEGER) = 150 THEN TRUE
				WHEN thn.code = ''BI'' AND hp.jumele >= 0 THEN TRUE
				ELSE FALSE
			END AS grosDebit,
			CASE WHEN hp.jumele >= 0 THEN h2.numero
				ELSE NULL
			END as jumele,
			CASE WHEN o.id=:idOrganisme THEN t.nom
				ELSE NULL
			END AS tournee,

			(SELECT o.nom AS nom
				FROM remocra.hydrant h 
				JOIN remocra.commune c on h.commune=c.id
				JOIN remocra.zone_competence_commune zcc on zcc.commune_id=c.id
				JOIN remocra.zone_competence zc on zc.id=zcc.zone_competence_id
				JOIN remocra.organisme o on o.zone_competence = zc.id
				JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
				WHERE h.id=:id AND ST_INTERSECTS(h.geometrie, zc.geometrie) AND typeO.code = ''CS'') as CSTC
			
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.hydrant_pibi hp on hp.id=h.id
			LEFT JOIN remocra.type_hydrant_diametre thd on thd.id=hp.diametre
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			-- Jumelage
			LEFT JOIN remocra.hydrant_pibi hp2 on hp2.id=hp.jumele
			LEFT JOIN remocra.hydrant h2 on h2.id=hp2.id
			-- Tournée
			LEFT JOIN remocra.hydrant_tournees ht on ht.hydrant=h.id
			LEFT JOIN remocra.tournee t on t.id = ht.tournees
			LEFT JOIN remocra.organisme o on o.id = t.affectation
		WHERE 	h.id=:id) 
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
		WHERE 	ha.hydrant=:id 
			AND than.nature = h.nature) 
		as a) as anomalies;'),

-- Requête par défaut des PENA
('Requête par défaut PENA', 'RESUME_PENA_DEFAUT', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data
	)
) as TEXT) as xml 
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "complement", h.complement),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "tournee", h.tournee),
			xmlelement(name "nature", h.nature),
			xmlelement(name "capacite", h.capacite),
			xmlelement(name "cstc", h.cstc),
			xmlelement(name "aspirations", h.aspirations)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie) AS adresse,
			c.nom AS commune,  
			h.complement, 
			h.dispo_terrestre,
			th.code AS nature,
			CASE WHEN o.id=:idOrganisme THEN t.nom
				ELSE NULL
			END AS tournee,
			CASE WHEN thn.code = ''CI_FIXE'' AND hp.illimitee THEN ''Illimitée''
				WHEN thn.code = ''CI_FIXE''  AND ((NOT hp.illimitee OR hp.illimitee IS NULL) AND hp.capacite IS NOT NULL AND CAST(NULLIF(TRIM(hp.capacite), '') AS Integer) IS NOT NULL AND CAST(NULLIF(TRIM(hp.capacite), '') AS Integer) > -1 ) then hp.capacite|| ''m3''
				ELSE NULL
			END as capacite,

			(select o.nom AS nom
			FROM remocra.hydrant h 
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.zone_competence_commune zcc on zcc.commune_id=c.id
			JOIN remocra.zone_competence zc on zc.id=zcc.zone_competence_id
			JOIN remocra.organisme o on o.zone_competence = zc.id
			JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
			where h.id=:id AND ST_INTERSECTS(h.geometrie, zc.geometrie) AND typeO.code = ''CS'') as cstc,

			(SELECT COUNT(ha)
				FROM remocra.hydrant h 
				JOIN remocra.hydrant_pena hp on hp.id=h.id
				JOIN remocra.hydrant_aspiration ha on h.id=ha.pena
				WHERE h.id=:id) AS aspirations
			
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			-- PENA
			JOIN remocra.hydrant_pena hp on hp.id=h.id
			-- Tournée
			LEFT JOIN remocra.hydrant_tournees ht on ht.hydrant=h.id
			LEFT JOIN remocra.tournee t on t.id = ht.tournees
			LEFT JOIN remocra.organisme o on o.id = t.affectation
		WHERE 	h.id=:id) 
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
		WHERE 	ha.hydrant=:id 
			AND than.nature = h.nature) 
		as a) as anomalies;');

-- Contenu réel du patch fin
--------------------------------------------------

commit;
