SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;

-- Ajout de la table zone_competence

CREATE TABLE remocra.zone_competence
(
  id bigserial NOT NULL,
  code character varying,
  geometrie geometry NOT NULL,
  nom character varying,
  CONSTRAINT zone_competence_pkey PRIMARY KEY (id )
);

-- Ajout lien Organisme -> Zone Compétence

ALTER TABLE remocra.organisme ADD COLUMN zone_competence bigint;

-- Insertion d'une valeur par défaut

insert into remocra.zone_competence(id, code, nom, geometrie) values (1, '83-06-13-84-04', '83-06-13-84-04', ST_GeomFromText('POLYGON ((1099333.0 6461708.0, 734605.0 6464842.0, 731803.0 6133292.0, 1096442.0 6130164.0, 1099333.0 6461708.0))', 2154));
SELECT pg_catalog.setval('zone_competence_id_seq', 1, true);

-- Mise à jour de la table organisme

update remocra.organisme set zone_competence = 1;

-- Passage de la FK en "NOT NULL"

ALTER TABLE remocra.organisme ALTER COLUMN zone_competence SET NOT NULL;

COMMIT;

