begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



insert into remocra.param_conf (cle, valeur, description) values (
    'WMS_BASE_URL',
    'http://localhost:8080/geoserver',
    'URL de base complète du serveur de cartes (Exemple : http://localhost:8080/geoserver)'
);



-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  50,
  'URL de base du serveur WMS'
);

commit;
