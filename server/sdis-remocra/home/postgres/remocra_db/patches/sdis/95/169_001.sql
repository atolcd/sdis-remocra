/*
============================================================================
  Ce fichier contient un mot de passe à attribuer avant de jouer le script
============================================================================
*/

CREATE SCHEMA remocra_sgo AUTHORIZATION remocra;

CREATE TABLE remocra_sgo.hydrant_disponibilite (
	code_disponibilite_sgo varchar NOT NULL, -- Code de la disponibilité du PEI dans le SGO
	code_disponibilite_remocra varchar NOT NULL, -- Code la disponibilité du PEI dans REMOCRA
	info_disponibilite_remocra varchar NULL, -- Indication sur la disponibilité REMOCRA
	CONSTRAINT hydrant_disponibilite_pkey PRIMARY KEY (code_disponibilite_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_disponibilite IS 'Correspondance entre la disponibilité d''un point d''eau du SGO et la disponibilité d''un point d''eau dans REMOCRA';

-- Column comments

COMMENT ON COLUMN remocra_sgo.hydrant_disponibilite.code_disponibilite_sgo IS 'Code de la disponibilité du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_disponibilite.code_disponibilite_remocra IS 'Code la disponibilité du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_disponibilite.info_disponibilite_remocra IS 'Indication sur la disponibilité REMOCRA';

CREATE TABLE remocra_sgo.hydrant_nature (
	code_nature_sgo varchar NOT NULL, -- Code de la nature du PEI dans le SGO
	code_nature_remocra varchar NOT NULL, -- Code la nature du PEI dans REMOCRA
	info_nature_remocra varchar NULL, -- Indication sur la nature REMOCRA
	CONSTRAINT hydrant_nature_pkey PRIMARY KEY (code_nature_remocra)
);
COMMENT ON TABLE remocra_sgo.hydrant_nature IS 'Correspondance entre la nature de point d''eau du SGO et la nature de point d''eau dans REMOCRA';

-- Column comments

COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_nature_sgo IS 'Code de la nature du PEI dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_nature_remocra IS 'Code la nature du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.info_nature_remocra IS 'Indication sur la nature REMOCRA';

INSERT INTO remocra_sgo.hydrant_disponibilite (code_disponibilite_sgo,code_disponibilite_remocra,info_disponibilite_remocra) VALUES
  ('','DISPO','PEI disponible'),
  ('','NON_CONFORME','PEI disponible mais présentant des caractéristiques limitant son usage. Faible débit, etc.'),
  ('','INDISPO','PEI non disponible');

INSERT INTO remocra_sgo.hydrant_nature (code_nature_sgo,code_nature_remocra,info_nature_remocra) VALUES
  ('','BI','BI'),
  ('','CHE','Château d''eau'),
  ('','CE','Cours d''eau'),
  ('','EB','Eau brute'),
  ('','PI','PI'),
  ('','PE','Plan d''eau'),
  ('','PA','Point d''aspiration'),
  ('','CI_FIXE','Réserve artificielle'),
  ('','RE','Retenue');

CREATE OR REPLACE VIEW remocra_sgo.hydrant
AS SELECT h.numero AS "NUMERO_POINT_EAU",
    h.numero AS "NOM_POINT_EAU",
    nat.code_nature_sgo AS "NUMERO_TYPE_POINT_EAU",
    NULL::character varying AS "NUMERO_LIEU",
    NULL::integer AS "NUMERO_DANS_RUE",
    NULL::integer AS "PRECISIONS_DANS_RUE",
    NULL::character varying AS "NUMERO_ESCALIER",
    NULL::character varying AS "NUMERO_ETAGE",
    hpibi.pression AS "PRESSION",
    hpibi.debit_max AS "DEBIT",
    NULL::integer AS "DIAMETRE",
    h.date_contr AS "DH_VERIFICATION",
    h.voie AS "ACCES",
    dispo.code_disponibilite_sgo AS "ETAT_POINT_EAU",
    st_x(h.geometrie) AS "COORD_X",
    st_y(h.geometrie) AS "COORD_Y",
    NULL::character varying AS "COORDONNEE_ALPHA",
    NULL::character varying AS "FEUILLE_IGN",
    NULL::character varying AS "NUM_PLAN",
    NULL::character varying AS "ID_RESEAU",
    NULL::timestamp without time zone AS "DH_DEB_INDISPO",
    NULL::timestamp without time zone AS "DH_FIN_INDISPO",
    hpibi.debit AS "DEBIT_1BAR",
    NULL::integer AS "DIAMETRE_CANALISATION",
    NULL::character varying AS "NON_CONFORMITE",
    NULL::integer AS "VOLUME",
    NULL::integer AS "ID_GEO",
    com.insee AS "NUMERO_COMMUNE",
    NULL::character varying AS "ETAT_POINT_EAU_AVANT_INDISPO",
    NULL::character varying AS "OBJET_PT_EAU",
    NULL::bigint AS "CLEGCES"
   FROM hydrant h
     LEFT JOIN hydrant_pibi hpibi ON hpibi.id = h.id
     LEFT JOIN hydrant_pena hpena ON hpena.id = h.id
     JOIN type_hydrant_nature thnat ON thnat.id = h.nature
     JOIN remocra_sgo.hydrant_nature nat ON nat.code_nature_remocra::text = thnat.code::text
     JOIN remocra_sgo.hydrant_disponibilite dispo ON dispo.code_disponibilite_remocra::text = h.dispo_terrestre::text
     JOIN commune com ON com.id = h.commune;

CREATE ROLE systeletl LOGIN
        PASSWORD '<password>'
        NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
      COMMENT ON ROLE remocra IS 'Rôle utilisé par Systel';

REVOKE ALL ON DATABASE postgres FROM systeletl;
REVOKE ALL ON DATABASE template_postgis FROM systeletl;
REVOKE ALL ON DATABASE remocra FROM systeletl;
REVOKE ALL ON DATABASE remocra_ref_pdi FROM systeletl;

grant usage on schema remocra_sgo to systeletl;

grant select on remocra_sgo.hydrant_nature to systeletl;
grant select on remocra_sgo.hydrant_disponibilite to systeletl;
grant select on remocra_sgo.hydrant to systeletl;

grant update (code_nature_sgo) on table remocra_sgo.hydrant_nature TO systeletl;
grant update (code_disponibilite_sgo) on table remocra_sgo.hydrant_disponibilite TO systeletl;
