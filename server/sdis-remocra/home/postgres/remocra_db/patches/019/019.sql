BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog, public;

drop table if exists remocra.hydrant_prescrit;

CREATE TABLE remocra.hydrant_prescrit
(
  id bigserial NOT NULL,
  date_prescrit timestamp without time zone,
  debit integer,
  geometrie geometry NOT NULL,
  nb_poteaux integer,
  organisme bigint,
  CONSTRAINT hydrant_prescrit_pkey PRIMARY KEY (id ),
  CONSTRAINT fkdc1ab241374add52 FOREIGN KEY (organisme)
      REFERENCES remocra.organisme (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

commit;