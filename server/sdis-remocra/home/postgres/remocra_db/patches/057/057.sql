begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 57;
    description_patch := 'Gestion des courriers';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début
--


update remocra.suivi_patches set description='Enregistrement famille et partition promethee' where numero = 56;



-- ------------------------------
-- Schéma Remocra
-- ----------

/* AJOUT OU MODIFICATION DE PARAMETRES - TOUS DOMAINES CONFONDUS */

-- Adresse mail de retour des accusés de réception des messages
INSERT INTO remocra.param_conf (cle,description,valeur) VALUES ('PDI_SMTP_AR_MAIL','Adresse mél utilisée pour recueillir les accusés de réception des messages envoyés par Remocra',(SELECT valeur FROM remocra.param_conf WHERE cle = 'PDI_SMTP_EME_MAIL'));

-- Adresse mail de retour des erreurs de notification
INSERT INTO remocra.param_conf (cle,description,valeur) VALUES ('PDI_SMTP_ERR_MAIL','Adresse mél utilisée pour recueillir les erreurs de non délivrance des messages envoyés par Remocra',(SELECT valeur FROM remocra.param_conf WHERE cle = 'PDI_SMTP_EME_MAIL'));

-- Dossier de stockage des courriers générés et mis à dispositions
INSERT INTO remocra.param_conf (cle,description,valeur) VALUES ('DOSSIER_COURRIER','Emplacement du dossier de stockage des courriers','/var/remocra/courriers');

-- Fichier du certificat PFX à utiliser pour la signature des courriers PDF
INSERT INTO remocra.param_conf (cle,description,valeur) VALUES ('PDI_PDF_SIGN_PFX_FILE','Signature des courriers PDF : Emplacement du fichier PFX de signature des courriers PDF (pas de signature si vide)','');

-- Mot de passe permettant l'accès au contenu du PFX
INSERT INTO remocra.param_conf (cle,description,valeur) VALUES ('PDI_PDF_SIGN_PFX_PASSWORD','Signature des courriers PDF : Mot de passe d''accès au contenu du fichier PFX (pas de signature si vide)','');

-- Mot de passe de la clef privée
INSERT INTO remocra.param_conf (cle,description,valeur) VALUES ('PDI_PDF_SIGN_KEY_PASSWORD','Signature des courriers PDF : Mot de passe de la clé (pas de signature si vide)','');


/* GESTION DES EMAILS */

-- Code email utilisé pour la traçabilité des messages
ALTER TABLE remocra.email ADD COLUMN code character varying NOT NULL DEFAULT md5('email-'||now()::text ||'-'|| floor(random()*10000 + 1));
COMMENT ON COLUMN remocra.email.code IS 'Code utilisé pour la traçabilité des méls';

-- Activer les demandes d'accusés de lecture
ALTER TABLE remocra.email ADD COLUMN accuser_reception boolean NOT NULL DEFAULT false;
COMMENT ON COLUMN remocra.email.accuser_reception IS 'Indique si le destinataire du mel est invité à accuser réception du message';

/* GESTION DES COURRIERS */

