BEGIN;

INSERT INTO  pdi.modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) 
	VALUES (16, 2, 'Nombre d''alertes par utilisateur dans une caserne', 'Nombre d''alertes par utilisateur dans une caserne', '/demandes/alertes', 'alertes_par_utilisateur', 'J', 3, 1);

INSERT INTO  pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	VALUES (58, 'Caserne', 1, true, 'vue_organisme_cis', 'combo', NULL, 'ORGANISME_CIS_ID', 16);

INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	VALUES (59, 'Début', 2, true, NULL, 'datefield', '2014-01-01', 'DATE_DEB', 16);
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	VALUES (60, 'Fin', 3, true, NULL, 'datefield', '2020-12-31', 'DATE_FIN', 16);

INSERT INTO remocra.type_droit (code, description, nom, version) VALUES ('ALERTES_EXPORT', 'Droit sur l''export du nombre d''alertes par utilisateur', 'alertes.export', 1);

INSERT INTO remocra.param_conf(cle,description,valeur,version) VALUES ('ID_TRAITEMENT_NB_ALERTES_PAR_UTILISATEUR','Traitement du nombre d''alertes par utilisateur','16',1);

-- Profils
-- CIS : Chef de centre
-- SDIS : Administrateur de l'application
-- SDIS : Chef de salle (salle ops : Chef de salle). devrait être limité car pas réellement SDIS mais organisation par salles. Pour le moment : SDIS (pas d'autre choix)

INSERT INTO remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
SELECT 'TRUE','FALSE','FALSE','FALSE',1, pd.id,td.id
FROM remocra.profil_droit pd, remocra.type_droit td
WHERE td.code = 'ALERTES_EXPORT'
AND pd.code in ('SDIS-ADM-APP','CIS-CHEF');

COMMIT;
