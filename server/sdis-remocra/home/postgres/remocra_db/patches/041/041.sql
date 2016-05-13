BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, tracabilite, pdi, public, pg_catalog;

-- Thématique RCI
insert into thematique(id, actif, code, nom) values (7, true, 'RCI', 'Recherche des Causes Incendie');
select setval('remocra.thematique_id_seq',id,false) from (select max(id)+1 as id from thematique) as compteur;


-- Droits et profils
insert into type_droit(code, description, nom, version) values ('RCI', 'Droit sur le module RCI', 'rci', 1);
select setval('remocra.type_droit_id_seq',id,false) from (select max(id)+1 as id from type_droit) as compteur;

insert into type_organisme(code, nom) values ('GENDARMERIE', 'Gendarmerie');
insert into type_organisme(code, nom) values ('POLICE', 'Police');

insert into profil_organisme(code, nom, type_organisme) values ('GENDARMERIE', 'Gendarmerie', (select id from type_organisme where code='GENDARMERIE'));
insert into profil_organisme(code, nom, type_organisme) values ('POLICE', 'Police', (select id from type_organisme where code='POLICE'));

insert into profil_utilisateur(code, nom, type_organisme) values ('GENDARME', 'Gendarme', (select id from type_organisme where code='GENDARMERIE'));
insert into profil_utilisateur(code, nom, type_organisme) values ('POLICIER', 'Policier', (select id from type_organisme where code='POLICE'));

insert into profil_droit(code, nom, feuille_de_style_geo_server) values('GENDARME', 'Gendarme', 'public');
insert into profil_droit(code, nom, feuille_de_style_geo_server) values('POLICIER', 'Policier', 'public');

insert into profil_organisme_utilisateur_droit(profil_organisme, profil_utilisateur, profil_droit) values(
    (select id from profil_organisme where code ='GENDARMERIE'),
    (select id from profil_utilisateur where code ='GENDARME'),
    (select id from profil_droit where code ='GENDARME')
);

insert into profil_organisme_utilisateur_droit(profil_organisme, profil_utilisateur, profil_droit) values(
    (select id from profil_organisme where code ='POLICE'),
    (select id from profil_utilisateur where code ='POLICIER'),
    (select id from profil_droit where code ='POLICIER')
);

-- Reprise des droits du profil Consultation
insert into droit(droit_create, droit_read, droit_update, droit_delete, profil_droit, type_droit)
    select droit_create, droit_read, droit_update, droit_delete,
    (select id from profil_droit where code='GENDARME'), type_droit
    from droit where profil_droit = (select id from profil_droit where code='CONSULTATION');
    
insert into droit(droit_create, droit_read, droit_update, droit_delete, profil_droit, type_droit)
    select droit_create, droit_read, droit_update, droit_delete,
    (select id from profil_droit where code='POLICIER'), type_droit
    from droit where profil_droit = (select id from profil_droit where code='CONSULTATION');

-- Ajout du droit RCI.C aux profils Gendarme, Pocilier et Administrateur applicatif
insert into droit(droit_create, droit_read, droit_update, droit_delete, profil_droit, type_droit) values (
    true, false, false, false,
    (select id from profil_droit where code='GENDARME'),
    (select id from type_droit where code='RCI')
);
insert into droit(droit_create, droit_read, droit_update, droit_delete, profil_droit, type_droit) values (
    true, false, false, false,
    (select id from profil_droit where code='POLICIER'),
    (select id from type_droit where code='RCI')
);
insert into droit(droit_create, droit_read, droit_update, droit_delete, profil_droit, type_droit) values (
    true, false, false, false,
    (select id from profil_droit where code='SDIS-ADM-APP'),
    (select id from type_droit where code='RCI')
);

-- Table: remocra.type_rci_degre_certitude

-- DROP TABLE remocra.type_rci_degre_certitude;

CREATE TABLE remocra.type_rci_degre_certitude
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_rci_degre_certitude_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_rci_degre_certitude
  OWNER TO postgres;


-- Table: remocra.type_rci_origine_alerte

-- DROP TABLE remocra.type_rci_origine_alerte;

CREATE TABLE remocra.type_rci_origine_alerte
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_rci_origine_alerte_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_rci_origine_alerte
  OWNER TO postgres;


-- Table: remocra.type_rci_prom_famille

-- DROP TABLE remocra.type_rci_prom_famille;

CREATE TABLE remocra.type_rci_prom_famille
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_rci_prom_famille_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_rci_prom_famille
  OWNER TO postgres;


-- Table: remocra.type_rci_prom_partition

-- DROP TABLE remocra.type_rci_prom_partition;

