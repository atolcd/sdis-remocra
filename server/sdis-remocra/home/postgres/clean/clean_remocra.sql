begin;

-- Nettoyage des données de remocra (copie de serveur, etc.)

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;




-- --------------------
-- Contenu des tables
-- --------------------

-- Schéma pdi
truncate pdi.traitement_cc cascade;
truncate pdi.traitement_parametre cascade;
truncate pdi.traitement cascade;



-- Schéma remocra
truncate remocra.alerte cascade;
truncate remocra.alerte_document cascade;
truncate remocra.alerte_elt cascade;
truncate remocra.alerte_elt_ano cascade;
truncate remocra.bloc_document cascade;
truncate remocra.bloc_document_profil_droits cascade;
truncate remocra.bloc_document_thematiques cascade;
truncate remocra.commune cascade;
truncate remocra.dde_mdp cascade;
truncate remocra.depot_document cascade;
truncate remocra.document cascade;
truncate remocra.email cascade;
truncate remocra.hydrant cascade;
truncate remocra.hydrant_anomalies cascade;
truncate remocra.hydrant_document cascade;
truncate remocra.hydrant_pena cascade;
truncate remocra.hydrant_pibi cascade;
truncate remocra.hydrant_prescrit cascade;
truncate remocra.metadonnee cascade;
truncate remocra.organisme cascade;
truncate remocra.permis cascade;
truncate remocra.permis_document cascade;
truncate remocra.rci cascade;
truncate remocra.rci_document cascade;
truncate remocra.synchronisation cascade;
truncate remocra.tournee cascade;
truncate remocra.utilisateur cascade;
truncate remocra.voie cascade;
truncate remocra.zone_competence cascade;
truncate remocra.zone_speciale cascade;



-- Schéma tracabilite
drop schema remocra_referentiel cascade;
create schema remocra_referentiel;



-- Schéma tracabilite
truncate tracabilite.hydrant cascade;




-- --------------------
-- Séquences
-- --------------------
SELECT pg_catalog.setval('traitement_idtraitement_seq', 1, false);

SELECT pg_catalog.setval('alerte_id_seq', 1, false);
SELECT pg_catalog.setval('alerte_document_id_seq', 1, false);
SELECT pg_catalog.setval('alerte_elt_id_seq', 1, false);
SELECT pg_catalog.setval('alerte_elt_ano_id_seq', 1, false);
SELECT pg_catalog.setval('bloc_document_id_seq', 1, false);
SELECT pg_catalog.setval('commune_id_seq', 1, false);
SELECT pg_catalog.setval('dde_mdp_id_seq', 1, false);
SELECT pg_catalog.setval('depot_document_id_seq', 1, false);
SELECT pg_catalog.setval('document_id_seq', 1, false);
SELECT pg_catalog.setval('email_id_seq', 1, false);
SELECT pg_catalog.setval('hydrant_id_seq', 1, false);
SELECT pg_catalog.setval('hydrant_prescrit_id_seq', 1, false);
SELECT pg_catalog.setval('metadonnee_id_seq', 1, false);
SELECT pg_catalog.setval('organisme_id_seq', 1, false);
SELECT pg_catalog.setval('permis_id_seq', 1, false);
SELECT pg_catalog.setval('permis_document_id_seq', 1, false);
SELECT pg_catalog.setval('rci_id_seq', 1, false);
SELECT pg_catalog.setval('rci_document_id_seq', 1, false);
SELECT pg_catalog.setval('synchronisation_id_seq', 1, false);
SELECT pg_catalog.setval('tournee_id_seq', 1, false);
SELECT pg_catalog.setval('utilisateur_id_seq', 1, false);
SELECT pg_catalog.setval('voie_id_seq', 1, false);
SELECT pg_catalog.setval('zone_competence_id_seq', 1, false);
SELECT pg_catalog.setval('zone_speciale_id_seq', 1, false);




commit;