-- Modèle de courrier
CREATE TABLE remocra.courrier_modele
(
  id bigserial NOT NULL,
  categorie character varying,
  libelle character varying NOT NULL,
  description character varying,
  modele_ott character varying NOT NULL,
  source_xml character varying NOT NULL,
  CONSTRAINT courrier_modele_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.courrier_modele IS 'Modèle de courrier reférencant un fichier OTT et permettant la génération d''un courrier au format ODT';
COMMENT ON COLUMN remocra.courrier_modele.id IS 'Identifiant interne autogénéré';
COMMENT ON COLUMN remocra.courrier_modele.categorie IS 'Code de catégorie permettant de regrouper les modèles de courriers entre eux. Issu de la métadonnée personnalisée "REMOCRA_CATEGORIE_MODELE" du fichier OTT';
COMMENT ON COLUMN remocra.courrier_modele.libelle IS 'Libellé du modèle de courrier affiché dans les listes déroulantes. Issu de la métadonnée "Titre" du fichier OTT';
COMMENT ON COLUMN remocra.courrier_modele.description IS 'Description du modèle de courrier. Issu de la métadonnée "Commentaire" du fichier OTT';
COMMENT ON COLUMN remocra.courrier_modele.modele_ott IS 'Fichier de modèle OTT à utiliser pour générer le document ODT';
COMMENT ON COLUMN remocra.courrier_modele.source_xml IS 'Requête SQL générant une structure XML exploitable par le modèle de courrier OTT via le language FreeMarker';

-- Courrier adressé à un destinataire
CREATE TABLE remocra.courrier_document
(
  id bigserial NOT NULL,
  document bigint NOT NULL,
  code character varying NOT NULL DEFAULT md5('courrier-destinataire-'||now()::text ||'-'|| floor(random()*10000 + 1)),
  nom_destinataire character varying NOT NULL,
  type_destinataire character varying NOT NULL,
  id_destinataire character varying,
  accuse timestamp without time zone,
  CONSTRAINT courrier_document_pkey PRIMARY KEY (id),
  CONSTRAINT courrier_document_document_fk FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

COMMENT ON TABLE remocra.courrier_document IS 'Courrier adressé à un destinataire unique référençant un document présent sur le système. Ce document peut donner lieu à un accusé de téléchargement';
COMMENT ON COLUMN remocra.courrier_document.id IS 'Identifiant interne autogénéré';
COMMENT ON COLUMN remocra.courrier_document.document IS 'Référence au document';
COMMENT ON COLUMN remocra.courrier_document.code IS 'Code unique exploité pour l''accusé de téléchargement';
COMMENT ON COLUMN remocra.courrier_document.nom_destinataire IS 'Indication sur le destinataire du courrier.';
COMMENT ON COLUMN remocra.courrier_document.type_destinataire IS 'Indication sur le type de destinataire du courrier. Organisme = ORGANISME, utilisateur = UTILISATEUR, autre = AUTRE';
COMMENT ON COLUMN remocra.courrier_document.id_destinataire IS 'Identifiant du destinataire du courrier dans la base Remocra quand le type = ORGANISME ou UTILISATEUR';
COMMENT ON COLUMN remocra.courrier_document.accuse IS 'Date et heure d''accusé de téléchargement';

-- Modèle de mél de notification de téléchargement d'un courrier
INSERT INTO remocra.email_modele (code, objet, corps) VALUES (
	'COURRIER_PAR_MAIL',
	'SDIS REMOCRA - COURRIER D''INFORMATION',
	'<title>Courrier SDIS REMOCRA</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<style type="text/css">
		div{width:800px;text-align:justify;}
		p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}
		p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}
		p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}
		table{border-collapse:collapse;}
		td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}
	</style>
	<div>
		<p>Madame, Monsieur,<br/>
		<br/>Le SDIS vous remercie de prendre connaissance du courrier qu''il vous a adressé en cliquant sur le lien ci-dessous :<br/>
		<br/><a href="[URL_TELECHARGEMENT]">Télécharger le courrier</a><br/>
		<br/>Cordialement.</p>
		<p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
		<p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
	</div>'
);



-- ------------------------------
-- Schéma Pdi
-- ----------
-- Vue des communes et EPCI
CREATE OR REPLACE VIEW pdi.vue_commune_ou_interco_one AS 
SELECT
	org.id,
	org.nom || ' (' || (torg.type) ||')' AS libelle
FROM
	remocra.organisme org
	JOIN (
		SELECT
			id,
			code AS type
		FROM
			remocra.type_organisme
		WHERE
			code IN ('COMMUNE', 'CT')
	) torg ON (torg.id = org.type_organisme) where org.actif and coalesce(org.email_contact, '') != ''
ORDER BY
	libelle;

-- Vue des modèles de courrier "COURRIER_INFO_TOURNEE"
CREATE OR REPLACE VIEW pdi.vue_modele_courrier_information_tournee AS 
SELECT
	id::bigint AS id,
	libelle AS libelle
FROM
	remocra.courrier_modele
WHERE
	categorie = 'COURRIER_INFORMATION_TOURNEE'
ORDER BY
	libelle;

-- Vue des modèles de courrier "COURRIER_RAPPORT_TOURNEE"
CREATE OR REPLACE VIEW pdi.vue_modele_courrier_rapport_tournee AS 
SELECT
	id::bigint AS id,
	libelle AS libelle
FROM
	remocra.courrier_modele
WHERE
	categorie = 'COURRIER_RAPPORT_TOURNEE'
ORDER BY
	libelle;

-- Vue des profils pour les CIS et SDIS
CREATE OR REPLACE VIEW pdi.vue_profil_sdis AS 
SELECT
	id,
	nom AS libelle
FROM
	remocra.profil_utilisateur
WHERE
	actif
	AND type_organisme IN ( SELECT id FROM remocra.type_organisme WHERE actif AND code IN ('CIS','SDIS'))
ORDER BY
	libelle;

