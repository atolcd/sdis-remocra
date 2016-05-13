BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog, public;

-- Droits
delete from remocra.droit where type_droit IN (select id from remocra.type_droit where code IN ('HYDRANTS_RECEPTION', 'ADRESSES_ALERTE', 'UTILISATEUR_FILTER_ORGANISME_SDIS'));
delete from remocra.type_droit where code IN ('HYDRANTS_RECEPTION', 'ADRESSES_ALERTE', 'UTILISATEUR_FILTER_ORGANISME_SDIS');

-- Thématique cartothèque
insert into remocra.thematique(id, nom, code, version) values (
  6, 'Cartothèque', 'CARTOTHEQUE', 1
);

SELECT pg_catalog.setval('remocra.thematique_id_seq', 7, false);


-- Type permis avis NON CONFORME
insert into remocra.type_permis_avis(id, code, nom, pprif, actif) values (7, 'NON_CONFORME', 'Non conforme', false, true);
SELECT pg_catalog.setval('remocra.type_permis_avis_id_seq', 8, false);


COMMIT;
