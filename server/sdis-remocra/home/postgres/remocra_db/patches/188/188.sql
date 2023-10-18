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
    numero_patch := 188;
    description_patch := 'Ajoute un lien contact-site et change la structure site-gestionnaire_site-gestionnaire';

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

-- Sauvegarde Table Site
Select * into remocra.site_tmp from remocra.site;
-- Sauvegarde Table Gestionnaire_Site
select * into remocra.gestionnaire_site_tmp from remocra.gestionnaire_site;

-- La table Site devient gestionnaire_site
    -- Concervation des données
ALTER TABLE IF EXISTS remocra.site
    DROP COLUMN "version",
    DROP CONSTRAINT fk_gestionnaire_site,
    DROP COLUMN gestionnaire_site,
    ADD id_gestionnaire int8,
    ADD CONSTRAINT fk_gestionnaire FOREIGN KEY (id_gestionnaire) REFERENCES remocra.gestionnaire(id)
;
-- Suppression de l'ancienne table gesitonnaire_site
ALTER TABLE IF EXISTS remocra.gestionnaire_site
    DROP CONSTRAINT fk_gestionnaire
;
DROP TABLE IF EXISTS remocra.gestionnaire_site;
    -- Pour renommer la 'nouvelle'
ALTER TABLE IF EXISTS remocra.site
RENAME TO gestionnaire_site;
ALTER SEQUENCE remocra.site_id_seq RENAME TO gestionnaire_site_id_seq;
ALTER TABLE remocra.gestionnaire_site RENAME CONSTRAINT site_pkey TO gestionnaire_site_pkey;

-- Table Gestionnaire
ALTER TABLE IF EXISTS remocra.gestionnaire
    DROP COLUMN "version"
;

-- Création liaison Contact - Site
ALTER TABLE IF EXISTS remocra.contact
    DROP COLUMN IF EXISTS gestionnaire_site, -- Au cas où on réexec le patch
    ADD id_gestionnaire_site int8 NULL,
    DROP CONSTRAINT IF EXISTS fk_gestionnaire_site, -- Au cas où on réexec le patch
    ADD CONSTRAINT fk_gestionnaire_site FOREIGN KEY (id_gestionnaire_site) REFERENCES remocra.gestionnaire_site(id);
COMMENT ON COLUMN remocra.contact.id_gestionnaire_site IS 'Identifiant du site de rattachement';

-- Appels externe
ALTER TABLE remocra.debit_simultane RENAME CONSTRAINT fk_site TO fk_gestionnaireSite;
ALTER TABLE remocra.debit_simultane RENAME COLUMN site TO gestionnaire_site;
ALTER TABLE remocra.hydrant RENAME COLUMN site TO gestionnaire_site;
ALTER TABLE remocra.hydrant RENAME CONSTRAINT fk_site TO fk_gestionnaire_site;
ALTER TABLE tracabilite.hydrant RENAME COLUMN site TO gestionnaire_site;




-- Update Fonction Incrément Tracabilité
----- insert_hydrant
CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(p_id_hydrant bigint, p_operation character varying)
    RETURNS void
    LANGUAGE plpgsql
AS $function$
BEGIN
insert into tracabilite.hydrant (num_transac, nom_operation, date_operation, id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco,
date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, auteur_modification, hbe, positionnement, materiau, vol_constate, capacite, diametre, debit, debit_max, pression,
pression_dyn, marque, modele, pression_dyn_deb, domaine, nature_deci, numero_voie, suffixe_voie, niveau, gestionnaire, gestionnaire_site, autorite_deci, en_face, jumele, dispositif_inviolabilite, reservoir,
service_eaux, debit_renforce, type_reseau_canalisation, type_reseau_alimentation, diametre_canalisation, surpresse, additive, illimitee, incertaine, sp_deci, utilisateur_modification, organisme, auteur_modification_flag)
SELECT txid_current() ,p_operation,  now() as date_operation, h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom,  th.nom, array_agg(anomalie.nom), h.observation,
CASE
    WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur')
            AND ((SELECT h.auteur_modification_flag
                FROM remocra.hydrant h
                WHERE h.id = p_id_hydrant) = 'USER')
        THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
                FROM remocra.hydrant h
                    JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
                    JOIN remocra.organisme o on (u.organisme = o.id)
                WHERE h.id = p_id_hydrant)
        ELSE (SELECT o.nom
                FROM remocra.hydrant h
                    JOIN remocra.organisme o ON (h.organisme = o.id) AND h.id = p_id_hydrant)
