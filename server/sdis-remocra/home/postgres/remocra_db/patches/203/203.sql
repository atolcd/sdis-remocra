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
    numero_patch := 203;
    description_patch := 'Ajoute un paramètre pour brider la taille des photos';

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
INSERT INTO remocra.parametre (cle_parametre, valeur_parametre, type_parametre, description_parametre, categorie_parametre)
VALUES('BRIDAGE_PHOTO', 'true', 'BOOLEAN', 'Brider la taille des photos prise sur l''application mobile', 'MOBILE')
ON CONFLICT (cle_parametre) DO NOTHING;


-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
