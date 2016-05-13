BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;


SET search_path = pdi, remocra, pg_catalog, public;

CREATE SEQUENCE pdi.traitement_idtraitement_seq;

ALTER TABLE pdi.traitement ALTER COLUMN idtraitement SET NOT NULL;

ALTER TABLE pdi.traitement ALTER COLUMN idtraitement SET DEFAULT nextval('pdi.traitement_idtraitement_seq'::regclass);

SELECT pg_catalog.setval('pdi.traitement_idtraitement_seq', (select coalesce(max(idtraitement),0) + 1 from pdi.traitement), true);

COMMIT;

