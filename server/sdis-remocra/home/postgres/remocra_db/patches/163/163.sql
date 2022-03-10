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
    numero_patch := 163;
    description_patch :='Import CTP: changement des libelles';

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

INSERT INTO remocra.type_hydrant_importctp_erreur(code, libelle, type, message) VALUES
('ERR_VISITES_MANQUANTES', 'Visites manquantes', 'ERREUR', 'Le PEI ne dispose pas de visite de réception et de visite de reconnaissance opérationnelle initiale');

UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Erreur adresse' WHERE code = 'ERR_MAUVAIS_EXT';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Le déplacement d’un PEI est limité, supprimez l’ancien PEI et créer un nouveau PEI' WHERE code = 'WARN_DEPLACEMENT';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Coordonnées incompatibles au format annoncé' WHERE code = 'ERR_COORD_GPS';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'DECIsère : la date du CT doit doitdit être renseignée' WHERE code = 'ERR_DATE_MANQ';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'DECIsère : l''organisme doit être renseigné' WHERE code = 'ERR_AGENT1_ABS';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'La pression statique est obligatoire' WHERE code = 'WARN_PRESS_VIDE';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Erreur de saisie pression trop élevée' WHERE code = 'ERR_PRESS_ELEVEE';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Débit obligatoire (à 1b sinon au débit max)' WHERE code = 'WARN_DEBIT_VIDE';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Sans mesures (Q et P), le CT n’est pas recevable' WHERE code = 'WARN_DEB_PRESS_VIDE';
UPDATE remocra.type_hydrant_importctp_erreur SET message = 'Le débit doit être un nombre entier' WHERE code = 'INFO_TRONC_DEBIT';



-- Contenu réel du patch fin
--------------------------------------------------

commit;
