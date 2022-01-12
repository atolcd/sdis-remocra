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
    numero_patch := 162;
    description_patch :='Erreurs import CTP';

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

CREATE TABLE remocra.type_hydrant_importctp_erreur (
  id serial,
  code CHARACTER VARYING,
  libelle CHARACTER VARYING,
  type CHARACTER VARYING,
  message CHARACTER VARYING
);
ALTER TABLE remocra.type_hydrant_importctp_erreur OWNER TO remocra;

INSERT INTO remocra.type_hydrant_importctp_erreur(code, libelle, type, message) VALUES
('ERR_FICHIER_INNAC', 'Fichier inaccessible', 'ERREUR', 'DECIsère : Impossible d''ouvrir le fichier'),
('ERR_MAUVAIS_EXT', 'Mauvaise extension', 'ERREUR', 'DECIsère : Impossible d''ouvrir le fichier : mauvaise extension'),
('ERR_ONGLET_ABS', 'Onglet “Saisies_resultats_CT” manquant', 'ERREUR', 'DECIsère : L''onglet “Saisies_resultats_CT”  n''est pas présent dans le fichier.'),
('ERR_MAUVAIS_NUM_PEI', 'Incohérence entre id PEI et numéro global', 'ERREUR', 'DECIsère : le PEI avec le code SDIS renseigné ne correspond à aucun PEI pour cette commune et ce numéro'),
('INFO_IGNORE', 'Ligne ignorée', 'INFO', 'DECIsère : Le contrôle technique de ce PEI n''est pas renseigné.'),
('ERR_DEHORS_ZC', 'PEI en dehors de la zone de compétence de l’AP', 'ERREUR', 'DECIsère : le PEI n''est pas dans votre zone de compétence'),
('WARN_DEPLACEMENT', 'Distance entre ancienne et nouvelle position trop importante', 'WARNING', 'DECIsère : Distance entre ancienne et nouvelle position trop importante'),
('ERR_COORD_GPS', 'Mauvais format des coordonnées GPS', 'ERREUR', 'DECIsère : Mauvais format des coordonnées GPS'),
('WARN_DATE_ANTE', 'Date antérieure ou égale à un autre CTP', 'WARNING', 'DECISère : attention, il existe un CT réalisé plus récent ou à la même date'),
('ERR_DATE_POST', 'Date postérieure à date du jour', 'ERREUR', 'DECIsère : la date du CT ne doit pas être postérieure à la date du jour'),
('ERR_FORMAT_DATE', 'Mauvais format de date', 'ERREUR', 'DECIsère : “la date du CT n''est pas au bon format”'),
('ERR_DATE_MANQ', 'Date de CTP manquante', 'ERREUR', 'DECIsère : la date du CT ne doit pas être vide'),
('ERR_AGENT1_ABS', 'Organisme vérificateur manquant', 'ERREUR', 'DECIsère : l''organisme ne doit pas être vide'),
('WARN_PRESS_VIDE', 'Débit rempli mais pression statique vide', 'WARNING', 'Débit rempli mais pression statique vide'),
('ERR_PRESS_ELEVEE', 'Mauvaise extension', 'ERREUR', 'Pression statique supérieur à 20 bar'),
('ERR_FORMAT_PRESS', 'Valeur de pression statique impossible (lettre, signe, …)', 'ERREUR', 'DECIsère : la pression statique n''est pas au bon format'),
('WARN_DEBIT_VIDE', 'Pression remplie mais débit vide', 'WARNING', 'Pression renseignée mais débit vide'),
('ERR_FORMAT_DEBIT', 'Valeur de débit impossible (lettre, signe, …)', 'ERREUR', 'DECIsère : le débit mesuré à 1 bar ou plus n''est pas au bon format'),
('WARN_DEB_PRESS_VIDE', 'Date remplie mais débit/pression vide', 'WARNING', 'Date renseignée mais débit/pression vide'),
('INFO_TRONC_DEBIT', 'Troncature de la valeur saisie (décimal renseigné)', 'INFO', 'Troncature de la valeur saisie (décimal renseigné)'),
('ERR_ANO_INCONNU', 'Anomalies inconnues', 'ERREUR', 'Au moins une anomalie renseignée est inconnue');

INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES
('HYDRANT_DEPLACEMENT_DIST_WARN', 'Distance de déplacement minimale pour laquelle afficher un avertissement lors de l''import de Contrôles Techniques Périodiques', 10, 1, 'Points d''eau');


-- Contenu réel du patch fin
--------------------------------------------------

commit;
