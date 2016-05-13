begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;


update remocra.rci set indice_rothermel=(indice_rothermel-1)*10 where indice_rothermel is not null;


-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  52,
  'Indice Rothermel de 0 à 100 avec un pas de 10'
);

commit;
