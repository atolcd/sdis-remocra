begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



DELETE FROM remocra.param_conf where cle = 'CITERNE_TOLERANCE_ASSOCIATION_PI_METRES';
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'CITERNE_TOLERANCE_ASSOCIATION_PI_METRES', '500', 'Tolérance de chargement des PI associables aux Citernes, exprimée en mètres');




-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  48,
  'Association de PI aux citernes : tolérance'
);

commit;
