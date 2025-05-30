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
    numero_patch := 133;
    description_patch := 'Ajout du champ service publique deci';

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

ALTER TABLE remocra.hydrant
ADD COLUMN sp_deci BIGINT,
ADD CONSTRAINT fk_sp_deci FOREIGN KEY (sp_deci)
	REFERENCES remocra.organisme (id) MATCH SIMPLE ,
ADD CONSTRAINT fk_gestionnaire FOREIGN KEY (gestionnaire)
	REFERENCES remocra.gestionnaire (id) MATCH SIMPLE;

COMMENT ON COLUMN remocra.hydrant.sp_deci IS 'Identifiant du service publique DECI';

CREATE INDEX hydrant_sp_deci_idx ON remocra.hydrant USING btree (sp_deci);
CREATE INDEX hydrant_gestionnaire_idx ON remocra.hydrant USING btree (gestionnaire);

ALTER TABLE tracabilite.hydrant ADD COLUMN sp_deci character varying;
COMMENT ON COLUMN tracabilite.hydrant.sp_deci IS 'Identifiant service publique deci';



CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

BEGIN
    update tracabilite.hydrant
    SET nom_operation = p_operation, numero =h.numero, geometrie = h.geometrie, insee = c.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom, type_hydrant = th.nom,
    agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre, dispo_hbe = h.dispo_hbe,
    observation = h.observation, auteur_modification = CASE WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur') THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
    FROM remocra.hydrant h
    JOIN remocra.organisme o on (h.organisme = o.id)
    JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
    WHERE h.id = p_id_hydrant) ELSE o.nom END, domaine = dom.nom, nature_deci = thnd.nom, numero_voie = h.numero_voie, suffixe_voie = h.suffixe_voie, niveau = thn.nom,
    gestionnaire = gestionnaire.nom, site = site.nom, autorite_deci = autorite_deci.nom, en_face = h.en_face, sp_deci = sp_deci.nom
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
    LEFT JOIN remocra.gestionnaire gestionnaire ON gestionnaire.id = h.gestionnaire
    LEFT JOIN remocra.organisme sp_deci ON sp_deci.id = h.sp_deci
    WHERE h.id = p_id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$function$;


CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(p_id_hydrant bigint, p_operation character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
BEGIN
    insert into tracabilite.hydrant (num_transac, nom_operation, date_operation,
    id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco, date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, auteur_modification,
    hbe, positionnement, materiau, vol_constate, capacite,
    diametre, debit, debit_max, pression, pression_dyn, marque, modele, pression_dyn_deb, domaine,
    nature_deci, numero_voie, suffixe_voie, niveau, gestionnaire, site, autorite_deci, en_face, jumele, dispositif_inviolabilite, reservoir, service_eaux, debit_renforce, type_reseau_canalisation, type_reseau_alimentation, diametre_canalisation, surpresse, additive, illimitee, incertaine, sp_deci)
    SELECT
      txid_current() ,p_operation,  now() as date_operation,
      h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom,  th.nom, array_agg(anomalie.nom), h.observation, CASE WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur') THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
      FROM remocra.hydrant h
      JOIN remocra.organisme o on (h.organisme = o.id)
      JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
      WHERE h.id = p_id_hydrant) ELSE o.nom END,
      pena.hbe, p.nom, mat.nom, v.nom, pena.capacite,
      d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb,
      dom.nom,
      thnd.nom, h.numero_voie, h.suffixe_voie, thn.nom, gestionnaire.nom, site.nom, autorite_deci.nom, h.en_face,
      jumele.numero, pibi.dispositif_inviolabilite, hr.nom, service_eaux.nom, pibi.debit_renforce, trc.nom, tra.nom, pibi.diametre_canalisation, pibi.surpresse, pibi.additive,
      pena.illimitee, pena.incertaine, sp_deci.nom
      FROM remocra.hydrant h
      JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
      JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
      LEFT JOIN remocra.commune c on (h.commune = c.id)
      LEFT JOIN remocra.organisme o on (h.organisme = o.id)
      LEFT JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
      LEFT JOIN remocra.type_hydrant_niveau thn ON thn.id = h.niveau
      LEFT JOIN remocra.site ON site.id = h.site
      LEFT JOIN remocra.organisme autorite_deci ON  autorite_deci.id = h.autorite_deci
      LEFT JOIN remocra.gestionnaire gestionnaire ON gestionnaire.id = h.gestionnaire
      LEFT JOIN remocra.organisme sp_deci ON sp_deci.id = h.sp_deci
      LEFT JOIN remocra.hydrant_pibi pibi on (pibi.id = h.id)
      LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
      LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
      LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
      LEFT JOIN remocra.hydrant jumele ON jumele.id = pibi.jumele
      LEFT JOIN remocra.hydrant_reservoir hr ON hr.id = pibi.reservoir
      LEFT JOIN remocra.organisme service_eaux ON service_eaux.id = pibi.service_eaux
      LEFT JOIN remocra.type_reseau_canalisation trc ON trc.id = pibi.type_reseau_canalisation
      LEFT JOIN remocra.type_reseau_alimentation tra ON tra.id = pibi.type_reseau_alimentation
      LEFT JOIN remocra.hydrant_pena pena on (pena.id = h.id)
      LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
      LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
      LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
      LEFT JOIN (remocra.hydrant_anomalies ha JOIN remocra.type_hydrant_anomalie a on (ha.anomalies = a.id)) anomalie on (anomalie.hydrant = h.id)
      LEFT JOIN remocra.type_hydrant_domaine dom on (h.domaine = dom.id)
      WHERE h.id = p_id_hydrant
      GROUP BY h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, h.observation, o.nom,
    pena.hbe, p.nom, mat.nom, v.nom, pena.capacite,
    d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb, dom.nom, thnd.nom, h.numero_voie, h.suffixe_voie, thn.nom, gestionnaire.nom, site.nom, autorite_deci.nom, h.en_face, 
    jumele.numero, pibi.dispositif_inviolabilite, hr.nom, service_eaux.nom, pibi.debit_renforce, trc.nom, tra.nom, pibi.diametre_canalisation, pibi.surpresse, pibi.additive,
    pena.illimitee, pena.incertaine, sp_deci.nom;

END;
$function$;


-- Contenu réel du patch fin
--------------------------------------------------

commit;

