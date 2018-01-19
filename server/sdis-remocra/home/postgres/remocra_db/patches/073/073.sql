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
    numero_patch := 73;
    description_patch := 'Journal des mouvements';

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

-- Table: remocra.hydrant
ALTER TABLE remocra.hydrant
    ADD utilisateur_modification bigint,
    ADD CONSTRAINT fk_hydrant_utilisateur_modification FOREIGN KEY (utilisateur_modification) REFERENCES remocra.utilisateur(id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION;


-- table tracabilite.hydrant

ALTER TABLE tracabilite.hydrant RENAME organisme TO auteur_modification; 
ALTER TABLE tracabilite.hydrant ADD domaine character varying; 

--Table remocra.param_conf

INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('NIVEAU_TRACABILITE', 'Niveau de traçabilité des points d''eau (valeurs admises : organisme , utilisateur)', 'organisme', 1, 'Points d''eau');


-- Function: tracabilite.insert_hydrant(bigint, character varying)

-- DROP FUNCTION tracabilite.insert_hydrant(bigint, character varying);

CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(
    p_id_hydrant bigint,
    p_operation character varying)
  RETURNS void AS
$BODY$
BEGIN
    insert into tracabilite.hydrant (num_transac, nom_operation, date_operation,
    id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco, date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, auteur_modification,
    hbe, positionnement, materiau, vol_constate,
    diametre, debit, debit_max, pression, pression_dyn, marque, modele, pression_dyn_deb, domaine)
    SELECT
      -- traca
      txid_current() ,p_operation,  now() as date_operation,
      -- hydrant
      h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom,  th.nom, array_agg(anomalie.nom), h.observation, CASE WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur') THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
      FROM remocra.hydrant h
      JOIN remocra.organisme o on (h.organisme = o.id)
      JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
      WHERE h.id = p_id_hydrant) ELSE o.nom END,
      -- pena
      pena.hbe, p.nom, mat.nom, v.nom,
      -- pibi
      d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb,
      dom.nom
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
      LEFT JOIN remocra.type_hydrant_domaine dom on (h.domaine = dom.id)
      WHERE h.id = p_id_hydrant
      GROUP BY h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, h.observation, o.nom,
    pena.hbe, p.nom, mat.nom, v.nom,
    d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb, dom.nom;
 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.insert_hydrant(bigint, character varying)
  OWNER TO postgres;


-- Function: tracabilite.update_hydrant(bigint, character varying, bigint)

-- DROP FUNCTION tracabilite.update_hydrant(bigint, character varying, bigint);

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(
    p_id_hydrant bigint,
    p_operation character varying,
    p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET numero =h.numero, geometrie = h.geometrie, insee = c.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom, type_hydrant = th.nom,
    agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre, dispo_hbe = h.dispo_hbe,
    observation = h.observation, auteur_modification = CASE WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur') THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
    FROM remocra.hydrant h
    JOIN remocra.organisme o on (h.organisme = o.id)
    JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
    WHERE h.id = p_id_hydrant) ELSE o.nom END, domaine = dom.nom
    FROM remocra.hydrant h
    JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
    JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
    LEFT JOIN remocra.commune c on (h.commune = c.id)
    LEFT JOIN remocra.organisme o on (h.organisme = o.id)
    LEFT JOIN remocra.type_hydrant_domaine dom on (h.domaine = dom.id)
    WHERE h.id = p_id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.update_hydrant(bigint, character varying, bigint)
  OWNER TO postgres;


-- Contenu réel du patch fin
--------------------------------------------------

commit;

