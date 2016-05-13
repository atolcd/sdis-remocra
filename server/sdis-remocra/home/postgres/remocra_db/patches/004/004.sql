
CREATE TABLE remocra.thematique
(
  id bigserial NOT NULL,
  nom character varying,
  version integer,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  CONSTRAINT thematique_pkey PRIMARY KEY (id ),
  CONSTRAINT thematique_code_key UNIQUE (code ),
  CONSTRAINT thematique_nom_key UNIQUE (nom )
);

CREATE TABLE remocra.metadonnee
(
  id bigserial NOT NULL,
  resume character varying,
  titre character varying NOT NULL,
  version integer,
  thematique bigint NOT NULL,
  url_fiche character varying,
  url_vignette character varying,
  CONSTRAINT metadonnee_pkey PRIMARY KEY (id ),
  CONSTRAINT fk507e37b0d27676e2 FOREIGN KEY (thematique)
      REFERENCES remocra.thematique (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Donnée thématique
INSERT INTO remocra.thematique (id, nom, version, actif, code) VALUES (0, 'Divers', NULL, true, 'DIVERS');
INSERT INTO remocra.thematique (id, nom, version, actif, code) VALUES (1, 'Point d''eau', NULL, true, 'POINTDEAU');
INSERT INTO remocra.thematique (id, nom, version, actif, code) VALUES (2, 'Adresses', NULL, true, 'ADRESSES');
INSERT INTO remocra.thematique (id, nom, version, actif, code) VALUES (3, 'Permis', NULL, true, 'PERMIS');
INSERT INTO remocra.thematique (id, nom, version, actif, code) VALUES (4, 'DFCI', NULL, true, 'DFCI');
INSERT INTO remocra.thematique (id, nom, version, actif, code) VALUES (5, 'Risques', NULL, true, 'RISQUES');
