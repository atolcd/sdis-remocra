BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;





SET search_path = remocra, pg_catalog;




-- delib_document renommée
drop table remocra.delib_document;

CREATE TABLE remocra.depot_document
(
  id bigserial NOT NULL,
  document bigint NOT NULL,
  utilisateur bigint NOT NULL,
  CONSTRAINT depot_document_pkey PRIMARY KEY (id),
  CONSTRAINT fk2b3f72c636f0130a FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk2b3f72c6a98055b2 FOREIGN KEY (utilisateur)
      REFERENCES remocra.utilisateur (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.depot_document
  OWNER TO postgres;



-- Suppresion des modèles de messages
delete from remocra.email_modele;

--
-- TOC entry 3517 (class 0 OID 51089)
-- Dependencies: 220 3518
-- Data for Name: email_modele; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO email_modele (id, code, corps, objet, version) VALUES (3, 'ERREUR_TRAITEMENT_AUTOMATISE', '<html xmlns="http://www.w3.org/1999/xhtml">
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
</html>', 'Erreur de traitement automatisé', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (4, 'PUBLICATION_KML_RISQUE', '<html xmlns="http://www.w3.org/1999/xhtml">
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
</html>', 'Publication d''un nouveau fichier KML des risques', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (5, 'UTILISATEUR_MAIL_INSCRIPTION', '<title>Inscription au site SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Vous avez été inscrit comme utilisateur du site "REMOcRA". Nous vous communiquons ci-dessous vos identifiants d''accès.<br/><br/><b>Identifiants d''accès au site</b></p><p><b> <a href="[URL_SITE]">[URL_SITE]</a></b></p><p><table cellpadding="5"><tbody><tr><td>Nom d''utilisateur : </td><td>[IDENTIFIANT]</td></tr><tr><td>Mot de passe : </td><td>[MOT_DE_PASSE]</td></tr></tbody></table><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - INSCRIPTION AU SITE', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (6, 'UTILISATEUR_MAIL_MOT_DE_PASSE_PERDU', '<title>Demande de réinitialisation de mot de passe pour le site SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Vous avez effectué une demande de réinitialisation de votre mot de passe pour le site "REMOcRA". Nous vous communiquons le lien à usage unique qui vous permettra de choisir votre nouveau mot de passe.<br/><br/></p><p><table cellpadding="5"><tbody><tr><td>Nom d''utilisateur : </td><td>[IDENTIFIANT]</td></tr><tr><td>Lien de réinitialisation</td><td><b><a href="[URL_SITE]#profil/password/reset/[CODE]">lien</a></b></td></tr></tbody></table><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - DEMANDE DE MODIFICATION DE MOT DE PASSE', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (7, 'UTILISATEUR_MAIL_MOT_DE_PASSE', '<title>Modification du mot de passe pour le site SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Vous avez modifié votre mot de passe pour le site "REMOcRA". Nous vous communiquons ci-dessous vos identifiants d''accès.<br/><br/><b>Identifiants d''accès au site</b></p><p><b><a href="[URL_SITE]">[URL_SITE]</a></b></p><p><table cellpadding="5"><tbody><tr><td>Nom d''utilisateur : </td><td>[IDENTIFIANT]</td></tr><tr><td>Mot de passe : </td><td>[MOT_DE_PASSE]</td></tr></tbody></table><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - MODIFICATION DE MOT DE PASSE', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (8, 'COMMUNE_HYDRANT_INDISPONIBLE', '<title>Hydrants indisponibles SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Des hydrants sont indisponibles sur votre communes, en voici la liste en piece jointe.<br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - Hydrants indisponibles', 1);


INSERT INTO email_modele (id, code, corps, objet, version) VALUES (9, 'DEPOT_DELIB', '<title>Dépôt de délibération(s) SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Un dépôt de délibération(s) a été réalisé par un utilisateur de l''organisme [NOM_ORGANISME]. Le fichier est disponible <a href="[URL_SITE]telechargement/document/[CODE]">ici</a><br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - Dépôt de délibérations - [NOM_ORGANISME]', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (10, 'DEPOT_DECLAHYDRANT', '<title>Déclaration d''un hydrant SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Un dossier de déclaration d''hydrant(s) a été déposé par un utilisateur de l''organisme [NOM_ORGANISME]. Le fichier est disponible <a href="[URL_SITE]telechargement/document/[CODE]">ici</a><br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - Déclaration d''un hydrant - [NOM_ORGANISME]', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (11, 'DEPOT_RECEPTRAVAUX', '<title>Réception de travaux SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Un dossier de réception de travaux a été déposé par un utilisateur de l''organisme [NOM_ORGANISME]. Le fichier est disponible <a href="[URL_SITE]telechargement/document/[CODE]">ici</a><br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - Réception de travaux - [NOM_ORGANISME]', 1);

--
-- TOC entry 3522 (class 0 OID 0)
-- Dependencies: 219
-- Name: email_modele_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('email_modele_id_seq', 12, true);




-- Types de droits
update type_droit set code='DEPOT_DELIB', description='Droit sur le dépôt de délibérations', nom='depot.delib' where code='ADRESSES_DELIB';
insert into type_droit(code, description, nom, version) values ('DEPOT_DECLAHYDRANT', 'Droit sur le dépôt de dossiers de déclaration d''hydrants', 'depot.declahydrant', 1);
insert into type_droit(code, description, nom, version) values ('DEPOT_RECEPTRAVAUX', 'Droit sur les dépôt de dossiers réception de travaux', 'depot.receptravaux', 1);




-- Paramètres de configuration
delete from param_conf where cle in ('WMS_PUBLIC_LAYERS', 'DOSSIER_DEPOT_DELIB', 'DOSSIER_DEPOT_DECLA_HYDRANT', 'DOSSIER_DEPOT_RECEP_TRAVAUX', 'EMAIL_DEST_DEPOT_DELIB', 'EMAIL_DEST_DEPOT_DECLAHYDRANT', 'EMAIL_DEST_DEPOT_RECEPTRAVAUX');

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('WMS_PUBLIC_LAYERS', 'Couches publiques du serveur WMS avec séparateur %. Exemple : remocra:ADMINISTRATIF%remocra:RISQUE', 'remocra:ADMINISTRATIF%remocra:RISQUE', 1);

INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_DELIB', 'Emplacement du dossier de stockage des délibérations', '/var/remocra/deliberations', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_DECLA_HYDRANT', 'Emplacement du dossier de stockage des déclarations d''hydrants', '/var/remocra/declahydrant', 1);
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_RECEP_TRAVAUX', 'Emplacement du dossier de stockage des dossiers de réception de travaux', '/var/remocra/receptravaux', 1);

-- adresse83@sdis83.fr
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_DEPOT_DELIB', 'Adresse du destinataire des emails de notification de dépôts de délibérations', 'cva@atolcd.com', 1);
-- referent_deci@sdis83.fr
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_DEPOT_DECLAHYDRANT', 'Adresse du destinataire des emails de notification de dépôts de dossiers de déclaration d''hydrant(s)', 'cva@atolcd.com', 1);
-- referent_dfci@sdis83.fr
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('EMAIL_DEST_DEPOT_RECEPTRAVAUX', 'Adresse du destinataire des emails de notification de dépôts de dossiers de réception de travaux', 'cva@atolcd.com', 1);




COMMIT;

