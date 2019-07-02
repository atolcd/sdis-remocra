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
    numero_patch := 119;
    description_patch := 'Fiche PEI: ajout de droits';

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
VALUES('HYDRANTS_VISITE_NP_D', 'Supprimer des visites non programmées', 'hydrants.visite_np_D', 1, 'Module PEI'),
('HYDRANTS_VISITE_RECO_D', 'Supprimer des visites de reconnaissance opérationnelle initiale', 'hydrants.visite_reco_D', 1, 'Module PEI'),
('HYDRANTS_VISITE_CTRL_D', 'Supprimer des visites de contrôle technique périodique', 'hydrants.visite_ctrl_D', 1, 'Module PEI'),
('HYDRANTS_GESTIONNAIRE_C', 'Créer, éditer les gestionnaires des PEI', 'hydrants.gestionnaire_C', 1, 'Module PEI');

-- Suppression d'un droit obsolète
DELETE FROM remocra.droit WHERE type_droit = (SELECT id FROM remocra.type_droit WHERE code = 'HYDRANTS_NUMEROTATION_R');
DELETE FROM remocra.type_droit WHERE code = 'HYDRANTS_NUMEROTATION_R';

ALTER TABLE remocra.hydrant ADD COLUMN en_face BOOLEAN DEFAULT FALSE;

-- Contenu réel du patch fin
--------------------------------------------------

commit;
