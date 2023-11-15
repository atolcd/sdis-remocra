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
	nameSchema varchar;
BEGIN
	FOR nameSchema IN (SELECT schema_name FROM information_schema.schemata) LOOP
		EXECUTE 'GRANT USAGE ON SCHEMA '|| nameSchema ||' TO read_only_user';
		EXECUTE 'GRANT USAGE ON ALL SEQUENCES IN SCHEMA '|| nameSchema ||' TO read_only_user';
		EXECUTE 'GRANT SELECT ON ALL TABLES IN SCHEMA '|| nameSchema ||' TO read_only_user';

		-- Permet d'affecter le droit de select aux futures tables du schéma
		EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA '|| nameSchema ||' GRANT SELECT ON TABLES TO read_only_user';
	END LOOP;
END$$;


-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
