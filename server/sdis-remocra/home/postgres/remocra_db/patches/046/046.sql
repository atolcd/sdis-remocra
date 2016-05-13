begin;

-- A partir de ce patch :
-- * La dernière instruction réalise obligatoirement un insert dans la table de suivi
-- * La description est visible dans la table de suivi (plus en haut du fichier pour éviter les doublons)
-- * Les numéros de suivi se suivent séquentiellement (s'il y a deux patches schema et data : créer un patch qui utilise \i)

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;


CREATE TABLE remocra.suivi_patches
(
  numero bigint NOT NULL,
  description character varying NOT NULL,
  "application" timestamp without time zone NOT NULL default now(),
  CONSTRAINT suivi_patches_pkey PRIMARY KEY (numero)
)
WITH (
  OIDS=FALSE
);


-- Application du patch
insert into remocra.suivi_patches(numero, description) values(
  46,
  'Suivi des patches'
);

commit;
