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
    numero_patch := 161;
    description_patch := 'Gestion du couple débit, pression dynamique';

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
ALTER TABLE remocra.hydrant_visite
ADD COLUMN debit_autre INTEGER,
ADD COLUMN pression_dyn_autre DOUBLE PRECISION;

ALTER TABLE remocra.hydrant_pibi
ADD COLUMN debit_autre INTEGER,
ADD COLUMN pression_dyn_autre DOUBLE PRECISION;

COMMENT ON COLUMN remocra.hydrant_pibi.debit_autre IS 'Débit à une autre pression dynamique (m3/h)';
COMMENT ON COLUMN remocra.hydrant_pibi.pression_dyn_autre IS 'Pression dynamique (bar)';

-- Contenu réel du patch fin
--------------------------------------------------

commit;
