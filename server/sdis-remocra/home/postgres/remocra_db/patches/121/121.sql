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
    numero_patch := 121;
    description_patch := 'Création des contacts des Gestionnaires';

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

DROP TABLE IF EXISTS remocra.contact CASCADE;
CREATE TABLE remocra.contact
(
  id BIGSERIAL NOT NULL,
  appartenance CHARACTER VARYING  NOT NULL,
  id_appartenance CHARACTER VARYING,
  fonction CHARACTER VARYING,
  civilite  CHARACTER VARYING NOT NULL,
  nom CHARACTER VARYING NOT NULL,
  prenom CHARACTER VARYING NOT NULL,
  numero_voie CHARACTER VARYING,
  suffixe_voie CHARACTER VARYING,
  lieu_dit CHARACTER VARYING,
  voie CHARACTER VARYING NOT NULL,
  code_postal CHARACTER VARYING NOT NULL,
  ville CHARACTER VARYING NOT NULL,
  pays CHARACTER VARYING NOT NULL,
  telephone CHARACTER VARYING,
  email CHARACTER VARYING NOT NULL,
  CONSTRAINT contact_pkey PRIMARY KEY (id)
);


-- Table: remocra.role

-- DROP TABLE remocra.role;

CREATE TABLE remocra.role
(
  id BIGSERIAL NOT NULL,
  nom CHARACTER VARYING,
  version INTEGER,
  actif BOOLEAN NOT NULL DEFAULT true,
  code CHARACTER VARYING NOT NULL,
  thematique BIGINT NOT NULL ,
  CONSTRAINT role_pkey PRIMARY KEY (id),
  CONSTRAINT role_thematique FOREIGN KEY (thematique)
      REFERENCES remocra.thematique (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.role
  OWNER TO postgres;
GRANT ALL ON TABLE remocra.role TO postgres;
GRANT SELECT ON TABLE remocra.role TO remocra;


-- Table: remocra.contact_roles

-- DROP TABLE remocra.contact_roles;

CREATE TABLE remocra.contact_roles
(
  contact bigint NOT NULL,
  roles bigint NOT NULL,
  CONSTRAINT contact_roles_pkey PRIMARY KEY (contact, roles),
  CONSTRAINT contact_roles_contact FOREIGN KEY (contact)
      REFERENCES remocra.contact (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contact_roles_roles FOREIGN KEY (roles)
      REFERENCES remocra.role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.contact_roles
  OWNER TO postgres;
GRANT ALL ON TABLE remocra.contact_roles TO postgres;
GRANT SELECT ON TABLE remocra.contact_roles TO remocra;


-- Contenu réel du patch fin
--------------------------------------------------

commit;


