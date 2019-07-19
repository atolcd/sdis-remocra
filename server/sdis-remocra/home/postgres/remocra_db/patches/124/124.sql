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
    numero_patch := 124;
    description_patch := 'Fiche PEI: requêtes onglet résumé par défaut';

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


DELETE FROM remocra.requete_fiche;

-- Requête par défaut des PIBI
INSERT INTO remocra.requete_fiche(libelle, code, source_sql) VALUES
('Requête par défaut PIBI', 'RESUME_PIBI_DEFAUT', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data
	)
) as TEXT) as xml 
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "observation", h.observation),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "nature", h.nature)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie) AS adresse,
			c.nom AS commune,  
			h.observation, 
			h.dispo_terrestre,
			th.code AS nature
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
		WHERE 	h.id=:id)
		AS h) as hydrant;'),

-- Requête par défaut des PENA
('Requête par défaut PENA', 'RESUME_PENA_DEFAUT', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data
	)
) as TEXT) as xml 
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "observation", h.observation),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "nature", h.nature)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie) AS adresse,
			c.nom AS commune,  
			h.observation, 
			h.dispo_terrestre,
			th.code AS nature
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
		WHERE 	h.id=:id)
		AS h) as hydrant;');

-- Contenu réel du patch fin
--------------------------------------------------

commit;
