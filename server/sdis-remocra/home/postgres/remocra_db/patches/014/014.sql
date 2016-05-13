SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;


CREATE TABLE remocra.hydrant_prescrit
(
  id bigserial NOT NULL,
  complement character varying,
  date_creation timestamp without time zone,
  geometrie geometry NOT NULL,
  lieu_dit character varying,
  voie character varying,
  voie2 character varying,
  commune bigint,
  nature bigint,
  organisme bigint,
  CONSTRAINT hydrant_prescrit_pkey PRIMARY KEY (id ),
  CONSTRAINT fkdc1ab241374add52 FOREIGN KEY (organisme)
      REFERENCES remocra.organisme (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkdc1ab241d10a0428 FOREIGN KEY (nature)
      REFERENCES remocra.type_hydrant_nature (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkdc1ab241d2da796c FOREIGN KEY (commune)
      REFERENCES remocra.commune (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

COMMIT;

