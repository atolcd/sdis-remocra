SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = remocra, pdi, pg_catalog, public;

BEGIN;


-- Suppresion colonne version de param_conf
ALTER TABLE remocra.param_conf drop column version;





-- Table: pdi.traitement_cc

-- DROP TABLE pdi.traitement_cc;

CREATE TABLE pdi.traitement_cc
(
  idtraitement integer NOT NULL,
  idutilisateur integer NOT NULL,
  CONSTRAINT traitement_cc_pkey PRIMARY KEY (idtraitement, idutilisateur),
  CONSTRAINT fk923581f54675afa4 FOREIGN KEY (idtraitement)
      REFERENCES pdi.traitement (idtraitement) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE pdi.traitement_cc
  OWNER TO postgres;





-- View: pdi.vue_communes

-- DROP VIEW pdi.vue_communes;

CREATE OR REPLACE VIEW pdi.vue_communes AS 
 SELECT allin.id, allin.libelle
   FROM (         SELECT (-1) AS id, 'VAR' AS libelle, NULL::unknown AS tricol
        UNION 
                 SELECT commune.id, commune.nom AS libelle, commune.nom AS tricol
                   FROM remocra.commune WHERE insee like '83%') allin
  ORDER BY allin.tricol NULLS FIRST;

ALTER TABLE pdi.vue_communes
  OWNER TO postgres;




-- Messages pour tous les utilisateurs par défaut
update remocra.utilisateur set message_remocra = true;






COMMIT;
