--
-- SDIS09 - Mise à disposition des disponibilités des PEI du SGO SYSTEL
--
-- Schéma remocra_sgo avec :
-- * table hydrant_nature        : correspondance codes SGO / Remocra
-- * table hydrant_disponibilite : correspondance codes SGO / Remocra
-- * vue hydrant_disponibilite   : récupération des disponibilités
--
-- Utilisateur systel avec accès en écriture aux codes SGO
--

begin;




DROP SCHEMA IF EXISTS remocra_sgo CASCADE;
CREATE SCHEMA remocra_sgo;
CREATE TABLE remocra_sgo.hydrant_nature (
  code_nature_sgo character varying NOT NULL,
  code_nature_remocra character varying NOT NULL,
  info_nature_remocra character varying,
  CONSTRAINT hydrant_nature_pkey PRIMARY KEY (code_nature_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_nature IS 'Correspondance entre la nature de point d''eau du SGO et la nature de point d''eau dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_nature_sgo IS 'Code de la nature du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_nature_remocra IS 'Code la nature du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.info_nature_remocra IS 'Indication sur la nature REMOCRA';

INSERT INTO remocra_sgo.hydrant_nature (code_nature_sgo,code_nature_remocra,info_nature_remocra) SELECT 'A_RENSEIGNER_SYSTEL',code, nom FROM remocra.type_hydrant_nature ORDER BY nom;

CREATE TABLE remocra_sgo.hydrant_disponibilite (
  code_disponibilite_sgo character varying NOT NULL,
  code_disponibilite_remocra character varying NOT NULL,
  info_disponibilite_remocra character varying,
  CONSTRAINT hydrant_disponibilite_pkey PRIMARY KEY (code_disponibilite_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_disponibilite IS 'Correspondance entre la disponibilité d''un point d''eau du SGO et la disponibilité d''un point d''eau dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_disponibilite.code_disponibilite_sgo IS 'Code de la disponibilité du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_disponibilite.code_disponibilite_remocra IS 'Code la disponibilité du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_disponibilite.info_disponibilite_remocra IS 'Indication sur la disponibilité REMOCRA';

INSERT INTO remocra_sgo.hydrant_disponibilite (code_disponibilite_sgo,code_disponibilite_remocra,info_disponibilite_remocra) VALUES ('1','DISPO','PEI disponible');
INSERT INTO remocra_sgo.hydrant_disponibilite (code_disponibilite_sgo,code_disponibilite_remocra,info_disponibilite_remocra) VALUES ('2','NON_CONFORME','PEI disponible mais présentant des caractéristiques limitant son usage. Faible débit, etc.');
INSERT INTO remocra_sgo.hydrant_disponibilite (code_disponibilite_sgo,code_disponibilite_remocra,info_disponibilite_remocra) VALUES ('12','INDISPO','PEI non disponible');

CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
SELECT
	h.numero::character varying AS "NUMERO_POINT_EAU",
	h.numero::character varying AS "NOM_POINT_EAU",
	nat.code_nature_sgo::character varying AS "NUMERO_TYPE_POINT_EAU",
	null::character varying AS "NUMERO_LIEU",
	null::integer AS "NUMERO_DANS_RUE",
	null::integer AS "PRECISIONS_DANS_RUE",
	null::character varying AS "NUMERO_ESCALIER",
	null::character varying AS "NUMERO_ETAGE",
	hpibi.pression::float AS "PRESSION",
	hpibi.debit::integer AS "DEBIT",
	null::integer AS "DIAMETRE",
	date_contr::timestamp AS "DH_VERIFICATION",
	null::character varying AS "ACCES",
	dispo.code_disponibilite_sgo::character varying AS "ETAT_POINT_EAU",
	st_x(h.geometrie)::float AS "COORD_X",
	st_y(h.geometrie)::float AS "COORD_Y",
	null::character varying AS "COORDONNEE_ALPHA",
	null::character varying AS "FEUILLE_IGN",
	null::character varying AS "NUM_PLAN",
	null::character varying AS "ID_RESEAU",
	null::timestamp AS "DH_DEB_INDISPO",
	null::timestamp AS "DH_FIN_INDISPO",
	null::integer AS "DEBIT_1BAR",
	null::integer AS "DIAMETRE_CANALISATION",
	null::character varying AS "NON_CONFORMITE",
	null::integer AS "VOLUME",
	null::integer AS "ID_GEO",
	com.insee::character varying AS "NUMERO_COMMUNE",
	null::character varying AS "ETAT_POINT_EAU_AVANT_INDISPO",
	null::character varying AS "OBJET_PT_EAU",
	null::bigint AS "CLEGCES"
FROM
	remocra.hydrant h
	LEFT JOIN remocra.hydrant_pibi hpibi ON (hpibi.id = h.id)
	LEFT JOIN remocra.hydrant_pena hpena ON (hpena.id = h.id)
	JOIN remocra.type_hydrant_nature thnat ON (thnat.id = h.nature)
	JOIN remocra_sgo.hydrant_nature nat ON (nat.code_nature_remocra = thnat.code)
	JOIN remocra_sgo.hydrant_disponibilite dispo ON (dispo.code_disponibilite_remocra = h.dispo_terrestre)
	JOIN remocra.commune com ON (com.id = h.commune);
COMMENT ON VIEW remocra_sgo.hydrant IS 'Etat des points d''eau en vue d''une exploitation par le SGO START de SYSTEL';




-- Rôle
CREATE FUNCTION createsystelroleonce8948() RETURNS void AS $$
BEGIN
   IF NOT EXISTS (
      SELECT *
      FROM   pg_catalog.pg_user
      WHERE  usename = 'systel') THEN

      CREATE ROLE systel LOGIN
        PASSWORD 'systel8948'
        NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
      COMMENT ON ROLE remocra IS 'Rôle utilisé par Systel';

   END IF;

END;
$$ LANGUAGE plpgsql;

select createsystelroleonce8948();
DROP FUNCTION createsystelroleonce8948();

-- Retrait des droits
REVOKE ALL ON DATABASE postgres FROM systel;
REVOKE ALL ON DATABASE template_postgis FROM systel;
REVOKE ALL ON DATABASE remocra FROM systel;
REVOKE ALL ON DATABASE remocra_ref_pdi FROM systel;

-- Ajout des droits - schéma
grant usage on schema remocra_sgo to systel;

-- Ajout des droits - sélection
grant select on remocra_sgo.hydrant_nature to systel;
grant select on remocra_sgo.hydrant_disponibilite to systel;
grant select on remocra_sgo.hydrant to systel;

-- Ajout des droits - modification
grant update (code_nature_sgo) on table remocra_sgo.hydrant_nature TO systel;
grant update (code_disponibilite_sgo) on table remocra_sgo.hydrant_disponibilite TO systel;




commit;

