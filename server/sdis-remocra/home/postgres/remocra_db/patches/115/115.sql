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
    numero_patch := 115;
    description_patch := 'Ajout de la nature de DECI des PEI (public, privé, sous convention)';

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

CREATE TABLE remocra.type_hydrant_nature_deci
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  version integer default 1,
  CONSTRAINT type_hydrant_nature_deci_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE remocra.type_hydrant_nature_deci
  IS 'Statut du PEI (ex: privé, public, privé sous convention)';

INSERT INTO remocra.type_hydrant_nature_deci VALUES
(1, true, 'PRIVE', 'Privé'),
(2, true, 'PUBLIC', 'Public'),
(3, true, 'CONVENTIONNE', 'Privé sous convention');

ALTER TABLE remocra.hydrant ADD COLUMN nature_deci bigint DEFAULT 2;

ALTER TABLE remocra.hydrant
ADD CONSTRAINT fk_nature_deci
FOREIGN KEY (nature_deci)
REFERENCES remocra.type_hydrant_nature_deci(id) MATCH SIMPLE;

CREATE INDEX hydrant_nature_deci_idx ON remocra.hydrant USING btree (nature_deci);
SELECT pg_catalog.setval('remocra.type_hydrant_nature_deci_id_seq', (select max(id) from remocra.type_hydrant_nature_deci));

-- Contenu réel du patch fin
--------------------------------------------------

commit;
