--
-- Paramètres par défaut
--

-- Reprise des valeurs des paramètres suivants :

-- * PDI_FTP_URL
-- * PDI_FTP_USER_NAME
-- * PDI_FTP_USER_PASSWORD

-- * PDI_IMAP_PASSWORD
-- * PDI_IMAP_PORT
-- * PDI_IMAP_URL
-- * PDI_IMAP_USER

-- * PDI_METADATA_FILTRE_CQL
-- * PDI_METADATA_URL_FICHE_COMPLETE
-- * PDI_METADATA_URL_GEOCATALOGUE

-- * PDI_SMTP_EME_MAIL
-- * PDI_SMTP_REP_MAIL

-- * EMAIL_DEST_DEPOT_RECEPTRAVAUX
-- * EMAIL_DEST_CREATION_RCI
-- * EMAIL_DEST_DEPOT_DECLAHYDRANT
-- * EMAIL_DEST_DEPOT_DELIB

-- * COMMUNES_INSEE_LIKE_FILTRE_SQL
-- * PDI_URL_SITE
-- * PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID (-1)
-- * PDI_NOTIFICATION_KML_UTILISATEUR_ID (-1)
-- * CLES_IGN
-- * WMS_PUBLIC_LAYERS

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog;

truncate param_conf;

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PERMIS_TOLERANCE_VOIES_METRES', 'Tolérance de chargement des voies, exprimée en mètres', '150', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_BLOC', 'Emplacement du dossier de stockage des documents des blocs', '/var/remocra/blocs', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DOC_HYDRANT', 'Emplacement du dossier de stockage des photos des hydrants', '/var/remocra/hydrants', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('MESSAGE_ENTETE', 'Texte affiché dans l''entête du site', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_URL', 'URL d''accès au site FTP', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOM_SCHEMA_ORACLE', 'Nom du schéma Oracle de la base APIS pour génération des instructions SQL', 'APIS83_GEO', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_CTRL_URGENT', 'Nombre de jours avant échéance où un contrôle est considéré comme "urgent"', '15', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_CTRL_WARN', 'Nombre de jours avant échéance où un contrôle est considéré comme "à faire bientôt"', '45', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_RECO_URGENT', 'Nombre de jours avant échéance où une reconnaisance est considérée comme "urgente"', '15', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_RECO_WARN', 'Nombre de jours avant échéance où une reconnaisance est considérée comme "à faire bientôt"', '45', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('HYDRANT_RENOUVELLEMENT_CTRL', 'Délai légal entre 2 contrôles (en jours)', '1095', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('HYDRANT_RENOUVELLEMENT_RECO', 'Délai légal entre 2 reconnaissances (en jours)', '365', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_SYNCHRO', 'Dossier de stockage temporaire des fichiers d''échanges APIS – REMOCRA', '/var/remocra/pdi/synchro', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_DOSSIER_EXPORT_SDIS', 'Nom du sous-dossier temporaire dans lequel sont déposés les fichiers d''échanges produits par REMOCRA', 'EXPORT_SDIS', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_DOSSIER_IMPORT_EXTRANET', 'Nom du sous-dossier temporaire dans lequel sont déposés les fichiers d''échanges récupérés d''APIS', 'IMPORT_EXTRANET', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DOSSIER_EXTRANET', 'Nom du sous-dossier du serveur FTP dans lequel récupérer les fichiers produits par APIS', 'EXPORT_EXTRANET', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DOSSIER_SDIS', 'Nom du sous-dossier du serveur FTP dans lequel déposer les fichiers produits par REMOCRA', 'IMPORT_SDIS', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DOSSIER_SYNCHRO', 'Dossier du serveur FTP dédié à la synchronisation', 'SYNCHRO', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_POSTGRESQL_NOM_SCHEMA_SYNCHRO', 'Nom du schéma PostgreSQL temporaire pour import des données APIS', 'sdis_referentiel', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_DELIB', 'Emplacement du dossier de stockage des délibérations', '/var/remocra/deliberations', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_DECLA_HYDRANT', 'Emplacement du dossier de stockage des déclarations d''hydrants', '/var/remocra/declahydrant', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_RECEP_TRAVAUX', 'Emplacement du dossier de stockage des dossiers de réception de travaux', '/var/remocra/receptravaux', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_PASSWORD', 'Mot de passe du serveur IMPAP utilisé pour la récupération du fichier KML des risques technologiques', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_PORT', 'Numéro du port du serveur IMAP utilisé pour la récupération du fichier KML des risques technologiques', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_URL', 'URL du serveur IMAP utilisé pour la récupération du fichier KML des risques technologiques', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_USER', 'Nom d''utilisateur du serveur IMAP utilisé pour la récupération du fichier KML des risques technologiques', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_ALERTE', 'Emplacement du dossier de stockage des alertes', '/var/remocra/alertes', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_PERMIS', 'Emplacement du dossier de stockage des permis', '/var/remocra/permis', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_GETFEATUREINFO', 'Emplacement du dossier des transformations GetFeatureInfo', '/var/remocra/getfeatureinfo', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_ATLAS', 'Traitement de téléchargement de l''Atlas', '7', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_PURGE_KML', 'Traitement de purge de la couche des risques express', '8', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PERMIS_TOLERANCE_CHARGEMENT_METRES', 'Tolérance de chargement des permis, exprimée en mètres', '5000', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_KML', 'Dossier de stockage du fichier KML des risques technologiques', '/var/remocra/pdi/kml', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_LOG', 'Dossier de stockage des fichiers de trace de l''ETL Pentaho Data Integration', '/var/remocra/pdi/log', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_MODELES', 'Dossier de stockage des modèles de documents utilisés par l''ETL Pentaho Data Integration', '/var/remocra/modeles', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_TMP', 'Dossier de travail temporaire de l''ETL Pentaho Data Integration', '/var/remocra/pdi/tmp', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_TRAITEMENT', 'Dossier de stockage des documents produits par l''ETL Pentaho Data Integration à proposer en téléchargement', '/var/remocra/pdi/export', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FICHIER_PARAMETRAGE', 'Chemin et nom complet du fichier de configuration de l''ETL Pentaho Data Integration', '/home/postgres/remocra_pdi/remocra.properties', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_FILTRE_CQL', 'Filtre à utiliser pour la récupération des métadonnées', 'OrganisationName like ''%''', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_FILTRE_MAX', 'Nombre d''enregistrements maximum à retourner', '20', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_URL_FICHE_COMPLETE', 'URL d''accès aux fiches de métadonnées HTML du CRIGE PACA', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_PORT', 'Port du site FTP', '21', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_USER_NAME', 'Nom d''utilisateur du compte FTP', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_USER_PASSWORD', 'Mot de passe du compte FTP', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_DOSSIER_DEPOT', 'Emplacement du dossier de stockage des fichiers de PDI', '/var/remocra/pdi/depot', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_URL_GEOCATALOGUE', 'URL d''accès au géocatalogue du CRIGE PACA', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_POSTGRESQL_NOM_SCHEMA_REFERENTIEL', 'Nom du schéma Postgresql dans lequel créer et synchroniser les tables de référentiels géographiques', 'remocra_referentiel', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_POSTGRESQL_NOM_SCHEMA_REMOCRA', 'Nom du schéma Postgresql dans lequel créer et synchroniser les tables métier', 'remocra', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_PURGE_MAIL_JOURS', 'Nombre de jours avant suppression des messages envoyés par Remocra', '1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_PURGE_TRAITEMENT_JOURS', 'Nombre de jours avant suppression des traitements réalisés par Remocra', '1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_EME_NAME', 'Nom de l''expediteur utilisé pour l''envoi de messages par Remocra', 'Application Remocra', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_PASSWORD', 'Mot de passe du serveur SMTP utilisé pour l''envoi de messages par Remocra', 'VOIR', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_EME_MAIL', 'Adresse mél d''expédition utilisée pour l''envoi de messages par Remocra', 'no-reply@sdis.fr', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_REP_MAIL', 'Adresse mél de réponse utilisée pour l''envoi de messages par Remocra', 'no-reply@sdis.fr', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_URL', 'URL du serveur SMTP utilisé pour l''envoi de messages par Remocra', 'localhost', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_PORT', 'Numéro du port du serveur SMTP utilisé pour l''envoi de messages par Remocra', '25', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_USER', 'Nom d''utilisateur du serveur SMTP utilisé pour l''envoi de messages par Remocra', 'VOIR', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_HYDRANTS_NON_NUM', 'Traitement des points d''eau non numérotés', '15', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_RESSOURCES_EXTERNES', 'Emplacement du dossier de stockage des ressources externes (pages accueil, cartes, images des légendes, etc.)', '/var/remocra/html', NULL);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('COMMUNES_INSEE_LIKE_FILTRE_SQL', 'Filtre SQL pour la récupération des communes. Exemple : "83%"', '%', NULL);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_NB_ALERTES_PAR_UTILISATEUR', 'Traitement du nombre d''alertes par utilisateur', '16', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_DEPOT_RECEPTRAVAUX', 'Adresse du destinataire des emails de notification de dépôts de dossiers de réception de travaux', 'no-reply@sdis.fr', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_CREATION_RCI', 'Adresse mél utilisée pour la diffusion lorsqu''un départ de feux est créé', 'no-reply@sdis.fr', NULL);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_DEPOT_DECLAHYDRANT', 'Adresse du destinataire des emails de notification de dépôts de dossiers de déclaration d''hydrant(s)', 'no-reply@sdis.fr', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('CITERNE_TOLERANCE_ASSOCIATION_PI_METRES', 'Tolérance de chargement des PI associables aux Citernes, exprimée en mètres', '500', NULL);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_URL_SITE', 'URL de base complète du site Remocra (Commence par "http" et finit juste avant le #)', 'http://localhost:8080/remocra/', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_PURGE_ALERTE_JOURS', 'Nombre de jours avant suppression des alertes prises en compte', '31', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_DEPOT_DELIB', 'Adresse du destinataire des emails de notification de dépôts de délibérations', 'ne-pas-repondre@atolcd.com', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_RCI', 'Emplacement du dossier de stockage des documents RCI', '/var/remocra/rci', NULL);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID', 'Identifiant de l''utilisateur à notifier dans le cadre de la notification générale (erreurs générales, autres, etc.)', '-1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOTIFICATION_KML_UTILISATEUR_ID', 'Identifiant de l''utilisateur à notifier dans le cas de la publication d''un nouveau fichier de risques KML', '-1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('CLES_IGN', 'Clé(s) IGN. Exemples (mono/multi) : "rm5nr4ejwapq6d58fm0869bd" ou "{''localhost'': ''4n507j21zeha5rp5pkll48vj'',''sdis83-remocra-prod'': ''rm5nr4ejwapq6d58fm0869bd''}"', '{''localhost'':''ruinfducv7xle3enpag3y3r2''}', NULL);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('WMS_PUBLIC_LAYERS', 'Couches publiques du serveur WMS avec séparateur %. Exemple : remocra:ADMINISTRATIF%remocra:RISQUE', '', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('WMS_BASE_URL', 'URL de base complète du serveur de cartes (Exemple : http://localhost:8080/geoserver)', 'http://localhost:8080/geoserver', NULL);

COMMIT;

