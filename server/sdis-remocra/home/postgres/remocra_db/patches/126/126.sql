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
    numero_patch := 126;
    description_patch := 'Tracabilité : ajout des nouveaux champs';

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

-- Ajout colonnes à la tracabilite
ALTER TABLE tracabilite.hydrant
  ADD COLUMN nature_deci character varying,
  ADD COLUMN numero_voie INTEGER,
  ADD COLUMN suffixe_voie character varying,
  ADD COLUMN niveau character varying,
  ADD COLUMN gestionnaire character varying,
  ADD COLUMN site character varying,
  ADD COLUMN autorite_deci character varying,
  ADD COLUMN jumele character varying,
  ADD COLUMN dispositif_inviolabilite BOOLEAN,
  ADD COLUMN reservoir character varying,
  ADD COLUMN service_eaux character varying,
  ADD COLUMN debit_renforce boolean,
  ADD COLUMN type_reseau_canalisation character varying,
  ADD COLUMN type_reseau_alimentation character varying,
  ADD COLUMN diametre_canalisation INTEGER,
  ADD COLUMN surpresse boolean,
  ADD COLUMN additive boolean,
  ADD COLUMN illimitee BOOLEAN,
  ADD COLUMN incertaine BOOLEAN,
  ADD COLUMN en_face BOOLEAN;

-- Modification de la fonction de traca des insert
CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(
    p_id_hydrant bigint,
    p_operation character varying)
  RETURNS void AS
$BODY$
BEGIN
    insert into tracabilite.hydrant (num_transac, nom_operation, date_operation,
    id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco, date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, auteur_modification,
    hbe, positionnement, materiau, vol_constate, capacite,
    diametre, debit, debit_max, pression, pression_dyn, marque, modele, pression_dyn_deb, domaine,
    nature_deci, numero_voie, suffixe_voie, niveau, gestionnaire, site, autorite_deci, en_face, jumele, dispositif_inviolabilite, reservoir, service_eaux, debit_renforce, type_reseau_canalisation, type_reseau_alimentation, diametre_canalisation, surpresse, additive, illimitee, incertaine)
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
      pena.hbe, p.nom, mat.nom, v.nom, pena.capacite,
      -- pibi
      d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb,
      dom.nom,
      --Nouveaux ajouts
      --hydrant
      thnd.nom, h.numero_voie, h.suffixe_voie, thn.nom, COALESCE(gestionnaire.nom, gestionnaire_orga.nom) AS gestionnaire, site.nom, autorite_deci.nom, h.en_face,
      --pibi
      jumele.numero, pibi.dispositif_inviolabilite, hr.nom, service_eaux.nom, pibi.debit_renforce, trc.nom, tra.nom, pibi.diametre_canalisation, pibi.surpresse, pibi.additive,
      --pena
      pena.illimitee, pena.incertaine
      FROM remocra.hydrant h
      -- hydrant
      JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
      JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
      LEFT JOIN remocra.commune c on (h.commune = c.id)
      LEFT JOIN remocra.organisme o on (h.organisme = o.id)
      LEFT JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
      LEFT JOIN remocra.type_hydrant_niveau thn ON thn.id = h.niveau
      LEFT JOIN remocra.site ON site.id = h.site
      LEFT JOIN remocra.organisme autorite_deci ON  autorite_deci.id = h.autorite_deci
      LEFT JOIN remocra.organisme gestionnaire_orga ON gestionnaire_orga.id = h.gestionnaire
      LEFT JOIN remocra.gestionnaire gestionnaire ON gestionnaire.id = h.gestionnaire
      -- pibi
      LEFT JOIN remocra.hydrant_pibi pibi on (pibi.id = h.id)
      LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
      LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
      LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
      LEFT JOIN remocra.hydrant jumele ON jumele.id = pibi.jumele
      LEFT JOIN remocra.hydrant_reservoir hr ON hr.id = pibi.reservoir
      LEFT JOIN remocra.organisme service_eaux ON service_eaux.id = pibi.service_eaux
      LEFT JOIN remocra.type_reseau_canalisation trc ON trc.id = pibi.type_reseau_canalisation
      LEFT JOIN remocra.type_reseau_alimentation tra ON tra.id = pibi.type_reseau_alimentation
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
    pena.hbe, p.nom, mat.nom, v.nom, pena.capacite,
    d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb, dom.nom, thnd.nom, h.numero_voie, h.suffixe_voie, thn.nom, gestionnaire.nom, gestionnaire_orga.nom, site.nom, autorite_deci.nom, h.en_face,
    jumele.numero, pibi.dispositif_inviolabilite, hr.nom, service_eaux.nom, pibi.debit_renforce, trc.nom, tra.nom, pibi.diametre_canalisation, pibi.surpresse, pibi.additive,
    pena.illimitee, pena.incertaine;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.insert_hydrant(bigint, character varying)
  OWNER TO postgres;

