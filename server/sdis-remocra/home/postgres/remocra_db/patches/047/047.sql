begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;




update pdi.modele_traitement set ref_nom = 'export_csv' where nom='Départs de feu' and ref_chemin='/demandes/rci' and ref_nom = 'export_xls';




-- Application du patch
insert into remocra.suivi_patches(numero, description) values(
  47,
  'Export des départs : export csv dans le traitement'
);

commit;
