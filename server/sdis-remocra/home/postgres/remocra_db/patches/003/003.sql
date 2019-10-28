SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;




-- Tables liées aux permis
CREATE TABLE remocra.type_permis_avis
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying,
  nom character varying NOT NULL,
  pprif boolean NOT NULL DEFAULT false,
  CONSTRAINT type_permis_avis_pkey PRIMARY KEY (id ),
  CONSTRAINT type_permis_avis_code_key UNIQUE (code )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_permis_avis
  OWNER TO postgres;


/*CREATE SEQUENCE remocra.type_permis_avis_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE remocra.type_permis_avis_id_seq
  OWNER TO postgres;*/




CREATE TABLE remocra.type_permis_interservice
(
  id bigserial NOT NULL,
  actif boolean NOT NULL DEFAULT true,
  code character varying,
  nom character varying NOT NULL,
  pprif boolean NOT NULL DEFAULT false,
  CONSTRAINT type_permis_interservice_pkey PRIMARY KEY (id ),
  CONSTRAINT type_permis_interservice_code_key UNIQUE (code )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_permis_interservice
  OWNER TO postgres;


/*CREATE SEQUENCE remocra.type_permis_interservice_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 3
  CACHE 1;
ALTER TABLE remocra.type_permis_interservice_id_seq
  OWNER TO postgres;*/




CREATE TABLE remocra.permis
(
  id bigserial NOT NULL,
  adresse character varying NOT NULL,
  annee integer NOT NULL,
  date_modification timestamp without time zone NOT NULL,
  geometrie geometry NOT NULL,
  nom character varying NOT NULL,
  numero character varying NOT NULL,
  observations character varying NOT NULL,
  parcelle_cadastrale character varying NOT NULL,
  section_cadastrale character varying NOT NULL,
  version integer DEFAULT 1,
  avis bigint NOT NULL,
  commune bigint NOT NULL,
  instructeur bigint NOT NULL,
  interservice bigint NOT NULL,
  service_instructeur bigint NOT NULL,
  CONSTRAINT permis_pkey PRIMARY KEY (id ),
  CONSTRAINT fkc4e3841a60bac826 FOREIGN KEY (avis)
      REFERENCES remocra.type_permis_avis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkc4e3841aad903b23 FOREIGN KEY (service_instructeur)
      REFERENCES remocra.type_organisme (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkc4e3841ad2da796c FOREIGN KEY (commune)
      REFERENCES remocra.commune (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkc4e3841ade630077 FOREIGN KEY (instructeur)
      REFERENCES remocra.utilisateur (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkc4e3841ae451849a FOREIGN KEY (interservice)
      REFERENCES remocra.type_permis_interservice (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.permis
  OWNER TO postgres;


/*CREATE SEQUENCE remocra.permis_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE remocra.permis_id_seq
  OWNER TO postgres;*/




-- Données de référence liées aux permis
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (1, true, 'FAVORABLE', 'Favorable', true);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (2, true, 'DEFAVORABLE', 'Défavorable', true);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (3, true, 'RFF', 'RFF', false);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (4, true, 'SRFF', 'SRFF', false);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (5, true, 'RM', 'RM', false);

SELECT pg_catalog.setval('type_permis_avis_id_seq', 5, true);


INSERT INTO type_permis_interservice (id, actif, code, nom, pprif) VALUES (1, true, 'VALIDE', 'Valide', true);
INSERT INTO type_permis_interservice (id, actif, code, nom, pprif) VALUES (2, true, 'NON VALIDE', 'Non valide', true);
INSERT INTO type_permis_interservice (id, actif, code, nom, pprif) VALUES (3, true, 'NON CONCERNE', 'Non concerné', false);

SELECT pg_catalog.setval('type_permis_interservice_id_seq', 3, true);




-- Communes : ajout colonne pprif
alter table remocra.commune ADD COLUMN pprif boolean NOT NULL DEFAULT false;




-- Reprojection des communes : 4326 vers 2154
update remocra.commune set geometrie=st_transform(st_geomfromwkb(geometrie, 4326), 2154);
update remocra.commune set geometrie=st_setsrid(geometrie, 2154);




-- Ajout des contraintes liées aux geom
CREATE FUNCTION initgeoonce1564812() RETURNS void AS $$
BEGIN
if not exists (select constraint_name 
                   from information_schema.constraint_column_usage 
                   where table_schema = 'remocra' and table_name = 'commune'  and constraint_name = 'enforce_dims_geometrie') then
		ALTER TABLE remocra.commune ADD CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2);
		ALTER TABLE remocra.commune ADD CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'MULTIPOLYGON'::text);
		ALTER TABLE remocra.commune ADD CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154);
END IF;

if not exists (select constraint_name 
                   from information_schema.constraint_column_usage 
                   where table_schema = 'remocra' and table_name = 'permis'  and constraint_name = 'enforce_dims_geometrie') then
		ALTER TABLE remocra.permis ADD CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2);
		ALTER TABLE remocra.permis ADD CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POINT'::text);
		ALTER TABLE remocra.permis ADD CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154);
END IF;

if not exists (select constraint_name 
                   from information_schema.constraint_column_usage 
                   where table_schema = 'remocra' and table_name = 'alerte'  and constraint_name = 'enforce_dims_geometrie') then
		ALTER TABLE remocra.alerte ADD CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2);
		ALTER TABLE remocra.alerte ADD CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POINT'::text);
		ALTER TABLE remocra.alerte ADD CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154);
