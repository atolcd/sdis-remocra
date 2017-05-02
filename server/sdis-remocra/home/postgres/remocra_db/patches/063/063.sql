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
    numero_patch := 63;
    description_patch := 'Métadonnées : trt extraction données';

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
--

-- Création de la table d'export
CREATE TABLE remocra.export_modele(
id bigserial NOT NULL, 					-- Identifiant interne autogénéré
categorie character varying, 			-- Code de catégorie permettant de regrouper les modèles d'exports entre eux (ex : EXPORT_FICHE_METADONNEE),
code character varying NOT NULL UNIQUE, -- Code exploité pour les échanges
libelle character varying NOT NULL, 	-- Libellé du modèle d'export affichable dans les listes déroulantes
description character varying, 			-- Description du modèle d'export
spatial boolean, 						-- Détermine si l'export doit être de type Spatial (SHP) ou CSV
source_sql character varying NOT NULL, 	-- Requête SQL générant un flux de données pourvant être exporté au sein d'un fichier
  CONSTRAINT export_modele_pkey PRIMARY KEY (id)
);

ALTER TABLE remocra.metadonnee ADD code_export character varying;

--  MODELE DE TRAITEMENT "réf les modèles d'export"
INSERT INTO pdi.modele_traitement(
	idmodele,
	code,
	nom,
	description,
	ref_chemin,
	ref_nom,
	type,
	message_echec,
	message_succes
)VALUES(
	23,
	0,
	'Référencer les modèles d''export de données',
	'Référence les modèles d''export de données.',
	'/demandes/export',
	'creer_liste_modeles',
	'T',
	3,
	1
);

--  MODELE DE TRAITEMENT "Exporter des données"
INSERT INTO pdi.modele_traitement(
	idmodele,
	code,
	nom,
	description,
	ref_chemin,
	ref_nom,
	type,
	message_echec,
	message_succes
)VALUES(
	24,
	0,
	'Exporter les données à partir d''un modèle',
	'Exporte les données à partir d''un modèle.',
	'/demandes/export',
	'exporter_donnees',
	'J',
	3,
	1
);

-- Paramètres pour : Exporter suivant le modèles d'export
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (83, 'Exporter les données à partir d''un modèle', 1, true, 'vue_modele_export', 'combo', NULL, 'MODELE_EXPORT_ID', 24);
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (84, 'Zone de compétence (emprise)', 2, true, 'vue_zone_competence', 'combo', NULL, 'ZONE_COMPETENCE_ID', 24);

-- Vues destinées aux paramêtre d'export

-- Vue pour lister les différents modèles d'export de disponibles
CREATE OR REPLACE VIEW pdi.vue_modele_export AS
SELECT
export_modele.id,
export_modele.libelle
FROM
remocra.export_modele
ORDER BY export_modele.libelle;

-- Vue pour lister les zones de compétences de l'application
CREATE OR REPLACE VIEW pdi.vue_zone_competence AS
SELECT
zone_competence.id,
zone_competence.nom as libelle
FROM
remocra.zone_competence
ORDER BY zone_competence.nom;

--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

