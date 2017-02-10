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
    numero_patch := 61;
    description_patch := 'Points d''aspiration';

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
--



-- ------------------------------
-- DONNEES DE REFERENCE
-- ------------------------------

-- Idée : reprendre le référentiel PI en dehors des anomalies débit / pression

-- type_hydrant_nature
insert into remocra.type_hydrant_nature(code, nom, type_hydrant, actif, version) values ('PA', 'PA', (select id from remocra.type_hydrant where code='PIBI'), true, 1);

-- type_hydrant_diametre_natures
insert into remocra.type_hydrant_diametre_natures(type_hydrant_diametre, natures)
  select type_hydrant_diametre, (select id from remocra.type_hydrant_nature where code='PA')
  from
    remocra.type_hydrant_diametre_natures thdn
    join remocra.type_hydrant_nature thn on (thn.id=thdn.natures)
  where thn.code='PI';

-- type_hydrant_anomalie_nature
insert into remocra.type_hydrant_anomalie_nature(val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, anomalie, nature, version)
  select val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, anomalie, (select id from remocra.type_hydrant_nature where code='PA'), 1
  from
    remocra.type_hydrant_anomalie_nature than
    join remocra.type_hydrant_nature thn on (thn.id=than.nature)
    join remocra.type_hydrant_anomalie tha on (tha.id=than.anomalie)
  where thn.code='PI' and tha.critere is not null;

-- type_hydrant_anomalie_nature_saisies
insert into remocra.type_hydrant_anomalie_nature_saisies(type_hydrant_anomalie_nature, saisies)
  select
    (/*type_hydrant_anomalie_nature PA équivalente à la PI*/select id from remocra.type_hydrant_anomalie_nature where anomalie=than.anomalie and nature=(select id from remocra.type_hydrant_nature where code='PA')),
    saisies
  from
    remocra.type_hydrant_anomalie_nature_saisies thans
    join remocra.type_hydrant_anomalie_nature than on (than.id=thans.type_hydrant_anomalie_nature)
    join remocra.type_hydrant_nature thn on (thn.id=than.nature)
  where thn.code='PI';



-- ------------------------------
-- PAS DE CALCUL DES DEBIT / PRESSIONS
-- ------------------------------

-- Pas de débits / pressions pour les PA

-- Function: remocra.calcul_debit_pression(bigint)

-- DROP FUNCTION remocra.calcul_debit_pression(bigint);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
	p_code_type_hydrant_nature varchar;
	p_code_diametre varchar;
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN

	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where critere is null);

	-- Récupération du type_hydrant_nature id
	select thn.code into p_code_type_hydrant_nature from remocra.hydrant h left join remocra.type_hydrant_nature thn on (thn.id=h.nature) where h.id = id_hydrant;

	-- Récupération du diamètre id
	select code into p_code_diametre from remocra.type_hydrant_diametre where id = p_rec.diametre;

	-- Les anomalies sont applicables aux PI et BI uniquement (pas aux PA)
	if FOUND and p_code_type_hydrant_nature in ('PI', 'BI') then
		if (p_code_diametre = 'DIAM80' or p_code_diametre = 'DIAM100' or p_code_diametre = 'DIAM150') then
			-- pression
			if p_rec.pression >= 0 AND  p_rec.pression < 1 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif p_rec.pression > 16 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_TROP_ELEVEE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
			-- pression dyn
			if p_rec.pression_dyn >= 0.1 AND  p_rec.pression_dyn < 1 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif p_rec.pression_dyn > 16 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_TROP_ELEVEE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;

		--choix de debit
		if (p_rec.debit_max IS NOT NULL) then
			p_rec.debit=p_rec.debit_max;  
		end if;

		if(p_code_diametre = 'DIAM80' or p_code_diametre = 'DIAM100') then
			-- débit
			if (p_rec.debit >= 0 AND  p_rec.debit < 30) then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;

		if p_code_diametre = 'DIAM80' then
			-- debit
			if p_rec.debit > 90 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;

		elsif p_code_diametre = 'DIAM100' then
			-- debit
			if  p_rec.debit >= 30 AND  p_rec.debit <60  then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif  p_rec.debit > 130 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;

		elsif p_code_diametre = 'DIAM150' then
			-- debit
			if  p_rec.debit >= 60 AND p_rec.debit < 120 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif  p_rec.debit > 150 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;
	end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.calcul_debit_pression(bigint)
  OWNER TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression(bigint) TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression(bigint) TO public;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression(bigint) TO remocra;



--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

