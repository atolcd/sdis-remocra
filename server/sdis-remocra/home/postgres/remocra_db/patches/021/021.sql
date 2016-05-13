SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = remocra, pg_catalog, public;

BEGIN;

truncate remocra.utilisateur RESTART IDENTITY cascade;
truncate remocra.organisme RESTART IDENTITY cascade;
truncate remocra.profil_organisme_utilisateur_droit RESTART IDENTITY cascade;
truncate remocra.profil_utilisateur RESTART IDENTITY cascade;
truncate remocra.profil_organisme RESTART IDENTITY cascade;
truncate remocra.type_organisme RESTART IDENTITY cascade;
truncate remocra.droit RESTART IDENTITY cascade;
truncate remocra.profil_droit RESTART IDENTITY cascade;
truncate remocra.type_droit RESTART IDENTITY cascade;

--
-- Data for Name: profil_droit; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (7, 'public', 'CG Base', 1, 'CG-BASE');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (8, 'public', 'DDTM DFCI', 1, 'DDTM-DFCI');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (10, 'public', 'DDTM Permis', 1, 'DDTM-PERMIS');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (11, 'public', 'Maire', 1, 'COM-MAIRE');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (14, 'public', 'Commune DFCI', 1, 'COM-DFCI');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (15, 'public', 'Commune hydrant E1', 1, 'COM-HYDRANT-E1');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (16, 'public', 'Commune hydrant E2', 1, 'COM-HYDRANT-E2');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (17, 'public', 'CT DFCI', 1, 'CT-DFCI');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (18, 'public', 'CT Permis', 1, 'CT-PERMIS');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (19, 'public', 'CT Hydrant E1', 1, 'CT-HYDRANT-E1');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (20, 'public', 'CT Hydrant E2', 1, 'CT-HYDRANT-E2');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (21, 'public', 'CIS Base', 1, 'CIS-BASE');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (22, 'public', 'CIS Hydrant E1', 1, 'CIS-HYDRANT-E1');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (23, 'public', 'CIS Hydrant E2', 1, 'CIS-HYDRANT-E2');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (24, 'public', 'Chef de centre', 1, 'CIS-CHEF');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (25, 'public', 'SDIS Base', 1, 'SDIS-BASE');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (26, 'public', 'Référent DECI', 1, 'SDIS-REF-DECI');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (27, 'public', 'Référent PENA', 1, 'SDIS-REF-PENA');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (28, 'public', 'Référent DFCI', 1, 'SDIS-REF-DFCI');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (29, 'public', 'Administrateur permis', 1, 'SDIS-ADM-PERMIS');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (30, 'public', 'Administrateur application', 1, 'SDIS-ADM-APP');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (31, 'public', 'SDIS Hydrant E1', 1, 'SDIS-HYDRANT-E1');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (32, 'public', 'SDIS Hydrant E2', 1, 'SDIS-HYDRANT-E2');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (33, 'public', 'Préventionniste', 1, 'SDIS-PREV');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (34, 'public', 'CODIS', 1, 'SDIS-CODIS');
INSERT INTO profil_droit (id, feuille_de_style_geo_server, nom, version, code) VALUES (35, 'public', 'Gestionnaire Hydrant', 1, 'GEST-HYDRANT');

SELECT pg_catalog.setval('profil_droit_id_seq', 35, true);

