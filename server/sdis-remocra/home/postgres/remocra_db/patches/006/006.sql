SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;

DROP TABLE remocra.type_hydrant CASCADE;
DROP TABLE remocra.type_hydrant_sous_type CASCADE;
DROP TABLE remocra.type_hydrant_critere CASCADE;
DROP TABLE remocra.type_hydrant_attention CASCADE;
DROP TABLE remocra.type_hydrant_diametre CASCADE;
DROP TABLE remocra.type_hydrant_domaine CASCADE;
DROP TABLE remocra.type_hydrant_marque CASCADE;
DROP TABLE remocra.type_hydrant_modele CASCADE;
DROP TABLE remocra.type_hydrant_materiau CASCADE;
DROP TABLE remocra.type_hydrant_positionnement CASCADE;
DROP TABLE remocra.type_hydrant_usage CASCADE;
DROP TABLE remocra.type_hydrant_vol_constate CASCADE;
DROP TABLE remocra.tournee CASCADE;
DROP TABLE remocra.hydrant CASCADE;
DROP TABLE remocra.hydrant_pena CASCADE;
DROP TABLE remocra.hydrant_pibi CASCADE;
DROP TABLE remocra.hydrant_attentions CASCADE;

CREATE TABLE remocra.type_hydrant
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant
  OWNER TO postgres;

CREATE TABLE remocra.type_hydrant_sous_type
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer,
  type_hydrant bigint,
  CONSTRAINT type_hydrant_sous_type_pkey PRIMARY KEY (id ),
  CONSTRAINT fkd09fb54386657e5d FOREIGN KEY (type_hydrant)
      REFERENCES remocra.type_hydrant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_sous_type
  OWNER TO postgres;  
  
CREATE TABLE remocra.type_hydrant_critere
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  type_hydrant bigint,
  CONSTRAINT type_hydrant_critere_pkey PRIMARY KEY (id ),
  CONSTRAINT fke96f692286657e5d FOREIGN KEY (type_hydrant)
      REFERENCES remocra.type_hydrant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_critere
  OWNER TO postgres;

