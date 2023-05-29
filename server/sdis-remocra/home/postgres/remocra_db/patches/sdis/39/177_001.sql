DROP SCHEMA IF EXISTS remocra_sgo CASCADE;
CREATE SCHEMA remocra_sgo;
CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
-- remocra_sgo.hydrant sourc
WITH der_modif AS (
         SELECT h_1.id_hydrant,
            max(h_1.date_operation) AS date_operation
           FROM tracabilite.hydrant h_1
          GROUP BY h_1.id_hydrant
        ), anomalies AS (
         SELECT a.hydrant,
            array_to_string(array_agg(ta.code), ','::text) AS anomalies
           FROM hydrant_anomalies a
             JOIN type_hydrant_anomalie ta ON ta.id = a.anomalies
          GROUP BY a.hydrant
        )
 SELECT NULL::integer AS quid,
    ("right"(split_part(h.numero::text, '.'::text, 1), 3) || '.'::text) || "right"(h.numero::text, 3) AS numero_ins,
    h.numero AS id,
    thn.nom AS cat_pei,
    "right"(h.numero::text, 5) AS numero,
        CASE
            WHEN thnd.code::text = 'PUBLIC'::text THEN 1
            ELSE 2
        END AS publicpriv,
    h.date_recep AS daterecept,
        CASE
            WHEN h.dispo_terrestre::text = 'DISPO'::text THEN 1
            WHEN h.dispo_terrestre::text = 'NON_CONFORME'::text THEN 2
            ELSE 3
        END AS etat,
    h.complement AS "precision",
    case
    	when h.code = 'PIBI' then diametre.code
    	else materiau.nom
    end AS type_pei,
    1 AS controleff,
    h.date_contr AS der_contro,
    h.date_reco AS der_visu,
    commune.insee,
    h.numero_voie AS numadress,
    h.suffixe_voie AS compadress,
    NULL::character varying AS type_voie,
    h.voie AS voieadress,
    pibi.pression,
    case
    	when h.code = 'PENA' then pena.capacite
    	else ''||reservoir.capacite
    end AS volume,
    pibi.debit,
    pibi.diametre_canalisation AS diam_cond,
    h.complement AS observatio,
    split_part(anomalies.anomalies, ','::text, 1) AS anomalie1,
    split_part(anomalies.anomalies, ','::text, 2) AS anomalie2,
    split_part(anomalies.anomalies, ','::text, 3) AS anomalie3,
    split_part(anomalies.anomalies, ','::text, 4) AS anomalie4,
    split_part(anomalies.anomalies, ','::text, 5) AS anomalie5,
    split_part(anomalies.anomalies, ','::text, 6) AS anomalie6,
    tournee.id || tournee.nom::text AS id_tournee,
    h.agent1 AS matricule,
    der_modif.date_operation AS date_modif
   FROM hydrant h
     LEFT JOIN hydrant_pibi pibi ON pibi.id = h.id
     LEFT JOIN hydrant_pena pena ON pena.id = h.id
     JOIN commune ON commune.id = h.commune
     JOIN type_hydrant_nature thn ON h.nature = thn.id
     JOIN type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
     LEFT JOIN type_hydrant_diametre diametre ON diametre.id = pibi.diametre
     LEFT JOIN type_hydrant_materiau materiau ON materiau.id = pena.materiau
     LEFT JOIN hydrant_reservoir reservoir ON reservoir.id = pibi.reservoir
     LEFT JOIN type_reseau_canalisation trc ON trc.id = pibi.type_reseau_canalisation
     JOIN der_modif ON der_modif.id_hydrant = h.id
     LEFT JOIN anomalies ON anomalies.hydrant = h.id
     LEFT JOIN hydrant_tournees ON hydrant_tournees.hydrant = h.id
     LEFT JOIN tournee ON tournee.id = hydrant_tournees.tournees;