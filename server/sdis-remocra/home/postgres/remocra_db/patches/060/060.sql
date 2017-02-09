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
    numero_patch := 60;
    description_patch := 'Pression dynamique au débit max';

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


-- Column: pression_dyn_deb

-- ALTER TABLE remocra.hydrant_pibi DROP COLUMN pression_dyn_deb;

ALTER TABLE remocra.hydrant_pibi ADD COLUMN pression_dyn_deb double precision;

-- Function: remocra.calcul_debit_pression(bigint)

-- DROP FUNCTION remocra.calcul_debit_pression(bigint);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
    p_code_diametre varchar;
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN

	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where critere is null);

	-- Récupération du diamètre id
	select code into p_code_diametre from remocra.type_hydrant_diametre where id = p_rec.diametre;

	if FOUND then
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

-- Column: pression_dyn_deb

-- ALTER TABLE tracabilite.hydrant DROP COLUMN pression_dyn_deb;

ALTER TABLE tracabilite.hydrant ADD COLUMN pression_dyn_deb double precision;


-- Function: tracabilite.insert_hydrant(bigint, character varying)

-- DROP FUNCTION tracabilite.insert_hydrant(bigint, character varying);

CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(
    p_id_hydrant bigint,
    p_operation character varying)
  RETURNS void AS
$BODY$
BEGIN
    insert into tracabilite.hydrant (num_transac, nom_operation, date_operation,
    id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco, date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, organisme,
    hbe, positionnement, materiau, vol_constate,
    diametre, debit, debit_max, pression, pression_dyn, marque, modele, pression_dyn_deb)
    SELECT
      -- traca
      txid_current() ,p_operation,  now() as date_operation,
      -- hydrant
      h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, array_agg(anomalie.nom), h.observation, o.nom,
      -- pena
      pena.hbe, p.nom, mat.nom, v.nom,
      -- pibi
      d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb
      FROM remocra.hydrant h
      -- hydrant
      JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
      JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
      LEFT JOIN remocra.commune c on (h.commune = c.id)
      LEFT JOIN remocra.organisme o on (h.organisme = o.id)
      -- pibi
      LEFT JOIN remocra.hydrant_pibi pibi on (pibi.id = h.id)
      LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
      LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
      LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
      -- pena
      LEFT JOIN remocra.hydrant_pena pena on (pena.id = h.id)
      LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
      LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
      LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
      -- anomalies
      LEFT JOIN (remocra.hydrant_anomalies ha JOIN remocra.type_hydrant_anomalie a on (ha.anomalies = a.id)) anomalie on (anomalie.hydrant = h.id)
      WHERE h.id = p_id_hydrant
      GROUP BY h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, h.observation, o.nom,
    pena.hbe, p.nom, mat.nom, v.nom,
    d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.insert_hydrant(bigint, character varying)
  OWNER TO postgres;

-- Function: tracabilite.update_hydrant_pibi(bigint, character varying, bigint)

-- DROP FUNCTION tracabilite.update_hydrant_pibi(bigint, character varying, bigint);

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_pibi(
    p_id_pibi bigint,
    p_operation character varying,
    p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET  diametre = d.nom, debit = pibi.debit,  debit_max = pibi.debit_max, pression = pibi.pression, pression_dyn = pibi.pression_dyn, marque = m.nom, modele = mod.nom, pression_dyn_deb = pibi.pression_dyn_deb
    FROM remocra.hydrant_pibi pibi
    LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
    LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
    LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
    WHERE pibi.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pibi;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.update_hydrant_pibi(bigint, character varying, bigint)
  OWNER TO postgres;

--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

