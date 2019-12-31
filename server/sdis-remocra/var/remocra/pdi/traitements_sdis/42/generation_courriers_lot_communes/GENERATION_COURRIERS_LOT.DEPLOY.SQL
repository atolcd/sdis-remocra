INSERT INTO pdi.modele_traitement VALUES ((SELECT MAX(idmodele) +1 FROM pdi.modele_traitement),1,'Génère des courriers d''information préalable à une ROP pour un lot de commune','Courrier d''information préalable à une ROP en lot','/var/remocra/pdi/traitements_sdis/42/generation_courriers_lot_communes/','generer_courriers_lot_communes.kjb','J',3,1);

INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Communes ou intercommunalités', 1, true, 'vue_commune_ou_interco_one', 'multicombo', null::character varying, 'COMMUNES_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Modèle de courrier', 2, true, 'vue_modele_courrier_information_tournee', 'combo', null::character varying, 'MODELE_COURRIER_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Profil d''utilisateur responsable', 3, true, 'vue_profil_sdis', 'combo', null::character varying, 'PROFIL_UTILISATEUR_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Notifier par mél les responsables', 4, true, '', 'checkbox', null::character varying, 'NOTIFIER_RESPONSABLE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Notifier par mél la mairie ou l''intercommunalité', 5, true, '', 'checkbox', null::character varying, 'NOTIFIER_COLLECTIVITE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));





INSERT INTO pdi.modele_traitement VALUES ((SELECT MAX(idmodele) +1 FROM pdi.modele_traitement),1,'Génère un courrier de rapport suite à une ROP pour un lot de commune','Courrier de rapport suite à une ROP en lot','/var/remocra/pdi/traitements_sdis/42/generation_courriers_lot_communes/','generer_courriers_lot_communes.kjb','J',3,1);

INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Communes ou intercommunalités', 1, true, 'vue_commune_ou_interco_one', 'multicombo', null::character varying, 'COMMUNES_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Modèle de courrier', 2, true, '"vue_modele_courrier_rapport_tournee"', 'combo', null::character varying, 'MODELE_COURRIER_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Profil d''utilisateur responsable', 3, true, 'vue_profil_sdis', 'combo', null::character varying, 'PROFIL_UTILISATEUR_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Notifier par mél les responsables', 4, true, '', 'checkbox', null::character varying, 'NOTIFIER_RESPONSABLE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Notifier par mél la mairie ou l''intercommunalité', 5, true, '', 'checkbox', null::character varying, 'NOTIFIER_COLLECTIVITE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
