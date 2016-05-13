--
-- PostgreSQL database dump
--

-- Started on 2015-03-11 16:29:48 CET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 3931 (class 0 OID 0)
-- Dependencies: 223
-- Name: email_modele_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('email_modele_id_seq', 13, true);


--
-- TOC entry 3932 (class 0 OID 0)
-- Dependencies: 254
-- Name: sous_type_alerte_elt_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('sous_type_alerte_elt_id_seq', 19, true);


--
-- TOC entry 3933 (class 0 OID 0)
-- Dependencies: 259
-- Name: thematique_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('thematique_id_seq', 7, true);


--
-- TOC entry 3934 (class 0 OID 0)
-- Dependencies: 263
-- Name: type_alerte_ano_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_alerte_ano_id_seq', 7, true);


--
-- TOC entry 3935 (class 0 OID 0)
-- Dependencies: 265
-- Name: type_alerte_elt_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_alerte_elt_id_seq', 5, true);


--
-- TOC entry 3936 (class 0 OID 0)
-- Dependencies: 270
-- Name: type_hydrant_anomalie_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_anomalie_id_seq', 107, true);


--
-- TOC entry 3937 (class 0 OID 0)
-- Dependencies: 272
-- Name: type_hydrant_anomalie_nature_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_anomalie_nature_id_seq', 198, true);


--
-- TOC entry 3938 (class 0 OID 0)
-- Dependencies: 275
-- Name: type_hydrant_critere_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_critere_id_seq', 9, true);


--
-- TOC entry 3939 (class 0 OID 0)
-- Dependencies: 277
-- Name: type_hydrant_diametre_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_diametre_id_seq', 3, true);


--
-- TOC entry 3940 (class 0 OID 0)
-- Dependencies: 280
-- Name: type_hydrant_domaine_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_domaine_id_seq', 5, true);


--
-- TOC entry 3941 (class 0 OID 0)
-- Dependencies: 281
-- Name: type_hydrant_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_id_seq', 2, true);


--
-- TOC entry 3942 (class 0 OID 0)
-- Dependencies: 283
-- Name: type_hydrant_marque_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_marque_id_seq', 5, true);


--
-- TOC entry 3943 (class 0 OID 0)
-- Dependencies: 285
-- Name: type_hydrant_materiau_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_materiau_id_seq', 5, true);


--
-- TOC entry 3944 (class 0 OID 0)
-- Dependencies: 287
-- Name: type_hydrant_modele_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_modele_id_seq', 27, true);


--
-- TOC entry 3945 (class 0 OID 0)
-- Dependencies: 289
-- Name: type_hydrant_nature_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_nature_id_seq', 8, true);


--
-- TOC entry 3946 (class 0 OID 0)
-- Dependencies: 291
-- Name: type_hydrant_positionnement_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_positionnement_id_seq', 2, true);


--
-- TOC entry 3947 (class 0 OID 0)
-- Dependencies: 293
-- Name: type_hydrant_saisie_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_saisie_id_seq', 6, true);


--
-- TOC entry 3948 (class 0 OID 0)
-- Dependencies: 295
-- Name: type_hydrant_vol_constate_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_hydrant_vol_constate_id_seq', 5, true);


--
-- TOC entry 3949 (class 0 OID 0)
-- Dependencies: 296
-- Name: type_permis_avis_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_permis_avis_id_seq', 7, true);


--
-- TOC entry 3950 (class 0 OID 0)
-- Dependencies: 298
-- Name: type_permis_interservice_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_permis_interservice_id_seq', 3, true);


--
-- TOC entry 3951 (class 0 OID 0)
-- Dependencies: 300
-- Name: type_rci_degre_certitude_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_rci_degre_certitude_id_seq', 5, true);


--
-- TOC entry 3952 (class 0 OID 0)
-- Dependencies: 302
-- Name: type_rci_origine_alerte_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_rci_origine_alerte_id_seq', 6, true);


--
-- TOC entry 3953 (class 0 OID 0)
-- Dependencies: 304
-- Name: type_rci_prom_categorie_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_rci_prom_categorie_id_seq', 35, true);


--
-- TOC entry 3954 (class 0 OID 0)
-- Dependencies: 305
-- Name: type_rci_prom_famille_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_rci_prom_famille_id_seq', 5, true);


--
-- TOC entry 3955 (class 0 OID 0)
-- Dependencies: 307
-- Name: type_rci_prom_partition_id_seq; Type: SEQUENCE SET; Schema: remocra; Owner: postgres
--

SELECT pg_catalog.setval('type_rci_prom_partition_id_seq', 15, true);


SET search_path = pdi, pg_catalog;

--
-- TOC entry 3889 (class 0 OID 549083)
-- Dependencies: 174
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
-- TOC entry 3890 (class 0 OID 549089)
-- Dependencies: 175 3889 3889
-- Data for Name: modele_traitement; Type: TABLE DATA; Schema: pdi; Owner: postgres
--

INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (7, 4, 'Compile les fiches Atlas dans un fichier zip selon le territoire d''un utilisateur et transmet le lien dans un courrier électronique.', 'Téléchargement des fiches Atlas', '/demandes/atlas', 'creer_atlas', 'T', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (11, 1, 'Mise à jour de la position des hydrants à partir d''un fichier de positions GPS', 'Mettre à jour les positions des hydrants', '/demandes/import_gps', 'mettre_a_jour_position_hydrant', 'T', 3, 2);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (8, 5, 'Supprime de fichier KML des risques technologiques', 'Suppression du fichier KML lié aux risques technologiques', '/demandes/kml_risque_technologique', 'supprimer_kml', 'J', 3, 2);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (4, 3, 'Liste des permis par avis, commune et sur une période donnée', 'Liste des permis', '/demandes/permis', 'export_csv', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (1, 1, 'Etat des hydrants PIBI par commune', 'Etat des hydrants PIBI', '/demandes/statistiques_hydrants', 'etat_hydrant', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (14, 1, 'Etat des hydrants PENA par commune', 'Etat des hydrants PENA', '/demandes/statistiques_hydrants/pena', 'etat_pena', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (10, 0, 'Mise à jour des métadonnées à partir du serveur régional du CRIGE PACA', 'Mettre à jour les fiches de métadonnées', '/demandes/metadonnee', 'mettre_a_jour_metadonnees', 'T', 3, 2);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (16, 2, 'Nombre d''alertes par utilisateur dans une caserne', 'Nombre d''alertes par utilisateur dans une caserne', '/demandes/alertes', 'alertes_par_utilisateur', 'J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (15, 1, 'Points d''eau à numéroter', 'Points d''eau à numéroter', '/demandes/statistiques_hydrants/hydrants_a_numeroter', 'hydrants_a_numeroter', 'J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (12, -1, 'Etat des hydrants PIBI indisponibles hier par commune', 'Etat des hydrants PIBI indisponibles', '/demandes/statistiques_hydrants', 'etat_hydrant_indisponibles', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (13, -1, 'Etat des hydrants PENA indisponibles hier par commune', 'Etat des hydrants PENA indisponibles', '/demandes/statistiques_hydrants/pena', 'etat_pena_indisponibles', 'J
J', 3, 1);
INSERT INTO modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) VALUES (17, 7, 'Départs de feu sur une période et pour une famille Prométhée', 'Départs de feu', '/demandes/rci', 'export_csv', 'J', 3, 1);


--
-- TOC entry 3891 (class 0 OID 549095)
-- Dependencies: 176 3890
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
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (47, 'Commune', 1, true, 'vue_communes', 'combo', NULL, 'COMMUNE_ID', 13);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (48, 'Commune', 1, true, 'vue_communes', 'combo', NULL, 'COMMUNE_ID', 14);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (62, 'Début', 2, true, NULL, 'datefield', '2014-01-01', 'DATE_DEB', 17);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (63, 'Fin', 3, true, NULL, 'datefield', '2020-12-31', 'DATE_FIN', 17);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (61, 'Famille prométhée', 1, true, 'vue_familles_promethee', 'combo', NULL, 'FAMILLE_PROMETHEE_ID', 17);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (49, 'Notifier les responsables', 2, true, '', 'checkbox', 'true', 'NOTIFIER_RESPONSABLES', 1);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (50, 'Notifier les responsables', 2, true, '', 'checkbox', 'true', 'NOTIFIER_RESPONSABLES', 12);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (51, 'Notifier les responsables', 2, true, '', 'checkbox', 'true', 'NOTIFIER_RESPONSABLES', 13);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (52, 'Notifier les responsables', 2, true, '', 'checkbox', 'true', 'NOTIFIER_RESPONSABLES', 14);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (53, 'Notifier les communes', 3, true, '', 'checkbox', 'true', 'NOTIFIER_COMMUNES', 1);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (54, 'Notifier les communes', 3, true, '', 'checkbox', 'true', 'NOTIFIER_COMMUNES', 12);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (55, 'Notifier les communes', 3, true, '', 'checkbox', 'true', 'NOTIFIER_COMMUNES', 13);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (56, 'Notifier les communes', 3, true, '', 'checkbox', 'true', 'NOTIFIER_COMMUNES', 14);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (57, 'Caserne', 1, true, 'vue_organisme_cis', 'combo', NULL, 'ORGANISME_CIS_ID', 15);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (58, 'Caserne', 1, true, 'vue_organisme_cis', 'combo', NULL, 'ORGANISME_CIS_ID', 16);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (59, 'Début', 2, true, NULL, 'datefield', '2014-01-01', 'DATE_DEB', 16);
INSERT INTO modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) VALUES (60, 'Fin', 3, true, NULL, 'datefield', '2020-12-31', 'DATE_FIN', 16);


--
-- TOC entry 3892 (class 0 OID 549101)
-- Dependencies: 177
-- Data for Name: statut; Type: TABLE DATA; Schema: pdi; Owner: postgres
--

INSERT INTO statut (idstatut, code, libelle) VALUES (1, 'A', 'En attente');
INSERT INTO statut (idstatut, code, libelle) VALUES (2, 'S', 'Terminé');
INSERT INTO statut (idstatut, code, libelle) VALUES (3, 'E', 'En erreur');
INSERT INTO statut (idstatut, code, libelle) VALUES (4, 'C', 'En cours');


SET search_path = remocra, pg_catalog;

--
-- TOC entry 3899 (class 0 OID 549399)
-- Dependencies: 242
-- Data for Name: profil_droit; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (7, 'CG Base', 'public', 1, 'CG-BASE');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (8, 'DDTM DFCI', 'public', 1, 'DDTM-DFCI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (10, 'DDTM Permis', 'public', 1, 'DDTM-PERMIS');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (11, 'Maire', 'public', 1, 'COM-MAIRE');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (14, 'Commune DFCI', 'public', 1, 'COM-DFCI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (15, 'Commune hydrant E1', 'public', 1, 'COM-HYDRANT-E1');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (16, 'Commune hydrant E2', 'public', 1, 'COM-HYDRANT-E2');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (17, 'CT DFCI', 'public', 1, 'CT-DFCI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (18, 'CT Permis', 'public', 1, 'CT-PERMIS');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (19, 'CT Hydrant E1', 'public', 1, 'CT-HYDRANT-E1');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (20, 'CT Hydrant E2', 'public', 1, 'CT-HYDRANT-E2');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (21, 'CIS Base', 'public', 1, 'CIS-BASE');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (22, 'CIS Hydrant E1', 'public', 1, 'CIS-HYDRANT-E1');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (23, 'CIS Hydrant E2', 'public', 1, 'CIS-HYDRANT-E2');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (24, 'Chef de centre', 'public', 1, 'CIS-CHEF');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (25, 'SDIS Base', 'public', 1, 'SDIS-BASE');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (26, 'Référent DECI', 'public', 1, 'SDIS-REF-DECI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (27, 'Référent PENA', 'public', 1, 'SDIS-REF-PENA');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (28, 'Référent DFCI', 'public', 1, 'SDIS-REF-DFCI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (29, 'Administrateur permis', 'permis', 2, 'SDIS-ADM-PERMIS');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (30, 'Administrateur application', 'public', 1, 'SDIS-ADM-APP');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (31, 'SDIS Hydrant E1', 'public', 1, 'SDIS-HYDRANT-E1');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (32, 'SDIS Hydrant E2', 'public', 1, 'SDIS-HYDRANT-E2');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (33, 'Préventionniste', 'public', 1, 'SDIS-PREV');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (34, 'CODIS', 'public', 1, 'SDIS-CODIS');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (35, 'Gestionnaire Hydrant', 'public', 1, 'GEST-HYDRANT');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (37, 'CHEF GPOP', 'public', 0, 'CHEFGPOP');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (36, 'Consultation', 'public', 0, 'CONSULTATION');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (38, 'CG DFCI', 'public', 0, 'CG-DFCI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (39, 'Gendarme', 'public', 1, 'GENDARME');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (40, 'Policier', 'public', 1, 'POLICIER');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (41, 'RCCI', 'public', 0, 'RCCI');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (42, 'RCCI chef de centre', 'public', 0, 'RCCI-CHEFCENTRE');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (43, 'RCCI préventionniste', 'public', 0, 'RCCI-PREVENTION');
INSERT INTO profil_droit (id, nom, feuille_de_style_geo_server, version, code) VALUES (44, 'Directeur', 'public', 0, 'DIRECTEUR');


--
-- TOC entry 3908 (class 0 OID 549503)
-- Dependencies: 267
-- Data for Name: type_droit; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_droit (id, code, description, nom, version) VALUES (1, 'UTILISATEUR_FILTER_ALL', 'Droit sur tous les utilisateurs', 'utilisateur.filter.*', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (2, 'UTILISATEUR_FILTER_ORGANISME_UTILISATEUR', 'Droit sur les utilisateur de l''organisme de l''utilisateur', 'utilisateur.filter.organisme.utilisateur', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (3, 'REFERENTIELS', 'Droit sur les référentiels', 'referentiels', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (4, 'ADRESSES', 'Droit sur les adresses', 'adresses', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (5, 'DFCI', 'Droit sur dfci', 'dfci', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (6, 'HYDRANTS', 'Droit la thématique hydrants', 'hydrants', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (7, 'HYDRANTS_RECONNAISSANCE', 'Droit sur la reconnaissance des hydrants', 'hydrants.reconnaissance', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (8, 'HYDRANTS_CONTROLE', 'Droit sur le contrôle des hydrants', 'hydrants.controle', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (9, 'HYDRANTS_NUMEROTATION', 'Droit sur la numérotation des hydrants', 'hydrants.numerotation', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (10, 'HYDRANTS_MCO', 'Droit sur les module MCO des hydrants', 'hydrants.mco', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (11, 'TOURNEE', 'Droit sur les tournées', 'tournee', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (12, 'TOURNEE_RESERVATION', 'Droit sur la réservation des tournées', 'tournee.reservation', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (13, 'HYDRANTS_PRESCRIT', 'Droit sur les hydrants prescrits', 'hydrants.prescrit', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (14, 'HYDRANTS_TRAITEMENT', 'Droit sur les traitements hydrants', 'hydrants.traitement', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (15, 'PERMIS', 'Droit sur les permis', 'permis', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (16, 'PERMIS_DOCUMENTS', 'Droit sur les documents des permis', 'permis.documents', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (17, 'PERMIS_TRAITEMENT', 'Droit sur les traitements des permis', 'permis.traitement', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (18, 'RISQUES_KML', 'Droit sur les risques express', 'risques.kml', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (19, 'DOCUMENTS', 'Accès à la banque de document (blocs)', 'documents', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (20, 'DEPOT_DELIB', 'Droit sur le dépôt de délibérations', 'depot.delib', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (21, 'DEPOT_DECLAHYDRANT', 'Droit sur le dépôt de dossiers de déclaration d''hydrants', 'depot.declahydrant', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (22, 'DEPOT_RECEPTRAVAUX', 'Droit sur les dépôt de dossiers réception de travaux', 'depot.receptravaux', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (23, 'HYDRANTS_EXPORT_NON_NUM', 'Droit sur l''export des hydrants non numérotés', 'hydrants.exportnonnum', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (25, 'ALERTES_EXPORT', 'Droit sur l''export du nombre d''alertes par utilisateur', 'alertes.export', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (26, 'RCI', 'Droit sur le module RCI', 'rci', 1);
INSERT INTO type_droit (id, code, description, nom, version) VALUES (27, 'CARTOGRAPHIES', 'Droit sur la réalisation de cartographies', 'cartographies', 1);


--
-- TOC entry 3897 (class 0 OID 549301)
-- Dependencies: 219 3908 3899
-- Data for Name: droit; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (571, false, false, true, false, 1, 7, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (572, false, false, true, false, 1, 8, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (573, false, false, true, false, 1, 10, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (574, false, false, true, false, 1, 11, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (575, false, false, true, false, 1, 14, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (576, false, false, true, false, 1, 15, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (577, false, false, true, false, 1, 16, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (578, false, false, true, false, 1, 17, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (579, false, false, true, false, 1, 18, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (580, false, false, true, false, 1, 19, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (581, false, false, true, false, 1, 20, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (582, false, false, true, false, 1, 21, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (583, false, false, true, false, 1, 22, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (584, false, false, true, false, 1, 23, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (585, false, false, true, false, 1, 24, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (586, false, false, true, false, 1, 25, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (588, false, false, true, false, 1, 27, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (589, false, false, true, false, 1, 28, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (590, false, false, true, false, 1, 29, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (592, false, false, true, false, 1, 31, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (593, false, false, true, false, 1, 32, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (594, false, false, true, false, 1, 33, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (595, false, false, true, false, 1, 34, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (596, false, false, true, false, 1, 35, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (597, true, false, false, false, 1, 7, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (598, true, false, false, false, 1, 8, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (599, true, false, false, false, 1, 10, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (600, true, false, false, false, 1, 11, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (601, true, false, false, false, 1, 14, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (602, true, false, false, false, 1, 15, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (603, true, false, false, false, 1, 16, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (604, true, false, false, false, 1, 17, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (605, true, false, false, false, 1, 18, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (606, true, false, false, false, 1, 19, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (607, true, false, false, false, 1, 20, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (608, true, false, false, false, 1, 21, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (609, true, false, false, false, 1, 22, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (610, true, false, false, false, 1, 23, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (611, true, false, false, false, 1, 24, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (612, true, false, false, false, 1, 25, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (613, true, false, false, false, 1, 26, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (614, true, false, false, false, 1, 27, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (615, true, false, false, false, 1, 28, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (616, true, false, false, false, 1, 29, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (618, true, false, false, false, 1, 31, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (619, true, false, false, false, 1, 32, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (620, true, false, false, false, 1, 33, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (621, true, false, false, false, 1, 34, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (622, false, false, true, false, 1, 7, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (623, false, false, true, false, 1, 8, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (624, false, false, true, false, 1, 10, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (625, false, false, true, false, 1, 11, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (626, false, false, true, false, 1, 14, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (627, false, false, true, false, 1, 15, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (628, false, false, true, false, 1, 16, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (629, false, false, true, false, 1, 17, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (630, false, false, true, false, 1, 18, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (631, false, false, true, false, 1, 19, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (632, false, false, true, false, 1, 20, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (633, false, false, true, false, 1, 21, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (634, false, false, true, false, 1, 22, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (635, false, false, true, false, 1, 23, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (636, false, false, true, false, 1, 24, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (637, false, false, true, false, 1, 25, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (638, false, false, true, false, 1, 26, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (639, false, false, true, false, 1, 27, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (640, false, false, true, false, 1, 28, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (641, false, false, true, false, 1, 29, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (643, false, false, true, false, 1, 31, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (644, false, false, true, false, 1, 32, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (645, false, false, true, false, 1, 33, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (646, false, false, true, false, 1, 34, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (647, false, false, true, false, 1, 7, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (648, false, false, true, false, 1, 8, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (649, false, false, true, false, 1, 10, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (650, false, false, true, false, 1, 11, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (651, false, false, true, false, 1, 14, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (652, false, false, true, false, 1, 15, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (653, false, false, true, false, 1, 16, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (654, false, false, true, false, 1, 17, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (655, false, false, true, false, 1, 18, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (656, false, false, true, false, 1, 19, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (657, false, false, true, false, 1, 20, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (658, false, false, true, false, 1, 21, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (659, false, false, true, false, 1, 22, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (660, false, false, true, false, 1, 23, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (661, false, false, true, false, 1, 24, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (662, false, false, true, false, 1, 25, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (663, false, false, true, false, 1, 26, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (664, false, false, true, false, 1, 27, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (665, false, false, true, false, 1, 28, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (666, false, false, true, false, 1, 29, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (668, false, false, true, false, 1, 31, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (669, false, false, true, false, 1, 32, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (670, false, false, true, false, 1, 33, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (671, false, false, true, false, 1, 34, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (672, false, false, true, false, 1, 35, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (673, false, false, true, false, 1, 11, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (674, false, false, true, false, 1, 15, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (675, false, false, true, false, 1, 19, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (676, false, false, true, false, 1, 23, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (677, false, false, true, false, 1, 24, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (678, false, false, true, false, 1, 29, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (679, false, false, true, false, 1, 32, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (680, false, false, true, false, 1, 33, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (681, false, false, true, false, 1, 35, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (682, true, false, true, false, 1, 16, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (683, true, false, true, false, 1, 20, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (684, true, false, true, false, 1, 22, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (685, true, false, true, false, 1, 31, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (687, true, true, true, false, 1, 27, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (688, true, false, false, false, 1, 23, 7);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (689, true, false, false, false, 1, 32, 7);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (690, true, false, false, false, 1, 16, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (691, true, false, false, false, 1, 20, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (692, true, false, false, false, 1, 22, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (693, true, false, false, false, 1, 31, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (695, true, false, false, false, 1, 16, 10);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (696, true, false, false, false, 1, 20, 10);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (697, true, false, false, false, 1, 26, 10);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (698, true, false, true, false, 1, 16, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (699, true, false, true, false, 1, 20, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (700, true, false, true, false, 1, 22, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (701, true, false, true, false, 1, 23, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (702, true, false, true, false, 1, 31, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (703, true, false, true, false, 1, 32, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (704, true, false, false, false, 1, 26, 13);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (705, true, false, false, false, 1, 33, 13);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (706, true, false, false, false, 1, 11, 14);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (707, true, false, false, false, 1, 16, 14);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (708, true, false, false, false, 1, 20, 14);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (709, true, false, false, false, 1, 26, 14);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (710, false, false, true, false, 1, 10, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (686, true, true, true, false, 3, 26, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (711, false, false, true, false, 1, 11, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (712, false, false, true, false, 1, 18, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (713, true, false, true, false, 1, 29, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (714, true, false, false, false, 1, 10, 16);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (715, true, false, false, false, 1, 11, 16);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (716, true, false, false, false, 1, 18, 16);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (717, true, false, false, false, 1, 10, 17);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (718, true, false, false, false, 1, 11, 17);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (719, true, false, false, false, 1, 29, 17);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (720, true, false, true, false, 1, 34, 18);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (721, true, false, false, false, 1, 11, 20);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (722, true, false, false, false, 1, 11, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (723, true, false, false, false, 1, 15, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (724, true, false, false, false, 1, 16, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (725, true, false, false, false, 1, 19, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (726, true, false, false, false, 1, 20, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (727, true, false, false, false, 1, 35, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (728, true, false, false, false, 1, 7, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (729, true, false, false, false, 1, 8, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (730, true, false, false, false, 1, 14, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (731, true, false, false, false, 1, 17, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (732, true, false, false, false, 1, 28, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (591, true, true, true, true, 1, 30, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (617, true, true, true, true, 1, 30, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (642, true, true, true, true, 1, 30, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (667, true, true, true, true, 1, 30, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (733, true, true, true, true, 1, 30, 1);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (734, true, true, true, true, 1, 30, 2);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (735, true, true, true, true, 1, 30, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (736, true, true, true, true, 1, 30, 7);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (737, true, true, true, true, 1, 30, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (738, true, true, true, true, 1, 30, 9);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (739, true, true, true, true, 1, 30, 10);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (740, true, true, true, true, 1, 30, 11);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (741, true, true, true, true, 1, 30, 12);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (742, true, true, true, true, 1, 30, 13);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (743, true, true, true, true, 1, 30, 14);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (744, true, true, true, true, 1, 30, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (745, true, true, true, true, 1, 30, 16);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (746, true, true, true, true, 1, 30, 17);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (747, true, true, true, true, 1, 30, 18);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (748, true, true, true, true, 1, 30, 20);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (749, true, true, true, true, 1, 30, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (750, true, true, true, true, 1, 30, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (587, false, false, true, false, 3, 26, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (694, true, false, true, true, 2, 26, 9);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (1, true, false, false, false, 0, 26, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (2, true, false, false, false, 0, 27, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (751, false, false, true, false, 0, 36, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (752, true, false, false, false, 0, 36, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (753, false, false, true, false, 0, 36, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (754, false, false, true, false, 0, 36, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (755, true, false, false, false, 1, 22, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (756, true, false, false, false, 1, 24, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (757, true, false, false, false, 1, 26, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (758, true, false, false, false, 1, 27, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (759, true, false, false, false, 1, 30, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (760, true, false, false, false, 1, 24, 25);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (761, true, false, false, false, 1, 30, 25);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (773, false, false, true, false, 0, 37, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (774, true, false, false, false, 0, 37, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (775, false, false, true, false, 0, 37, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (776, false, false, true, false, 0, 37, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (777, false, false, true, false, 0, 37, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (778, true, false, true, false, 0, 37, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (779, true, false, false, false, 0, 37, 17);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (782, false, false, true, false, 1, 39, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (783, true, false, false, false, 1, 39, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (784, false, false, true, false, 1, 39, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (785, false, false, true, false, 1, 39, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (786, false, false, true, false, 1, 40, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (787, true, false, false, false, 1, 40, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (788, false, false, true, false, 1, 40, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (789, false, false, true, false, 1, 40, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (790, true, false, false, false, 1, 39, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (791, true, false, false, false, 1, 40, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (793, true, false, false, false, 1, 30, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (792, true, true, true, true, 2, 30, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (794, true, true, true, true, 0, 41, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (795, true, false, false, false, 0, 41, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (796, false, false, true, false, 0, 41, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (797, false, false, true, false, 0, 41, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (798, false, false, true, false, 0, 41, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (799, true, false, false, false, 0, 42, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (800, false, false, true, false, 0, 42, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (801, true, true, true, true, 0, 42, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (802, true, false, false, false, 0, 42, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (803, false, false, true, false, 0, 42, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (804, true, false, false, false, 0, 42, 25);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (805, false, false, true, false, 0, 42, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (806, false, false, true, false, 0, 42, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (807, true, false, false, false, 0, 43, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (808, true, false, false, false, 0, 43, 13);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (809, false, false, true, false, 0, 43, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (810, false, false, true, false, 0, 43, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (811, false, false, true, false, 0, 43, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (812, true, true, true, true, 0, 43, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (813, false, false, true, false, 0, 43, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (814, true, true, true, true, 0, 8, 26);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (815, true, false, false, false, 0, 38, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (816, false, false, true, false, 0, 38, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (817, false, false, true, false, 0, 38, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (818, true, false, false, false, 0, 38, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (819, false, false, true, false, 0, 38, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (820, true, false, false, false, 0, 29, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (821, true, false, false, false, 0, 24, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (822, true, false, false, false, 0, 21, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (823, true, false, false, false, 0, 22, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (824, true, false, false, false, 0, 23, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (825, true, false, false, false, 0, 34, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (826, true, false, false, false, 0, 33, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (827, true, false, false, false, 0, 41, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (828, true, false, false, false, 0, 42, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (829, true, false, false, false, 0, 43, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (830, true, false, false, false, 0, 26, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (831, true, false, false, false, 0, 28, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (832, true, false, false, false, 0, 27, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (833, true, false, false, false, 0, 25, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (834, true, false, false, false, 0, 31, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (835, true, false, false, false, 0, 32, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (837, true, false, false, false, 0, 44, 27);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (844, true, true, true, true, 0, 44, 6);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (845, true, true, true, true, 0, 44, 8);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (846, true, false, false, false, 0, 44, 23);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (848, true, true, true, true, 0, 44, 9);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (850, true, true, true, true, 0, 44, 7);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (852, false, false, true, false, 0, 44, 15);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (843, false, false, true, false, 1, 44, 19);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (842, false, false, true, false, 1, 44, 5);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (836, true, false, false, false, 1, 44, 4);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (853, true, false, false, false, 1, 44, 16);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (838, true, false, true, false, 1, 44, 21);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (840, true, false, true, false, 1, 44, 20);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (851, true, false, false, false, 1, 44, 14);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (847, false, false, true, false, 1, 44, 10);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (841, true, false, false, false, 1, 44, 22);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (856, false, false, true, false, 1, 44, 3);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (849, false, false, true, false, 1, 44, 13);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (857, false, false, true, false, 2, 44, 18);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (854, true, false, false, false, 1, 44, 17);
INSERT INTO droit (id, droit_create, droit_delete, droit_read, droit_update, version, profil_droit, type_droit) VALUES (855, true, false, false, false, 1, 44, 26);


--
-- TOC entry 3898 (class 0 OID 549318)
-- Dependencies: 222
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
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (12, 'COMMUNE_HYDRANT', '<title>Hydrants SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Veuillez trouver ci-joint en piece la liste des hydrants de votre commune.<br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - Hydrants', 1);
INSERT INTO email_modele (id, code, corps, objet, version) VALUES (13, 'CREATION_RCI', '<title>Création d''un départ de feu SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Le nouveau départ de feu portant le code "[CODE]" vient d''être renseigné dans <a href="[URL_SITE]">REMOcRA</a> par l''utilisateur [IDENTIFIANT] de l''organisme [NOM_ORGANISME].<br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>', 'SDIS83 REMOCRA - RCCI', 1);


--
-- TOC entry 3895 (class 0 OID 549167)
-- Dependencies: 189
-- Data for Name: type_organisme; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_organisme (id, code, nom, actif) VALUES (1, 'REMOCRA', 'Application Remocra', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (2, 'CONSEIL-GENERAL', 'Conseil général', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (3, 'DDTM', 'DDTM', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (4, 'COMMUNE', 'Commune', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (5, 'CT', 'Communauté territoriale', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (6, 'CIS', 'CIS', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (7, 'SDIS', 'SDIS', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (8, 'GEST-HYDRANT', 'Gestionnaire hydrant', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (9, 'MILITAIRE', 'Militaire', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (10, 'ENSOSP', 'ENSOSP', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (11, 'MODFCI', 'Maitre ouvrage DFCI', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (12, 'ONF', 'ONF', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (13, 'GENDARMERIE', 'Gendarmerie', true);
INSERT INTO type_organisme (id, code, nom, actif) VALUES (14, 'POLICE', 'Police', true);


--
-- TOC entry 3900 (class 0 OID 549409)
-- Dependencies: 244 3895
-- Data for Name: profil_organisme; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (1, 'Conseil général', 2, true, 'CONSEIL-GENERAL');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (2, 'DDTM', 3, true, 'DDTM');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (3, 'Commune étape 1', 4, true, 'COM-ETAPE-1');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (4, 'Commune étape 2', 4, true, 'COM-ETAPE-2');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (5, 'Communauté territoriale étape 1', 5, true, 'CT-ETAPE-1');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (6, 'Communauté territoriale étape 2', 5, true, 'CT-ETAPE-2');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (12, 'CIS étape 1', 6, true, 'CIS-ETAPE-1');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (13, 'CIS étape 2', 6, true, 'CIS-ETAPE-2');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (14, 'SDIS', 7, true, 'SDIS');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (15, 'Gestionnaire hydrant', 8, true, 'GEST-HYDRANT');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (16, 'Militaire', 9, true, 'MILITAIRE');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (17, 'ENSOSP', 10, true, 'ENSOSP');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (18, 'Maître ouvrage DFCI', 11, true, 'MODFCI');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (19, 'ONF', 12, false, 'ONF');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (20, 'Gendarmerie', 13, true, 'GENDARMERIE');
INSERT INTO profil_organisme (id, nom, type_organisme, actif, code) VALUES (21, 'Police', 14, true, 'POLICE');


--
-- TOC entry 3902 (class 0 OID 549426)
-- Dependencies: 248 3895
-- Data for Name: profil_utilisateur; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (11, 'CT Hydrant', 5, true, 'CT-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (1, 'CG Base', 2, true, 'CG-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (2, 'DDTM DFCI', 3, true, 'DDTM-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (3, 'DDTM Permis', 3, true, 'DDTM-PERMIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (4, 'Maire', 4, true, 'COM-MAIRE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (5, 'Commune DFCI', 4, true, 'COM-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (6, 'Commune Hydrant', 4, true, 'COM-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (7, 'CT DFCI', 5, true, 'CT-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (8, 'CT Permis', 5, true, 'CT-PERMIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (12, 'CIS Base', 6, true, 'CIS-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (13, 'CIS Hydrant', 6, true, 'CIS-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (14, 'Chef de centre', 6, true, 'CIS-CHEF');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (15, 'SDIS Base', 7, true, 'SDIS-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (16, 'Référent DECI', 7, true, 'SDIS-REF-DECI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (17, 'Référent PENA', 7, true, 'SDIS-REF-PENA');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (18, 'Référent DFCI', 7, true, 'SDIS-REF-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (19, 'Administrateur permis', 7, true, 'SDIS-ADM-PERMIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (20, 'Administrateur application', 7, true, 'SDIS-ADM-APP');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (21, 'SDIS Hydrant étape 1', 7, true, 'SDIS-HYDRANT-E1');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (22, 'Préventionniste', 7, true, 'SDIS-PREV');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (23, 'CODIS', 7, true, 'SDIS-CODIS');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (25, 'SDIS Hydrant étape 2', 7, true, 'SDIS-HYDRANT-E2');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (26, 'Gestionnaire hydrant', 8, true, 'GEST-HYDRANT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (28, 'Chef de salle', 7, true, 'SDIS-CHEF');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (29, 'MILITAIRE Base', 9, true, 'MILITAIRE-BASE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (30, 'ENSOSP Consultation', 10, true, 'ENSOSP-CONSULT');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (31, 'ONF DFCI', 12, true, 'ONF-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (32, 'CG DFCI', 2, true, 'CG-DFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (33, 'Maître ouvrage DFCI', 11, true, 'MODDFCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (34, 'Gendarme', 13, true, 'GENDARME');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (35, 'Policier', 14, true, 'POLICIER');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (36, 'RCCI', 7, true, 'RCCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (37, 'RCCI préventionniste', 7, true, 'RCCI-PREVENTION');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (38, 'RCCI chef de centre', 6, true, 'RCCI-CHEFCENTRE');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (39, 'ONF RCCI', 12, true, 'ONF-RCCI');
INSERT INTO profil_utilisateur (id, nom, type_organisme, actif, code) VALUES (40, 'Directeur', 7, true, 'DIRECTEUR');


--
-- TOC entry 3901 (class 0 OID 549419)
-- Dependencies: 246 3902 3899 3900
-- Data for Name: profil_organisme_utilisateur_droit; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (139, 1, 7, 1, 1);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (140, 1, 8, 2, 2);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (141, 1, 10, 2, 3);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (142, 1, 11, 3, 4);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (143, 1, 11, 4, 4);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (144, 1, 14, 3, 5);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (145, 1, 14, 4, 5);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (146, 1, 15, 3, 6);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (147, 1, 16, 4, 6);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (148, 1, 17, 5, 7);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (149, 1, 17, 6, 7);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (150, 1, 18, 5, 8);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (151, 1, 18, 6, 8);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (152, 1, 19, 5, 11);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (153, 1, 20, 6, 11);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (154, 1, 21, 12, 12);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (155, 1, 21, 13, 12);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (156, 1, 22, 12, 13);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (157, 1, 23, 13, 13);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (158, 1, 24, 12, 14);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (159, 1, 24, 13, 14);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (160, 1, 25, 14, 15);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (161, 1, 26, 14, 16);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (162, 1, 27, 14, 17);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (163, 1, 28, 14, 18);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (164, 1, 29, 14, 19);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (165, 1, 30, 14, 20);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (166, 1, 31, 14, 21);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (167, 1, 33, 14, 22);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (168, 1, 34, 14, 23);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (169, 1, 32, 14, 25);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (170, 1, 35, 15, 26);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (1, 0, 24, 14, 14);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (2, 0, 24, 14, 28);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (171, 1, 36, 16, 29);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (172, 1, 36, 17, 30);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (175, 0, 28, 2, 18);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (173, 1, 17, 18, 33);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (176, 1, 39, 20, 34);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (177, 1, 40, 21, 35);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (179, 0, 41, 14, 36);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (182, 0, 43, 14, 37);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (183, 0, 42, 12, 38);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (174, 1, 17, 19, 31);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (184, 0, 8, 19, 39);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (185, 0, 38, 1, 32);
INSERT INTO profil_organisme_utilisateur_droit (id, version, profil_droit, profil_organisme, profil_utilisateur) VALUES (186, 0, 44, 14, 40);


--
-- TOC entry 3907 (class 0 OID 549492)
-- Dependencies: 264
-- Data for Name: type_alerte_elt; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (1, true, 'ROUTE', 'Etablissement');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (2, true, 'RESIDENCE', 'Résidence');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (3, true, 'RESEAUROUTIER', 'Réseau routier');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (4, true, 'AUTRE', 'Autre');
INSERT INTO type_alerte_elt (id, actif, code, nom) VALUES (5, true, 'DFCI', 'DFCI');


--
-- TOC entry 3903 (class 0 OID 549447)
-- Dependencies: 253 3907
-- Data for Name: sous_type_alerte_elt; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (1, true, 'ERPICPE', 'ERP/ICPE', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (2, true, 'SERVICEPUBLIC', 'Service public', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (3, true, 'CAMPING', 'Camping', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (4, true, 'SPORTPLEINAIR', 'Sport plein air', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (5, true, 'ENTREPRISE', 'Entreprise', 0, 1);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (6, true, 'RESIDENCE', 'Résidence', 0, 2);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (7, true, 'PERIMETRE', 'Périmètre', 2, 2);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (9, true, 'PLACE', 'Place', 2, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (10, true, 'PARKING', 'Parking', 2, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (11, true, 'STATIONSERVICE', 'Station service', 0, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (12, true, 'POINTREMARQUABLE', 'Point remarquable', 0, 4);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (13, true, 'SQUAREPARC', 'Square/Parc', 2, 4);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (14, true, 'HYDRANT', 'Hydrant', 0, 4);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (15, true, 'DEBROUSSAILLEMENT', 'Débroussaillement', 2, 5);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (16, true, 'PISTE', 'Pistes', 1, 5);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (8, true, 'ROUTEPISTE', 'Route', 1, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (17, true, 'DIFPASSAGE', 'Difficulté de passage', 0, 5);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (18, true, 'NUMVOIE', 'Numéro de voirie', 0, 3);
INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (19, true, 'ETSSURF', 'Emprise établissements', 2, 1);


--
-- TOC entry 3904 (class 0 OID 549456)
-- Dependencies: 255
-- Data for Name: suivi_patches; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO suivi_patches (numero, description, application) VALUES (46, 'Suivi des patches', '2014-07-04 14:31:50.869356');
INSERT INTO suivi_patches (numero, description, application) VALUES (47, 'Export des départs : export csv dans le traitement', '2014-07-24 12:01:58.345354');
INSERT INTO suivi_patches (numero, description, application) VALUES (48, 'Association de PI aux citernes : tolérance', '2014-09-03 16:23:48.655872');
INSERT INTO suivi_patches (numero, description, application) VALUES (49, 'Calcul des coordonnées DFCI des PENA sans coordonnées', '2014-09-24 21:10:49.338915');
INSERT INTO suivi_patches (numero, description, application) VALUES (50, 'URL de base du serveur WMS', '2014-12-15 17:14:05.9596');
INSERT INTO suivi_patches (numero, description, application) VALUES (51, 'Reprise trigger remocra.calcul_indispo (ambiguité sur id)', '2015-01-21 15:49:29.901107');


--
-- TOC entry 3905 (class 0 OID 549468)
-- Dependencies: 258
-- Data for Name: thematique; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO thematique (id, nom, version, actif, code) VALUES (0, 'Divers', NULL, true, 'DIVERS');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (1, 'Point d''eau', NULL, true, 'POINTDEAU');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (2, 'Adresses', NULL, true, 'ADRESSES');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (3, 'Permis', NULL, true, 'PERMIS');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (4, 'DFCI', NULL, true, 'DFCI');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (5, 'Risques', NULL, true, 'RISQUES');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (6, 'Cartothèque', 1, true, 'CARTOTHEQUE');
INSERT INTO thematique (id, nom, version, actif, code) VALUES (7, 'Recherche des Causes Incendie', NULL, true, 'RCI');


--
-- TOC entry 3906 (class 0 OID 549483)
-- Dependencies: 262
-- Data for Name: type_alerte_ano; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (1, true, 'ABSENT', 'Manquant');
INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (4, true, 'INEXISTANT', 'A supprimer');
INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (5, true, 'AUTRE', 'Autre');
INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (6, true, 'TRAVAUX DFCI', 'Travaux DFCI');
INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (7, true, 'TRAVAUX DFCI', 'Travaux DFCI');
INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (2, true, 'POSITION', 'Erreur de localisation');
INSERT INTO type_alerte_ano (id, actif, code, nom) VALUES (3, true, 'NOM', 'Erreur de dénomination');


--
-- TOC entry 3909 (class 0 OID 549510)
-- Dependencies: 268
-- Data for Name: type_hydrant; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant (id, actif, code, nom, version) VALUES (1, true, 'PIBI', 'PIBI', 1);
INSERT INTO type_hydrant (id, actif, code, nom, version) VALUES (2, true, 'PENA', 'PENA', 1);


--
-- TOC entry 3913 (class 0 OID 549536)
-- Dependencies: 274
-- Data for Name: type_hydrant_critere; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (1, true, 'ABORD', 'Abords', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (2, true, 'ACCESS', 'Accessibilité', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (3, true, 'ASPECT', 'Aspect', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (4, true, 'HYDRAU', 'Capacité hydraulique', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (5, true, 'DIVER', 'Divers', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (6, true, 'ETANCH', 'Etanchéité', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (7, true, 'OEUVRE', 'Mise en œuvre', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (8, true, 'SECU', 'Sécurité', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (9, true, 'SIGN', 'Signalisation', 1);


--
-- TOC entry 3910 (class 0 OID 549518)
-- Dependencies: 269 3913
-- Data for Name: type_hydrant_anomalie; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (4, true, 'ANUMROTER', '', 'A numéroter', 3, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (94, false, 'AIREDEMANUVRENONUTILISABLE', '', 'Aire de manœuvre non utilisable', 0, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (6, true, 'APROTGER', '', 'A protéger', 2, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (95, false, 'ANOMALIEHAUTEUROULONGUEURASPIRATION', '', 'Anomalie hauteur ou longueur aspiration', 0, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (102, false, 'ECHELLEEXTRIEUREHORSDUSAGE', '', 'Echelle extérieure hors d’usage', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (103, false, 'PASSERELLEHORSSERVICERENDANTLUSAGEIMPOSSIBLE', '', 'Passerelle hors service (rendant l’usage impossible)', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (104, false, 'TRAPPEHBEOUVERTUREHORSSERVICEINTERDISANTLEPOMPAGE', '', 'Trappe HBE Ouverture hors service (interdisant le pompage) ', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (105, false, 'VOLUMETHORIQUEIDENTIFIFLANCOUPANNEAU', '', 'Volume théorique identifié (flanc ou panneau)', 0, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (2, true, 'ADBROUSSAILLER', NULL, 'A débroussailler', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (3, true, 'ADPLACER', NULL, 'A déplacer', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (5, true, 'APEINDRE', NULL, 'A peindre', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (7, true, 'ATOURNER', NULL, 'A tourner', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (8, true, 'ABORDSDGAGER', NULL, 'Abords à dégager', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (11, true, 'AIREDEPOMPAGE30MNONCONFORME', NULL, 'Aire de pompage Ø 30 m non conforme', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (13, true, 'AIREDESCURIT30MNONCONFORMEINCLUTLAIREDEPOSER', NULL, 'Aire de sécurité Ø 30 m non conforme (inclut l’aire de poser)', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (14, true, 'ANCRAGE', NULL, 'Ancrage', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (15, true, 'APPROCHES50MNONCONFORMES', NULL, 'Approche(s) > 50 m non conforme(s)', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (20, true, 'CAPOTCASSEMANQUANT', NULL, 'Capot casse - manquant', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (16, true, 'BANDEAUJAUNEABSENTAU13SUPRIEUR', NULL, 'Bandeau jaune absent au 1/3 supérieur', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (21, true, 'CARREDEMANUVREHORSSERVICE', NULL, 'Carre de manœuvre hors service', 1, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (23, true, 'CORROSION', NULL, 'Corrosion', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (24, true, 'COULEURNONNORMALISEAREPEINDRE', NULL, 'Couleur non normalisée (A repeindre)', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (27, true, 'DBROUSSAILLEMENTETOUCLAIRCIEFAIRECOURONNEDE25MAUTOURDELAIREDEMANUVRE', NULL, 'Débroussaillement et/ou éclaircie à faire (couronne de 25 m autour de l’aire de manœuvre)', 1, 8);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (28, true, 'DISTANCE5M', NULL, 'Distance > 5m', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (31, true, 'ENTRAVAUX', NULL, 'En travaux', 1, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (32, true, 'FERMETUREHORSSERVICEFUITE', NULL, 'Fermeture hors service (Fuite)', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (33, true, 'FUITE', NULL, 'Fuite', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (34, true, 'FUITERACCORD', NULL, 'Fuite ½ raccord', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (35, true, 'FUITELABASE', NULL, 'Fuite à la base', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (36, true, 'FUITEAUPURGEUR', NULL, 'Fuite au purgeur', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (37, true, 'FUITEAUVOLANT', NULL, 'Fuite au volant', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (38, true, 'GRIPP', NULL, 'Grippé', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (39, true, 'HAUTEURGOMTRIQUE', NULL, 'Hauteur géométrique', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (41, true, 'HYDRANTDTRUITCASS', NULL, 'Hydrant détruit – Cassé', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (42, true, 'HYDRANTENTERR', NULL, 'Hydrant enterré', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (43, true, 'IMMATRICULATIONAUDESSUSABSENTEOUERRONE', NULL, 'Immatriculation au-dessus absente ou erronée', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (44, true, 'IMMATRICULATIONSURFLANCOUPANNEAUABSENTEOUERRONEPOURTOUTTYPE', NULL, 'Immatriculation sur (flanc ou panneau) absente ou erronée (pour tout type)', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (48, true, 'INACCESSIBLEAUXENGINS', NULL, 'Inaccessible aux engins', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (50, true, 'INTROUVABLE', NULL, 'Introuvable', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (51, true, 'ISOLATIONBOUCHECLEFHORSSERVICE', NULL, 'Isolation (Bouche à Clef) Hors service', 1, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (52, true, 'ISOLATIONBOUCHECLEFINTROUVABLE', NULL, 'Isolation (Bouche à Clef) Introuvable', 1, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (55, true, 'MANQUEBOUCHON100', NULL, 'Manque bouchon 100', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (56, true, 'MANQUEBOUCHON70', NULL, 'Manque bouchon 70', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (57, true, 'MANQUEDEUXBOUCHONS', NULL, 'Manque deux bouchons', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (58, true, 'MANQUEPLAQUESIGNALTIQUEBIUNIQUEMENT', NULL, 'Manque plaque signalétique (BI uniquement)', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (59, true, 'OBSTACLELOUVERTURE', NULL, 'Obstacle à l’ouverture', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (62, true, 'PISOUSCOFFRE', NULL, 'Pi sous coffre', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (63, true, 'PISOUSSCELL', NULL, 'Pi sous scellé', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (65, true, 'PROCHEDUNEINSTALLATIONLECTRIQUE', NULL, 'Proche d’une installation électrique', 1, 8);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (68, true, 'SANSEAU', NULL, 'Sans eau', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (69, true, 'SOCLEDEPROPRET', NULL, 'Socle de propreté', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (70, true, 'STABILITVERTICALIT', NULL, 'Stabilité / verticalité', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (76, true, 'VIDANGECOLONNE', NULL, 'Vidange colonne', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (77, true, 'VOLANTDEMANUVREABSENTCASSCARRNONNORMALIS', NULL, 'Volant de manœuvre absent, cassé Carré non normalisé', 1, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (79, true, 'VOLUMEDEDGAGEMENTINSUFFISANT50CMAUTOURHYDRANT', NULL, 'Volume de dégagement insuffisant (50 cm autour hydrant)', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (47, true, 'INACCESSIBLECHEMINEMENTIMPRATICABLE', '', 'Inaccessible – Cheminement impraticable', 2, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (12, true, 'AIREDEPOSERHBELISSE10MNONCONFORME', '', 'Aire de poser HBE lisse Ø 10 m non conforme', 2, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (22, true, 'PEINTUREVERTEREFAIRE', '', 'Peinture verte (refaire)', 2, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (17, true, 'BOUCHONOBTURATEUR100MMLORIFICEDASPIRATIONABSENT', '', 'Bouchon obturateur Ø100 mm à l’orifice d’aspiration absent', 2, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (97, false, 'TROUDHOMMEOUPUISARDDASPIRATIONHORSSERVICE', '', 'Trou d’homme ou puisard d’aspiration hors service', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (98, false, 'VANNEABSENTECASSEOUINOPRANTE', '', 'Vanne absente, cassée ou inopérante', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (99, false, 'TENONSDURACCORDDASPIRATIONMALORIENTSOUCASSS', '', 'Tenons du ½ raccord d’aspiration mal orientés ou cassés', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (100, false, 'VANNEANTISIPHONINOPRANTEOUABSENTE', '', 'Vanne anti siphon inopérante ou absente', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (101, false, 'ECHECLASPIRATION', '', 'Echec à l’aspiration', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (18, true, 'BOUCHONOBTURATEUR65OU40MMREMPLISSAGEABSENT', '', 'Bouchon obturateur Ø65 ou 40 mm (remplissage) absent', 2, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (80, true, 'DEBIT_INSUFF', NULL, 'Débit insuffisant', 1, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (81, true, 'DEBIT_TROP_ELEVE', NULL, 'Débit trop élevé', 1, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (82, true, 'DEBIT_INSUFF_NC', '', 'Débit insuffisant (non conforme à la NFS 62 200)', 3, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (106, true, 'PRESSION_DYN_TROP_ELEVEE', NULL, 'Pression dynamique trop élevée', 1, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (107, true, 'PRESSION_DYN_INSUFF', '', 'Pression dynamique insuffisante', 3, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (83, true, 'PRESSION_INSUFF', '', 'Pression statique insuffisante', 4, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (84, true, 'PRESSION_TROP_ELEVEE', '', 'Pression statique trop élevée', 2, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (96, false, 'RACCORDCASSOUNONCONFORME', '', 'Raccord non conforme', 1, 7);


--
-- TOC entry 3920 (class 0 OID 549601)
-- Dependencies: 288 3909
-- Data for Name: type_hydrant_nature; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (1, true, 'PI', 'PI', 1, 1);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (2, true, 'BI', 'BI', 1, 1);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (3, true, 'CI_FIXE', 'Citerne fixe', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (4, true, 'CI_ENTERRE', 'Citerne enterrée', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (5, true, 'CE', 'Cours d''eau', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (6, true, 'PE', 'Plan d''eau', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (7, true, 'PU', 'Puisard', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (8, true, 'RE', 'Retenue', 1, 2);


--
-- TOC entry 3911 (class 0 OID 549528)
-- Dependencies: 271 3910 3920
-- Data for Name: type_hydrant_anomalie_nature; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (30, 1, NULL, 0, 2, 22, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (31, 1, NULL, 0, 2, 23, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (11, 1, NULL, 4, 2, 33, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (27, 1, NULL, 0, 2, 17, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (28, 1, NULL, 0, 2, 18, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (29, 1, NULL, 1, 1, 44, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (111, NULL, NULL, 1, 0, 55, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (112, NULL, NULL, 1, 0, 56, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (52, NULL, NULL, 2, 2, 8, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (55, NULL, NULL, 2, 0, 8, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (56, NULL, NULL, 1, 0, 14, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (57, NULL, NULL, 1, 0, 14, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (58, NULL, NULL, 0, 0, 59, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (59, NULL, NULL, 0, 0, 59, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (60, NULL, NULL, 0, 0, 62, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (61, NULL, NULL, 0, 0, 63, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (64, NULL, NULL, 0, 0, 69, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (65, NULL, NULL, 3, 0, 70, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (66, NULL, NULL, 0, 0, 79, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (67, NULL, NULL, 0, 0, 79, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (68, NULL, NULL, 2, 0, 2, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (69, NULL, NULL, 2, 0, 2, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (70, NULL, NULL, 0, 0, 3, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (71, NULL, NULL, 0, 0, 3, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (72, NULL, NULL, 0, 0, 7, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (4, 1, NULL, 5, 3, 47, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (73, NULL, NULL, 0, 0, 28, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (74, NULL, NULL, 0, 0, 28, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (75, NULL, NULL, 3, 0, 42, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (79, NULL, NULL, 5, 1, 48, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (80, NULL, NULL, 5, 0, 48, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (81, NULL, NULL, 5, 0, 50, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (82, NULL, NULL, 5, 0, 50, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (83, NULL, NULL, 0, 0, 5, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (84, NULL, NULL, 0, 0, 6, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (85, NULL, NULL, 0, 0, 6, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (86, NULL, NULL, 0, 0, 20, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (87, NULL, NULL, 0, 0, 24, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (88, NULL, NULL, 5, 0, 41, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (89, NULL, NULL, 2, 0, 38, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (90, NULL, NULL, 5, 0, 68, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (91, NULL, NULL, 5, 0, 68, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (94, NULL, NULL, 5, 0, 31, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (95, NULL, NULL, 5, 0, 31, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (96, NULL, NULL, 5, 0, 21, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (97, NULL, NULL, 5, 0, 21, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (99, NULL, NULL, 0, 0, 51, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (100, NULL, NULL, 0, 0, 51, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (101, NULL, NULL, 0, 0, 51, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (102, NULL, NULL, 0, 0, 52, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (103, NULL, NULL, 0, 0, 52, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (104, NULL, NULL, 1, 0, 37, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (105, NULL, NULL, 1, 0, 36, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (106, NULL, NULL, 5, 0, 35, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (107, NULL, NULL, 5, 0, 32, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (108, NULL, NULL, 0, 0, 34, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (109, NULL, NULL, 1, 0, 76, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (110, NULL, NULL, 1, 0, 76, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (113, NULL, NULL, 2, 0, 57, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (114, NULL, NULL, 5, 0, 65, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (115, NULL, NULL, 5, 0, 65, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (116, NULL, NULL, 4, 0, 4, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (117, NULL, NULL, 5, 0, 58, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (118, NULL, NULL, 5, 0, 80, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (119, NULL, NULL, 5, 0, 80, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (121, NULL, NULL, 4, 1, 82, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (123, NULL, NULL, 5, 0, 83, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (124, NULL, NULL, 5, 0, 83, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (125, NULL, NULL, 5, 0, 84, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (126, NULL, NULL, 5, 0, 84, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (127, NULL, NULL, 4, 1, 82, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (76, NULL, NULL, 5, 1, 47, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (77, NULL, NULL, 5, 1, 47, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (78, NULL, NULL, 5, 1, 47, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (2, 1, NULL, 5, 23, 47, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (134, NULL, NULL, 5, 0, 107, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (135, NULL, NULL, 5, 0, 107, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (136, NULL, NULL, 5, 0, 106, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (137, NULL, NULL, 5, 0, 106, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (138, NULL, NULL, 2, 0, 96, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (3, 1, NULL, 5, 9, 47, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (139, NULL, NULL, 5, 0, 94, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (141, NULL, NULL, 5, 0, 94, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (142, NULL, NULL, 5, 0, 94, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (143, NULL, NULL, 5, 0, 94, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (144, NULL, NULL, 5, 0, 94, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (10, 1, NULL, 3, 2, 27, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (9, 1, NULL, 3, 2, 27, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (8, 1, NULL, 3, 2, 27, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (145, NULL, NULL, 3, 0, 27, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (146, NULL, NULL, 3, 0, 27, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (147, NULL, NULL, 3, 0, 27, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (148, NULL, NULL, 4, 0, 33, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (149, NULL, NULL, 5, 0, 97, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (150, NULL, NULL, 5, 0, 97, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (151, NULL, NULL, 5, 0, 97, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (152, NULL, NULL, 5, 0, 97, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (153, NULL, NULL, 5, 0, 97, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (154, NULL, NULL, 5, 0, 97, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (155, NULL, NULL, 5, 0, 98, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (156, NULL, NULL, 5, 0, 98, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (157, NULL, NULL, 5, 0, 98, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (158, NULL, NULL, 5, 0, 98, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (159, NULL, NULL, 5, 0, 98, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (160, NULL, NULL, 5, 0, 98, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (161, NULL, NULL, 5, 0, 99, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (162, NULL, NULL, 5, 0, 99, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (163, NULL, NULL, 5, 0, 99, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (164, NULL, NULL, 5, 0, 99, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (165, NULL, NULL, 5, 0, 99, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (166, NULL, NULL, 5, 0, 99, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (167, NULL, NULL, 5, 0, 100, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (168, NULL, NULL, 5, 0, 100, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (169, NULL, NULL, 5, 0, 95, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (171, NULL, NULL, 5, 0, 95, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (172, NULL, NULL, 5, 0, 95, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (173, NULL, NULL, 5, 0, 95, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (174, NULL, NULL, 5, 0, 101, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (175, NULL, NULL, 5, 0, 101, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (176, NULL, NULL, 5, 0, 101, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (177, NULL, NULL, 5, 0, 101, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (178, NULL, NULL, 5, 0, 101, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (179, NULL, NULL, 5, 0, 101, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (45, NULL, 0, NULL, 2, 43, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (48, NULL, 5, NULL, 3, 15, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (49, NULL, 5, NULL, 3, 11, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (51, NULL, 5, NULL, 4, 12, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (50, NULL, 5, NULL, 4, 13, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (140, NULL, NULL, 5, 1, 94, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (98, NULL, NULL, 5, 1, 77, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (180, NULL, NULL, 4, 0, 102, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (181, NULL, NULL, 4, 0, 102, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (120, NULL, NULL, 0, 1, 81, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (182, NULL, NULL, 0, 0, 17, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (183, NULL, NULL, 0, 0, 18, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (184, NULL, NULL, 1, 0, 44, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (185, NULL, NULL, 0, 0, 22, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (186, NULL, NULL, 1, 0, 105, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (187, NULL, NULL, 1, 0, 105, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (188, NULL, NULL, 0, 0, 23, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (44, NULL, 0, NULL, 2, 16, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (189, NULL, 0, NULL, 0, 16, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (190, NULL, 0, NULL, 0, 43, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (191, NULL, 5, NULL, 0, 103, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (192, NULL, 5, NULL, 0, 103, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (193, NULL, 5, NULL, 0, 104, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (194, NULL, 5, NULL, 0, 15, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (195, NULL, 5, NULL, 0, 11, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (196, NULL, 5, NULL, 0, 12, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (197, NULL, 5, NULL, 0, 13, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (198, NULL, 5, NULL, 0, 104, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (122, NULL, NULL, 0, 1, 81, 1);


--
-- TOC entry 3922 (class 0 OID 549620)
-- Dependencies: 292
-- Data for Name: type_hydrant_saisie; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (1, true, 'LECT', 'Lecture', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (2, true, 'RECEP', 'Réception', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (3, true, 'CREA', 'Création', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (4, true, 'RECO', 'Reconnaisance', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (5, true, 'CTRL', 'Contrôle', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (6, true, 'VERIF', 'Vérification', 1);


--
-- TOC entry 3912 (class 0 OID 549533)
-- Dependencies: 273 3911 3922
-- Data for Name: type_hydrant_anomalie_nature_saisies; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (76, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (76, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (76, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (77, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (77, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (77, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (78, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (78, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (78, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (52, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (52, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (52, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (55, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (55, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (55, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (89, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (89, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (90, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (90, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (91, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (91, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (94, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (94, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (94, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (95, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (95, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (95, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (96, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (96, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (96, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (97, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (97, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (97, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (99, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (99, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (100, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (100, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (101, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (101, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (102, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (102, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (103, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (103, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (104, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (104, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (105, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (105, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (106, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (106, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (107, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (107, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (108, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (108, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (109, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (109, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (110, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (110, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (111, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (111, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (111, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (112, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (112, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (112, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (113, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (113, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (113, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (116, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (116, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (116, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (117, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (117, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (117, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (138, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (138, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (138, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (139, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (139, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (139, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (141, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (141, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (141, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (142, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (142, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (142, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (143, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (143, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (143, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (144, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (144, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (144, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (145, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (145, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (145, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (146, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (146, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (146, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (147, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (147, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (147, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (148, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (148, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (148, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (149, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (149, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (149, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (150, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (150, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (150, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (151, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (151, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (151, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (152, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (152, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (152, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (153, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (153, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (153, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (154, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (154, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (154, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (155, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (155, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (155, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (156, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (156, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (156, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (157, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (157, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (157, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (158, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (158, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (158, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (159, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (159, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (159, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (160, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (160, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (160, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (161, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (161, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (161, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (162, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (162, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (162, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (163, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (163, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (163, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (164, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (164, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (164, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (165, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (165, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (165, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (166, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (166, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (166, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (167, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (167, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (167, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (168, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (168, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (168, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (169, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (169, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (169, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (171, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (171, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (171, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (172, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (172, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (172, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (173, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (173, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (173, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (174, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (174, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (175, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (175, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (176, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (176, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (177, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (177, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (178, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (178, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (179, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (179, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (180, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (180, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (180, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (181, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (181, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (181, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (182, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (182, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (182, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (183, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (183, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (183, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (184, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (184, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (184, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (185, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (185, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (185, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (186, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (186, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (186, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (187, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (187, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (187, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (188, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (188, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (188, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (189, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (189, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (189, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (190, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (190, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (190, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (191, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (191, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (191, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (192, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (192, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (192, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (193, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (193, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (193, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (194, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (194, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (194, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (195, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (195, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (195, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (51, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (51, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (51, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (196, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (196, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (196, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (50, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (50, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (50, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (197, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (197, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (197, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (198, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (198, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (198, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (140, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (140, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (140, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (98, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (98, 6);


--
-- TOC entry 3914 (class 0 OID 549546)
-- Dependencies: 276
-- Data for Name: type_hydrant_diametre; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_diametre (id, actif, code, nom, version) VALUES (1, true, 'DIAM80', '80', 1);
INSERT INTO type_hydrant_diametre (id, actif, code, nom, version) VALUES (2, true, 'DIAM100', '100', 1);
INSERT INTO type_hydrant_diametre (id, actif, code, nom, version) VALUES (3, true, 'DIAM150', '150', 1);


--
-- TOC entry 3915 (class 0 OID 549556)
-- Dependencies: 278 3914 3920
-- Data for Name: type_hydrant_diametre_natures; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (1, 1);
INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (2, 1);
INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (3, 1);
INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (2, 2);


--
-- TOC entry 3916 (class 0 OID 549559)
-- Dependencies: 279
-- Data for Name: type_hydrant_domaine; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (1, true, 'DEPARTEMENT', 'Départemental', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (2, true, 'DOMAINE', 'Domanial', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (3, true, 'COMMUNAL', 'Communal', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (4, true, 'MILITAIRE', 'Militaire', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (5, true, 'PRIVE', 'Privé', 1);


--
-- TOC entry 3917 (class 0 OID 549571)
-- Dependencies: 282
-- Data for Name: type_hydrant_marque; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (2, true, 'BAYARD', 'BAYARD', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (3, true, 'PAM', 'PAM', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (4, true, 'AVK', 'AVK', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (5, true, 'Inconnu', 'Inconnu', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (1, true, 'PEYTAVIN', 'PEYTAVIN', 1);


--
-- TOC entry 3918 (class 0 OID 549581)
-- Dependencies: 284
-- Data for Name: type_hydrant_materiau; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (1, true, 'METAL', 'Métal', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (2, true, 'METAL CALO', 'Métal calorifugé', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (3, true, 'BETON', 'Béton', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (4, true, 'METAL MEMBRANE', 'Métal avec membrane interne', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (5, true, 'AUTRE', 'Autre', 1);


--
-- TOC entry 3919 (class 0 OID 549591)
-- Dependencies: 286 3917
-- Data for Name: type_hydrant_modele; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (2, true, 'RACCORDKEYSERNONCONFORME', 'RACCORD KEYSER NON CONFORME', 1, 1);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (3, true, 'NONINCONGELABLE', 'NON INCONGELABLE', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (4, true, 'INCONGELABLE', 'INCONGELABLE', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (5, true, 'EMERAUDE', 'EMERAUDE', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (6, true, 'EMERAUDEPARSEC', 'EMERAUDE PARSEC', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (7, true, 'SAPHIR', 'SAPHIR', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (8, true, 'SAPHIRPARSEC', 'SAPHIR PARSEC', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (9, true, 'RETRO', 'RETRO', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (10, true, 'DAUPHIN', 'DAUPHIN', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (11, true, 'PAM', 'PAM', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (12, true, 'ELANCIO', 'ELANCIO', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (13, true, 'ATLAS', 'ATLAS', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (16, true, 'ATLASPLUS', 'ATLAS PLUS', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (17, true, 'HERMES', 'HERMES', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (18, true, 'HERMESPLUS', 'HERMES PLUS', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (19, true, 'RATIONNEL', 'RATIONNEL', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (20, true, 'AJAX', 'AJAX', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (21, true, 'C9', 'C9', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (22, true, 'PEGASE', 'PEGASE', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (23, true, 'VEGA', 'VEGA', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (24, true, 'PHENIX', 'PHENIX', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (25, true, 'ORION', 'ORION', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (26, true, 'ORION2', 'ORION 2', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (27, true, 'DAUPHINE', 'DAUPHINE', 1, 4);


--
-- TOC entry 3921 (class 0 OID 549610)
-- Dependencies: 290
-- Data for Name: type_hydrant_positionnement; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_positionnement (id, actif, code, nom, version) VALUES (1, true, 'POSEE', 'Posée', 1);
INSERT INTO type_hydrant_positionnement (id, actif, code, nom, version) VALUES (2, true, 'SEMI', 'Semi-enterrée', 1);


--
-- TOC entry 3923 (class 0 OID 549629)
-- Dependencies: 294
-- Data for Name: type_hydrant_vol_constate; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (1, true, 'VOL4', '4/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (2, true, 'VOL3', '3/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (3, true, 'VOL2', '2/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (4, true, 'VOL1', '1/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (5, true, 'VOL0', '0', 1);


--
-- TOC entry 3896 (class 0 OID 549180)
-- Dependencies: 191
-- Data for Name: type_permis_avis; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (1, true, 'FAVORABLE', 'Favorable', true);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (2, true, 'DEFAVORABLE', 'Défavorable', true);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (3, true, 'RFF', 'RFF', false);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (4, true, 'SRFF', 'SRFF', false);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (5, true, 'RM', 'RM', false);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (6, true, 'ATTENTE', 'En attente', false);
INSERT INTO type_permis_avis (id, actif, code, nom, pprif) VALUES (7, true, 'NON_CONFORME', 'Non conforme', false);


--
-- TOC entry 3924 (class 0 OID 549641)
-- Dependencies: 297
-- Data for Name: type_permis_interservice; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_permis_interservice (id, actif, code, nom, pprif) VALUES (1, true, 'VALIDE', 'Valide', true);
INSERT INTO type_permis_interservice (id, actif, code, nom, pprif) VALUES (2, true, 'NON VALIDE', 'Non valide', true);
INSERT INTO type_permis_interservice (id, actif, code, nom, pprif) VALUES (3, true, 'NON CONCERNE', 'Non concerné', false);


--
-- TOC entry 3925 (class 0 OID 549651)
-- Dependencies: 299
-- Data for Name: type_rci_degre_certitude; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_rci_degre_certitude (id, actif, code, nom, version) VALUES (1, true, 'CERTAINE', 'Certaine', 1);
INSERT INTO type_rci_degre_certitude (id, actif, code, nom, version) VALUES (2, true, 'SUPPOSE', 'Supposée', 1);
INSERT INTO type_rci_degre_certitude (id, actif, code, nom, version) VALUES (4, true, 'PROBABLE', 'Probable', 1);
INSERT INTO type_rci_degre_certitude (id, actif, code, nom, version) VALUES (5, true, 'INCONNUE', 'Inconnue', 1);


--
-- TOC entry 3926 (class 0 OID 549661)
-- Dependencies: 301
-- Data for Name: type_rci_origine_alerte; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_rci_origine_alerte (id, actif, code, nom, version) VALUES (1, true, 'VIGIE', 'Vigie', 1);
INSERT INTO type_rci_origine_alerte (id, actif, code, nom, version) VALUES (2, true, 'POLICEGENDARMERIE', 'Police-Gendarmerie', 1);
INSERT INTO type_rci_origine_alerte (id, actif, code, nom, version) VALUES (3, true, 'POPULATION', 'Population', 1);
INSERT INTO type_rci_origine_alerte (id, actif, code, nom, version) VALUES (4, true, 'PATROUILLE', 'Patrouille', 1);
INSERT INTO type_rci_origine_alerte (id, actif, code, nom, version) VALUES (5, true, 'MOYENARIEN', 'Moyen aérien', 1);
INSERT INTO type_rci_origine_alerte (id, actif, code, nom, version) VALUES (6, true, 'AUTRE', 'Autre', 1);


--
-- TOC entry 3894 (class 0 OID 549145)
-- Dependencies: 185
-- Data for Name: type_rci_prom_famille; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_rci_prom_famille (id, actif, code, nom, version) VALUES (1, true, 'NATURELLE', 'Naturelle', 1);
INSERT INTO type_rci_prom_famille (id, actif, code, nom, version) VALUES (2, true, 'ACCIDENTELLE', 'Accidentelle liée aux installations', 1);
INSERT INTO type_rci_prom_famille (id, actif, code, nom, version) VALUES (3, true, 'MALVEILLANCE', 'Malveillance origine humaine intentionnelle', 1);
INSERT INTO type_rci_prom_famille (id, actif, code, nom, version) VALUES (4, true, 'INVOLONTAIREPRO', 'Involontaire liée aux travaux professionnels', 1);
INSERT INTO type_rci_prom_famille (id, actif, code, nom, version) VALUES (5, true, 'INVOLONTAIREPART', 'Involontaire liée aux particuliers', 1);


--
-- TOC entry 3928 (class 0 OID 549683)
-- Dependencies: 306 3894
-- Data for Name: type_rci_prom_partition; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (1, true, 'NATFOUDRE', 'Foudre', 1, 1);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (2, true, 'ACCLIGNEELEC', 'Ligne électrique', 1, 2);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (3, true, 'ACCCHEMINFER', 'Chemin de fer', 1, 2);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (4, true, 'ACCVEHICULE', 'Véhicule', 1, 2);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (5, true, 'ACCDEPOTORD', 'Dépôt ordure', 1, 2);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (6, true, 'MALCONFLIT', 'Conflit', 1, 3);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (7, true, 'MALINTERET', 'Intérêt', 1, 3);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (8, true, 'MALPYROMAN', 'Pyromanie', 1, 3);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (9, true, 'TRAVPROFORST', 'Travaux forestiers', 1, 4);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (10, true, 'TRAVPROAGRIC', 'Travaux agricoles', 1, 4);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (11, true, 'TRAVPROINDUS', 'Travaux industriels, publics, artisanaux...', 1, 4);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (12, true, 'TRAVPROREPR', 'Reprise', 1, 4);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (13, true, 'TRAVPARTTRAV', 'Travaux', 1, 5);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (14, true, 'TRAVPARTLOIS', 'Loisirs', 1, 5);
INSERT INTO type_rci_prom_partition (id, actif, code, nom, version, famille) VALUES (15, true, 'TRAVPARTJETOB', 'Jet d''objets incandescents', 1, 5);


--
-- TOC entry 3927 (class 0 OID 549671)
-- Dependencies: 303 3928
-- Data for Name: type_rci_prom_categorie; Type: TABLE DATA; Schema: remocra; Owner: postgres
--

INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (1, true, 'NATFOUDRE', 'Foudre', 1, 1);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (2, true, 'ACCLIGNEELECRUPT', 'Rupture', 1, 2);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (3, true, 'ACCLIGNEELECAMOR', 'Amorçage', 1, 2);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (4, true, 'ACCCHEMINFER', 'Chemin de fer', 1, 3);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (5, true, 'ACCVEHICULEECH', 'Echappement, freins...', 1, 4);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (6, true, 'ACCVEHICULEINC', 'Incendie', 1, 4);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (7, true, 'ACCDEPOTORDOFF', 'Officiel', 1, 5);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (8, true, 'ACCDEPOTORDCLAN', 'Clandestin', 1, 5);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (9, true, 'MALCONFLITSOL', 'Occupation du sol', 1, 6);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (10, true, 'MALCONFLITCHAS', 'Chasse', 1, 6);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (11, true, 'MALINTERETSOL', 'Occupation du sol', 1, 7);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (12, true, 'MALINTERETCYN', 'Cynégétique', 1, 7);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (13, true, 'MALINTERETPAS', 'Pastoralisme', 1, 7);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (14, true, 'MALPYROMAN', 'Pyromanie', 1, 8);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (15, true, 'TRAVPROFORSTMAC', 'Machine-outil', 1, 9);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (16, true, 'TRAVPROFORSTVEGP', 'Feu végétaux sur pied', 1, 9);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (17, true, 'TRAVPROFORSTVEGC', 'Feu végétaux coupés', 1, 9);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (18, true, 'TRAVPROAGRICMAC', 'Machine-outil', 1, 10);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (19, true, 'TRAVPROAGRICVEGP', 'Feu végétaux sur pied', 1, 10);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (20, true, 'TRAVPROAGRICVEGC', 'Feu végétaux coupés', 1, 10);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (21, true, 'TRAVPROAGRICPAS', 'Feu pastoral', 1, 10);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (22, true, 'TRAVPROINDUSMAC', 'Machine-outil', 1, 11);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (23, true, 'TRAVPROINDUSVEGP', 'Feu végétaux sur pied', 1, 11);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (24, true, 'TRAVPROINDUSVEGC', 'Feu végétaux coupés', 1, 11);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (25, true, 'TRAVPROREPR', 'Reprise', 1, 12);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (26, true, 'TRAVPARTTRAVMAC', 'Machine-outil', 1, 13);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (27, true, 'TRAVPARTTRAVVEGP', 'Feu végétaux sur pied', 1, 13);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (28, true, 'TRAVPARTTRAVCEGC', 'Feu végétaux coupés', 1, 13);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (29, true, 'TRAVPARTLOISJEU', 'Jeu d''enfants, pétard...', 1, 14);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (30, true, 'TRAVPARTLOISFART', 'Feu d''artifice', 1, 14);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (31, true, 'TRAVPARTLOISBARB', 'Barbecue, réchaud, feu loisir', 1, 14);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (32, true, 'TRAVPARTJETOBMEGP', 'Mégot de promeneur', 1, 15);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (33, true, 'TRAVPARTJETOBMEGV', 'Mégot par véhicule', 1, 15);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (34, true, 'TRAVPARTJETOBFUS', 'Fusée de détresse', 1, 15);
INSERT INTO type_rci_prom_categorie (id, actif, code, nom, version, partition) VALUES (35, true, 'TRAVPARTJETOBCEND', 'Déversement cendres chaudes', 1, 15);


-- Completed on 2015-03-11 16:29:48 CET

--
-- PostgreSQL database dump complete
--

