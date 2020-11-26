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
    numero_patch := 139;
    description_patch :='Ajout de droit de consultation et url du dashboard';

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

INSERT INTO remocra.type_droit(code, description, nom, categorie, version) VALUES
('DASHBOARD_R', 'Consulter le tableau de board', 'dashboard_R', 'Général', 1);

INSERT INTO remocra.param_conf(cle, description, valeur, version) VALUES ('DASHBOARD_BASE_URL', 'URL de base complète du serveur tableau de board', 'http://localhost:5000/', 1);


-- Contenu réel du patch fin
--------------------------------------------------

commit;
