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
    numero_patch := 128;
    description_patch := 'Suivi de la date de visite de réception des PEI';

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

-- Suivi de la date de réception
UPDATE remocra.type_hydrant_saisie SET nom = 'Visite de réception'
WHERE code = 'CREA';

ALTER TABLE remocra.hydrant
ADD COLUMN date_crea TIMESTAMP WITHOUT TIME ZONE;

-- Renommage requete_fiche => hydrant_resume, ajout d'une contrainte d'unicité sur le code
ALTER TABLE remocra.requete_fiche
RENAME TO hydrant_resume;

ALTER TABLE remocra.hydrant_resume
ADD CONSTRAINT code_unique UNIQUE (code);

-- Droit modification caractéristiques techniques d'un PEI
UPDATE remocra.type_droit
SET description = 'Modifier les caractéristiques techniques'
WHERE code = 'HYDRANTS_MCO_C';

-- Résumé PEI: changement du format des arguments
UPDATE remocra.hydrant_resume
SET source_sql = REPLACE(source_sql, ':id', '${HYDRANT_ID}');

-- Correction erreur description manquante au patch 118
UPDATE remocra.suivi_patches
SET description = 'Adaptation du module points d''eau prescrits'
WHERE numero = 118;

-- Zoom sur les adresses adresse.data.gouv.fr
INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES
('HYDRANT_ZOOM_NUMERO', 'Autoriser le zoom sur une adresse adresse.data.gouv.fr', false, 1, 'Points d''eau');

COMMENT ON COLUMN remocra.debit_simultane_hydrant.debit IS 'Debit_simultane_mesure lié';
COMMENT ON COLUMN remocra.hydrant_resume.code IS 'Code SQL de récupération des valeurs (renvoie un XML)';
COMMENT ON COLUMN remocra.hydrant_visite.anomalies IS 'Tableau des identifiants des anomalies du PEI séparées par une virgule ( [] si aucune';
-- Contenu réel du patch fin
--------------------------------------------------

commit;