CREATE TABLE remocra.type_hydrant_attention
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  critere bigint,
  CONSTRAINT type_hydrant_attention_pkey PRIMARY KEY (id ),
  CONSTRAINT fk1f3ed802fd3ae2e2 FOREIGN KEY (critere)
      REFERENCES remocra.type_hydrant_critere (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_attention
  OWNER TO postgres;
  

CREATE TABLE remocra.type_hydrant_diametre
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_diametre_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_diametre
  OWNER TO postgres;
  

CREATE TABLE remocra.type_hydrant_domaine
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_domaine_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_domaine
  OWNER TO postgres;  
  
CREATE TABLE remocra.type_hydrant_marque
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_marque_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_marque
  OWNER TO postgres;

CREATE TABLE remocra.type_hydrant_modele
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  marque bigint,
  CONSTRAINT type_hydrant_modele_pkey PRIMARY KEY (id ),
  CONSTRAINT fk41b5bdf8cd9e6420 FOREIGN KEY (marque)
      REFERENCES remocra.type_hydrant_marque (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_modele
  OWNER TO postgres;
  
CREATE TABLE remocra.type_hydrant_materiau
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_materiau_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_materiau
  OWNER TO postgres;
  
CREATE TABLE remocra.type_hydrant_positionnement
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_positionnement_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_positionnement
  OWNER TO postgres;  

CREATE TABLE remocra.type_hydrant_usage
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_usage_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_usage
  OWNER TO postgres;  

CREATE TABLE remocra.type_hydrant_vol_constate
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_hydrant_vol_constate_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_hydrant_vol_constate
  OWNER TO postgres;

CREATE TABLE remocra.tournee
(
  id bigserial NOT NULL,
  deb_sync timestamp without time zone,
  last_sync timestamp without time zone,
  version integer DEFAULT 1,
  affectation bigint,
  CONSTRAINT tournee_pkey PRIMARY KEY (id ),
  CONSTRAINT fkbc630036dbf82b2f FOREIGN KEY (affectation)
      REFERENCES remocra.utilisateur (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.tournee
  OWNER TO postgres;  
  
CREATE TABLE remocra.hydrant
(
  id bigserial NOT NULL,
  agent1 character varying,
  agent2 character varying,
  annee_fabrication integer,
  complement character varying,
  date_contr timestamp without time zone,
  date_reco timestamp without time zone,
  geometrie geometry NOT NULL,
  num_parcellaire character varying,
  observation character varying,
  version integer DEFAULT 1,
  voie character varying,
  commune bigint,
  domaine bigint,
  marque bigint,
  modele bigint,
  photo bigint,
  sous_type bigint,
  tournee bigint,
  code character varying,
  numero character varying,
  CONSTRAINT hydrant_pkey PRIMARY KEY (id ),
  CONSTRAINT fk51b8f0285d29d8a8 FOREIGN KEY (domaine)
      REFERENCES remocra.type_hydrant_domaine (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk51b8f0289ee8021 FOREIGN KEY (photo)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk51b8f028c5d4270d FOREIGN KEY (sous_type)
      REFERENCES remocra.type_hydrant_sous_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk51b8f028cd9e6420 FOREIGN KEY (marque)
      REFERENCES remocra.type_hydrant_marque (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk51b8f028cf1bdf92 FOREIGN KEY (modele)
      REFERENCES remocra.type_hydrant_modele (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk51b8f028d2da796c FOREIGN KEY (commune)
      REFERENCES remocra.commune (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk51b8f028da542518 FOREIGN KEY (tournee)
      REFERENCES remocra.tournee (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.hydrant
  OWNER TO postgres;
  

CREATE TABLE remocra.hydrant_pena
(
  capacite character varying,
  coorddfci character varying,
  numero character varying,
  piste character varying,
  id bigint NOT NULL,
  materiau bigint,
  positionnement bigint,
  usage bigint,
  vol_constate bigint,
  CONSTRAINT hydrant_pena_pkey PRIMARY KEY (id ),
  CONSTRAINT fkd60e141f72ee2d28 FOREIGN KEY (usage)
      REFERENCES remocra.type_hydrant_usage (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkd60e141f7dc71cd6 FOREIGN KEY (positionnement)
      REFERENCES remocra.type_hydrant_positionnement (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkd60e141f8ac9c5e3 FOREIGN KEY (vol_constate)
      REFERENCES remocra.type_hydrant_vol_constate (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkd60e141fb34721ef FOREIGN KEY (id)
      REFERENCES remocra.hydrant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkd60e141fe51486ba FOREIGN KEY (materiau)
      REFERENCES remocra.type_hydrant_materiau (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.hydrant_pena
  OWNER TO postgres;

CREATE TABLE remocra.hydrant_pibi
(
  debit integer,
  debit_max integer,
  ident_internescp character varying,
  numero character varying,
  pression double precision,
  pression_dyn double precision,
  id bigint NOT NULL,
  diametre bigint,
  CONSTRAINT hydrant_pibi_pkey PRIMARY KEY (id ),
  CONSTRAINT fkd60e21b7a5a0e880 FOREIGN KEY (diametre)
      REFERENCES remocra.type_hydrant_diametre (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkd60e21b7b34721ef FOREIGN KEY (id)
      REFERENCES remocra.hydrant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.hydrant_pibi
  OWNER TO postgres;


CREATE TABLE remocra.hydrant_attentions
(
  hydrant bigint NOT NULL,
  attentions bigint NOT NULL,
  CONSTRAINT hydrant_attentions_pkey PRIMARY KEY (hydrant , attentions ),
  CONSTRAINT fk5cee3f8c50004fc FOREIGN KEY (hydrant)
      REFERENCES remocra.hydrant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk5cee3f8cc92df319 FOREIGN KEY (attentions)
      REFERENCES remocra.type_hydrant_attention (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.hydrant_attentions
  OWNER TO postgres;  
  

-- Données Hydrant
INSERT INTO remocra.type_hydrant (id, actif, code, nom, version) VALUES (1, true, 'PIBI', 'Pi/Bi', 1);
INSERT INTO remocra.type_hydrant (id, actif, code, nom, version) VALUES (2, true, 'PENA', 'Pena', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_id_seq', 2, true);

INSERT INTO remocra.type_hydrant_sous_type(id, actif, code, nom, version, type_hydrant) VALUES (1, true, 'PI', 'Pi', 1 , 1);
INSERT INTO remocra.type_hydrant_sous_type(id, actif, code, nom, version, type_hydrant) VALUES (2, true, 'BI', 'Bi', 1 , 1);
INSERT INTO remocra.type_hydrant_sous_type(id, actif, code, nom, version, type_hydrant) VALUES (3, true, 'CITERNE', 'Citerne', 1 , 2);
INSERT INTO remocra.type_hydrant_sous_type(id, actif, code, nom, version, type_hydrant) VALUES (4, true, 'EAU', 'Cours ou plan d''eau', 1 , 2);
INSERT INTO remocra.type_hydrant_sous_type(id, actif, code, nom, version, type_hydrant) VALUES (5, true, 'PUISARD', 'Puisard relié par canalisation à un plan ou cours d''eau', 1 , 2);

SELECT pg_catalog.setval('remocra.type_hydrant_sous_type_id_seq', 5, true);

INSERT INTO remocra.type_hydrant_diametre(id, actif, code, nom, version) VALUES (1, true, 'DIAM80', '80', 1);
INSERT INTO remocra.type_hydrant_diametre(id, actif, code, nom, version) VALUES (2, true, 'DIAM100', '100', 1);
INSERT INTO remocra.type_hydrant_diametre(id, actif, code, nom, version) VALUES (3, true, 'DIAM150', '150', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_diametre_id_seq', 3, true);

INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (1, true, 'DEPARTEMENT', 'Départemental', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (2, true, 'DOMAINE', 'Domanial', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (3, true, 'COMMUNAL', 'Communal', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (4, true, 'MILITAIRE', 'Militaire', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (5, true, 'PRIVE', 'Privé', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_domaine_id_seq', 5, true);

INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (1, true, 'VOL4', '4/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (2, true, 'VOL3', '3/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (3, true, 'VOL2', '2/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (4, true, 'VOL1', '1/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (5, true, 'VOL0', '0', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_vol_constate_id_seq', 5, true);

INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (1, true, 'METAL', 'Métal', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (2, true, 'METAL CALO', 'Métal calorifugé', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (3, true, 'BETON', 'Béton', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (4, true, 'METAL MEMBRANE', 'Métal avec membrane interne', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_materiau_id_seq', 4, true);

INSERT INTO remocra.type_hydrant_positionnement(id, actif, code, nom, version) VALUES (1, true, 'POSEE', 'Posée', 1);
INSERT INTO remocra.type_hydrant_positionnement(id, actif, code, nom, version) VALUES (2, true, 'SEMI', 'Semi-enterrée', 1);
INSERT INTO remocra.type_hydrant_positionnement(id, actif, code, nom, version) VALUES (3, true, 'ENTERRE', 'Enterrée', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_positionnement_id_seq', 3, true);

INSERT INTO remocra.type_hydrant_usage(id, actif, code, nom, version) VALUES (1, true, 'URBAIN', 'Urbain', 1);
INSERT INTO remocra.type_hydrant_usage(id, actif, code, nom, version) VALUES (2, true, 'DFCI', 'DFCI', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_usage_id_seq', 2, true);  


-- Pour tester les point d'attentions 

INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version, type_hydrant) VALUES (1, true, 'ABORD', 'Abords', 1, 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version, type_hydrant) VALUES (2, true, 'ACCESS', 'Accessibilité', 1, 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version, type_hydrant) VALUES (3, true, 'ASPECT', 'Aspect', 1, 1);

SELECT pg_catalog.setval('remocra.type_hydrant_critere_id_seq', 3, true);

INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (1, true, 'DEG', 'Abords à dégager', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (2, true, 'OBS', 'Obstacle à l''ouverture', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (3, true, 'Stab', 'Stabilité / verticalité', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (4, true, 'ANC', 'Ancrage', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (5, true, 'SOC', 'Socle de propreté', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (6, true, 'COF', 'PI sous coffre', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (7, true, 'SCE', 'PI sous scellé', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (8, true, 'VOL', 'Volume de dégagement insuffisant (50 cm autour hydrant)', 1, 1);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (9, true, 'DEB', 'A débrousailler', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (10, true, 'PRO', 'A Protéger', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (11, true, 'TOUR', 'A Tourner', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (12, true, 'DEPL', 'A Déplacer', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (13, true, 'DIST', 'Distance > 5m', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (14, true, 'ENT', 'Hydrant enterré', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (15, true, 'INNAC', 'Innaccessible au engins', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (16, true, 'INT', 'Introuvable', 1, 2);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (17, true, 'PEIND', 'A Peindre', 1, 3);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (18, true, 'CAPOTCASS', 'Capot cassé - manquant', 1, 3);
INSERT INTO remocra.type_hydrant_attention (id, actif, code, nom, version, critere) VALUES (19, true, 'COUL', 'Couleur non normalisé (à peindre)', 1, 3);

SELECT pg_catalog.setval('remocra.type_hydrant_attention_id_seq', 19, true);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DOC_HYDRANT', 'Emplacement du dossier de stockage des photos des hydrants', '/var/remocra/hydrants', 1);
  
COMMIT;
