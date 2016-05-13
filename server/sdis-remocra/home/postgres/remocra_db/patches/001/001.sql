SET search_path = remocra, pg_catalog;

TRUNCATE remocra.utilisateur CASCADE;
TRUNCATE remocra.organisme CASCADE;

ALTER TABLE organisme_com
	DROP CONSTRAINT fk23af2915b6f1509a;

ALTER TABLE organisme_dde
	DROP CONSTRAINT fk23af2b79b6f1509a;

ALTER TABLE organisme_ddt
	DROP CONSTRAINT fk23af2b88b6f1509a;

ALTER TABLE organisme_rem
	DROP CONSTRAINT fk23af602eb6f1509a;

ALTER TABLE organisme_sdi
	DROP CONSTRAINT fk23af63ccb6f1509a;

CREATE SEQUENCE droit_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE SEQUENCE profil_droit_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE SEQUENCE profil_organisme_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE SEQUENCE profil_organisme_utilisateur_droit_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE SEQUENCE profil_utilisateur_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE SEQUENCE type_droit_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE SEQUENCE type_organisme_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE TABLE droit (
	id bigint DEFAULT nextval('droit_id_seq'::regclass) NOT NULL,
	droit_create boolean NOT NULL,
	droit_delete boolean NOT NULL,
	droit_read boolean NOT NULL,
	droit_update boolean NOT NULL,
	version integer DEFAULT 1,
	profil_droit bigint NOT NULL,
	type_droit bigint NOT NULL
);

CREATE TABLE profil_droit (
	id bigint DEFAULT nextval('profil_droit_id_seq'::regclass) NOT NULL,
	libelle character varying,
	feuille_de_style_geo_server character varying,
	version integer DEFAULT 1
);

CREATE TABLE profil_organisme (
	id bigint DEFAULT nextval('profil_organisme_id_seq'::regclass) NOT NULL,
	libelle character varying,
	version integer DEFAULT 1,
	type_organisme bigint NOT NULL
);

CREATE TABLE profil_organisme_utilisateur_droit (
	id bigint DEFAULT nextval('profil_organisme_utilisateur_droit_id_seq'::regclass) NOT NULL,
	version integer DEFAULT 1,
	profil_droit bigint NOT NULL,
	profil_organisme bigint NOT NULL,
	profil_utilisateur bigint NOT NULL
);

CREATE TABLE profil_utilisateur (
	id bigint DEFAULT nextval('profil_utilisateur_id_seq'::regclass) NOT NULL,
	libelle character varying,
	version integer DEFAULT 1,
	type_organisme bigint NOT NULL
);

CREATE TABLE type_droit (
	id bigint DEFAULT nextval('type_droit_id_seq'::regclass) NOT NULL,
	code character varying,
	description character varying,
	nom character varying,
	version integer
);

CREATE TABLE type_organisme (
	id bigint DEFAULT nextval('type_organisme_id_seq'::regclass) NOT NULL,
	code character varying,
	nom character varying
);

ALTER TABLE organisme
	ADD COLUMN profil_organisme bigint NOT NULL,
	ADD COLUMN type_organisme bigint NOT NULL;

ALTER TABLE utilisateur
	DROP COLUMN profil,
	ADD COLUMN profil_utilisateur bigint NOT NULL;

ALTER TABLE droit
	ADD CONSTRAINT droit_pkey PRIMARY KEY (id);

ALTER TABLE profil_droit
	ADD CONSTRAINT profil_droit_pkey PRIMARY KEY (id);

ALTER TABLE profil_organisme
	ADD CONSTRAINT profil_organisme_pkey PRIMARY KEY (id);

ALTER TABLE profil_organisme_utilisateur_droit
	ADD CONSTRAINT profil_organisme_utilisateur_droit_pkey PRIMARY KEY (id);

ALTER TABLE profil_utilisateur
	ADD CONSTRAINT profil_utilisateur_pkey PRIMARY KEY (id);

ALTER TABLE type_droit
	ADD CONSTRAINT type_droit_pkey PRIMARY KEY (id);

ALTER TABLE type_organisme
	ADD CONSTRAINT type_organisme_pkey PRIMARY KEY (id);

ALTER TABLE droit
	ADD CONSTRAINT fk5b6ae8c12db1a65 FOREIGN KEY (type_droit) REFERENCES type_droit(id);