--
-- Data for Name: type_organisme; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_organisme (id, code, nom, actif) VALUES (1, 'REMOCRA', 'Application Remocra', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (2, 'CONSEIL-GENERAL', 'Conseil général', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (3, 'DDTM', 'DDTM', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (4, 'COMMUNE', 'Commune', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (5, 'CT', 'Communauté territoriale', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (6, 'CIS', 'CIS', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (7, 'SDIS', 'SDIS', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (8, 'GEST-HYDRANT', 'Gestionnaire hydrant', true);

SELECT pg_catalog.setval('type_organisme_id_seq', 8, true);

--
-- Data for Name: profil_organisme; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (1, 'Conseil général', 2, true, 'CONSEIL-GENERAL');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (2, 'DDTM', 3, true, 'DDTM');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (3, 'Commune étape 1', 4, true, 'COM-ETAPE-1');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (4, 'Commune étape 2', 4, true, 'COM-ETAPE-2');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (5, 'Communauté territoriale étape 1', 5, true, 'CT-ETAPE-1');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (6, 'Communauté territoriale étape 2', 5, true, 'CT-ETAPE-2');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (12, 'CIS étape 1', 6, true, 'CIS-ETAPE-1');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (13, 'CIS étape 2', 6, true, 'CIS-ETAPE-2');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (14, 'SDIS', 7, true, 'SDIS');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (15, 'Gestionnaire hydrant', 8, true, 'GEST-HYDRANT');

SELECT pg_catalog.setval('profil_organisme_id_seq', 15, true);

--
-- Data for Name: profil_utilisateur; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (11, 'CT Hydrant', 5, true, 'CT-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (1, 'CG Base', 2, true, 'CG-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (2, 'DDTM DFCI', 3, true, 'DDTM-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (3, 'DDTM Permis', 3, true, 'DDTM-PERMIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (4, 'Maire', 4, true, 'COM-MAIRE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (5, 'Commune DFCI', 4, true, 'COM-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (6, 'Commune Hydrant', 4, true, 'COM-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (7, 'CT DFCI', 5, true, 'CT-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (8, 'CT Permis', 5, true, 'CT-PERMIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (12, 'CIS Base', 6, true, 'CIS-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (13, 'CIS Hydrant', 6, true, 'CIS-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (14, 'Chef de centre', 6, true, 'CIS-CHEF');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (15, 'SDIS Base', 7, true, 'SDIS-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (16, 'Référent DECI', 7, true, 'SDIS-REF-DECI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (17, 'Référent PENA', 7, true, 'SDIS-REF-PENA');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (18, 'Référent DFCI', 7, true, 'SDIS-REF-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (19, 'Administrateur permis', 7, true, 'SDIS-ADM-PERMIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (20, 'Administrateur application', 7, true, 'SDIS-ADM-APP');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (21, 'SDIS Hydrant étape 1', 7, true, 'SDIS-HYDRANT-E1');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (22, 'Préventionniste', 7, true, 'SDIS-PREV');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (23, 'CODIS', 7, true, 'SDIS-CODIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (25, 'SDIS Hydrant étape 2', 7, true, 'SDIS-HYDRANT-E2');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (26, 'Gestionnaire hydrant', 8, true, 'GEST-HYDRANT');

SELECT pg_catalog.setval('profil_utilisateur_id_seq', 26, true);

--
-- Data for Name: type_droit; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_droit (id, code, description, nom, version) VALUES (1, 'UTILISATEUR_FILTER_ALL', 'Droit sur tous les utilisateurs', 'utilisateur.filter.*', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (2, 'UTILISATEUR_FILTER_ORGANISME_UTILISATEUR', 'Droit sur les utilisateur de l''organisme de l''utilisateur', 'utilisateur.filter.organisme.utilisateur', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (3, 'REFERENTIELS', 'Droit sur les référentiels', 'referentiels', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (4, 'ADRESSES', 'Droit sur les adresses', 'adresses', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (5, 'DFCI', 'Droit sur dfci', 'dfci', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (6, 'HYDRANTS', 'Droit la thématique hydrants', 'hydrants', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (7, 'HYDRANTS_RECONNAISSANCE', 'Droit sur la reconnaissance des hydrants', 'hydrants.reconnaissance', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (8, 'HYDRANTS_CONTROLE', 'Droit sur le contrôle des hydrants', 'hydrants.controle', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (9, 'HYDRANTS_NUMEROTATION', 'Droit sur la numérotation des hydrants', 'hydrants.numerotation', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (10, 'HYDRANTS_MCO', 'Droit sur les module MCO des hydrants', 'hydrants.mco', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (11, 'TOURNEE', 'Droit sur les tournées', 'tournee', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (12, 'TOURNEE_RESERVATION', 'Droit sur la réservation des tournées', 'tournee.reservation', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (13, 'HYDRANTS_PRESCRIT', 'Droit sur les hydrants prescrits', 'hydrants.prescrit', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (14, 'HYDRANTS_TRAITEMENT', 'Droit sur les traitements hydrants', 'hydrants.traitement', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (15, 'PERMIS', 'Droit sur les permis', 'permis', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (16, 'PERMIS_DOCUMENTS', 'Droit sur les documents des permis', 'permis.documents', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (17, 'PERMIS_TRAITEMENT', 'Droit sur les traitements des permis', 'permis.traitement', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (18, 'RISQUES_KML', 'Droit sur les risques express', 'risques.kml', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (19, 'DOCUMENTS', 'Accès à la banque de document (blocs)', 'documents', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (20, 'DEPOT_DELIB', 'Droit sur le dépôt de délibérations', 'depot.delib', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (21, 'DEPOT_DECLAHYDRANT', 'Droit sur le dépôt de dossiers de déclaration d''hydrants', 'depot.declahydrant', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (22, 'DEPOT_RECEPTRAVAUX', 'Droit sur les dépôt de dossiers réception de travaux', 'depot.receptravaux', 1);

-- INSERT INTO type_droit (id, code, description, nom, version) VALUES (2, 'UTILISATEUR_FILTER_ORGANISME_SDIS', 'Droit sur les utilisateurs du SDIS', 'utilisateur.filter.organisme.sdis', 1);
-- INSERT INTO type_droit (id, code, description, nom, version) VALUES (7, 'ADRESSES_ALERTE', 'Droit sur les alertes d''adresse', 'adresses.alerte', 1);
-- INSERT INTO type_droit (id, code, description, nom, version) VALUES (14, 'HYDRANTS_BLOCDOCUMENTS', 'Accès à la banque de document hydrants', 'hydrants.blocdocuments', 1);
-- INSERT INTO type_droit (id, code, description, nom, version) VALUES (20, 'HYDRANTS_RECEPTION', 'Droit sur la réception des hydrants', 'hydrants.reception', 1);



SELECT pg_catalog.setval('type_droit_id_seq', 30, true);

-- sp   = 67  - nom ~ '^SP *'
-- prev = 20  - nom ~ '^PREV *'
-- com  = 153 - nom ~ '^83\d{3}$'
-- var  = 1 (conseil général, DDTM, DDE, SDIS)


-- insertion des profil_utilisateur_droit
insert into remocra.profil_organisme_utilisateur_droit (profil_utilisateur, profil_organisme, profil_droit)
select u.id, o.id, coalesce(d1.id, d2.id)
from remocra.profil_utilisateur u 
join remocra.profil_organisme o using (type_organisme)
left join remocra.profil_droit d1 on (d1.code = u.code)
left join remocra.profil_droit d2 on (d2.code = u.code || '-E' || substring(o.code from '.$'))
order by u.id, o.id;


-- insertion des 3 organismes départementaux (VAR)
insert into remocra.organisme (profil_organisme, type_organisme, zone_competence, nom, code)
select po.id, tyo.id, zc.id, tyo.nom, tyo.code
from remocra.type_organisme tyo
join remocra.profil_organisme po using (code)
cross join remocra.zone_competence zc
where tyo.code IN( 'CONSEIL-GENERAL' , 'DDTM', 'SDIS')
and zc.code = 'VAR';

-- insertion des organismes "communes"
insert into remocra.organisme (profil_organisme, type_organisme, zone_competence, nom, code)
select po.id, tyo.id, zc.id, c.nom, 'COM-' || c.insee
from remocra.zone_competence zc
inner join remocra.commune c on (zc.code = c.insee)
cross join remocra.type_organisme tyo 
cross join remocra.profil_organisme po
where zc.nom like '83___'
and tyo.code = 'COMMUNE'
and po.code = 'COM-ETAPE-1'
order by zc.code;

-- insertion des organismes "cis"
insert into remocra.organisme (profil_organisme, type_organisme, zone_competence, nom, code)
select po.id, tyo.id, zc.id, zc.nom, zc.nom
from remocra.zone_competence zc
cross join remocra.type_organisme tyo 
cross join remocra.profil_organisme po
where zc.nom ~ '^SP *'
and tyo.code = 'CIS'
and po.code = 'CIS-ETAPE-1'
order by zc.code;

-- insertion d'un utilisateur par couple organisme/profil_utilisateur (dont un "sdis-adm-app / remocra" qui a tous les droits)
insert into remocra.utilisateur(identifiant, nom, prenom, password, salt, organisme, profil_utilisateur, email)
select lower(login) as identifiant, initcap(replace(login, '-', ' ')) as nom, '' as prenom,  
	md5('remocra' || '{' || md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || login) || '}') as pwd, 
	md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || login) as salt, 
	organisme, profil_utilisateur, 'cva@atolcd.com' as email
from (
select replace(o.code || substring(pu.code from position('-' in pu.code)), ' ', '-') as login, o.id as organisme, pu.id as profil_utilisateur
from organisme o
join profil_utilisateur pu using (type_organisme)) a;


-- Modification du comportement par défaut
alter table remocra.droit ALTER column droit_create SET DEFAULT false;
alter table remocra.droit ALTER column droit_delete SET DEFAULT false;
alter table remocra.droit ALTER column droit_update SET DEFAULT false;
alter table remocra.droit ALTER column droit_read SET DEFAULT false;

-- insertion des droits --------------------------

-- Ligne complète
insert into remocra.droit (type_droit, profil_droit, droit_read)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'REFERENTIELS';

insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'ADRESSES'
and prd.code != 'GEST-HYDRANT';

insert into remocra.droit (type_droit, profil_droit, droit_read)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'DFCI'
and prd.code != 'GEST-HYDRANT';

insert into remocra.droit (type_droit, profil_droit, droit_read)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'DOCUMENTS';

-- Hydrants 

insert into remocra.droit (type_droit, profil_droit, droit_read)
select td.id, prd.id, true
from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS'
and prd.code IN ('COM-MAIRE', 'COM-HYDRANT-E1', 'CT-HYDRANT-E1', 'CIS-HYDRANT-E2', 'CIS-CHEF', 'SDIS-ADM-PERMIS', 'SDIS-HYDRANT-E2', 'SDIS-PREV', 'GEST-HYDRANT');

insert into remocra.droit (type_droit, profil_droit, droit_create, droit_read)
select td.id, prd.id, true, true
from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS'
and prd.code IN ('COM-HYDRANT-E2', 'CT-HYDRANT-E2', 'CIS-HYDRANT-E1', 'SDIS-HYDRANT-E1');

insert into remocra.droit (type_droit, profil_droit, droit_create, droit_delete, droit_read)
select td.id, prd.id, true, true, true
from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS'
and prd.code IN ('SDIS-REF-DECI', 'SDIS-REF-PENA');

-- Hydrant reconnaissance
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS_RECONNAISSANCE'
and prd.code IN ('CIS-HYDRANT-E2', 'SDIS-HYDRANT-E2');

-- Hydrant contrôle
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS_CONTROLE'
and prd.code IN ('COM-HYDRANT-E2', 'CT-HYDRANT-E2', 'CIS-HYDRANT-E1', 'SDIS-HYDRANT-E1');

-- Hydrant numérotation
insert into remocra.droit (type_droit, profil_droit, droit_create, droit_read)
select td.id, prd.id, true, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS_NUMEROTATION'
and prd.code IN ('SDIS-REF-DECI');

-- Hydrant mco
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS_MCO'
and prd.code IN ('COM-HYDRANT-E2', 'CT-HYDRANT-E2', 'SDIS-REF-DECI');

-- Tournee
insert into remocra.droit (type_droit, profil_droit, droit_create, droit_read)
select td.id, prd.id, true, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'TOURNEE'
and prd.code IN ('COM-HYDRANT-E2', 'CT-HYDRANT-E2', 'CIS-HYDRANT-E1', 'CIS-HYDRANT-E2', 'SDIS-HYDRANT-E1', 'SDIS-HYDRANT-E2');

-- Hydrant Prescrit
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS_PRESCRIT'
and prd.code IN ('SDIS-REF-DECI', 'SDIS-PREV');

-- Hydrant traitement
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'HYDRANTS_TRAITEMENT'
and prd.code IN ('COM-MAIRE', 'COM-HYDRANT-E2', 'CT-HYDRANT-E2', 'SDIS-REF-DECI');

-- Permis
insert into remocra.droit (type_droit, profil_droit, droit_read)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'PERMIS'
and prd.code IN ('DDTM-PERMIS', 'COM-MAIRE', 'CT-PERMIS');

insert into remocra.droit (type_droit, profil_droit, droit_read, droit_create)
select td.id, prd.id, true, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'PERMIS'
and prd.code IN ('SDIS-ADM-PERMIS');

-- Permis document
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'PERMIS_DOCUMENTS'
and prd.code IN ('DDTM-PERMIS', 'COM-MAIRE', 'CT-PERMIS');

-- Permis traitement
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'PERMIS_TRAITEMENT'
and prd.code IN ('DDTM-PERMIS', 'COM-MAIRE', 'SDIS-ADM-PERMIS');

-- Risque.kml
insert into remocra.droit (type_droit, profil_droit, droit_read, droit_create)
select td.id, prd.id, true, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'RISQUES_KML'
and prd.code IN ('SDIS-CODIS');

-- depot delib
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'DEPOT_DELIB'
and prd.code IN ('COM-MAIRE');

-- depot déclaration hydrant
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'DEPOT_DECLAHYDRANT'
and prd.code IN ('COM-MAIRE', 'COM-HYDRANT-E1', 'COM-HYDRANT-E2', 'CT-HYDRANT-E1', 'CT-HYDRANT-E2', 'GEST-HYDRANT');

-- depot réception travaux
insert into remocra.droit (type_droit, profil_droit, droit_create)
select td.id, prd.id, true from remocra.type_droit td 
cross join remocra.profil_droit prd
where td.code = 'DEPOT_RECEPTRAVAUX'
and prd.code IN ('CG-BASE','DDTM-DFCI','COM-DFCI','CT-DFCI','SDIS-REF-DFCI');

-- insertion de tous les droits à sdis-admin-application
insert into remocra.droit (droit_create, droit_delete, droit_update, droit_read, profil_droit, type_droit)
select true, true, true, true, p.id, t.id from remocra.type_droit t 
cross join remocra.profil_droit p
where p.code = 'SDIS-ADM-APP'
and (p.id, t.id) not in (select profil_droit, type_droit from remocra.droit);

update remocra.droit set droit_create = true, droit_delete = true , droit_update = true, droit_read = true where profil_droit in (select id from remocra.profil_droit where code = 'SDIS-ADM-APP');

COMMIT;
