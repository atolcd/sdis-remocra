BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog;

delete from remocra.droit where type_droit = (select id from remocra.type_droit where code = 'RISQUES');
delete from remocra.type_droit where code = 'RISQUES';

insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_PRESCRIT', 'Droit sur les hydrants prescrits', 'hydrants.prescrit', 1);
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_TRAITEMENT', 'Droit sur les traitements hydrants', 'hydrants.traitement', 1);
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_RECEPTION', 'Droit sur la réception des hydrants', 'hydrants.reception', 1);
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_RECONNAISSANCE', 'Droit sur la reconnaissance des hydrants', 'hydrants.reconnaissance', 1);
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_CONTROLE', 'Droit sur le contrôle des hydrants', 'hydrants.controle', 1);
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_NUMEROTATION', 'Droit sur la numérotation des hydrants', 'hydrants.numerotation', 1);
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_MCO', 'Droit sur les module MCO des hydrants', 'hydrants.mco', 1);
insert into remocra.type_droit(code, description, nom, version) values ('TOURNEE', 'Droit sur les tournées', 'tournee', 1);
insert into remocra.type_droit(code, description, nom, version) values ('TOURNEE_RESERVATION', 'Droit sur la réservation des tournées', 'tournee.reservation', 1);
insert into remocra.type_droit(code, description, nom, version) values ('PERMIS_TRAITEMENT', 'Droit sur les traitements des permis', 'permis.traitement', 1);
insert into remocra.type_droit(code, description, nom, version) values ('RISQUES_KML', 'Droit sur les risques express', 'risques.kml', 1);

commit;