
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = pdi, pg_catalog;



-- Traitement PIBI indisponibles de la veille
BEGIN;

INSERT INTO traitement ( demande, execution, idutilisateur, urlressource, idmodele, idstatut) VALUES (
current_timestamp, NULL,
/* Référent pour les PIBI */
(select u.id from remocra.utilisateur u
join remocra.organisme o on u.organisme = o.id 
join remocra.profil_organisme_utilisateur_droit poud on ( poud.profil_utilisateur = u.profil_utilisateur and poud.profil_organisme = o.profil_organisme )
join remocra.profil_droit pd on ( pd.id = poud.profil_droit and pd.code = 'SDIS-REF-DECI')
where u.actif and u.message_remocra limit 1),
NULL, /*le job*/(select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles'), 1);

INSERT INTO traitement_parametre (idparametre, idtraitement, valeur) VALUES (
/*id du paramètre modèle*/
(select idparametre from pdi.modele_traitement_parametre where idmodele=(select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles') and form_etiquette='Commune'),
/*le traitement*/(select max(idtraitement) from pdi.traitement),
/*VAR*/'-1');


/*paramètre NOTIFIER_RESPONSABLES*/
INSERT INTO traitement_parametre (idparametre, idtraitement,valeur) VALUES(
(select idparametre from pdi.modele_traitement_parametre where idmodele=(select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles') and nom ='NOTIFIER_RESPONSABLES'),
(select max(idtraitement) from pdi.traitement),
'true');

/*paramètre NOTIFIER_COMMUNES*/
INSERT INTO traitement_parametre (idparametre, idtraitement,valeur) VALUES(
(select idparametre from pdi.modele_traitement_parametre where idmodele=(select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles') and nom ='NOTIFIER_COMMUNES'),
(select max(idtraitement) from pdi.traitement),
'true');

COMMIT;


-- Traitement PENA indisponibles de la veille
BEGIN;
INSERT INTO traitement (demande, execution, idutilisateur, urlressource, idmodele, idstatut) VALUES (
current_timestamp, NULL,
/* Référent pour les PENA */
(select u.id from  remocra.utilisateur u
join remocra.organisme o on u.organisme = o.id 
join remocra.profil_organisme_utilisateur_droit poud on ( poud.profil_utilisateur = u.profil_utilisateur and poud.profil_organisme = o.profil_organisme )
join remocra.profil_droit pd on ( pd.id = poud.profil_droit and pd.code = 'SDIS-REF-PENA')
where u.actif and u.message_remocra limit 1),
NULL, /*le job*/(select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles'), 1);

INSERT INTO traitement_parametre (idparametre, idtraitement, valeur) VALUES (
/*id du paramètre modèle*/
(select idparametre from pdi.modele_traitement_parametre where idmodele=(select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles') and form_etiquette='Commune'),
/*le traitement*/(select max(idtraitement) from pdi.traitement), /*VAR*/'-1');

/*paramètre NOTIFIER_RESPONSABLES*/
INSERT INTO traitement_parametre (idparametre, idtraitement,valeur) VALUES(
(select idparametre from pdi.modele_traitement_parametre where idmodele=(select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles') and nom ='NOTIFIER_RESPONSABLES'),
(select max(idtraitement) from pdi.traitement),
'true');

/*paramètre NOTIFIER_COMMUNES*/
INSERT INTO traitement_parametre (idparametre, idtraitement,valeur) VALUES(
(select idparametre from pdi.modele_traitement_parametre where idmodele=(select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles') and nom ='NOTIFIER_COMMUNES'),
(select max(idtraitement) from pdi.traitement),
'true');


COMMIT;

