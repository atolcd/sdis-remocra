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

UPDATE remocra.type_hydrant_nature
SET nom = 'Citerne à l''air libre',
code = 'CI_AIR'
WHERE code = 'CI_FIXE';
INSERT INTO remocra.type_hydrant_nature(code, nom, type_hydrant) VALUES ('CI_ENT', 'Citerne enterrée', 2);

-- Contenu réel du patch fin
--------------------------------------------------

commit;