ALTER TABLE droit
	ADD CONSTRAINT fk5b6ae8c3723a725 FOREIGN KEY (profil_droit) REFERENCES profil_droit(id) ON DELETE CASCADE;

ALTER TABLE organisme
	ADD CONSTRAINT fk805999d374ebaf33 FOREIGN KEY (profil_organisme) REFERENCES profil_organisme(id);

ALTER TABLE organisme
	ADD CONSTRAINT fk805999d3f5378273 FOREIGN KEY (type_organisme) REFERENCES type_organisme(id);

ALTER TABLE profil_organisme
	ADD CONSTRAINT fkcad83790f5378273 FOREIGN KEY (type_organisme) REFERENCES type_organisme(id);

ALTER TABLE profil_organisme_utilisateur_droit
	ADD CONSTRAINT fkcdd9b3e12343353 FOREIGN KEY (profil_utilisateur) REFERENCES profil_utilisateur(id);

ALTER TABLE profil_organisme_utilisateur_droit
	ADD CONSTRAINT fkcdd9b3e13723a725 FOREIGN KEY (profil_droit) REFERENCES profil_droit(id);

ALTER TABLE profil_organisme_utilisateur_droit
	ADD CONSTRAINT fkcdd9b3e174ebaf33 FOREIGN KEY (profil_organisme) REFERENCES profil_organisme(id);

ALTER TABLE profil_utilisateur
	ADD CONSTRAINT fk82645600f5378273 FOREIGN KEY (type_organisme) REFERENCES type_organisme(id);

ALTER TABLE type_droit
	ADD CONSTRAINT type_droit_code_key UNIQUE (code);

ALTER TABLE type_organisme
	ADD CONSTRAINT type_organisme_code_key UNIQUE (code);

ALTER TABLE utilisateur
	ADD CONSTRAINT fkdd1633832343353 FOREIGN KEY (profil_utilisateur) REFERENCES profil_utilisateur(id);


