BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;





SET search_path = pdi, pg_catalog;

-- Suppression des demandes en cours
delete from pdi.traitement_parametre;
delete from pdi.traitement;


-- Suppression des modèles
delete from pdi.modele_traitement_parametre;
delete from pdi.modele_traitement;

delete from pdi.modele_message;

--
-- TOC entry 3513 (class 0 OID 18043)
-- Dependencies: 178 3518
-- Data for Name: modele_message; Type: TABLE DATA; Schema: pdi; Owner: postgres
--

INSERT INTO modele_message (idmodele, corps, objet) VALUES (1, '<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Succès traitement SDIS83 REMOCRA</title>
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
				Le traitement <b>"[TRAITEMENT_NOM]"</b> demandé le <b>[TRAITEMENT_DATE_DEMANDE]</b> a été exécuté avec succès.<br/><br/>
				[URL_TELECHARGEMENT]<br/><br/>
				Cordialement.
			<p>
			<p class="footer">Ce message vous a été envoyé automatiquement, merci de ne par répondre à l''expéditeur.<br>En cas de difficultés, merci de contacter l''administrateur de la plate-forme.</p>
		</div>
	</body>
</html>', 'Demande [TRAITEMENT_DOSSIER_NUMERO] : Traitement effectué avec succès');
INSERT INTO modele_message (idmodele, corps, objet) VALUES (3, '<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Erreur traitement SDIS83 REMOCRA</title>
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
				Le traitement <b>"[TRAITEMENT_NOM]"</b> demandé le <b>[TRAITEMENT_DATE_DEMANDE]</b> a provoqué une erreur.<br/>
				Les opérations demandées n''ont pas pu être réalisées. Merci de contacter l''administrateur de la plate-forme pour identifier l''origine de l''erreur.<br/><br/>
				Cordialement.
			<p>
			<p class="footer">Ce message vous a été envoyé automatiquement, merci de ne par répondre à l''expéditeur.<br>En cas de difficultés, merci de contacter l''administrateur de la plate-forme.</p>
		</div>
	</body', 'Demande [TRAITEMENT_DOSSIER_NUMERO] : Erreur de traitement');
INSERT INTO modele_message (idmodele, corps, objet) VALUES (2, '<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Succès traitement SDIS83 REMOCRA</title>
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
				Le traitement <b>"[TRAITEMENT_NOM]"</b> demandé le <b>[TRAITEMENT_DATE_DEMANDE]</b> a été exécuté avec succès.<br/><br/>
				Cordialement.
			<p>
			<p class="footer">Ce message vous a été envoyé automatiquement, merci de ne par répondre à l''expéditeur.<br>En cas de difficultés, merci de contacter l''administrateur de la plate-forme.</p>
		</div>
	</body>
</html>', 'Demande [TRAITEMENT_DOSSIER_NUMERO] : Traitement effectué avec succès');


--
-- TOC entry 3514 (class 0 OID 18049)
-- Dependencies: 179 3513 3513 3518
-- Data for Name: modele_traitement; Type: TABLE DATA; Schema: pdi; Owner: postgres
--

INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (7, 4, 'Compile les fiches Atlas dans un fichier zip selon le territoire d''un utilisateur et transmet le lien dans un courrier électronique.', 'Téléchargement des fiches Atlas', '/demandes/atlas', 'creer_atlas', 'T', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (11, 1, 'Mise à jour de la position des hydrants à partir d''un fichier de positions GPS', 'Mettre à jour les positions des hydrants', '/demandes/import_gps', 'mettre_a_jour_position_hydrant', 'T', 3, 2);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (10, 5, 'Mise à jour des métadonnées à partir du serveur régional du CRIGE PACA', 'Mettre à jour les fiches de métadonnées', '/demandes/metadonnee', 'mettre_a_jour_metadonnees', 'T', 3, 2);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (8, 5, 'Supprime de fichier KML des risques technologiques', 'Suppression du fichier KML lié aux risques technologiques', '/demandes/kml_risque_technologique', 'supprimer_kml', 'J', 3, 2);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (1, 1, 'Etat des hydrants par commune', 'Etat des hydrants', '/demandes/statistiques_hydrants', 'etat_hydrant', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (12, 1, 'Etat des hydrants indisponibles hier par commune', 'Etat des hydrants indisponibles', '/demandes/statistiques_hydrants', 'etat_hydrant_indisponibles', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (4, 3, 'Liste des permis par avis, commune et sur une période donnée', 'Liste des permis', '/demandes/permis', 'export_csv', 'J
J', 3, 1);


--
-- TOC entry 3515 (class 0 OID 18055)
-- Dependencies: 180 3514 3518
-- Data for Name: modele_traitement_parametre; Type: TABLE DATA; Schema: pdi; Owner: postgres
--

INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (41, 'Avis', 2, true, 'vue_permis_etats', 'combo', NULL, 'TYPE_PERMIS_AVIS_ID', 4);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (43, 'Début', 3, true, NULL, 'datefield', '2013-01-01', 'DATE_DEB', 4);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (44, 'Fin', 4, true, NULL, 'datefield', '2013-01-01', 'DATE_FIN', 4);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (45, 'Fichier des positions GPS', 1, true, NULL, 'filefield', NULL, 'FICHIER_GPS', 11);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (1, 'Commune', 1, false, 'vue_communes', 'combo', NULL, 'COMMUNE_ID', 1);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (6, 'Utilisateur', 1, true, 'vue_utilisateurs', 'combo', NULL, 'LST_UTILISATEURS', 7);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (46, 'Commune', 1, true, 'vue_communes', 'combo', NULL, 'COMMUNE_ID', 12);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (42, 'Commune', 1, true, 'vue_communes', 'combo', NULL, 'COMMUNE_ID', 4);





SET search_path = remocra, pg_catalog;

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


--
-- TOC entry 3522 (class 0 OID 0)
-- Dependencies: 219
-- Name: email_modele_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('email_modele_id_seq', 10, true);





COMMIT;

