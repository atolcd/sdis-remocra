begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



-- Pour l'ensemble des SDIS : rien




-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  49,
  'Calcul des coordonnées DFCI des PENA sans coordonnées'
);

commit;
