SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;


-- Vue des communes
CREATE OR REPLACE VIEW pdi.vue_communes AS
SELECT Allin.id, Allin.libelle
FROM (
  (SELECT -1 as id, 'VAR' as libelle, null as tricol)
  UNION
  (SELECT commune.id, commune.nom AS libelle, commune.nom AS tricol FROM remocra.commune)
) as Allin ORDER BY  Allin.tricol nulls first;



-- Vue des états de permis
CREATE OR REPLACE VIEW pdi.vue_permis_etats AS
SELECT Allin.id, Allin.libelle
FROM (
  (SELECT -1 as id, 'Tous' as libelle, null as tricol)
  UNION
  (SELECT type_permis_avis.id, type_permis_avis.nom AS libelle, type_permis_avis.nom AS tricol FROM remocra.type_permis_avis)
) as Allin ORDER BY  Allin.tricol nulls first;




-- Purge éventuelle
delete from pdi.traitement_parametre where idtraitement in (select idtraitement from pdi.traitement where idmodele = 4);
delete from pdi.traitement where idmodele = 4;
delete from pdi.modele_traitement_parametre where idmodele = 4;
delete from pdi.modele_traitement where idmodele = 4;


INSERT INTO pdi.modele_traitement (idmodele, nom, description, type, ref_chemin, ref_nom, message_succes, message_echec, code) VALUES (4, 'Liste des permis', 'Liste des permis par avis, commune et sur une période donnée','', '', '', 1, 3, 3);

