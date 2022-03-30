begin;

CREATE OR REPLACE VIEW remocra_sgo.hydrant
AS WITH remocra_hydrant_nature AS (
         SELECT h_1.id,
                CASE
                    WHEN thn.code::text = 'CI_AIR'::text THEN 'CI_ALIBRE'::character varying
                    WHEN thn.code::text = 'CI_ENT'::text THEN 'CI_ENTERRE'::character varying
                    ELSE thn.code
                END AS code
           FROM hydrant h_1
             LEFT JOIN hydrant_pena hpe_1 ON hpe_1.id = h_1.id
             LEFT JOIN type_hydrant_positionnement thp ON thp.id = hpe_1.positionnement
             LEFT JOIN type_hydrant_nature thn ON thn.id = h_1.nature
        )
 SELECT h.id AS id_hydrant,
        CASE
            WHEN h.dispo_terrestre::text = 'DISPO'::text THEN 0
            WHEN h.dispo_terrestre::text = 'INDISPO'::text THEN 1
            WHEN h.dispo_terrestre::text = 'NON_CONFORME'::text THEN 2
            ELSE 3
        END AS indisponible,
    (u.nom::text || ' '::text) || u.prenom::text AS modifie_par,
    anomalie.anomalies,
    hpe.capacite,
    hpi.pression,
    hd.code_sgo AS diametre,
    NULL::integer AS diametre_canalisation,
    h.complement AS acces,
    h.voie AS situation,
    hpi.gest_reseau AS gestionnaire,
    hdo.code_sgo AS code_domaine,
    hpi.debit,
    GREATEST(h.date_recep, h.date_contr, h.date_reco) AS date_verification,
    h.gest_point_eau AS proprietaire,
    carr.nom AS carroyage,
    c.insee AS code_insee,
    st_astext(h.geometrie) AS geometrie,
    h.numero_interne AS numero,
    hn.code_sgo AS sous_type
   FROM hydrant h
     LEFT JOIN utilisateur u ON u.id = h.utilisateur_modification
     LEFT JOIN ( SELECT a.hydrant,
            array_to_string(array_agg(ta.code), ';'::text) AS anomalies
           FROM hydrant_anomalies a
             JOIN type_hydrant_anomalie ta ON ta.id = a.anomalies
          GROUP BY a.hydrant) anomalie ON anomalie.hydrant = h.id
     LEFT JOIN hydrant_pena hpe ON hpe.id = h.id
     LEFT JOIN hydrant_pibi hpi ON hpi.id = h.id
     LEFT JOIN type_hydrant_diametre thd ON thd.id = hpi.diametre
     LEFT JOIN type_hydrant_domaine thdo ON thdo.id = h.domaine
     LEFT JOIN remocra_referentiel.carroyage_deci carr ON st_contains(carr.geometrie, h.geometrie) AND carr.type_carroyage::text = '333'::text
     LEFT JOIN commune c ON c.id = h.commune
     LEFT JOIN remocra_sgo.hydrant_diametre hd ON hd.code_remocra::text = thd.code::text
     LEFT JOIN remocra_hydrant_nature rhn ON rhn.id = h.id
     LEFT JOIN remocra_sgo.hydrant_domaine hdo ON hdo.code_remocra::text = thdo.code::text
     LEFT JOIN remocra_sgo.hydrant_nature hn ON hn.code_remocra::text = rhn.code::text;

COMMENT ON VIEW remocra_sgo.hydrant IS 'Etat des points d''eau en vue d''une exploitation par le SGO';

commit;