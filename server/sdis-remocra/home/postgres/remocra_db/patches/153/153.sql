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
    numero_patch := 153;
    description_patch := 'Role user_template';

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


CREATE ROLE user_template NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOLOGIN;
GRANT CONNECT ON DATABASE remocra TO user_template;
GRANT USAGE ON SCHEMA remocra TO user_template;
GRANT USAGE ON SCHEMA remocra_referentiel TO user_template;
GRANT USAGE ON SCHEMA prevarisc TO user_template;
GRANT USAGE ON SCHEMA tracabilite TO user_template;
GRANT USAGE ON SCHEMA remocra_sig TO user_template;
GRANT USAGE ON SCHEMA sdis_referentiel TO user_template;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA remocra TO user_template;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA remocra TO user_template;
GRANT ALL ON ALL TABLES IN SCHEMA remocra_referentiel TO user_template;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA remocra_referentiel TO user_template;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA prevarisc TO user_template;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA prevarisc TO user_template;
GRANT SELECT ON ALL TABLES IN SCHEMA tracabilite TO user_template;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA remocra_sig TO user_template;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA remocra_sig TO user_template;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA sdis_referentiel TO user_template;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA sdis_referentiel TO user_template;

-- Contenu réel du patch fin
--------------------------------------------------

commit;