END,
pena.hbe, p.nom, mat.nom, v.nom, pena.capacite, d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb, dom.nom,thnd.nom, h.numero_voie, h.suffixe_voie,
thn.nom, gestionnaire.nom, gestionnaire_site.nom, autorite_deci.nom, h.en_face, jumele.numero, pibi.dispositif_inviolabilite, hr.nom, service_eaux.nom, pibi.debit_renforce, trc.nom, tra.nom,
pibi.diametre_canalisation, pibi.surpresse, pibi.additive, pena.illimitee, pena.incertaine, sp_deci.nom, h.utilisateur_modification, h.organisme, h.auteur_modification_flag
FROM remocra.hydrant h
JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
LEFT JOIN remocra.commune c on (h.commune = c.id)
LEFT JOIN remocra.organisme o on (h.organisme = o.id)
LEFT JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
LEFT JOIN remocra.type_hydrant_niveau thn ON thn.id = h.niveau
LEFT JOIN remocra.gestionnaire_site ON gestionnaire_site.id = h.gestionnaire_site
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
GROUP BY h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom,
th.nom, h.observation, o.nom, pena.hbe, p.nom, mat.nom, v.nom, pena.capacite, d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom, pibi.pression_dyn_deb, dom.nom, thnd.nom, h.numero_voie,
h.suffixe_voie, thn.nom, gestionnaire.nom, gestionnaire_site.nom, autorite_deci.nom, h.en_face, jumele.numero, pibi.dispositif_inviolabilite, hr.nom, service_eaux.nom, pibi.debit_renforce, trc.nom, tra.nom,
pibi.diametre_canalisation, pibi.surpresse, pibi.additive, pena.illimitee, pena.incertaine, sp_deci.nom;
END;
$function$
;

----- update_hydrant
CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint)
    RETURNS void
    LANGUAGE plpgsql
AS $function$
BEGIN
update tracabilite.hydrant
SET nom_operation = p_operation, numero =h.numero, geometrie = h.geometrie, insee = c.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom,
type_hydrant = th.nom, agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre,
dispo_hbe = h.dispo_hbe, observation = h.observation,
auteur_modification =
CASE -- SI le  niveau de traca est fixé sur utilisateur
    WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur'
        -- ET que le auteur_modification_flag est bien USER
        -- Si le flag est API ou encore ETL ou...... alors on veux connaitre l'organisme pas l'utilisateur
        AND ((SELECT h.auteur_modification_flag FROM remocra.hydrant h WHERE h.id = p_id_hydrant) = 'USER'))
    THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
            FROM remocra.hydrant h
                JOIN remocra.utilisateur u ON (h.utilisateur_modification = u.id)
                JOIN remocra.organisme o ON (u.organisme = o.id)
            AND h.id = p_id_hydrant)
    ELSE o.nom
END,
domaine = dom.nom, nature_deci = thnd.nom, numero_voie = h.numero_voie, suffixe_voie = h.suffixe_voie, niveau = thn.nom,
gestionnaire = gestionnaire.nom, gestionnaire_site = gestionnaire_site.nom, autorite_deci = autorite_deci.nom, en_face = h.en_face, sp_deci = sp_deci.nom,
utilisateur_modification = h.utilisateur_modification, organisme = h.organisme, auteur_modification_flag = COALESCE(h.auteur_modification_flag, 'ETL')
FROM remocra.hydrant h
JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
LEFT JOIN remocra.commune c on (h.commune = c.id)
LEFT JOIN remocra.organisme o on (h.organisme = o.id)
LEFT JOIN remocra.type_hydrant_domaine dom on (h.domaine = dom.id)
LEFT JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
LEFT JOIN remocra.type_hydrant_niveau thn ON thn.id = h.niveau
LEFT JOIN remocra.gestionnaire_site ON gestionnaire_site.id = h.gestionnaire_site
LEFT JOIN remocra.organisme autorite_deci ON  autorite_deci.id = h.autorite_deci
LEFT JOIN remocra.gestionnaire gestionnaire ON gestionnaire.id = h.gestionnaire
LEFT JOIN remocra.organisme sp_deci ON sp_deci.id = h.sp_deci
WHERE h.id = p_id_hydrant
AND num_transac = p_num_transac
AND id_hydrant = p_id_hydrant;
END;
$function$
;

