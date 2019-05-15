begin;
/*
*  Patch pour la synchro avec le SGO
*/
-- Création du schéma remocra_sgo
DROP SCHEMA IF EXISTS remocra_sgo CASCADE;
CREATE SCHEMA remocra_sgo
  AUTHORIZATION postgres;

GRANT ALL ON SCHEMA remocra_sgo TO postgres;
GRANT USAGE ON SCHEMA remocra_sgo TO remocra;

-- Création de la table de correspondance des codes de diamètre de PIBI
CREATE TABLE remocra_sgo.hydrant_diametre
(
  code_sgo character varying NOT NULL,
  code_remocra character varying NOT NULL,
  info_remocra character varying,
  CONSTRAINT hydrant_diametre_pkey PRIMARY KEY (code_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_diametre IS 'Correspondance entre le diamètre de point d''eau du SGO et le diametre de point d''eau dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_diametre.code_sgo IS 'Code du diamète du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_diametre.code_remocra IS 'Code du diamète du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_diametre.info_remocra IS 'Indication sur le diamète REMOCRA';

-- Création de la table de correspondance des codes de domaine
CREATE TABLE remocra_sgo.hydrant_domaine
(
  code_sgo character varying NOT NULL,
  code_remocra character varying NOT NULL,
  info_remocra character varying,
  CONSTRAINT hydrant_domaine_pkey PRIMARY KEY (code_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_domaine IS 'Correspondance entre le domaine du point d''eau du SGO et le domaine du point d''eau dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_domaine.code_sgo IS 'Code du domaine du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_domaine.code_remocra IS 'Code du domaine du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_domaine.info_remocra IS 'Indication sur le domaine REMOCRA';

-- Création de la table de correspondance des codes de nature
CREATE TABLE remocra_sgo.hydrant_nature
(
  code_sgo character varying NOT NULL,
  code_remocra character varying NOT NULL,
  info_remocra character varying,
  CONSTRAINT hydrant_nature_pkey PRIMARY KEY (code_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_nature IS 'Correspondance entre la nature du point d''eau du SGO et la nature du point d''eau dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_sgo IS 'Code de la nature du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_remocra IS 'Code de la nature du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.info_remocra IS 'Indication sur la nature REMOCRA';

-- Population des tables de correspondance
INSERT INTO remocra_sgo.hydrant_diametre
  SELECT 'TBD' AS code_sgo,
    code AS code_remocra,
    nom AS info_remocra
  FROM remocra.type_hydrant_diametre;

INSERT INTO remocra_sgo.hydrant_domaine
  SELECT 'TBD' AS code_sgo,
    code AS code_remocra,
    nom AS info_remocra
  FROM remocra.type_hydrant_domaine;

INSERT INTO remocra_sgo.hydrant_nature
  SELECT 'TBD' AS code_sgo,
    code AS code_remocra,
    nom AS info_remocra
  FROM remocra.type_hydrant_nature;

INSERT INTO remocra_sgo.hydrant_nature VALUES ('TBD', 'CI_ALIBRE', 'Citerne a l''air libre');

INSERT INTO remocra_sgo.hydrant_nature VALUES ('TBD', 'CI_ENTERRE', 'Citerne enterrée ');

-- Mise à jour des codes du SGO
UPDATE remocra_sgo.hydrant_diametre SET code_sgo = '100' WHERE code_remocra = 'DIAM100';
UPDATE remocra_sgo.hydrant_diametre SET code_sgo = '150' WHERE code_remocra = 'DIAM150';
UPDATE remocra_sgo.hydrant_diametre SET code_sgo = '70' WHERE code_remocra = 'DIAM70';
UPDATE remocra_sgo.hydrant_diametre SET code_sgo = '80' WHERE code_remocra = 'DIAM80';

UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'V' WHERE code_remocra = 'AUTOROUTE';
UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'P' WHERE code_remocra = 'COMMUNAL';
UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'P' WHERE code_remocra = 'DEPARTEMENT';
UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'V' WHERE code_remocra = 'DOMAINE';
UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'V' WHERE code_remocra = 'MILITAIRE';
UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'V' WHERE code_remocra = 'PRIVE';
UPDATE remocra_sgo.hydrant_domaine SET code_sgo = 'P' WHERE code_remocra = 'PUBLIC';

UPDATE remocra_sgo.hydrant_nature SET code_sgo = 'Bouches' WHERE code_remocra = 'BI';
UPDATE remocra_sgo.hydrant_nature SET code_sgo = 'Citerne a l''air libre' WHERE code_remocra = 'CI_ALIBRE';
UPDATE remocra_sgo.hydrant_nature SET code_sgo = 'Citerne enteree' WHERE code_remocra = 'CI_ENTERRE';
UPDATE remocra_sgo.hydrant_nature SET code_sgo = 'Point d''aspiration' WHERE code_remocra = 'PA';
UPDATE remocra_sgo.hydrant_nature SET code_sgo = 'Poteau' WHERE code_remocra = 'PI';

-- Vue des PEI pour exploitation dans le SGO
CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
  WITH remocra_hydrant_nature AS (
  SELECT h.id,
  CASE WHEN thn.code = 'CI_FIXE' AND thp.code = 'AIR_LIBRE' THEN 'CI_ALIBRE'
    WHEN thn.code = 'CI_FIXE' AND thp.code = 'ENTERRE' THEN 'CI_ENTERRE'
    ELSE thn.code END AS code
  FROM remocra.hydrant h
    LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
    LEFT JOIN remocra.type_hydrant_positionnement thp ON thp.id = hpe.positionnement
    LEFT JOIN remocra.type_hydrant_nature thn ON thn.id = h.nature
)
  SELECT h.id AS ID_HYDRANT,
    CASE WHEN h.dispo_terrestre = 'DISPO' THEN 0 WHEN h.dispo_terrestre = 'INDISPO' THEN 1 WHEN h.dispo_terrestre = 'NON_CONFORME' THEN 2 ELSE 3 END AS INDISPONIBLE,
    u.nom || ' ' || u.prenom AS MODIFIE_PAR,
    anomalie.anomalies AS ANOMALIES,
    hpe.capacite AS CAPACITE,
    hpi.pression AS PRESSION,
    hd.code_sgo AS DIAMETRE,
    null::integer AS DIAMETRE_CANALISATION,
    h.voie2 AS ACCES,
    h.voie AS SITUATION,
    hpi.gest_reseau AS GESTIONNAIRE,
    hdo.code_sgo AS CODE_DOMAINE,
    hpi.debit AS DEBIT,
    GREATEST(h.date_recep, h.date_contr, h.date_reco) AS DATE_VERIFICATION,
    h.gest_point_eau AS PROPRIETAIRE,
    carr.nom AS CARROYAGE,
    c.insee AS CODE_INSEE,
    ST_AsText(h.geometrie) AS GEOMETRIE,
    h.numero AS NUMERO,
    hn.code_sgo AS SOUS_TYPE
  FROM remocra.hydrant h
    LEFT JOIN remocra.utilisateur u ON u.id = h.utilisateur_modification
    LEFT JOIN (
      SELECT
        a.hydrant,
        array_to_string(array_agg(ta.code),';') AS anomalies
      FROM
        remocra.hydrant_anomalies a
        JOIN remocra.type_hydrant_anomalie ta ON (ta.id = a.anomalies)
      GROUP BY
        a.hydrant) AS anomalie ON anomalie.hydrant = h.id
    LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
    LEFT JOIN remocra.hydrant_pibi hpi ON hpi.id = h.id
    LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = hpi.diametre
    LEFT JOIN remocra.type_hydrant_domaine thdo ON thdo.id = h.domaine
    LEFT JOIN remocra_referentiel.carroyage_deci carr ON ST_Contains(carr.geometrie, h.geometrie) AND carr.type_carroyage = '333'
    LEFT JOIN remocra.commune c ON c.id = h.commune
    LEFT JOIN remocra_sgo.hydrant_diametre hd ON hd.code_remocra = thd.code
    LEFT JOIN remocra_hydrant_nature rhn ON rhn.id = h.id
    LEFT JOIN remocra_sgo.hydrant_domaine hdo ON hdo.code_remocra = thdo.code
    LEFT JOIN remocra_sgo.hydrant_nature hn ON hn.code_remocra = rhn.code;
COMMENT ON VIEW remocra_sgo.hydrant IS 'Etat des points d''eau en vue d''une exploitation par le SGO GIPSI';

-- Journal des modifications des PEI
CREATE OR REPLACE VIEW remocra_sgo.journal AS
  SELECT h.id AS numero_enregistrement,
    h.date_operation AS date_heure,
    substring(h.nom_operation from 1 for 1) AS type_modification,
    h.id_hydrant AS cle
  FROM tracabilite.hydrant h
  WHERE h.date_operation >= now() - interval '2 months';
COMMENT ON VIEW remocra_sgo.journal IS 'Journal des modification des PEI pour synchronisation avec le SGO';

-- Rôle
CREATE FUNCTION createimpiroleonce8948() RETURNS void AS $$
BEGIN
   IF NOT EXISTS (
      SELECT *
      FROM   pg_catalog.pg_user
      WHERE  usename = 'impi') THEN

      CREATE ROLE impi LOGIN
        PASSWORD 'impi6173'
        NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
      COMMENT ON ROLE remocra IS 'Rôle utilisé par IMPI';

   END IF;

END;
$$ LANGUAGE plpgsql;

select createimpiroleonce8948();
DROP FUNCTION createimpiroleonce8948();

-- Retrait des droits
REVOKE ALL ON DATABASE postgres FROM impi;
REVOKE ALL ON DATABASE template_postgis FROM impi;
REVOKE ALL ON DATABASE remocra FROM impi;
REVOKE ALL ON DATABASE remocra_ref_pdi FROM impi;

-- Ajout des droits - schéma
grant usage on schema remocra_sgo to impi;

-- Ajout des droits - sélection
grant select on remocra_sgo.hydrant_diametre to impi;
grant select on remocra_sgo.hydrant_domaine to impi;
grant select on remocra_sgo.hydrant_nature to impi;
grant select on remocra_sgo.hydrant to impi;
grant select on remocra_sgo.journal to impi;

-- Ajout des droits - modification
grant update (code_sgo) on table remocra_sgo.hydrant_nature TO impi;
grant update (code_sgo) on table remocra_sgo.hydrant_diametre TO impi;
grant update (code_sgo) on table remocra_sgo.hydrant_domaine TO impi;

commit;
