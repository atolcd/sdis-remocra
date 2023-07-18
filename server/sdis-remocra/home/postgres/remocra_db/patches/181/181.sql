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
    numero_patch := 181;
    description_patch := 'Ajoute un paramètre de configuration + des types de droit pour autoriser ou non la création depuis l''application mobile';

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

INSERT INTO remocra.param_conf (cle, description, valeur, "version", nomgroupe)
VALUES('CREATION_PEI_MOBILE', 'Aurtorise la création de Point d''eau depuis l''application mobile', 'true', 1, 'Mobile');

INSERT INTO remocra.type_droit (code, description, nom, "version", categorie)
VALUES('CREATION_PEI_MOBILE', 'Droit de création des hydrants depuis l''application mobile', 'CREATION_PEI_MOBILE', 1, 'Mobile');

INSERT INTO remocra.type_droit (code, description, nom, "version", categorie)
VALUES('CREATION_GESTIONNAIRE_MOBILE', 'Donne à l''utilisateur les droits pour la création / modification d''un gestionnaire depuis l''appli mobile', 'CREATION_GESTIONNAIRE_MOBILE', 1, 'Mobile');

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
