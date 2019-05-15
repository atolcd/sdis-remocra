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
    numero_patch := 105;
    description_patch := 'Correction trigger de traça';

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

CREATE SCHEMA remocra_sig
  AUTHORIZATION postgres;

GRANT ALL ON SCHEMA remocra_sig TO postgres;
GRANT USAGE ON SCHEMA remocra_sig TO remocra;

CREATE OR REPLACE VIEW remocra_sig.hydrant AS
WITH hydrant_date_maj AS (
  SELECT id_hydrant,
    MAX(date_operation) AS date_maj
  FROM tracabilite.hydrant
  GROUP BY id_hydrant
),
hydrant_date_bascule_dispo AS (
  -- Première date de bascule en indisponibilité après la
  -- dernière date de disponibilité totale ou partielle connue
  SELECT
    h.id_hydrant,
    MIN(h.date_operation) AS date_operation
  FROM
    tracabilite.hydrant h
    JOIN (
      --Dernière date de disponibilite totale ou partielle connue
      SELECT
        h.id_hydrant,
        max(h.date_operation) AS date_operation
      FROM
        tracabilite.hydrant h
      WHERE
        dispo_terrestre <> 'INDISPO' AND dispo_terrestre IS NOT NULL
      GROUP BY
        h.id_hydrant) dispo ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation)
  WHERE
    dispo_terrestre = 'INDISPO'
  GROUP BY
    h.id_hydrant
  UNION
  -- Première date de bascule en non conforme après la
  -- dernière date de disponibilité ou de non disponibilité connue
  SELECT
    h.id_hydrant,
    MIN(h.date_operation) AS date_operation
  FROM
    tracabilite.hydrant h
    JOIN (
      --Dernière date de disponibilite totale ou partielle connue
      SELECT
        h.id_hydrant,
        max(h.date_operation) AS date_operation
      FROM
        tracabilite.hydrant h
      WHERE
        dispo_terrestre <> 'NON_CONFORME' AND dispo_terrestre IS NOT NULL
      GROUP BY
        h.id_hydrant) dispo ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation)
  WHERE
    dispo_terrestre = 'NON_CONFORME'
  GROUP BY
    h.id_hydrant
  UNION
  -- Première date de bascule en disponibilité après la
  -- dernière date de disponibilité partielle ou indisponibilité connue
  SELECT
    h.id_hydrant,
    MIN(h.date_operation) AS date_operation
  FROM
    tracabilite.hydrant h
    JOIN (
      --Dernière date de disponibilite totale ou partielle connue
      SELECT
        h.id_hydrant,
        max(h.date_operation) AS date_operation
      FROM
        tracabilite.hydrant h
      WHERE
        dispo_terrestre <> 'DISPO' AND dispo_terrestre IS NOT NULL
      GROUP BY
        h.id_hydrant) dispo ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation)
  WHERE
    dispo_terrestre = 'DISPO'
  GROUP BY
    h.id_hydrant)

SELECT c.insee,
  h.numero::text AS id_sdis,
  ''::text AS id_gestion,
  ''::text AS nom_gest,
  h.numero_interne::text AS ref_terr,
  REPLACE(thn.code, 'CI_FIXE', 'CI') AS type_pei,
  ''::text AS type_rd,
  thd.nom::integer AS diam_pei,
  '' AS diam_cana,
  ''::text AS source_pei,
  lower(REPLACE(thdo.nom, 'é', 'e')) AS statut,
  ''::text AS nom_etab,
  h.voie AS situation,
  hpi.pression_dyn AS press_dyn,
  hpi.pression AS press_stat,
  hpi.debit AS debit,
  hpe.vol_constate AS volume,
  CASE WHEN h.dispo_terrestre = 'DISPO' OR h.dispo_terrestre = 'NON_CONFORME' THEN true ELSE false END AS disponible,
  hdbd.date_operation::date AS date_dispo,
  h.date_recep::date AS date_mes,
  hdm.date_maj::date AS date_maj,
  h.date_contr::date AS date_ct,
  h.date_reco::date AS date_ro,
  ''::text AS prec,
  ROUND(ST_X(h.geometrie)::numeric,2) AS x,
  ROUND(ST_Y(h.geometrie)::numeric,2) AS y,
  ROUND(ST_X(ST_Transform(h.geometrie,4326))::numeric,8) AS long,
  ROUND(ST_Y(ST_Transform(h.geometrie,4326))::numeric,8) AS lat
FROM remocra.hydrant h
  LEFT JOIN remocra.commune c ON c.id = h.commune
  LEFT JOIN remocra.type_hydrant_nature thn ON thn.id = h.nature
  LEFT JOIN remocra.hydrant_pibi hpi ON hpi.id = h.id
  LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = hpi.diametre
  LEFT JOIN remocra.type_hydrant_domaine thdo ON thdo.id = h.domaine AND thdo.code IN ('PRIVE', 'PUBLIC')
  LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
  LEFT JOIN hydrant_date_maj hdm ON hdm.id_hydrant = h.id
  LEFT JOIN hydrant_date_bascule_dispo hdbd ON hdbd.id_hydrant = h.id;


-- Contenu réel du patch fin
--------------------------------------------------

commit;
