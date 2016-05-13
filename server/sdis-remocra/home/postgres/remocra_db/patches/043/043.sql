begin;

-- Nouveau droit Cartographies et ajout au profil admin

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog;

insert into remocra.type_droit(code, description, nom, version) values ('CARTOGRAPHIES', 'Droit sur la réalisation de cartographies', 'cartographies', 1);

insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td
cross join remocra.profil_droit prd
where td.code = 'CARTOGRAPHIES'
and prd.code IN ('SDIS-ADM-APP');

commit;