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
    numero_patch := 189;
    description_patch := 'Ajoute un user template read_only';

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

CREATE ROLE read_only_user NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT NOLOGIN;
GRANT CONNECT ON DATABASE remocra TO read_only_user;

DO $$
DECLARE
    schema_name varchar;
BEGIN
    FOR schema_name IN (SELECT DISTINCT table_schema FROM information_schema.role_table_grants WHERE grantee = 'remocra' AND lower(is_grantable) = 'yes') LOOP
            EXECUTE 'GRANT USAGE ON SCHEMA '|| schema_name ||' TO read_only_user';
            EXECUTE 'GRANT USAGE ON ALL SEQUENCES IN SCHEMA '|| schema_name ||' TO read_only_user';
        END LOOP;
    FOR schema_name IN (SELECT DISTINCT object_schema FROM information_schema.role_usage_grants WHERE grantee = 'remocra' AND lower(is_grantable) = 'yes') LOOP
            EXECUTE 'GRANT SELECT ON ALL TABLES IN SCHEMA '|| schema_name ||' TO read_only_user';
            EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA '|| schema_name ||' GRANT SELECT ON TABLES TO read_only_user';
        END LOOP;
END$$;

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;