-- Modification de la fonction de traca des update des infos communes
CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(
    p_id_hydrant bigint,
    p_operation character varying,
    p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET nom_operation = p_operation, numero =h.numero, geometrie = h.geometrie, insee = c.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom, type_hydrant = th.nom,
    agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre, dispo_hbe = h.dispo_hbe,
    observation = h.observation, auteur_modification = CASE WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur') THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
    FROM remocra.hydrant h
    JOIN remocra.organisme o on (h.organisme = o.id)
    JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
    WHERE h.id = p_id_hydrant) ELSE o.nom END, domaine = dom.nom, nature_deci = thnd.nom, numero_voie = h.numero_voie, suffixe_voie = h.suffixe_voie, niveau = thn.nom,
    gestionnaire = COALESCE(gestionnaire.nom, gestionnaire_orga.nom), site = site.nom, autorite_deci = autorite_deci.nom, en_face = h.en_face
    FROM remocra.hydrant h
    JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
    JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
    LEFT JOIN remocra.commune c on (h.commune = c.id)
    LEFT JOIN remocra.organisme o on (h.organisme = o.id)
    LEFT JOIN remocra.type_hydrant_domaine dom on (h.domaine = dom.id)
    LEFT JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
    LEFT JOIN remocra.type_hydrant_niveau thn ON thn.id = h.niveau
    LEFT JOIN remocra.site ON site.id = h.site
    LEFT JOIN remocra.organisme autorite_deci ON  autorite_deci.id = h.autorite_deci
    LEFT JOIN remocra.organisme gestionnaire_orga ON gestionnaire_orga.id = h.gestionnaire
    LEFT JOIN remocra.gestionnaire gestionnaire ON gestionnaire.id = h.gestionnaire
    WHERE h.id = p_id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.update_hydrant(bigint, character varying, bigint)
  OWNER TO postgres;

-- Modification de la fonction de traca des update des infos propres aux pena
CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_pena(
    p_id_pena bigint,
    p_operation character varying,
    p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET hbe = pena.hbe, materiau = mat.nom, vol_constate = v.nom, positionnement = p.nom, capacite = pena.capacite, illimitee = pena.illimitee, incertaine = pena.incertaine
    FROM remocra.hydrant_pena pena
    LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
    LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
    LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
    WHERE pena.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pena;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.update_hydrant_pena(bigint, character varying, bigint)
  OWNER TO postgres;

--- Modification de la fonction de traca des update des infos propres au pibi
CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_pibi(
    p_id_pibi bigint,
    p_operation character varying,
    p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET  diametre = d.nom, debit = pibi.debit,  debit_max = pibi.debit_max, pression = pibi.pression, pression_dyn = pibi.pression_dyn, marque = m.nom, modele = mod.nom, pression_dyn_deb = pibi.pression_dyn_deb,
    jumele = jumele.numero, dispositif_inviolabilite = pibi.dispositif_inviolabilite, reservoir = hr.nom, service_eaux = service_eaux.nom, debit_renforce = pibi.debit_renforce, type_reseau_canalisation = trc.nom, type_reseau_alimentation = tra.nom, diametre_canalisation = pibi.diametre_canalisation, surpresse = pibi.surpresse, additive = pibi.additive
    FROM remocra.hydrant_pibi pibi
    LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
    LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
    LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
    LEFT JOIN remocra.hydrant jumele ON jumele.id = pibi.jumele
    LEFT JOIN remocra.hydrant_reservoir hr ON hr.id = pibi.reservoir
    LEFT JOIN remocra.organisme service_eaux ON service_eaux.id = pibi.service_eaux
    LEFT JOIN remocra.type_reseau_canalisation trc ON trc.id = pibi.type_reseau_canalisation
    LEFT JOIN remocra.type_reseau_alimentation tra ON tra.id = pibi.type_reseau_alimentation
    WHERE pibi.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pibi;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.update_hydrant_pibi(bigint, character varying, bigint)
  OWNER TO postgres;


-- Contenu réel du patch fin
--------------------------------------------------

commit;
