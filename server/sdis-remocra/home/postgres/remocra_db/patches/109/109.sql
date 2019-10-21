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
    numero_patch := 109;
    description_patch := 'Mise à jour de la carte crise (Operationnel /anticipation)';

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

ALTER TABLE remocra.crise RENAME COLUMN carte TO carte_ant;
ALTER TABLE remocra.crise ADD COLUMN carte_op CHARACTER VARYING ;

COMMENT ON COLUMN remocra.crise.carte_ant IS 'Groupe de couches complémentaires à carte.json (anticipation)'; 
COMMENT ON COLUMN remocra.crise.carte_op IS 'Groupe de couches complémentaires à carte.json (operationnel)'; 

 
-- Contenu réel du patch fin
--------------------------------------------------

commit;

