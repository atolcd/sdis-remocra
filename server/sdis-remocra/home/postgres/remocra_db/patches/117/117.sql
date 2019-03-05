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
    numero_patch := 117;
    description_patch := 'Organismes et organismes parents';

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

ALTER TABLE remocra.organisme ADD COLUMN organisme_parent bigint;
ALTER TABLE remocra.organisme ALTER COLUMN organisme_parent SET DEFAULT null;
UPDATE remocra.organisme SET organisme_parent=null;

ALTER TABLE remocra.organisme
ADD CONSTRAINT fk_organisme_parent
FOREIGN KEY (organisme_parent)
REFERENCES remocra.organisme(id) MATCH SIMPLE;

ALTER TABLE remocra.type_organisme ADD COLUMN type_organisme_parent bigint;
ALTER TABLE remocra.type_organisme ALTER COLUMN type_organisme_parent SET DEFAULT null;
UPDATE remocra.type_organisme SET type_organisme_parent=null;

ALTER TABLE remocra.type_organisme
ADD CONSTRAINT fk_type_organisme_parent
FOREIGN KEY (type_organisme_parent)
REFERENCES remocra.type_organisme(id) MATCH SIMPLE;

CREATE INDEX organisme_organisme_parent_idx ON remocra.organisme USING btree (organisme_parent);
CREATE INDEX type_organisme_type_organisme_parent_idx ON remocra.type_organisme USING btree (type_organisme_parent);

-- Contenu réel du patch fin
--------------------------------------------------

commit;
