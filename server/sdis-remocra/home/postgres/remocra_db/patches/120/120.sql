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
    numero_patch := 120;
    description_patch := 'Fiche PEI: ajout de droits de suppression des visites manquants';

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

SELECT pg_catalog.setval('remocra.type_droit_id_seq', (select max(id) from remocra.type_droit));

INSERT INTO remocra.type_droit (code, description, nom, version, categorie)
VALUES('HYDRANTS_VISITE_CREA_D', 'Supprimer des visites de réception (création)', 'hydrants.visite_crea_D', 1, 'Module PEI'),
('HYDRANTS_VISITE_RECEP_D', 'Supprimer des visites de reconnaissance opérationnelle initiale', 'hydrants.visite_recep_D', 1, 'Module PEI');

UPDATE remocra.type_droit set description = 'Supprimer des visites de reconnaissance opérationnelle périodique' where code = 'HYDRANTS_VISITE_RECO_D';

-- Contenu réel du patch fin
--------------------------------------------------

commit;