-- vue remocra_sig.hydrant
-- remocra_sig.hydrant source
    -- Au cas ou le sdis utilise une vue personnalisée
    -- Retrouver le patch correspondant (s'il existe) et réexecuter ou le modifier pour prendre en compte les changements sur SITE
ALTER VIEW remocra_sig.hydrant RENAME TO hydrant_before_188;

CREATE OR REPLACE VIEW remocra_sig.hydrant
AS WITH hydrant_date_maj AS (
         SELECT hydrant.id_hydrant,
            max(hydrant.date_operation) AS date_maj
           FROM tracabilite.hydrant
          GROUP BY hydrant.id_hydrant
        ), aspiration AS (
         SELECT hydrant_aspiration.pena AS id_hydrant
           FROM remocra.hydrant_aspiration
        ), hydrant_date_bascule_dispo AS (
         SELECT h_1.id_hydrant,
            min(h_1.date_operation) AS date_operation
           FROM tracabilite.hydrant h_1
             JOIN ( SELECT h_2.id_hydrant,
                    max(h_2.date_operation) AS date_operation
                   FROM tracabilite.hydrant h_2
                  WHERE h_2.dispo_terrestre::text <> 'INDISPO'::text AND h_2.dispo_terrestre IS NOT NULL
                  GROUP BY h_2.id_hydrant) dispo ON dispo.id_hydrant = h_1.id_hydrant AND dispo.date_operation < h_1.date_operation
          WHERE h_1.dispo_terrestre::text = 'INDISPO'::text
          GROUP BY h_1.id_hydrant
        UNION
         SELECT h_1.id_hydrant,
            min(h_1.date_operation) AS date_operation
           FROM tracabilite.hydrant h_1
             JOIN ( SELECT h_2.id_hydrant,
                    max(h_2.date_operation) AS date_operation
                   FROM tracabilite.hydrant h_2
                  WHERE h_2.dispo_terrestre::text <> 'NON_CONFORME'::text AND h_2.dispo_terrestre IS NOT NULL
                  GROUP BY h_2.id_hydrant) dispo ON dispo.id_hydrant = h_1.id_hydrant AND dispo.date_operation < h_1.date_operation
          WHERE h_1.dispo_terrestre::text = 'NON_CONFORME'::text
          GROUP BY h_1.id_hydrant
        UNION
         SELECT h_1.id_hydrant,
            min(h_1.date_operation) AS date_operation
           FROM tracabilite.hydrant h_1
             JOIN ( SELECT h_2.id_hydrant,
                    max(h_2.date_operation) AS date_operation
                   FROM tracabilite.hydrant h_2
                  WHERE h_2.dispo_terrestre::text <> 'DISPO'::text AND h_2.dispo_terrestre IS NOT NULL
                  GROUP BY h_2.id_hydrant) dispo ON dispo.id_hydrant = h_1.id_hydrant AND dispo.date_operation < h_1.date_operation
          WHERE h_1.dispo_terrestre::text = 'DISPO'::text
          GROUP BY h_1.id_hydrant
        )
 SELECT c.insee,
    h.numero::text AS id_sdis,
    ''::text AS id_gestion,
    o.nom AS nom_gest,
    h.numero_interne::text AS ref_terr,
    replace(thn.code::text, 'CI_FIXE'::text, 'CI'::text) AS type_pei,
    ''::text AS type_rd,
    thd.nom AS diam_pei,
    hpi.diametre_canalisation::text AS diam_cana,
    ''::text AS source_pei,
    lower(replace(thnd.nom::text, 'é'::text, 'e'::text)) AS statut,
    s.nom AS nom_etab,
    h.voie AS situation,
    hpi.pression_dyn AS press_dyn,
    hpi.pression AS press_stat,
    hpi.debit,
    hpe.capacite::bigint AS volume,
        CASE
            WHEN h.dispo_terrestre::text = 'DISPO'::text OR h.dispo_terrestre::text = 'NON_CONFORME'::text THEN true
            ELSE false
        END AS disponible,
    hdbd.date_operation::date AS date_dispo,
    h.date_recep::date AS date_mes,
    hdm.date_maj::date AS date_maj,
    h.date_contr::date AS date_ct,
    h.date_reco::date AS date_ro,
    ''::text AS prec,
    round(st_x(h.geometrie)::numeric, 2) AS x,
    round(st_y(h.geometrie)::numeric, 2) AS y,
    round(st_x(st_transform(h.geometrie, 4326))::numeric, 8) AS long,
    round(st_y(st_transform(h.geometrie, 4326))::numeric, 8) AS lat,
        CASE
            WHEN (h.id IN ( SELECT a.id_hydrant
               FROM aspiration a)) THEN 'Aire aspi'::character varying
            ELSE h.code
        END AS sous_type
   FROM remocra.hydrant h
     LEFT JOIN remocra.commune c ON c.id = h.commune
     LEFT JOIN remocra.type_hydrant_nature thn ON thn.id = h.nature
     LEFT JOIN remocra.hydrant_pibi hpi ON hpi.id = h.id
     LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = hpi.diametre
     LEFT JOIN remocra.type_hydrant_domaine thdo ON thdo.id = h.domaine AND (thdo.code::text = ANY (ARRAY['PRIVE'::character varying::text, 'PUBLIC'::character varying::text]))
     LEFT JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
     LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
     LEFT JOIN hydrant_date_maj hdm ON hdm.id_hydrant = h.id
     LEFT JOIN hydrant_date_bascule_dispo hdbd ON hdbd.id_hydrant = h.id
     LEFT JOIN remocra.organisme o ON o.id = hpi.service_eaux
     LEFT JOIN remocra.gestionnaire_site s ON s.id = h.gestionnaire_site;

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
