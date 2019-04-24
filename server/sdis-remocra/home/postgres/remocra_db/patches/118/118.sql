begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 118;
    description_patch := 'Mise à jour de la BDD pour la refonte de la fiche PEI';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début

SELECT pg_catalog.setval('remocra.type_droit_id_seq', (select max(id) from remocra.type_droit));

INSERT INTO remocra.type_droit (code, description, nom, version, categorie)
VALUES('HYDRANTS_ADRESSE_C', 'Créer, éditer l''adresse des PEI', 'hydrants.adresse_C', 1, 'Module PEI');
VALUES('HYDRANTS_GESTIONNAIRE_C', 'Créer, éditer les gestionnaires des PEI', 'hydrants.gestionnaire_C', 1, 'Module PEI');

DROP TABLE IF EXISTS remocra.type_reseau_alimentation CASCADE;
CREATE TABLE remocra.type_reseau_alimentation
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  CONSTRAINT type_reseau_alimentation_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.type_reseau_alimentation IS 'Réseau d''alimentation des hydrants';
COMMENT ON COLUMN remocra.type_reseau_alimentation.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_reseau_alimentation.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_reseau_alimentation.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_reseau_alimentation.nom IS 'Libellé du réseau d''alimentation';

INSERT INTO remocra.type_reseau_alimentation(actif, code, nom, version) VALUES
(TRUE, 'MAILLE', 'Maillé', 1),
(TRUE, 'RAMIFIE', 'Ramifié', 1),
(TRUE, 'BOUCLE', 'Bouclé', 1);