CREATE TABLE remocra.type_rci_prom_partition
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  famille bigint,
  CONSTRAINT type_rci_prom_partition_pkey PRIMARY KEY (id),
  CONSTRAINT fk257dcb7aa7ef692 FOREIGN KEY (famille)
      REFERENCES remocra.type_rci_prom_famille (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_rci_prom_partition
  OWNER TO postgres;


-- Table: remocra.type_rci_prom_categorie

-- DROP TABLE remocra.type_rci_prom_categorie;

CREATE TABLE remocra.type_rci_prom_categorie
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  partition bigint,
  CONSTRAINT type_rci_prom_categorie_pkey PRIMARY KEY (id),
  CONSTRAINT fkcaf3ab04d230270a FOREIGN KEY (partition)
      REFERENCES remocra.type_rci_prom_partition (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_rci_prom_categorie
  OWNER TO postgres;


insert into type_rci_degre_certitude(code, nom) values ('CERTAINE', 'Certaine');
insert into type_rci_degre_certitude(code, nom) values ('SUPPOSE', 'Supposée');
insert into type_rci_degre_certitude(code, nom) values ('DTERMINE', 'Déterminée');
insert into type_rci_degre_certitude(code, nom) values ('PROBABLE', 'Probable');
insert into type_rci_degre_certitude(code, nom) values ('INCONNUE', 'Inconnue');


insert into type_rci_origine_alerte(code, nom) values ('VIGIE', 'Vigie');
insert into type_rci_origine_alerte(code, nom) values ('POLICEGENDARMERIE', 'Police-Gendarmerie');
insert into type_rci_origine_alerte(code, nom) values ('POPULATION', 'Population');
insert into type_rci_origine_alerte(code, nom) values ('PATROUILLE', 'Patrouille');
insert into type_rci_origine_alerte(code, nom) values ('MOYENARIEN', 'Moyen aérien');
insert into type_rci_origine_alerte(code, nom) values ('AUTRE', 'Autre');


insert into type_rci_prom_famille(code, nom) values ('NATURELLE', 'Naturelle');
insert into type_rci_prom_famille(code, nom) values ('ACCIDENTELLE', 'Accidentelle liée aux installations');
insert into type_rci_prom_famille(code, nom) values ('MALVEILLANCE', 'Malveillance origine humaine intentionnelle');
insert into type_rci_prom_famille(code, nom) values ('INVOLONTAIREPRO', 'Involontaire liée aux travaux professionnels');
insert into type_rci_prom_famille(code, nom) values ('INVOLONTAIREPART', 'Involontaire liée aux particuliers');


insert into type_rci_prom_partition(code, nom, famille) values ('NATFOUDRE', 'Foudre', (select id from type_rci_prom_famille where code = 'NATURELLE'));
insert into type_rci_prom_partition(code, nom, famille) values ('ACCLIGNEELEC', 'Ligne électrique', (select id from type_rci_prom_famille where code = 'ACCIDENTELLE'));
insert into type_rci_prom_partition(code, nom, famille) values ('ACCCHEMINFER', 'Chemin de fer', (select id from type_rci_prom_famille where code = 'ACCIDENTELLE'));
insert into type_rci_prom_partition(code, nom, famille) values ('ACCVEHICULE', 'Véhicule', (select id from type_rci_prom_famille where code = 'ACCIDENTELLE'));
insert into type_rci_prom_partition(code, nom, famille) values ('ACCDEPOTORD', 'Dépôt ordure', (select id from type_rci_prom_famille where code = 'ACCIDENTELLE'));
insert into type_rci_prom_partition(code, nom, famille) values ('MALCONFLIT', 'Conflit', (select id from type_rci_prom_famille where code = 'MALVEILLANCE'));
insert into type_rci_prom_partition(code, nom, famille) values ('MALINTERET', 'Intérêt', (select id from type_rci_prom_famille where code = 'MALVEILLANCE'));
insert into type_rci_prom_partition(code, nom, famille) values ('MALPYROMAN', 'Pyromanie', (select id from type_rci_prom_famille where code = 'MALVEILLANCE'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPROFORST', 'Travaux forestiers', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPRO'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPROAGRIC', 'Travaux agricoles', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPRO'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPROINDUS', 'Travaux industriels, publics, artisanaux...', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPRO'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPROREPR', 'Reprise', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPRO'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPARTTRAV', 'Travaux', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPART'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPARTLOIS', 'Loisirs', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPART'));
insert into type_rci_prom_partition(code, nom, famille) values ('TRAVPARTJETOB', 'Jet d''objets incandescents', (select id from type_rci_prom_famille where code = 'INVOLONTAIREPART'));


insert into type_rci_prom_categorie(code, nom, partition) values ('NATFOUDRE', 'Foudre', (select id from type_rci_prom_partition where code = 'NATFOUDRE'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCLIGNEELECRUPT', 'Rupture', (select id from type_rci_prom_partition where code = 'ACCLIGNEELEC'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCLIGNEELECAMOR', 'Amorçage', (select id from type_rci_prom_partition where code = 'ACCLIGNEELEC'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCCHEMINFER', 'Chemin de fer', (select id from type_rci_prom_partition where code = 'ACCCHEMINFER'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCVEHICULEECH', 'Echappement, freins...', (select id from type_rci_prom_partition where code = 'ACCVEHICULE'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCVEHICULEINC', 'Incendie', (select id from type_rci_prom_partition where code = 'ACCVEHICULE'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCDEPOTORDOFF', 'Officiel', (select id from type_rci_prom_partition where code = 'ACCDEPOTORD'));
insert into type_rci_prom_categorie(code, nom, partition) values ('ACCDEPOTORDCLAN', 'Clandestin', (select id from type_rci_prom_partition where code = 'ACCDEPOTORD'));
insert into type_rci_prom_categorie(code, nom, partition) values ('MALCONFLITSOL', 'Occupation du sol', (select id from type_rci_prom_partition where code = 'MALCONFLIT'));
insert into type_rci_prom_categorie(code, nom, partition) values ('MALCONFLITCHAS', 'Chasse', (select id from type_rci_prom_partition where code = 'MALCONFLIT'));
insert into type_rci_prom_categorie(code, nom, partition) values ('MALINTERETSOL', 'Occupation du sol', (select id from type_rci_prom_partition where code = 'MALINTERET'));
insert into type_rci_prom_categorie(code, nom, partition) values ('MALINTERETCYN', 'Cynégétique', (select id from type_rci_prom_partition where code = 'MALINTERET'));
insert into type_rci_prom_categorie(code, nom, partition) values ('MALINTERETPAS', 'Pastoralisme', (select id from type_rci_prom_partition where code = 'MALINTERET'));
insert into type_rci_prom_categorie(code, nom, partition) values ('MALPYROMAN', 'Pyromanie', (select id from type_rci_prom_partition where code = 'MALPYROMAN'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROFORSTMAC', 'Machine-outil', (select id from type_rci_prom_partition where code = 'TRAVPROFORST'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROFORSTVEGP', 'Feu végétaux sur pied', (select id from type_rci_prom_partition where code = 'TRAVPROFORST'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROFORSTVEGC', 'Feu végétaux coupés', (select id from type_rci_prom_partition where code = 'TRAVPROFORST'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROAGRICMAC', 'Machine-outil', (select id from type_rci_prom_partition where code = 'TRAVPROAGRIC'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROAGRICVEGP', 'Feu végétaux sur pied', (select id from type_rci_prom_partition where code = 'TRAVPROAGRIC'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROAGRICVEGC', 'Feu végétaux coupés', (select id from type_rci_prom_partition where code = 'TRAVPROAGRIC'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROAGRICPAS', 'Feu pastoral', (select id from type_rci_prom_partition where code = 'TRAVPROAGRIC'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROINDUSMAC', 'Machine-outil', (select id from type_rci_prom_partition where code = 'TRAVPROINDUS'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROINDUSVEGP', 'Feu végétaux sur pied', (select id from type_rci_prom_partition where code = 'TRAVPROINDUS'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROINDUSVEGC', 'Feu végétaux coupés', (select id from type_rci_prom_partition where code = 'TRAVPROINDUS'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPROREPR', 'Reprise', (select id from type_rci_prom_partition where code = 'TRAVPROREPR'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTTRAVMAC', 'Machine-outil', (select id from type_rci_prom_partition where code = 'TRAVPARTTRAV'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTTRAVVEGP', 'Feu végétaux sur pied', (select id from type_rci_prom_partition where code = 'TRAVPARTTRAV'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTTRAVCEGC', 'Feu végétaux coupés', (select id from type_rci_prom_partition where code = 'TRAVPARTTRAV'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTLOISJEU', 'Jeu d''enfants, pétard...', (select id from type_rci_prom_partition where code = 'TRAVPARTLOIS'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTLOISFART', 'Feu d''artifice', (select id from type_rci_prom_partition where code = 'TRAVPARTLOIS'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTLOISBARB', 'Barbecue, réchaud, feu loisir', (select id from type_rci_prom_partition where code = 'TRAVPARTLOIS'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTJETOBMEGP', 'Mégot de promeneur', (select id from type_rci_prom_partition where code = 'TRAVPARTJETOB'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTJETOBMEGV', 'Mégot par véhicule', (select id from type_rci_prom_partition where code = 'TRAVPARTJETOB'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTJETOBFUS', 'Fusée de détresse', (select id from type_rci_prom_partition where code = 'TRAVPARTJETOB'));
insert into type_rci_prom_categorie(code, nom, partition) values ('TRAVPARTJETOBCEND', 'Déversement cendres chaudes', (select id from type_rci_prom_partition where code = 'TRAVPARTJETOB'));


-- Traitement

CREATE OR REPLACE VIEW pdi.vue_categorie_promethee AS 
 SELECT allin.id, allin.libelle
   FROM (         SELECT (-1) AS id, 'Toutes'::character varying AS libelle, NULL::character varying AS tricol1, NULL::character varying AS tricol2
        UNION 
                 SELECT type_rci_prom_categorie.id, (type_rci_prom_partition.nom::text || ' - '::text) || type_rci_prom_categorie.nom::text AS libelle, type_rci_prom_partition.nom AS tricol1, type_rci_prom_categorie.nom AS tricol2
                   FROM remocra.type_rci_prom_categorie
              LEFT JOIN remocra.type_rci_prom_partition ON type_rci_prom_partition.id = type_rci_prom_categorie.partition) allin
  ORDER BY allin.tricol1 NULLS FIRST, allin.tricol2 NULLS FIRST;

ALTER TABLE pdi.vue_categorie_promethee
  OWNER TO postgres;


INSERT INTO  pdi.modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) 
VALUES (
    (select max(idmodele)+1 as idmodele from modele_traitement), 7, 'Départs de feu sur une période et pour une catégorie Prométhée', 'Départs de feu', '/demandes/rci', 'export_xls', 'J', 3, 1);

INSERT INTO  pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
VALUES (
    (select max(idparametre)+1 as idparametre from modele_traitement_parametre), 'Catégorie prométhée', 1, true, 'vue_categorie_promethee', 'combo', NULL, 'CATEGORIE_PROMETHEE_ID',
    (select idmodele from modele_traitement where ref_chemin='/demandes/rci' and ref_nom='export_xls')
);

INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
VALUES (
    (select max(idparametre)+1 as idparametre from modele_traitement_parametre), 'Début', 2, true, NULL, 'datefield', '2014-01-01', 'DATE_DEB',
    (select idmodele from modele_traitement where ref_chemin='/demandes/rci' and ref_nom='export_xls')
);
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
VALUES (
    (select max(idparametre)+1 as idparametre from modele_traitement_parametre), 'Fin', 3, true, NULL, 'datefield', '2020-12-31', 'DATE_FIN',
    (select idmodele from modele_traitement where ref_chemin='/demandes/rci' and ref_nom='export_xls')
);


-- Table: remocra.rci

-- DROP TABLE remocra.rci;

CREATE TABLE remocra.rci
(
  id bigserial NOT NULL,
  cause character varying,
  commentaire_conclusions character varying,
  complement character varying,
  coorddfci character varying,
  date_incendie timestamp without time zone NOT NULL,
  date_modification timestamp without time zone NOT NULL,
  direction_vent integer,
  force_vent integer,
  forces_ordre character varying,
  gdh character varying,
  gel_lieux boolean,
  geometrie geometry NOT NULL,
  hygrometrie integer,
  indice_rothermel integer,
  point_eclosion character varying,
  premier_cos character varying,
  premier_engin character varying,
  superficie_finale double precision,
  superficie_referent double precision,
  superficie_secours double precision,
  temperature double precision,
  vent_local boolean,
  version integer,
  voie character varying,
  categorie_promethee bigint,
  commune bigint,
  degre_certitude bigint,
  origine_alerte bigint,
  utilisateur bigint NOT NULL,
  CONSTRAINT rci_pkey PRIMARY KEY (id),
  CONSTRAINT fk1b85821870359 FOREIGN KEY (degre_certitude)
      REFERENCES remocra.type_rci_degre_certitude (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk1b85838423ee7 FOREIGN KEY (origine_alerte)
      REFERENCES remocra.type_rci_origine_alerte (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk1b8583fb5f1fe FOREIGN KEY (categorie_promethee)
      REFERENCES remocra.type_rci_prom_categorie (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk1b858a98055b2 FOREIGN KEY (utilisateur)
      REFERENCES remocra.utilisateur (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk1b858d2da796c FOREIGN KEY (commune)
      REFERENCES remocra.commune (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.rci
  OWNER TO postgres;


-- Table: remocra.rci_document

-- DROP TABLE remocra.rci_document;

CREATE TABLE remocra.rci_document
(
  id bigserial NOT NULL,
  document bigint NOT NULL,
  rci bigint NOT NULL,
  CONSTRAINT rci_document_pkey PRIMARY KEY (id),
  CONSTRAINT fk6c6e2d2236f0130a FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk6c6e2d224556cb5c FOREIGN KEY (rci)
      REFERENCES remocra.rci (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.rci_document
  OWNER TO postgres;


-- Paramètres de configuration
insert into param_conf(cle, description, valeur) values (
    'DOSSIER_DEPOT_RCI',
    'Emplacement du dossier de stockage des documents RCI',
    '/var/remocra/rci'
);

COMMIT;
