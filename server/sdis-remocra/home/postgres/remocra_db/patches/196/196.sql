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
    numero_patch := 196;
    description_patch := 'Remet toutes les séquences gérées par hibernate à jour';

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
SELECT setval('hydrant_indispo_temporaire_id_seq', (SELECT MAX(id) FROM remocra.hydrant_indispo_temporaire)+1);
SELECT setval('type_hydrant_indispo_statut_id_seq', (SELECT MAX(id) FROM remocra.type_hydrant_indispo_statut)+1);
SELECT setval('type_oldeb_acces_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_acces)+1);
SELECT setval('type_oldeb_action_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_action)+1);
SELECT setval('type_oldeb_anomalie_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_anomalie)+1);
SELECT setval('type_oldeb_avis_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_avis)+1);
SELECT setval('type_oldeb_caracteristique_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_caracteristique)+1);
SELECT setval('type_oldeb_categorie_anomalie_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_categorie_anomalie)+1);
SELECT setval('type_oldeb_categorie_caracteristique_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_categorie_caracteristique)+1);
SELECT setval('type_oldeb_debroussaillement_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_debroussaillement)+1);
SELECT setval('type_oldeb_residence_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_residence)+1);
SELECT setval('type_oldeb_zone_urbanisme_id_seq', (SELECT MAX(id) FROM remocra.type_oldeb_zone_urbanisme)+1);
-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;