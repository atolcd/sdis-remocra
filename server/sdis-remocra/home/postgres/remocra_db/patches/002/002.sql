SET search_path = remocra, pdi, pg_catalog;


BEGIN;

-- Traitement PDI de téléchargement de fiches Atlas
INSERT INTO pdi.modele_traitement (idmodele, nom, description, type, ref_chemin, ref_nom, message_succes, message_echec, code) VALUES (7, 'Téléchargement des fiches Atlas', 'Compile les fiches Atlas dans un fichier zip selon le territoire d''un utilisateur et transmet le lien dans un courrier électronique.','', '', '', 1, 3, 4);
INSERT INTO pdi.modele_traitement_parametre (idparametre, nom, form_obligatoire, form_num_ordre, form_etiquette, form_type_valeur, form_valeur_defaut, form_source_donnee, idmodele) VALUES (6, 'LST_UTILISATEURS', true, 1, 'Utilisateur', 'combo', NULL, 'vue_utilisateurs', 7);


--- Vue des utilisateurs
CREATE OR REPLACE VIEW pdi.vue_utilisateurs AS 
 SELECT utilisateur.id, utilisateur.identifiant AS libelle
   FROM remocra.utilisateur;


INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_ATLAS', 'Traitement de téléchargement de l''Atlas', '7', 1);


COMMIT;

