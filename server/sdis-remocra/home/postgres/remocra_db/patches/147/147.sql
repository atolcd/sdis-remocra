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
    numero_patch := 147;
    description_patch :='Mise à jour vue Afigéo';

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
DROP VIEW remocra_sig.hydrant;
CREATE OR REPLACE VIEW remocra_sig.hydrant
AS WITH hydrant_date_maj AS (
         SELECT hydrant.id_hydrant,
            max(hydrant.date_operation) AS date_maj
           FROM tracabilite.hydrant
          GROUP BY hydrant.id_hydrant
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
    thd.nom::integer AS diam_pei,
    hpi.diametre_canalisation::text AS diam_cana,
    ''::text AS source_pei,
    lower(replace(thdo.nom::text, 'é'::text, 'e'::text)) AS statut,
    s.nom AS nom_etab,
    h.voie AS situation,
    hpi.pression_dyn AS press_dyn,
    hpi.pression AS press_stat,
    hpi.debit,
    hpe.vol_constate AS volume,
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
    round(st_y(st_transform(h.geometrie, 4326))::numeric, 8) AS lat
   FROM remocra.hydrant h
     LEFT JOIN remocra.commune c ON c.id = h.commune
     LEFT JOIN remocra.type_hydrant_nature thn ON thn.id = h.nature
     LEFT JOIN remocra.hydrant_pibi hpi ON hpi.id = h.id
     LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = hpi.diametre
     LEFT JOIN remocra.type_hydrant_domaine thdo ON thdo.id = h.domaine AND (thdo.code::text = ANY (ARRAY['PRIVE'::character varying::text, 'PUBLIC'::character varying::text]))
     LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
     LEFT JOIN hydrant_date_maj hdm ON hdm.id_hydrant = h.id
     LEFT JOIN hydrant_date_bascule_dispo hdbd ON hdbd.id_hydrant = h.id
	 LEFT JOIN remocra.organisme o ON o.id = hpi.service_eaux
	 LEFT JOIN remocra.site s ON s.id = h.site;

-- Contenu réel du patch fin
--------------------------------------------------

commit;