INSERT INTO pdi.modele_traitement_parametre (idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (41, 'LST_AVIS', true, 1, 'Avis', 'combo', NULL, 'vue_permis_etats',4);
INSERT INTO pdi.modele_traitement_parametre (idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (42, 'LST_COMMUNES', true, 1, 'Commune', 'combo', NULL, 'vue_communes',4);
INSERT INTO pdi.modele_traitement_parametre (idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (43, 'DATE_DEB', true, 2, 'Début', 'datefield', '2013-01-01', NULL, 4);
INSERT INTO pdi.modele_traitement_parametre (idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (44, 'DATE_FIM', true, 2, 'Fin', 'datefield', '2013-01-01', NULL, 4);





-- Permis : voie (res_route ou piste)
CREATE TABLE remocra.voie
(
  id bigserial NOT NULL,
  geometrie geometry NOT NULL,
  mot_classant character varying NOT NULL,
  nom character varying NOT NULL,
  source character varying NOT NULL,
  commune bigint NOT NULL,
  CONSTRAINT voie_pkey PRIMARY KEY (id ),
  CONSTRAINT fk375195d2da796c FOREIGN KEY (commune)
      REFERENCES remocra.commune (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.voie
  OWNER TO postgres;




ALTER TABLE remocra.permis rename column adresse to complement;
ALTER TABLE remocra.permis add column voie character varying NOT NULL default '';
ALTER TABLE remocra.permis ALTER COLUMN voie DROP DEFAULT;




-- Paramètres de configuration
delete from param_conf where cle in ('PERMIS_TOLERANCE_VOIES_METRES', 'PDI_NOTIFICATION_KML_MODELE_ID', 'PDI_NOTIFICATION_KML_UTILISATEUR_ID');
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PERMIS_TOLERANCE_VOIES_METRES', 'Tolérance de chargement des voies, exprimée en mètres', '150', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOTIFICATION_KML_MODELE_ID', 'Identifiant du modèle de message à utiliser dans le cas de la publication d''un nouveau fichier de risques KML', '1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOTIFICATION_KML_UTILISATEUR_ID', 'Identifiant de l''utilisateur à notifier dans le cas de la publication d''un nouveau fichier de risques KML', '29', 1);

delete from param_conf where cle in ('PDI_NOTIFICATION_GENERAL_MODELE_ID', 'PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID');
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOTIFICATION_GENERAL_MODELE_ID', 'Identifiant du modèle de message à utiliser dans le cadre de la notification générale (erreurs générales, autres, etc.)', '1', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID', 'Identifiant de l''utilisateur à notifier dans le cadre de la notification générale (erreurs générales, autres, etc.)', '29', 1);

delete from param_conf where cle in ('PDI_FTP_DATABASE_SQL_FILE', 'PDI_FTP_DATABASE_ZIP_FILE', 'PDI_FTP_DOSSIER_DOCUMENT', 'PDI_FTP_DOSSIER_SQL', 'PDI_FTP_PORT', 'PDI_FTP_URL', 'PDI_FTP_USER_NAME', 'PDI_FTP_USER_PASSWORD');
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DATABASE_SQL_FILE', 'Nom du fichier SQL d''échange contenu dans le fichier ZIP', 'remocra.sql', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DATABASE_ZIP_FILE', 'Nom du fichier ZIP d''échange SQL', 'remocra.zip', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DOSSIER_DOCUMENT', 'Nom du sous-dossier FTP pour le stockage des documents', 'DOCS', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_DOSSIER_SQL', 'Nom du sous-dossier FTP pour le stockage des fichiers d''échanges SQL', 'SQL', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_PORT', 'Port du site FTP', '21', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_URL', 'URL d''accès au site FTP', 'XX.XX.XX.XX', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_USER_NAME', 'Nom d''utilisateur du compte FTP', 'remocra', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('PDI_FTP_USER_PASSWORD', 'Mot de passe du compte FTP', 'XXXXXXX', 1);



-- Modèle d'email (remocra)
CREATE TABLE remocra.email_modele
(
  id bigserial NOT NULL,
  code character varying,
  corps character varying,
  objet character varying,
  version integer DEFAULT 1,
  CONSTRAINT email_modele_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.email_modele
  OWNER TO postgres;



delete from email_modele where code in ('ERREUR_TRAITEMENT_AUTOMATISE', 'PUBLICATION_KML_RISQUE', 'UTILISATEUR_MAIL_INSCRIPTION', 'UTILISATEUR_MAIL_MOT_DE_PASSE_PERDU', 'UTILISATEUR_MAIL_MOT_DE_PASSE');

-- PDI : Erreur lors de l'exécution d'un traitement automatisé
insert into email_modele(code, corps, objet) values ('ERREUR_TRAITEMENT_AUTOMATISE', '<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<style type="text/css">
			div{

			}
			
			p.body{
				text-decoration:none;
				color:#000000;
				font-family: arial;
				font-size: 14px;
			}
			
			p.footer{
				text-decoration:none;
				font-style: italic;
				color:#AAAAAA;
				font-family: arial;
				font-size: 12px;
			}
		</style>
	</head>
	<body>
		<div>
			<p class="body">
				Bonjour [UTILISATEUR_NOM_COMPLET],<br/><br/>
				Une erreur s''est produite lors de l''exécution d''un traitement automatisé sur le serveur du site <a href="[URL_SITE]">[URL_SITE]</a><br/>
				Merci de consulter le document ci-joint pour analyser l''origine de cette erreur.<br/><br/>
				Cordialement.
			<p>
			<p class="footer">Ce message vous a été envoyé automatiquement, merci de ne par répondre à l''expéditeur.<br>En cas de difficultés, merci de contacter l''administrateur de la plate-forme.</p>
		</div>
	</body>
</html>', 'Erreur de traitement automatisé');

-- PDI : Information relative à la publication d'un fichier des risques
insert into email_modele(code, corppgs, objet) values ('PUBLICATION_KML_RISQUE', '<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<style type="text/css">
			div{

			}
			
			p.body{
				text-decoration:none;
				color:#000000;
				font-family: arial;
				font-size: 14px;
			}
			
			p.footer{
				text-decoration:none;
				font-style: italic;
				color:#AAAAAA;
				font-family: arial;
				font-size: 12px;
			}
		</style>
	</head>
	<body>
		<div>
			<p class="body">
				Bonjour [UTILISATEUR_NOM_COMPLET],<br/><br/>
				Un nouveau fichier KML des risques a été publié sur le site <a href="[URL_SITE]">[URL_SITE]</a><br/><br/>
				Cordialement.
			<p>
			<p class="footer">Ce message vous a été envoyé automatiquement, merci de ne par répondre à l''expéditeur.<br>En cas de difficultés, merci de contacter l''administrateur de la plate-forme.</p>
		</div>
	</body>
</html>', 'Publication d''un nouveau fichier KML des risques');

-- Email suite à une création d'utilisateur
insert into email_modele(code, corps, objet) values ('UTILISATEUR_MAIL_INSCRIPTION', '<title>Inscription au site SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Vous avez été inscrit comme utilisateur du site "REMOcRA". Nous vous communiquons ci-dessous vos identifiants d''accès.<br/><br/><b>Identifiants d''accès au site</b></p><p><b> <a href="[URL_SITE]remocra">[URL_SITE]remocra</a></b></p><p><table cellpadding="5"><tbody><tr><td>Nom d''utilisateur : </td><td>[IDENTIFIANT]</td></tr><tr><td>Mot de passe : </td><td>[MOT_DE_PASSE]</td></tr></tbody></table><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - INSCRIPTION AU SITE');

-- Email suite à une demande de réinitialisation de mot de passe
insert into email_modele(code, corps, objet) values ('UTILISATEUR_MAIL_MOT_DE_PASSE_PERDU', '<title>Demande de réinitialisation de mot de passe pour le site SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Vous avez effectué une demande de réinitialisation de votre mot de passe pour le site "REMOcRA". Nous vous communiquons le lien à usage unique qui vous permettra de choisir votre nouveau mot de passe.<br/><br/></p><p><table cellpadding="5"><tbody><tr><td>Nom d''utilisateur : </td><td>[IDENTIFIANT]</td></tr><tr><td>Lien de réinitialisation</td><td><b><a href="[URL_SITE]remocra#profil/password/reset/[CODE]">lien</a></b></td></tr></tbody></table><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - DEMANDE DE MODIFICATION DE MOT DE PASSE'); 

-- Email de confirmation du mot de passe modifié (depuis Mon Profil ou via une demande de réinitialisation)
insert into email_modele(code, corps, objet) values ('UTILISATEUR_MAIL_MOT_DE_PASSE', '<title>Modification du mot de passe pour le site SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Vous avez modifié votre mot de passe pour le site "REMOcRA". Nous vous communiquons ci-dessous vos identifiants d''accès.<br/><br/><b>Identifiants d''accès au site</b></p><p><b><a href="[URL_SITE]remocra">[URL_SITE]remocra</a></b></p><p><table cellpadding="5"><tbody><tr><td>Nom d''utilisateur : </td><td>[IDENTIFIANT]</td></tr><tr><td>Mot de passe : </td><td>[MOT_DE_PASSE]</td></tr></tbody></table><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - MODIFICATION DE MOT DE PASSE');




CREATE TABLE remocra.dde_mdp
(
  id bigserial NOT NULL,
  code character varying NOT NULL,
  date_demande timestamp without time zone NOT NULL DEFAULT now(),
  utilisateur bigint NOT NULL,
  CONSTRAINT dde_mdp_pkey PRIMARY KEY (id ),
  CONSTRAINT fk5a4fde5fa98055b2 FOREIGN KEY (utilisateur)
      REFERENCES remocra.utilisateur (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dde_mdp_code_key UNIQUE (code )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.dde_mdp
  OWNER TO postgres;



TRUNCATE sous_type_alerte_elt CASCADE;
TRUNCATE type_alerte_elt CASCADE;

INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (1, true, 'ROUTE', 'Etablissement');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (2, true, 'RESIDENCE', 'Résidence');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (3, true, 'RESEAUROUTIER', 'Réseau routier');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (4, true, 'AUTRE', 'Autre');

SELECT pg_catalog.setval('type_alerte_elt_id_seq', 5, true);

INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (1, true, 'ERPICPE', 'ERP/ICPE', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (2, true, 'SERVICEPUBLIC', 'Service public', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (3, true, 'CAMPING', 'Camping', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (4, true, 'SPORTPLEINAIR', 'Sport plein air', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (5, true, 'ENTREPRISE', 'Entreprise', 0, 1);

INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (6, true, 'RESIDENCE', 'Résidence', 0, 2);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (7, true, 'PERIMETRE', 'Périmètre', 2, 2);

INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (8, true, 'ROUTEPISTE', 'Route/piste', 1, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (9, true, 'PLACE', 'Place', 2, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (10, true, 'PARKING', 'Parking', 2, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (11, true, 'STATIONSERVICE', 'Station service', 0, 3);

INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (12, true, 'POINTREMARQUABLE', 'Point remarquable', 0, 4);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (13, true, 'SQUAREPARC', 'Square/Parc', 2, 4);

SELECT pg_catalog.setval('sous_type_alerte_elt_id_seq', 14, true);



-- Communes PPRIF
update remocra.commune set pprif = false;
update remocra.commune set pprif = true where insee like '83%' and lower(nom) similar to
'%(plan%de%la%tour|vidauban|le%beausset|evenos|signes|adrets|frejus|bormes|tanneron|la%cadiere|s%te%maxime|le%castellet|collobrieres|s%t%raphael)%';


-- Suppression des tables inutiles
DROP TABLE IF EXISTS remocra.organisme_com;
DROP TABLE IF EXISTS remocra.organisme_dde;
DROP TABLE IF EXISTS remocra.organisme_ddt;
DROP TABLE IF EXISTS remocra.organisme_rem;
DROP TABLE IF EXISTS remocra.organisme_sdi;




INSERT INTO remocra.type_droit(code, nom, description, version) VALUES ('PERMIS_DOCUMENTS', 'permis.documents', 'Droit sur les les documents des permis', 1);

-- Jeu de données de tests
--INSERT INTO remocra.droit(profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (2, (SELECT id from remocra.type_droit where code = 'PERMIS'), true, true, true, true);
--INSERT INTO remocra.droit(profil_droit, type_droit, droit_create, droit_read, droit_update, droit_delete) VALUES (2, (SELECT id from remocra.type_droit where code = 'PERMIS_DOCUMENTS'), true, false, false, false);



-- Documents

drop table remocra.alerte_document;
drop table remocra.document;

CREATE TABLE remocra.document
(
  id bigserial NOT NULL,
  code character varying NOT NULL,
  date timestamp without time zone NOT NULL DEFAULT now(),
  fichier character varying NOT NULL,
  repertoire character varying NOT NULL,
  type character varying NOT NULL,
  CONSTRAINT document_pkey PRIMARY KEY (id ),
  CONSTRAINT document_code_key UNIQUE (code ),
  CONSTRAINT document_fichier_repertoire_key UNIQUE (fichier , repertoire )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.document
  OWNER TO postgres;


CREATE TABLE remocra.alerte_document
(
  id bigserial NOT NULL,
  alerte bigint NOT NULL,
  document bigint NOT NULL,
  CONSTRAINT alerte_document_pkey PRIMARY KEY (id ),
  CONSTRAINT fkedde2a1136f0130a FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fkedde2a11d653dae6 FOREIGN KEY (alerte)
      REFERENCES remocra.alerte (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.alerte_document
  OWNER TO postgres;


CREATE TABLE remocra.delib_document
(
  id bigserial NOT NULL,
  document bigint NOT NULL,
  utilisateur bigint NOT NULL,
  CONSTRAINT delib_document_pkey PRIMARY KEY (id ),
  CONSTRAINT fk61f2b89636f0130a FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk61f2b896a98055b2 FOREIGN KEY (utilisateur)
      REFERENCES remocra.utilisateur (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.delib_document
  OWNER TO postgres;


CREATE TABLE remocra.permis_document
(
  id bigserial NOT NULL,
  document bigint NOT NULL,
  permis bigint NOT NULL,
  CONSTRAINT permis_document_pkey PRIMARY KEY (id ),
  CONSTRAINT fkf1087ba036f0130a FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fkf1087ba08cb9e08 FOREIGN KEY (permis)
      REFERENCES remocra.permis (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.permis_document
  OWNER TO postgres;



COMMIT;