END IF;

if not exists (select constraint_name 
                   from information_schema.constraint_column_usage 
                   where table_schema = 'remocra' and table_name = 'alerte_elt'  and constraint_name = 'enforce_dims_geometrie') then
		ALTER TABLE remocra.alerte_elt ADD CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2);
		ALTER TABLE remocra.alerte_elt ADD CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154);
END IF;

END;
$$ LANGUAGE plpgsql;

select initgeoonce1564812();

DROP FUNCTION initgeoonce1564812();




-- Paramètres de configuration
delete from remocra.param_conf;

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_ALERTE', 'Emplacement du dossier de stockage des alertes', '/var/remocra/alertes', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_DELIB', 'Emplacement du dossier de stockage des données Remocra', '/var/remocra/deliberations', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_PDI', 'Emplacement du dossier de stockage des fichiers de PDI', '/var/remocra/pdi/depot', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_PERMIS', 'Emplacement du dossier de stockage des permis', '/var/remocra/permis', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_GETFEATUREINFO', 'Emplacement du dossier des transformations GetFeatureInfo', '/var/remocra/getfeatureinfo', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_ATLAS', 'Traitement de téléchargement de l''Atlas', '7', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_PURGE_KML', 'Traitement de purge de la couche des risques express', '8', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('MESSAGE_ENTETE', 'Texte affiché dans l''entête du site', 'Ceci est un serveur de tests.', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PERMIS_TOLERANCE_CHARGEMENT_METRES', 'Tolérance de chargement des permis, exprimée en mètres', '5000', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_KML', 'Dossier de stockage du fichier KML des risques technologiques', '/var/remocra/pdi/kml', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_LOG', 'Dossier de stockage des fichiers de trace de l''ETL Pentaho Data Integration', '/var/remocra/pdi/log', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_MODELES', 'Dossier de stockage des modèles de documents utilisés par l''ETL Pentaho Data Integration', '/var/remocra/modeles', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_TMP', 'Dossier de travail temporaire de l''ETL Pentaho Data Integration', '/var/remocra/pdi/tmp', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_CHEMIN_TRAITEMENT', 'Dossier de stockage des documents produits par l''ETL Pentaho Data Integration à proposer en téléchargement', '/var/remocra/pdi/export', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FICHIER_PARAMETRAGE', 'Chemin et nom complet du fichier de configuration de l''ETL Pentaho Data Integration', '/home/postgres/remocra_pdi/remocra.properties', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_PASSWORD', 'Mot de passe du serveur IMPAP utilisé pour la récupération du fichier KML des risques technologiques', 'postgres', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_PORT', 'Numéro du port du serveur IMAP utilisé pour la récupération du fichier KML des risques technologiques', '25', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_URL', 'URL du serveur IMAP utilisé pour la récupération du fichier KML des risques technologiques', 'smtp.dmz.priv.atolcd.com', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_IMAP_USER', 'Nom d''utilisateur du serveur IMAP utilisé pour la récupération du fichier KML des risques technologiques', 'postgres', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_FILTRE_CQL', 'Filtre à utiliser pour la récupération des métadonnées', 'OrganisationName like ''%CRIGE%''', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_FILTRE_MAX', 'Nombre d''enregistrements maximum à retourner', '20', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_URL_FICHE_COMPLETE', 'URL d''accès aux fiches de métadonnées HTML du CRIGE PACA', 'http://www.crige-paca.org/carto/fonctionnalites/geocatalogue/geosource.php', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_METADATA_URL_GEOCATALOGUE', 'URL d''accès au géocatalogue du CRIGE PACA', 'http://geocatalogue.crige-paca.org/geonetwork', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_POSTGRESQL_NOM_SCHEMA_REFERENTIEL', 'Nom du schéma Postgresql dans lequel créer et synchroniser les tables de référentiels géographiques', 'remocra_referentiel', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_POSTGRESQL_NOM_SCHEMA_REMOCRA', 'Nom du schéma Postgresql dans lequel créer et synchroniser les tables métier', 'remocra', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_PURGE_MAIL_JOURS', 'Nombre de jours avant suppression des messages envoyés par Remocra', '1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_PURGE_TRAITEMENT_JOURS', 'Nombre de jours avant suppression des traitements réalisés par Remocra', '1', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_EME_MAIL', 'Adresse mél d''expédition utilisée pour l''envoi de messages par Remocra', 'cda@atolcd.com', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_EME_NAME', 'Nom de l''expediteur utilisé pour l''envoi de messages par Remocra', 'Application Remocra', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_PASSWORD', 'Mot de passe du serveur SMTP utilisé pour l''envoi de messages par Remocra', 'postgres', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_PORT', 'Numéro du port du serveur SMTP utilisé pour l''envoi de messages par Remocra', '25', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_REP_MAIL', 'Adresse mél de réponse utilisée pour l''envoi de messages par Remocra', 'no-reply@atolcd.com', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_URL', 'URL du serveur SMTP utilisé pour l''envoi de messages par Remocra', 'smtp.dmz.priv.atolcd.com', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_SMTP_USER', 'Nom d''utilisateur du serveur SMTP utilisé pour l''envoi de messages par Remocra', 'postgres', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_URL_SITE', 'URL de base complète du site Remocra (Commence par "http" et finit juste avant le #)', 'http://sdis83-remocra.priv.atolcd.com', 1);




-- Purge éventuelle
delete from pdi.traitement_parametre where idtraitement in(select idtraitement from pdi.traitement where idmodele in (8, 9, 10));
delete from pdi.traitement where idmodele in (8, 9, 10);
delete from pdi.modele_traitement_parametre where idmodele in (8, 9, 10);
delete from pdi.modele_traitement where idmodele in (8, 9, 10);

-- RISQUES
INSERT INTO pdi.modele_traitement (idmodele, nom, description, type, ref_chemin, ref_nom, message_succes, message_echec, code) VALUES
(8, 'Suppression du fichier KML lié aux risques technologiques', 'Supprime de fichier KML des risques technologiques','J', '/demandes/kml_risque_technologique', 'supprimer_kml', 1, 3, 5);

-- DIVERS
INSERT INTO pdi.modele_traitement (idmodele, nom, description, type, ref_chemin, ref_nom, message_succes, message_echec, code) VALUES
(9, 'Export des données d''une table dans un fichier pour une commune', 'Exporte les données d''une table dans un fichier','J', '/demandes/export_table_spatiale', 'exporter_table', 1, 3, 0);
INSERT INTO pdi.modele_traitement_parametre (
idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (
91, 'TABLE_OID', true, 1, 'Nom de la table', 'combo', NULL, 'vue_tables_spatiales', 9);
INSERT INTO pdi.modele_traitement_parametre (
idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (
92, 'COMMUNE_ID', true, 2, 'Commune', 'combo', NULL, 'vue_communes', 9);
INSERT INTO pdi.modele_traitement_parametre (
idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (
93, 'FORMAT_EXPORT', true, 3, 'Format d''export', 'combo', NULL, 'vue_formats_export_sig', 9);

INSERT INTO pdi.modele_traitement (idmodele, nom, description, type, ref_chemin, ref_nom, message_succes, message_echec, code) VALUES
(10, 'Mettre à jour les fiches de métadonnées', 'Mise à jour des métadonnées à partir du serveur régional du CRIGE PACA','T', '/demandes/metadonnee', 'mettre_a_jour_metadonnees', 1, 3, 0);



-- Vues PDI
CREATE OR REPLACE VIEW pdi.vue_formats_export_sig AS 
 SELECT t.i::bigint AS id, t.formats[t.i]::character varying AS libelle
   FROM ( SELECT generate_series(1, array_upper(t.formats, 1)) AS i, t.formats
           FROM ( SELECT ARRAY['ESRI Shapefile (SHP)'::text, 'Keyhole Markup Language (KML)'::text] AS formats) t) t;

CREATE OR REPLACE VIEW pdi.vue_tables_spatiales AS 
 SELECT DISTINCT vtable.table_oid::bigint AS id, ((vtable.schema_nom::text || '.'::text) || vtable.table_nom::text)::character varying AS libelle
   FROM pg_attribute
   RIGHT JOIN ( SELECT pg_namespace.oid AS schema_oid, pg_namespace.nspname::character varying AS schema_nom, pg_class.oid AS table_oid, pg_class.relname::character varying AS table_nom, obj_description(pg_class.oid, 'pg_class'::name)::character varying AS table_description
           FROM pg_class
      LEFT JOIN pg_namespace ON pg_class.relnamespace = pg_namespace.oid
     WHERE pg_namespace.nspname <> 'information_schema'::name AND pg_namespace.nspname !~~ 'pg_%'::text AND pg_class.relkind = 'r'::"char") vtable ON pg_attribute.attrelid = vtable.table_oid
  WHERE vtable.schema_nom::text = 'remocra_oci'::text AND format_type(pg_attribute.atttypid, pg_attribute.atttypmod)::character varying::text = 'geometry'::text
  ORDER BY ((vtable.schema_nom::text || '.'::text) || vtable.table_nom::text)::character varying;

CREATE OR REPLACE VIEW pdi.vue_telechargements AS 
 SELECT traitement.idtraitement, md5((((traitement.idtraitement || '_'::text) || traitement.idutilisateur) || '_'::text) || traitement.urlressource::text)::character varying AS code, traitement.urlressource AS ressource
   FROM pdi.traitement
  WHERE traitement.idstatut = 2 AND traitement.urlressource IS NOT NULL;




COMMIT;