--  MODELE DE TRAITEMENT "maj des modèles de courrier"
INSERT INTO pdi.modele_traitement(
	idmodele,
	code,
	nom,
	description,
	ref_chemin,
	ref_nom,
	type,
	message_echec,
	message_succes
)VALUES(
	20,
	0,
	'Référencer les modèles de courriers',
	'Référence les modèles de courriers disponibles pour la génération de courriers personnalisés. Attention, la liste actuelle des modèles de courriers sera entièrement remplacée.',
	'/demandes/generation_courriers/commun/maintenance_modeles',
	'creer_liste_modeles',
	'T',
	3,
	1
);

--  MODELE DE TRAITEMENT  "courrier d'information préalable à une tournée"

-- Modèle de traitement de génération d'un courrier d'information avant une tournée
INSERT INTO pdi.modele_traitement(
	idmodele,
	code,
	nom,
	description,
	ref_chemin,
	ref_nom,
	type,
	message_echec,
	message_succes
)VALUES(
	21,
	1,
	'Reconnaissance opérationnelle PEI - Courrier d''information préalable',
	'Génère un courrier d''information préalable à une reconnaissance opérationnelle des PEI',
	'/demandes/generation_courriers/reconnaissance_operationnelle_pei',
	'generer_et_notifier_courrier_info_prealable',
	'J',
	3,
	1
);

-- Liste des communes ou intercommunalités
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (71, 'Commune ou intercommunalité', 1, true, 'vue_commune_ou_interco_one', 'combo', NULL, 'ORGANISME_COLLECTIVITE_ID', 21);

-- Date de début de passage
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (72, 'Date de début de passage', 2, true, NULL, 'datefield', NULL, 'DATE_DEB', 21);

-- Date de fin de passage
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (73, 'Date de fin de passage', 3, false, NULL, 'datefield', NULL, 'DATE_FIN', 21);

-- Liste des modèles de courriers
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (74, 'Modèle de courrier', 4, true, 'vue_modele_courrier_information_tournee', 'combo', NULL, 'MODELE_COURRIER_ID', 21);

-- Profil de correpondant
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (75, 'Profil d''utilisateur responsable', 5, true, 'vue_profil_sdis', 'combo', NULL, 'PROFIL_UTILISATEUR_ID', 21);

-- Notifier les responsables
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (76, 'Notifier par mél les responsables', 6, true, NULL, 'checkbox', NULL, 'NOTIFIER_RESPONSABLE', 21);

-- Notifier la mairie ou l'intercommunalité
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (77, 'Notifier par mél la mairie ou l''intercommunalité', 7, true, NULL, 'checkbox', NULL, 'NOTIFIER_COLLECTIVITE', 21);

--  MODELE DE TRAITEMENT  "courrier de rapport de tournée"
-- Modèle de traitement de génération d'un courrier d'information avant une tournée
INSERT INTO pdi.modele_traitement(
	idmodele,
	code,
	nom,
	description,
	ref_chemin,
	ref_nom,
	type,
	message_echec,
	message_succes
)VALUES(
	22,
	1,
	'Reconnaissance opérationnelle PEI - Courrier de rapport',
	'Génère un courrier de rapport suite à une reconnaissance opérationnelle des PEI',
	'/demandes/generation_courriers/reconnaissance_operationnelle_pei',
	'generer_et_notifier_courrier_rapport',
	'J',
	3,
	1
);

-- Liste des communes ou intercommunalités
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (78, 'Commune ou intercommunalité', 1, true, 'vue_commune_ou_interco_one', 'combo', NULL, 'ORGANISME_COLLECTIVITE_ID', 22);

-- Liste des modèles de courriers
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (79, 'Modèle de courrier', 4, true, 'vue_modele_courrier_rapport_tournee', 'combo', NULL, 'MODELE_COURRIER_ID', 22);

-- Profil de correpondant
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (80, 'Profil d''utilisateur responsable', 5, true, 'vue_profil_sdis', 'combo', NULL, 'PROFIL_UTILISATEUR_ID', 22);

-- Notifier les responsables
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (81, 'Notifier par mél les responsables', 6, true, NULL, 'checkbox', NULL, 'NOTIFIER_RESPONSABLE', 22);

-- Notifier la mairie ou l'intercommunalité
INSERT INTO pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele)
VALUES (82, 'Notifier par mél la mairie ou l''intercommunalité', 7, true, NULL, 'checkbox', NULL, 'NOTIFIER_COLLECTIVITE', 22);



--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

