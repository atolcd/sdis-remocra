begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;


delete from remocra.param_conf where cle = 'DEFAULT_ORIENTATION_X' or cle = 'DEFAULT_ORIENTATION_Y';
insert into remocra.param_conf (cle, valeur, description) values (
    'DEFAULT_ORIENTATION_X', 'E', 'Orientation par défaut en longitude (saisie des coordonnées) : E ou O');
insert into remocra.param_conf (cle, valeur, description) values (
    'DEFAULT_ORIENTATION_Y', 'N', 'Orientation par défaut en latitude (saisie des coordonnées) : N ou S');


-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  53,
  'Orientations par défaut lors de la saisie des coordonnées (Nord / Est)'
);

commit;