DROP TABLE IF EXISTS remocra.hydrant_reservoir CASCADE;
CREATE TABLE remocra.hydrant_reservoir
(
  id BIGSERIAL NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  capacite INTEGER,
  actif BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT hydrant_reservoir_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.hydrant_reservoir IS 'Réservoir pouvant alimenter un ou plusieurs hydrants';
COMMENT ON COLUMN remocra.hydrant_reservoir.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.hydrant_reservoir.nom IS 'Libellé du réservoir';
COMMENT ON COLUMN remocra.hydrant_reservoir.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.hydrant_reservoir.capacite IS 'Capacité en m3';

DROP TABLE IF EXISTS remocra.type_reseau_canalisation CASCADE;
CREATE TABLE remocra.type_reseau_canalisation
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  CONSTRAINT type_reseau_canalisation_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.type_reseau_canalisation IS 'Type de réseau d''alimentation des hydrants';
COMMENT ON COLUMN remocra.type_reseau_canalisation.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_reseau_canalisation.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_reseau_canalisation.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_reseau_canalisation.nom IS 'Libellé du type de réseau d''alimentation';

DROP TABLE IF EXISTS remocra.type_hydrant_niveau CASCADE;
CREATE TABLE remocra.type_hydrant_niveau
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  CONSTRAINT type_hydrant_niveau_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.type_hydrant_niveau IS 'Type de niveau des hydrants';
COMMENT ON COLUMN remocra.type_hydrant_niveau.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_hydrant_niveau.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_hydrant_niveau.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_hydrant_niveau.nom IS 'Libellé du type de niveau';

INSERT INTO remocra.type_hydrant_niveau(actif, code, nom, version) VALUES
(TRUE, 'VP', 'Voie publique', 1),
(TRUE, 'INFRA', 'Infrastructure', 1),
(TRUE, 'SUPER', 'Superstructure', 1);

DROP TABLE IF EXISTS remocra.type_hydrant_aspiration CASCADE;
CREATE TABLE remocra.type_hydrant_aspiration
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  CONSTRAINT type_hydrant_aspiration_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.type_hydrant_aspiration IS 'Type d''aspiration des hydrants';
COMMENT ON COLUMN remocra.type_hydrant_aspiration.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_hydrant_aspiration.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_hydrant_aspiration.nom IS 'Libellé du type d''aspiration';

DROP TABLE IF EXISTS remocra.hydrant_aspiration CASCADE;
CREATE TABLE remocra.hydrant_aspiration
(
  id BIGSERIAL NOT NULL,
  numero CHARACTER VARYING,
  normalise BOOLEAN DEFAULT FALSE,
  hauteur BOOLEAN DEFAULT FALSE,
  type_aspiration BIGINT,
  deporte BOOLEAN DEFAULT FALSE,
  geometrie geometry,
  pena BIGINT,
  version INTEGER DEFAULT 1,
  CONSTRAINT hydrant_aspiration_pkey PRIMARY KEY (id),
  CONSTRAINT fk_type_aspiration FOREIGN KEY (type_aspiration)
	REFERENCES remocra.type_hydrant_aspiration (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_pena FOREIGN KEY (pena)
	REFERENCES remocra.hydrant_pena (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.hydrant_aspiration IS 'Aspiration des PENA';
COMMENT ON COLUMN remocra.hydrant_aspiration.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.hydrant_aspiration.numero IS 'Hauteur d''aspiration en mètres';
COMMENT ON COLUMN remocra.hydrant_aspiration.normalise IS 'Indique si l''hydrant est normalisé';
COMMENT ON COLUMN remocra.hydrant_aspiration.hauteur IS 'Hauteur d''aspiration supérieure à 3 mètres';
COMMENT ON COLUMN remocra.hydrant_aspiration.type_aspiration IS 'Indentifiant du type d''aspiration';
COMMENT ON COLUMN remocra.hydrant_aspiration.deporte IS 'Indique si le dispositif d''aspiration est à proximité ou déporté';
COMMENT ON COLUMN remocra.hydrant_aspiration.geometrie IS 'Coordonnées du dispositif d''aspiration';
COMMENT ON COLUMN remocra.hydrant_aspiration.pena IS 'PENA auquel le dispositif est lié';
CREATE INDEX hydrant_aspiration_type_aspiration_idx ON remocra.hydrant_aspiration USING btree (type_aspiration);
CREATE INDEX hydrant_aspiration_pena_idx ON remocra.hydrant_aspiration USING btree (pena);

DROP TABLE IF EXISTS remocra.gestionnaire CASCADE;
CREATE TABLE remocra.gestionnaire
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  CONSTRAINT gestionnaire_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.gestionnaire IS 'Gestionnaire de PEI';
COMMENT ON COLUMN remocra.gestionnaire.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.gestionnaire.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.gestionnaire.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.gestionnaire.nom IS 'Libellé du gestionnaire';

DROP TABLE IF EXISTS remocra.gestionnaire_site CASCADE;
CREATE TABLE remocra.gestionnaire_site
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  gestionnaire BIGINT,
  CONSTRAINT gestionnaire_site_pkey PRIMARY KEY (id),
  CONSTRAINT fk_gestionnaire FOREIGN KEY (gestionnaire)
	REFERENCES remocra.gestionnaire (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.gestionnaire_site IS 'Gestionnaire de site de PEI';
COMMENT ON COLUMN remocra.gestionnaire_site.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.gestionnaire_site.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.gestionnaire_site.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.gestionnaire_site.nom IS 'Libellé du gestionnaire de site';
COMMENT ON COLUMN remocra.gestionnaire_site.gestionnaire IS 'Identifiant du gestionnaire auquel est rattaché le gestionnaire de site';
CREATE INDEX gestionnaire_site_gestionnaire_idx ON remocra.gestionnaire_site USING btree (gestionnaire);

DROP TABLE IF EXISTS remocra.site CASCADE;
CREATE TABLE remocra.site
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  gestionnaire_site BIGINT,
  CONSTRAINT site_pkey PRIMARY KEY (id),
  CONSTRAINT fk_gestionnaire_site FOREIGN KEY (gestionnaire_site)
	REFERENCES remocra.gestionnaire_site (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.site IS 'Site de PEI';
COMMENT ON COLUMN remocra.site.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.site.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.site.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.site.nom IS 'Libellé du site';
COMMENT ON COLUMN remocra.site.gestionnaire_site IS 'Identifiant du gestionnaire de site auquel est rattaché le site';

DROP TABLE IF EXISTS remocra.hydrant_visite CASCADE;
CREATE TABLE remocra.hydrant_visite
(
  id BIGSERIAL NOT NULL,
  hydrant BIGINT NOT NULL,
  date timestamp without time zone DEFAULT NOW(),
  type BIGINT,
  ctrl_debit_pression boolean,
  agent1 CHARACTER VARYING,
  agent2 CHARACTER VARYING,
  debit INTEGER,
  debit_max INTEGER,
  pression DOUBLE PRECISION,
  pression_dyn DOUBLE PRECISION,
  pression_dyn_deb DOUBLE PRECISION,
  anomalies CHARACTER VARYING,
  observations CHARACTER VARYING,
  CONSTRAINT hydrant_visite_pkey PRIMARY KEY (id),
  CONSTRAINT fk_type FOREIGN KEY (type)
	REFERENCES remocra.type_hydrant_saisie (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_hydrant FOREIGN KEY (hydrant)
	REFERENCES remocra.hydrant (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.hydrant_visite IS 'Visite d''un hydrant';
COMMENT ON COLUMN remocra.hydrant_visite.hydrant IS 'PEI associé à la visite';
COMMENT ON COLUMN remocra.hydrant_visite.date IS 'Date de la visite';
COMMENT ON COLUMN remocra.hydrant_visite.type IS 'Identifiant du type de visite de l''hydrant';
COMMENT ON COLUMN remocra.hydrant_visite.ctrl_debit_pression IS 'Indique si la visite est un contrôle avec mesure de débit et de pression';
CREATE INDEX hydrant_visite_type_idx ON remocra.hydrant_visite USING btree (type);
CREATE INDEX hydrant_visite_hydrant_idx ON remocra.hydrant_visite USING btree (hydrant);

DROP TABLE IF EXISTS remocra.service_eaux CASCADE;
CREATE TABLE remocra.service_eaux
(
  id BIGSERIAL NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  version INTEGER DEFAULT 1,
  CONSTRAINT service_eaux_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.service_eaux IS 'Service des eaux';
COMMENT ON COLUMN remocra.service_eaux.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.service_eaux.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.service_eaux.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.service_eaux.nom IS 'Libellé du service des eaux';

ALTER TABLE remocra.hydrant_pibi RENAME COLUMN choc TO renversable;

ALTER TABLE remocra.hydrant_pibi
ADD COLUMN jumele BIGINT,
ADD COLUMN dispositif_inviolabilite BOOLEAN,
ADD COLUMN reservoir BIGINT,
ADD COLUMN service_eaux BIGINT,
ADD COLUMN debit_renforce boolean,
ADD COLUMN type_reseau_canalisation BIGINT,
ADD COLUMN type_reseau_alimentation BIGINT,
ADD COLUMN diametre_canalisation INTEGER,
ADD COLUMN surpresse boolean,
ADD COLUMN additive boolean,
ADD CONSTRAINT fk_jumele FOREIGN KEY (jumele)
	REFERENCES remocra.hydrant_pibi (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
ADD CONSTRAINT fk_reservoir FOREIGN KEY (reservoir)
	REFERENCES remocra.hydrant_reservoir (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
ADD CONSTRAINT fk_service_eaux FOREIGN KEY (service_eaux)
	REFERENCES remocra.organisme (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
ADD CONSTRAINT fk_type_reseau_canalisation FOREIGN KEY (type_reseau_canalisation)
	REFERENCES remocra.type_reseau_canalisation (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
ADD CONSTRAINT fk_type_reseau_alimentation FOREIGN KEY (type_reseau_alimentation)
	REFERENCES remocra.type_reseau_alimentation (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION;
COMMENT ON COLUMN remocra.hydrant_pibi.jumele IS 'Identifiant du PIBI jumelé';
COMMENT ON COLUMN remocra.hydrant_pibi.dispositif_inviolabilite IS 'Présence d''un dispositif d''inviolabilité';
COMMENT ON COLUMN remocra.hydrant_pibi.reservoir IS 'Identifiant du réservoir alimentant l''hydrant';
COMMENT ON COLUMN remocra.hydrant_pibi.service_eaux IS 'Organisme de type Service des eaux concerné';
COMMENT ON COLUMN remocra.hydrant_pibi.debit_renforce IS 'Caractère renforcé du débit';
COMMENT ON COLUMN remocra.hydrant_pibi.type_reseau_canalisation IS 'Identifiant du type de canalisation du réseau';
COMMENT ON COLUMN remocra.hydrant_pibi.diametre IS 'Diamètre des canalisation';
COMMENT ON COLUMN remocra.hydrant_pibi.surpresse IS 'Indique si le réseau est surpressé';
COMMENT ON COLUMN remocra.hydrant_pibi.additive IS 'Indique si le réseau est additivé';
CREATE INDEX hydrant_pibi_jumele_idx ON remocra.hydrant_pibi USING btree (jumele);
CREATE INDEX hydrant_pibi_reservoir_idx ON remocra.hydrant_pibi USING btree (reservoir);
CREATE INDEX hydrant_pibi_service_eaux_idx ON remocra.hydrant_pibi USING btree (service_eaux);
CREATE INDEX hydrant_pibi_type_reseau_canalisation_idx ON remocra.hydrant_pibi USING btree (type_reseau_canalisation);
CREATE INDEX hydrant_pibi_type_reseau_alimentation_idx ON remocra.hydrant_pibi USING btree (type_reseau_alimentation);

ALTER TABLE remocra.hydrant_pena
ADD COLUMN illimitee BOOLEAN DEFAULT FALSE,
ADD COLUMN incertaine BOOLEAN DEFAULT FALSE;
COMMENT ON COLUMN remocra.hydrant_pena.illimitee IS 'Indique si la capacite du PENA est illimitée';
COMMENT ON COLUMN remocra.hydrant_pena.incertaine IS 'Indique si la capacite du PENA est incertaine';

ALTER TABLE remocra.hydrant
ADD COLUMN numero_voie INTEGER,
ADD COLUMN suffixe_voie character varying,
ADD COLUMN niveau BIGINT,
ADD COLUMN gestionnaire BIGINT,
ADD COLUMN site BIGINT,
ADD COLUMN autorite_deci BIGINT,
ADD CONSTRAINT fk_autorite_deci FOREIGN KEY (autorite_deci)
	REFERENCES remocra.organisme (id) MATCH SIMPLE,
ADD CONSTRAINT fk_niveau FOREIGN KEY (niveau)
	REFERENCES remocra.type_hydrant_niveau (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
ADD CONSTRAINT fk_site FOREIGN KEY (site)
	REFERENCES remocra.site (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION;
COMMENT ON COLUMN remocra.hydrant.numero_voie IS 'Numéro de voie (adresse)';
COMMENT ON COLUMN remocra.hydrant.suffixe_voie IS 'Suffixe de voie de l''adresse (bis, ter, etc)';
COMMENT ON COLUMN remocra.hydrant.niveau IS 'Identifiant du type de niveau de l''hydrant';
COMMENT ON COLUMN remocra.hydrant.gestionnaire IS 'Identifiant du gestionnaire de l''hydrant';
COMMENT ON COLUMN remocra.hydrant.site IS 'Identifiant du site auquel appartient l''hydrant';
COMMENT ON COLUMN remocra.hydrant.autorite_deci IS 'Identifiant de l''autorité de police DECI';
CREATE INDEX hydrant_autorite_deci_idx ON remocra.hydrant USING btree (autorite_deci);
CREATE INDEX hydrant_niveau_idx ON remocra.hydrant USING btree (niveau);
CREATE INDEX hydrant_site_idx ON remocra.hydrant USING btree (site);

SELECT pg_catalog.setval('remocra.type_organisme_id_seq', (select max(id) from remocra.type_organisme));
INSERT INTO remocra.type_organisme (code, nom, actif)
VALUES ('SERVICEEAUX', 'Service des eaux', true);

UPDATE remocra.type_hydrant_saisie SET nom = CASE
   WHEN code = 'CREA' THEN 'Visite de réception (création)'
   WHEN code = 'RECEP' THEN 'Reconnaissance opérationnelle initiale'
   WHEN code = 'CTRL' THEN 'Contrôle technique périodique'
   WHEN code = 'RECO' THEN 'Reconnaissance opérationnelle périodique'
END
WHERE code IN ('CREA','RECEP', 'CTRL', 'RECO');

SELECT pg_catalog.setval('remocra.type_hydrant_saisie_id_seq', (select max(id) from remocra.type_hydrant_saisie));
INSERT INTO remocra.type_hydrant_saisie(actif, code, nom, version)
VALUES (true, 'NP', 'Non programmée', 1);



-- Contenu réel du patch fin
--------------------------------------------------

commit;