INSERT INTO type_droit(id, code, nom, description, version) VALUES (1, 'UTILISATEUR_FILTER_ALL', 'utilisateur.filter.*', 'Droit sur tout les utilisateurs', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (2, 'UTILISATEUR_FILTER_ORGANISME_SDIS', 'utilisateur.filter.organisme.sdis', 'Droit sur les utilisateurs du SDIS', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (3, 'UTILISATEUR_FILTER_ORGANISME_UTILISATEUR', 'utilisateur.filter.organisme.utilisateur', 'Droit sur les utilisateur de l''organisme de l''utilisateur', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (4, 'REFERENTIELS', 'referentiels', 'Droit sur les référentiels', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (5, 'ADRESSES', 'adresses', 'Droit sur les adresses', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (6, 'ADRESSES_DELIB', 'adresses.delib', 'Droit sur les adresse delib', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (7, 'ADRESSES_ALERTE', 'adresses.alerte', 'Droit sur les alertes d''adresse', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (8, 'DFCI', 'dfci', 'Droit sur dfci', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (9, 'POINTSEAU', 'pointseau', 'Droit sur les points d''eau', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (10, 'PERMIS', 'permis', 'Droit sur les permis', 1);
INSERT INTO type_droit(id, code, nom, description, version) VALUES (11, 'RISQUES', 'risques', 'Droit sur les risques', 1);

SELECT pg_catalog.setval('type_droit_id_seq', 11, true);
	
INSERT INTO type_organisme (id, code, nom) VALUES (1, 'REMOCRA', 'Application Remocra');
INSERT INTO type_organisme (id, code, nom) VALUES (2, 'COMMUNE', 'Commune');
INSERT INTO type_organisme (id, code, nom) VALUES (3, 'DDE', 'DDE');
INSERT INTO type_organisme (id, code, nom) VALUES (4, 'DDTM', 'DDTM');
INSERT INTO type_organisme (id, code, nom) VALUES (5, 'SDIS', 'SDIS');

INSERT INTO profil_organisme (id, type_organisme, libelle, version) VALUES (1, 2, 'Commune Etape 1', 1);
INSERT INTO profil_organisme (id, type_organisme, libelle, version) VALUES (2, 2, 'Commune Etape 2', 1);
INSERT INTO profil_organisme (id, type_organisme, libelle, version) VALUES (3, 5, 'SDIS Beta', 1);
INSERT INTO profil_organisme (id, type_organisme, libelle, version) VALUES (4, 3, 'DDE', 1);
INSERT INTO profil_organisme (id, type_organisme, libelle, version) VALUES (5, 4, 'DDTM', 1);
INSERT INTO profil_organisme (id, type_organisme, libelle, version) VALUES (6, 1, 'REMOCRA', 1);

INSERT INTO organisme (id, actif, code, email_contact, nom, type_organisme, profil_organisme, version) VALUES (1, true, 'REMOCRA', 'cva@atolcd.com', 'Application Remocra', 1, 6, 1);
INSERT INTO organisme (id, actif, code, email_contact, nom, type_organisme, profil_organisme, version) VALUES (2, true, 'SDIS', 'cva@atolcd.com', 'SDIS 83', 5, 3, 1);
INSERT INTO organisme (id, actif, code, email_contact, nom, type_organisme, profil_organisme, version) VALUES (3, true, 'DDE', 'cva@atolcd.com', 'DDE', 3, 4, 1);
INSERT INTO organisme (id, actif, code, email_contact, nom, type_organisme, profil_organisme, version) VALUES (4, true, 'DDTM', 'cva@atolcd.com', 'DDTM', 4, 5, 1);
INSERT INTO organisme (id, actif, code, email_contact, nom, type_organisme, profil_organisme, version) VALUES (5, true, 'COMTOULON', 'cva@atolcd.com', 'Commune Toulon', 2, 1, 1);
	
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (1, 2, 'Maire de la commune', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (2, 2, 'Secretaire de mairie', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (3, 2, 'Agent voie publique', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (4, 5, 'Capitaine de brigade', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (5, 5, 'Pompier', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (6, 3, 'Agent de la DDE', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (7, 4, 'Agent de la DDTM', 1);
INSERT INTO profil_utilisateur (id, type_organisme, libelle, version) VALUES (8, 1, 'Application REMOCRA', 1);



INSERT INTO profil_droit (id, libelle, feuille_de_style_geo_server, version) VALUES (1, 'Tous les droits', 'public',  1);
INSERT INTO profil_droit (id, libelle, feuille_de_style_geo_server, version) VALUES (2, 'Maire de la commune en étape 1', 'commune', 1);
INSERT INTO profil_droit (id, libelle, feuille_de_style_geo_server, version) VALUES (3, 'Maire de la commune en étape 2', 'commune', 1);
INSERT INTO profil_droit (id, libelle, feuille_de_style_geo_server, version) VALUES (4, 'Sdis Admin', 'sdis', 1);
INSERT INTO profil_droit (id, libelle, feuille_de_style_geo_server, version) VALUES (5, 'Sdis Utilisateur', 'sdis', 1);

-- Tout les droits
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (1, 1 , (SELECT id from remocra.type_droit where code = 'UTILISATEUR_FILTER_ALL'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (2, 1 , (SELECT id from remocra.type_droit where code = 'UTILISATEUR_FILTER_ORGANISME_SDIS'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (3, 1 , (SELECT id from remocra.type_droit where code = 'REFERENTIELS'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (4, 1 , (SELECT id from remocra.type_droit where code = 'ADRESSES'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (5, 1 , (SELECT id from remocra.type_droit where code = 'ADRESSES_DELIB'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (6, 1 , (SELECT id from remocra.type_droit where code = 'ADRESSES_ALERTE'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (7, 1 , (SELECT id from remocra.type_droit where code = 'DFCI'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (8, 1 , (SELECT id from remocra.type_droit where code = 'POINTSEAU'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (9, 1 , (SELECT id from remocra.type_droit where code = 'PERMIS'), true, true ,true, true);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (10, 1 , (SELECT id from remocra.type_droit where code = 'RISQUES'), true, true ,true, true);

-- Commune
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (11, 2, (SELECT id from remocra.type_droit where code = 'REFERENTIELS'), false, true ,false, false);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (12, 2, (SELECT id from remocra.type_droit where code = 'ADRESSES_DELIB'), true, false ,false, false);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (13, 2, (SELECT id from remocra.type_droit where code = 'ADRESSES_ALERTE'), true, true ,false, false);

-- Sdis Admin
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (14, 4, (SELECT id from remocra.type_droit where code = 'REFERENTIELS'), false, true ,false, false);
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (15, 4, (SELECT id from remocra.type_droit where code = 'ADRESSES_ALERTE'), true, true ,false, false);

-- Sdis Utilisateur
INSERT INTO droit (id, profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (16, 5, (SELECT id from remocra.type_droit where code = 'REFERENTIELS'), false, true ,false, false);

-- Profils Communes 
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (1, 1, 1, 2 );
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (2, 2, 1, 2 );
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (3, 3, 1, 2 );

INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (4, 1, 2, 2 );
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (5, 2, 2, 2 );
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (6, 3, 2, 2);

--Profils SDIS
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (7, 4, 3, 4);
INSERT INTO profil_organisme_utilisateur_droit(id, profil_utilisateur, profil_organisme, profil_droit) VALUES (8, 5, 3, 5);


INSERT INTO remocra.utilisateur(identifiant, email, nom, prenom, telephone, message_remocra, password, salt, organisme, profil_utilisateur)
       VALUES ('remocra', 'kettle@atolcd.com', 'Remocra', 'SystÃ¨me', '', false, 'a94b424685f4d0043d2e1bedd4bfbc23', 'uh0i88k5', (SELECT id from remocra.organisme where code = 'REMOCRA'), 8);

INSERT INTO remocra.utilisateur(identifiant, email, nom, prenom, telephone, message_remocra, password, salt, organisme, profil_utilisateur)
       VALUES ('admin', 'cva@atolcd.com', 'Denice', 'Brice', '(+33)3 80 18 18 18', false, 'a94b424685f4d0043d2e1bedd4bfbc23', 'uh0i88k5', (SELECT id from remocra.organisme where code = 'SDIS'), 4);

INSERT INTO remocra.utilisateur(identifiant, email, nom, prenom, telephone, message_remocra, password, salt, organisme, profil_utilisateur)
       VALUES ('pompier', 'cva@atolcd.com', 'Dujardin', 'Jean', '(+33)3 80 18 18 18', false, 'a94b424685f4d0043d2e1bedd4bfbc23', 'uh0i88k5', (SELECT id from remocra.organisme where code = 'SDIS'), 5);

INSERT INTO remocra.utilisateur(identifiant, email, nom, prenom, telephone, message_remocra, password, salt, organisme, profil_utilisateur)
       VALUES ('dde', 'cva@atolcd.com', 'Luke', 'Lucky', '(+33)3 80 18 18 18',false, 'a94b424685f4d0043d2e1bedd4bfbc23', 'uh0i88k5', (SELECT id from remocra.organisme where code = 'DDE'), 6);

INSERT INTO remocra.utilisateur(identifiant, email, nom, prenom, telephone, message_remocra, password, salt, organisme, profil_utilisateur)
       VALUES ('ddtm', 'cva@atolcd.com', 'Valentin', 'Georges', '(+33)3 80 18 18 18', false, 'a94b424685f4d0043d2e1bedd4bfbc23', 'uh0i88k5', (SELECT id from remocra.organisme where code = 'DDTM'), 7);

INSERT INTO remocra.utilisateur(identifiant, email, nom, prenom, telephone, message_remocra, password, salt, organisme, profil_utilisateur)
       VALUES ('toulon', 'cva@atolcd.com', 'Jean', 'Jean', '(+33)2 34 56 78 96', false, 'a94b424685f4d0043d2e1bedd4bfbc23', 'uh0i88k5', (SELECT id from remocra.organisme where code = 'COMTOULON'), 1);


SELECT setval('remocra.type_organisme_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.type_organisme) AS compteur;
SELECT setval('remocra.profil_organisme_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.profil_organisme) AS compteur;
SELECT setval('remocra.organisme_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.organisme) AS compteur;
SELECT setval('remocra.profil_utilisateur_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.profil_utilisateur) AS compteur;
SELECT setval('remocra.profil_droit_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.profil_droit) AS compteur;
SELECT setval('remocra.droit_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.droit) AS compteur;
SELECT setval('remocra.profil_organisme_utilisateur_droit_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.profil_organisme_utilisateur_droit) AS compteur;
SELECT setval('remocra.utilisateur_id_seq',id,false) FROM (SELECT max(id)+1 AS id FROM remocra.utilisateur) AS compteur;

