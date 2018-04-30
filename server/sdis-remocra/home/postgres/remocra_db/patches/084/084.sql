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
    numero_patch := 84;
    description_patch := 'Module de recherches et analyses & index';

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


-- table remocra.requete_modele
DROP TABLE IF EXISTS remocra.requete_modele CASCADE;
CREATE TABLE remocra.requete_modele (
	id bigserial NOT NULL,
	categorie character varying NOT NULL,
	code character varying NOT NULL,
	libelle character varying NOT NULL,
	description character varying,
	spatial boolean,
	source_sql character varying NOT NULL,
	CONSTRAINT requete_modele_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.requete_modele IS 'Modèle de requête exploité par REMOCRA';
COMMENT ON COLUMN remocra.requete_modele.id IS 'Identifiant interne auto-généré';
COMMENT ON COLUMN remocra.requete_modele.categorie IS 'Libellé de catégorie permettant de regrouper ou de filtrer potentiellement les modèles selon un thème. Non exploité actuellement dans REMOCRA';
COMMENT ON COLUMN remocra.requete_modele.code IS 'Code permettant d''identifier de manière unique et pérenne un modèle de requête';
COMMENT ON COLUMN remocra.requete_modele.libelle IS 'Libellé affiché en liste déroulante lors du choix du modèle de requête';
COMMENT ON COLUMN remocra.requete_modele.description IS 'Description longue du modèle de requête. Non exploité actuellement dans ReMOCRA';
COMMENT ON COLUMN remocra.requete_modele.spatial IS 'Le résultat possède une colonne nommée "wkt" contenant de l''information géographique encodée au format WKT';
COMMENT ON COLUMN remocra.requete_modele.source_sql IS 'Definition de la requête SQL à exécuter sur la base de données REMOCRA pour fournir les informations à retourner. A renseigner dans un CDATA. La requête peut exploiter des paramètres sous la forme ${NOM_DU_PARAMETRE}. Chaque paramètre mentionné doit être référencé dans la table "requete_modele_parametre"';


-- table remocra.requete_modele_parametre
DROP TABLE IF EXISTS remocra.requete_modele_parametre CASCADE;
CREATE TABLE remocra.requete_modele_parametre (
	id bigserial NOT NULL,
	nom character varying  NOT NULL,
	type_valeur character varying NOT NULL,
	obligatoire boolean NOT NULL,
	source_sql character varying,
	source_sql_valeur character varying,
	source_sql_libelle character varying,
	formulaire_etiquette character varying NOT NULL,
	formulaire_type_controle character varying NOT NULL,
	formulaire_num_ordre bigint NOT NULL,
	formulaire_valeur_defaut character varying,
	requete_modele bigint NOT NULL,
	CONSTRAINT requete_modele_parametre_pkey PRIMARY KEY (id),
	CONSTRAINT requete_modele_parametre_requete_modele FOREIGN KEY (requete_modele) REFERENCES remocra.requete_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

COMMENT ON TABLE remocra.requete_modele_parametre IS 'Paramètre associé à un modèle de requête';
COMMENT ON COLUMN remocra.requete_modele_parametre.id IS 'Identifiant interne auto-généré';
COMMENT ON COLUMN remocra.requete_modele_parametre.nom IS 'Nom du paramètre. Sans espace ni caractère accentué. De préférence en majuscule';
COMMENT ON COLUMN remocra.requete_modele_parametre.type_valeur IS 'Type de valeur attendue par la requête SQL';
COMMENT ON COLUMN remocra.requete_modele_parametre.obligatoire IS 'La saisie d''une valeur pour le parametre est obligatoire';
COMMENT ON COLUMN remocra.requete_modele_parametre.source_sql IS 'Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA pour fournir les informations facilitantou limitant la saisie de valeurs pour l''utilisateur. A renseigner dans un CDATA';
COMMENT ON COLUMN remocra.requete_modele_parametre.source_sql_valeur IS 'Colonne de la requête SQL éventuelle contenant la valeur du paramètre';
COMMENT ON COLUMN remocra.requete_modele_parametre.source_sql_libelle IS 'Colonne de la requête SQL éventuelle contenant le libellé associé à la valeur du paramètre';
COMMENT ON COLUMN remocra.requete_modele_parametre.formulaire_etiquette IS 'Etiquette associée au champ de saisie';
COMMENT ON COLUMN remocra.requete_modele_parametre.formulaire_type_controle IS 'Type de contrôle associé au champ de saisie';
COMMENT ON COLUMN remocra.requete_modele_parametre.formulaire_num_ordre IS 'Position d''affichage du champ de saisie dans le formulaire';
COMMENT ON COLUMN remocra.requete_modele_parametre.formulaire_valeur_defaut IS 'Valeur par défaut proposée dans le champ de saisie';
COMMENT ON COLUMN remocra.requete_modele_parametre.requete_modele IS 'Modèle de requête parent';

-- table remocra.requete_modele_droit
DROP TABLE IF EXISTS remocra.requete_modele_droit CASCADE;
CREATE TABLE remocra.requete_modele_droit (
	requete_modele bigint,
	profil_droit bigint,
	CONSTRAINT requete_modele_droit_pkey PRIMARY KEY (requete_modele,profil_droit),
	CONSTRAINT requete_modele_droit_requete_modele FOREIGN KEY (requete_modele) REFERENCES remocra.requete_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT requete_modele_droit_profil_droit FOREIGN KEY (profil_droit) REFERENCES remocra.profil_droit (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
COMMENT ON TABLE remocra.requete_modele_droit IS 'Profil de droit autorisé pour executer une requête';
COMMENT ON COLUMN remocra.requete_modele_droit.requete_modele  IS 'Identifiant du modèle de requête';
COMMENT ON COLUMN remocra.requete_modele_droit.profil_droit  IS 'Identifiant du profil de droit autorisé';


--table remocra.requete_modele_selection
DROP TABLE IF EXISTS remocra.requete_modele_selection CASCADE;
CREATE TABLE remocra.requete_modele_selection
(
	id bigserial NOT NULL,
	requete character varying,
	modele bigint NOT NULL,
	utilisateur bigint NOT NULL,
	date timestamp without time zone,
        etendu geometry,
	CONSTRAINT uk_requete_modele_selection_modele_utilisateur UNIQUE (modele, utilisateur),
	CONSTRAINT requete_modele_selection_pkey PRIMARY KEY (id),
	CONSTRAINT requete_modele_selection_utilisateur FOREIGN KEY (utilisateur) REFERENCES remocra.utilisateur (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT requete_modele_selection_modele FOREIGN KEY (modele) REFERENCES remocra.requete_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
COMMENT ON TABLE remocra.requete_modele_selection IS 'Requêtes personnalisées lancées par utilisateur dans REMOCRA';
COMMENT ON COLUMN remocra.requete_modele_selection.id IS 'Identifiant interne auto-généré';
COMMENT ON COLUMN remocra.requete_modele_selection.requete IS 'Requête joué par l''utilisateur';
COMMENT ON COLUMN remocra.requete_modele_selection.modele IS 'Identifiant du modèle de requête utilisé';
COMMENT ON COLUMN remocra.requete_modele_selection.utilisateur IS 'Utilisateur aillant lancé la requête';
COMMENT ON COLUMN remocra.requete_modele_selection.date IS 'Heure d''execution de la requête';

-- table remocra.requete_modele_selection_setail
DROP TABLE IF EXISTS remocra.requete_modele_selection_detail CASCADE;
CREATE TABLE remocra.requete_modele_selection_detail
(
	id bigserial NOT NULL,
	selection bigint NOT NULL,
	geometrie geometry,
	CONSTRAINT requete_modele_selection_detail_pkey PRIMARY KEY (id),
	CONSTRAINT requete_modele_selection FOREIGN KEY (selection) REFERENCES remocra.requete_modele_selection (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
COMMENT ON TABLE remocra.requete_modele_selection_detail IS 'Détail des éléments des requêtes personnalisées REMOCRA';
COMMENT ON COLUMN remocra.requete_modele_selection_detail.id IS 'Identifiant interne auto-généré';
COMMENT ON COLUMN remocra.requete_modele_selection_detail.selection IS 'Sélection parente';
COMMENT ON COLUMN remocra.requete_modele_selection_detail.geometrie IS 'Géométrie des éléments sélectionnés';
CREATE INDEX requete_modele_selection_detail_geometrie_idx ON remocra.requete_modele_selection_detail USING gist(geometrie);
CREATE INDEX requete_modele_selection_detail_selection_idx ON remocra.requete_modele_selection_detail(selection);


INSERT INTO pdi.modele_traitement(
           idmodele, code, description, nom, ref_chemin, ref_nom, type,
           message_echec, message_succes)
   VALUES ((SELECT MAX(idmodele)+1 FROM pdi.modele_traitement), -1, 'Génère les exports des requêtes personnalisées', 'Générer export requêtes personnalisées', '/demandes/requetes_personnalisees/', 'extraire_selection', 'J',
           3, 1);

INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Requête personnalisée spatiale', 1, true, '',
            'checkbox', null, 'SPATIAL', (SELECT MAX(idmodele) FROM pdi.modele_traitement));

INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Requête personnalisée', 2, true, '',
            'textfield', null, 'REQUETE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));

INSERT INTO remocra.param_conf(
            cle, description, valeur, version, nomgroupe)
    VALUES ('PDI_PURGE_REQUETE_HEURES', 'Intervalle de temps pour la suppression des requêtes personnalisées lancées par les utilisateurs', '3', 1, 'Traitements et purge');

INSERT INTO remocra.param_conf(
            cle, description, valeur, version, nomgroupe)
     VALUES ('ID_TRAITEMENT_REQUETAGE', 'Traitement pour l''export des données du module requêtage',  (SELECT MAX(idmodele) FROM pdi.modele_traitement), 1, 'Traitements et purge');


INSERT INTO remocra.type_droit (code, nom, description, categorie, version) values
  ('HYDRANTS_ANALYSE_C', 'hydrants.analyse_C', 'Analyser les résultats des requêtes', 'Module PEI', '1');

INSERT INTO pdi.modele_traitement(
           idmodele, code, description, nom, ref_chemin, ref_nom, type,
           message_echec, message_succes)
   VALUES ((SELECT MAX(idmodele)+1 FROM pdi.modele_traitement), 0, 'Référencer les modèles de requêtes', 'Référence les modèles de requête personnalisée', '/demandes/requetes_personnalisees/referencement_requetes_personnalisees', 'creer_liste_modeles_requetes', 'J',
           3, 1);

--creation des indexs

CREATE INDEX alerte_rapporteur_idx ON remocra.alerte USING btree (rapporteur);
CREATE INDEX alerte_document_alerte_idx ON remocra.alerte_document USING btree (alerte);
CREATE INDEX alerte_document_document_idx ON remocra.alerte_document USING btree (document);
CREATE INDEX alerte_elt_alerte_idx ON remocra.alerte_elt USING btree (alerte);
CREATE INDEX alerte_elt_sous_type_alerte_elt_idx ON remocra.alerte_elt USING btree (sous_type_alerte_elt);
CREATE INDEX alerte_elt_ano_alerte_elt_idx ON remocra.alerte_elt_ano USING btree (alerte_elt);
CREATE INDEX alerte_elt_ano_type_alerte_ano_idx ON remocra.alerte_elt_ano USING btree (type_alerte_ano);
CREATE INDEX bloc_document_document_idx ON remocra.bloc_document USING btree (document);
CREATE INDEX courrier_document_document_idx ON remocra.courrier_document USING btree (document);
CREATE INDEX dde_mdp_utilisateur_idx ON remocra.dde_mdp USING btree (utilisateur);
CREATE INDEX depot_document_document_idx ON remocra.depot_document USING btree (document);
CREATE INDEX depot_document_utilisateur_idx ON remocra.depot_document USING btree (utilisateur);
CREATE INDEX hydrant_commune_idx ON remocra.hydrant USING btree (commune);
CREATE INDEX hydrant_domaine_idx ON remocra.hydrant USING btree (domaine);
CREATE INDEX hydrant_nature_idx ON remocra.hydrant USING btree (nature);
CREATE INDEX hydrant_tournee_idx ON remocra.hydrant USING btree (tournee);
CREATE INDEX hydrant_organisme_idx ON remocra.hydrant USING btree (organisme);
CREATE INDEX hydrant_zone_speciale_idx ON remocra.hydrant USING btree (zone_speciale);
CREATE INDEX hydrant_utilisateur_modification_idx ON remocra.hydrant USING btree (utilisateur_modification);
CREATE INDEX hydrant_document_document_idx ON remocra.hydrant_document USING btree (document);
CREATE INDEX hydrant_document_hydrant_idx ON remocra.hydrant_document USING btree (hydrant);
CREATE INDEX hydrant_indispo_temporaire_statut_idx ON remocra.hydrant_indispo_temporaire USING btree (statut);
CREATE INDEX hydrant_pena_materiau_idx ON remocra.hydrant_pena USING btree (materiau);
CREATE INDEX hydrant_pena_positionnement_idx ON remocra.hydrant_pena USING btree (positionnement);
CREATE INDEX hydrant_pena_vol_constate_idx ON remocra.hydrant_pena USING btree (vol_constate);
CREATE INDEX hydrant_pibi_diametre_idx ON remocra.hydrant_pibi USING btree (diametre);
CREATE INDEX hydrant_pibi_marque_idx ON remocra.hydrant_pibi USING btree (marque);
CREATE INDEX hydrant_pibi_modele_idx ON remocra.hydrant_pibi USING btree (modele);
CREATE INDEX hydrant_pibi_pena_idx ON remocra.hydrant_pibi USING btree (pena);
CREATE INDEX hydrant_prescrit_organisme_idx ON remocra.hydrant_prescrit USING btree (organisme);
CREATE INDEX metadonnee_thematique_idx ON remocra.metadonnee USING btree (thematique);
CREATE INDEX oldeb_zone_urbanisme_idx ON remocra.oldeb USING btree (zone_urbanisme);
CREATE INDEX oldeb_acces_idx ON remocra.oldeb USING btree (acces);
CREATE INDEX oldeb_propriete_oldeb_idx ON remocra.oldeb_propriete USING btree (oldeb);
CREATE INDEX oldeb_propriete_proprietaire_idx ON remocra.oldeb_propriete USING btree (proprietaire);
CREATE INDEX oldeb_propriete_residence_idx ON remocra.oldeb_propriete USING btree (residence);
CREATE INDEX oldeb_visite_oldeb_idx ON remocra.oldeb_visite USING btree (oldeb);
CREATE INDEX oldeb_visite_utilisateur_idx ON remocra.oldeb_visite USING btree (utilisateur);
CREATE INDEX oldeb_visite_debroussaillement_parcelle_idx ON remocra.oldeb_visite USING btree (debroussaillement_parcelle);
CREATE INDEX oldeb_visite_debroussaillement_acces_idx ON remocra.oldeb_visite USING btree (debroussaillement_acces);
CREATE INDEX oldeb_visite_avis_idx ON remocra.oldeb_visite USING btree (avis);
CREATE INDEX oldeb_visite_action_idx ON remocra.oldeb_visite USING btree (action);
CREATE INDEX oldeb_visite_document_document_idx ON remocra.oldeb_visite_document USING btree (document);
CREATE INDEX oldeb_visite_document_visite_idx ON remocra.oldeb_visite_document USING btree (visite);
CREATE INDEX oldeb_visite_suite_visite_idx ON remocra.oldeb_visite_suite USING btree (visite);
CREATE INDEX oldeb_visite_suite_suite_idx ON remocra.oldeb_visite_suite USING btree (suite);
CREATE INDEX organisme_profil_organisme_idx ON remocra.organisme USING btree (profil_organisme);
CREATE INDEX organisme_type_organisme_idx ON remocra.organisme USING btree (type_organisme);
CREATE INDEX organisme_zone_competence_idx ON remocra.organisme USING btree (zone_competence);
CREATE INDEX permis_avis_idx ON remocra.permis USING btree (avis);
CREATE INDEX permis_commune_idx ON remocra.permis USING btree (commune);
CREATE INDEX permis_instructeur_idx ON remocra.permis USING btree (instructeur);
CREATE INDEX permis_interservice_idx ON remocra.permis USING btree (interservice);
CREATE INDEX permis_service_instructeur_idx ON remocra.permis USING btree (service_instructeur);
CREATE INDEX permis_document_document_idx ON remocra.permis_document USING btree (document);
CREATE INDEX permis_document_permis_idx ON remocra.permis_document USING btree (permis);
CREATE INDEX profil_organisme_type_organisme_idx ON remocra.profil_organisme USING btree (type_organisme);
CREATE INDEX profil_organisme_utilisateur_droit_profil_droit_idx ON remocra.profil_organisme_utilisateur_droit USING btree (profil_droit);
CREATE INDEX profil_utilisateur_type_organisme_idx ON remocra.profil_utilisateur USING btree (type_organisme);
CREATE INDEX rci_categorie_promethee_idx ON remocra.rci USING btree (categorie_promethee);
CREATE INDEX rci_commune_idx ON remocra.rci USING btree (commune);
CREATE INDEX rci_degre_certitude_idx ON remocra.rci USING btree (degre_certitude);
CREATE INDEX rci_origine_alerte_idx ON remocra.rci USING btree (origine_alerte);
CREATE INDEX rci_utilisateur_idx ON remocra.rci USING btree (utilisateur);
CREATE INDEX rci_arrivee_ddtm_onf_idx ON remocra.rci USING btree (arrivee_ddtm_onf);
CREATE INDEX rci_arrivee_sdis_idx ON remocra.rci USING btree (arrivee_sdis);
CREATE INDEX rci_arrivee_gendarmerie_idx ON remocra.rci USING btree (arrivee_gendarmerie);
CREATE INDEX rci_arrivee_police_idx ON remocra.rci USING btree (arrivee_police);
CREATE INDEX rci_famille_promethee_idx ON remocra.rci USING btree (famille_promethee);
CREATE INDEX rci_partition_promethee_idx ON remocra.rci USING btree (partition_promethee);
CREATE INDEX rci_document_document_idx ON remocra.rci_document USING btree (document);
CREATE INDEX rci_document_rci_idx ON remocra.rci_document USING btree (rci);
CREATE INDEX requete_modele_parametre_requete_modele_idx ON remocra.requete_modele_parametre USING btree (requete_modele);
CREATE INDEX requete_modele_selection_utilisateur_idx ON remocra.requete_modele_selection USING btree (utilisateur);
CREATE INDEX sous_type_alerte_elt_type_alerte_elt_idx ON remocra.sous_type_alerte_elt USING btree (type_alerte_elt);
CREATE INDEX synchronisation_thematique_idx ON remocra.synchronisation USING btree (thematique);
CREATE INDEX tournee_affectation_idx ON remocra.tournee USING btree (affectation);
CREATE INDEX tournee_reservation_idx ON remocra.tournee USING btree (reservation);
CREATE INDEX type_hydrant_anomalie_critere_idx ON remocra.type_hydrant_anomalie USING btree (critere);
CREATE INDEX type_hydrant_anomalie_nature_anomalie_idx ON remocra.type_hydrant_anomalie_nature USING btree (anomalie);
CREATE INDEX type_hydrant_anomalie_nature_nature_idx ON remocra.type_hydrant_anomalie_nature USING btree (nature);
CREATE INDEX type_hydrant_modele_marque_idx ON remocra.type_hydrant_modele USING btree (marque);
CREATE INDEX type_hydrant_nature_type_hydrant_idx ON remocra.type_hydrant_nature USING btree (type_hydrant);
CREATE INDEX type_oldeb_anomalie_categorie_idx ON remocra.type_oldeb_anomalie USING btree (categorie);
CREATE INDEX type_oldeb_caracteristique_categorie_idx ON remocra.type_oldeb_caracteristique USING btree (categorie);
CREATE INDEX type_rci_prom_categorie_partition_idx ON remocra.type_rci_prom_categorie USING btree (partition);
CREATE INDEX type_rci_prom_partition_famille_idx ON remocra.type_rci_prom_partition USING btree (famille);
CREATE INDEX utilisateur_organisme_idx ON remocra.utilisateur USING btree (organisme);
CREATE INDEX utilisateur_profil_utilisateur_idx ON remocra.utilisateur USING btree (profil_utilisateur);
CREATE INDEX voie_commune_idx ON remocra.voie USING btree (commune);
CREATE INDEX hydrant_prescrit_geometrie_idx ON remocra.hydrant_prescrit USING gist (geometrie);
CREATE INDEX rci_geometrie_idx ON remocra.rci USING gist (geometrie);


-- Contenu réel du patch fin
--------------------------------------------------

commit;

