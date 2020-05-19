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
    numero_patch := 136;
    description_patch :='Couverture hydraulique';

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

INSERT INTO remocra.type_droit(code, description, nom, categorie, version) VALUES
('PLANIFIER_DECI', 'Planifier la DECI', 'planifier_deci', 'Module PEI', 1);

CREATE TABLE remocra.type_etude
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_etude_pkey PRIMARY KEY (id)
);

INSERT INTO remocra.type_etude(code, nom) VALUES
('PERMIS_CONSTRUIRE', 'Permis de construire'),
('PERMIS_AMENAGER', 'Permis d''aménager'),
('CONSEIL', 'Conseil'),
('AVIS_DIVERS_URBANISME', 'Avis divers urbanisme');

CREATE TABLE remocra.type_etude_statut
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer DEFAULT 1,
  CONSTRAINT type_etude_statut_pkey PRIMARY KEY (id)
);

INSERT INTO remocra.type_etude_statut(code, nom) VALUES
('EN_COURS', 'En cours'),
('TERMINEE', 'Terminée');

CREATE TABLE remocra.etude
(
  id bigserial NOT NULL,
  type bigint NOT NULL,
  date_maj timestamp without time zone DEFAULT now(),
  numero character varying NOT NULL,
  nom character varying NOT NULL,
  description character varying NOT NULL,
  organisme bigserial,
  statut bigint NOT NULL,
  CONSTRAINT etude_statut_pkey PRIMARY KEY (id),
  CONSTRAINT fk_statut FOREIGN KEY (statut)
      REFERENCES remocra.type_etude_statut (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_type FOREIGN KEY (type)
      REFERENCES remocra.type_etude (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_organisme FOREIGN KEY (organisme)
      REFERENCES remocra.organisme (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE remocra.etude_communes
(
  id bigserial NOT NULL,
  etude bigint,
  commune bigint,
  CONSTRAINT etude_communes_pkey PRIMARY KEY (id),
  CONSTRAINT etude_communes_etude FOREIGN KEY (etude)
      REFERENCES remocra.etude (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT etude_communes_commune FOREIGN KEY (commune)
      REFERENCES remocra.commune (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uk_etude_communes UNIQUE (etude, commune)
);

CREATE TABLE remocra.etude_documents
(
  id bigserial NOT NULL,
  etude bigint,
  document bigint,
  nom CHARACTER VARYING,
  CONSTRAINT etude_documents_pkey PRIMARY KEY (id),
  CONSTRAINT etude_documents_etude FOREIGN KEY (etude)
      REFERENCES remocra.etude (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT etude_documents_document FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uk_etude_documents UNIQUE (etude, document)
);

CREATE TABLE remocra.etude_hydrant_projet
(
  id bigserial NOT NULL,
  etude bigint NOT NULL,
  type_deci bigint NOT NULL,
  type character varying NOT NULL,
  diametre_nominal bigint,
  diametre_canalisation integer,
  capacite integer,
  debit integer,
  geometrie geometry,
  CONSTRAINT etude_hydrant_projet_pkey PRIMARY KEY (id),
  CONSTRAINT fk_etude FOREIGN KEY (etude)
      REFERENCES remocra.etude (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_type_deci FOREIGN KEY (type_deci)
      REFERENCES remocra.type_hydrant_nature_deci (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diametre_nominal FOREIGN KEY (diametre_nominal)
      REFERENCES remocra.type_hydrant_diametre (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO remocra.param_conf(cle, description, valeur, nomgroupe) VALUES
('DOSSIER_DEPOT_PLANIFICATION', 'Emplacement du dossier de stockage des documents relatif à la planification DECI', '/var/remocra/planification', 'Chemins sur disque');
-- Contenu réel du patch fin
--------------------------------------------------

commit;
