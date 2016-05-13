begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, tracabilite, pdi, public, pg_catalog;


-- Schéma pdi
SELECT pg_catalog.setval('pdi.traitement_idtraitement_seq', (select max(idtraitement) from pdi.traitement));

-- Schéma remocra
SELECT pg_catalog.setval('remocra.alerte_document_id_seq', (select max(id) from remocra.alerte_document));
SELECT pg_catalog.setval('remocra.alerte_elt_ano_id_seq', (select max(id) from remocra.alerte_elt_ano));
SELECT pg_catalog.setval('remocra.alerte_elt_id_seq', (select max(id) from remocra.alerte_elt));
SELECT pg_catalog.setval('remocra.alerte_id_seq', (select max(id) from remocra.alerte));
SELECT pg_catalog.setval('remocra.bloc_document_id_seq', (select max(id) from remocra.bloc_document));
SELECT pg_catalog.setval('remocra.commune_id_seq', (select max(id) from remocra.commune));
SELECT pg_catalog.setval('remocra.dde_mdp_id_seq', (select max(id) from remocra.dde_mdp));
SELECT pg_catalog.setval('remocra.depot_document_id_seq', (select max(id) from remocra.depot_document));
SELECT pg_catalog.setval('remocra.document_id_seq', (select max(id) from remocra.document));
SELECT pg_catalog.setval('remocra.droit_id_seq', (select max(id) from remocra.droit));
SELECT pg_catalog.setval('remocra.email_id_seq', (select max(id) from remocra.email));
SELECT pg_catalog.setval('remocra.email_modele_id_seq', (select max(id) from remocra.email_modele));
-- remocra.hibernate_sequence ?
SELECT pg_catalog.setval('remocra.hydrant_document_id_seq', (select max(id) from remocra.hydrant_document));
SELECT pg_catalog.setval('remocra.hydrant_id_seq', (select max(id) from remocra.hydrant));
SELECT pg_catalog.setval('remocra.hydrant_prescrit_id_seq', (select max(id) from remocra.hydrant_prescrit));
SELECT pg_catalog.setval('remocra.metadonnee_id_seq', (select max(id) from remocra.metadonnee));
SELECT pg_catalog.setval('remocra.organisme_id_seq', (select max(id) from remocra.organisme));
SELECT pg_catalog.setval('remocra.permis_document_id_seq', (select max(id) from remocra.permis_document));
SELECT pg_catalog.setval('remocra.permis_id_seq', (select max(id) from remocra.permis));
SELECT pg_catalog.setval('remocra.profil_droit_id_seq', (select max(id) from remocra.profil_droit));
SELECT pg_catalog.setval('remocra.profil_organisme_id_seq', (select max(id) from remocra.profil_organisme));
SELECT pg_catalog.setval('remocra.profil_organisme_utilisateur_droit_id_seq', (select max(id) from remocra.profil_organisme_utilisateur_droit));
SELECT pg_catalog.setval('remocra.profil_utilisateur_id_seq', (select max(id) from remocra.profil_utilisateur));
SELECT pg_catalog.setval('remocra.rci_document_id_seq', (select max(id) from remocra.rci_document));
SELECT pg_catalog.setval('remocra.rci_id_seq', (select max(id) from remocra.rci));
SELECT pg_catalog.setval('remocra.sous_type_alerte_elt_id_seq', (select max(id) from remocra.sous_type_alerte_elt));
--SELECT pg_catalog.setval('remocra.synchronisation_id_seq', (select max(id) from remocra.synchronisation));
SELECT pg_catalog.setval('remocra.thematique_id_seq', (select max(id) from remocra.thematique));
SELECT pg_catalog.setval('remocra.tournee_id_seq', (select max(id) from remocra.tournee));
SELECT pg_catalog.setval('remocra.type_alerte_ano_id_seq', (select max(id) from remocra.type_alerte_ano));
SELECT pg_catalog.setval('remocra.type_alerte_elt_id_seq', (select max(id) from remocra.type_alerte_elt));
SELECT pg_catalog.setval('remocra.type_droit_id_seq', (select max(id) from remocra.type_droit));
SELECT pg_catalog.setval('remocra.type_hydrant_anomalie_id_seq', (select max(id) from remocra.type_hydrant_anomalie));
SELECT pg_catalog.setval('remocra.type_hydrant_anomalie_nature_id_seq', (select max(id) from remocra.type_hydrant_anomalie_nature));
SELECT pg_catalog.setval('remocra.type_hydrant_critere_id_seq', (select max(id) from remocra.type_hydrant_critere));
SELECT pg_catalog.setval('remocra.type_hydrant_diametre_id_seq', (select max(id) from remocra.type_hydrant_diametre));
SELECT pg_catalog.setval('remocra.type_hydrant_domaine_id_seq', (select max(id) from remocra.type_hydrant_domaine));
SELECT pg_catalog.setval('remocra.type_hydrant_id_seq', (select max(id) from remocra.type_hydrant));
SELECT pg_catalog.setval('remocra.type_hydrant_marque_id_seq', (select max(id) from remocra.type_hydrant_marque));
SELECT pg_catalog.setval('remocra.type_hydrant_materiau_id_seq', (select max(id) from remocra.type_hydrant_materiau));
SELECT pg_catalog.setval('remocra.type_hydrant_modele_id_seq', (select max(id) from remocra.type_hydrant_modele));
SELECT pg_catalog.setval('remocra.type_hydrant_nature_id_seq', (select max(id) from remocra.type_hydrant_nature));
SELECT pg_catalog.setval('remocra.type_hydrant_positionnement_id_seq', (select max(id) from remocra.type_hydrant_positionnement));
SELECT pg_catalog.setval('remocra.type_hydrant_saisie_id_seq', (select max(id) from remocra.type_hydrant_saisie));
SELECT pg_catalog.setval('remocra.type_hydrant_vol_constate_id_seq', (select max(id) from remocra.type_hydrant_vol_constate));
SELECT pg_catalog.setval('remocra.type_organisme_id_seq', (select max(id) from remocra.type_organisme));
SELECT pg_catalog.setval('remocra.type_rci_degre_certitude_id_seq', (select max(id) from remocra.type_rci_degre_certitude));
SELECT pg_catalog.setval('remocra.type_rci_origine_alerte_id_seq', (select max(id) from remocra.type_rci_origine_alerte));
SELECT pg_catalog.setval('remocra.type_rci_prom_categorie_id_seq', (select max(id) from remocra.type_rci_prom_categorie));
SELECT pg_catalog.setval('remocra.type_rci_prom_famille_id_seq', (select max(id) from remocra.type_rci_prom_famille));
SELECT pg_catalog.setval('remocra.type_rci_prom_partition_id_seq', (select max(id) from remocra.type_rci_prom_partition));
SELECT pg_catalog.setval('remocra.type_permis_avis_id_seq', (select max(id) from remocra.type_permis_avis));
SELECT pg_catalog.setval('remocra.type_permis_interservice_id_seq', (select max(id) from remocra.type_permis_interservice));
SELECT pg_catalog.setval('remocra.utilisateur_id_seq', (select max(id) from remocra.utilisateur));
SELECT pg_catalog.setval('remocra.voie_id_seq', (select max(id) from remocra.voie));
SELECT pg_catalog.setval('remocra.zone_competence_id_seq', (select max(id) from remocra.zone_competence));

-- Schéma tracabilite
SELECT pg_catalog.setval('tracabilite.hydrant_id_seq', (select max(id) from tracabilite.hydrant));



commit;
