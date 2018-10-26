begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

/*--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 97;
    description_patch := 'Initialisation module Crise';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début*/


INSERT INTO remocra.param_conf(
       cle, description, valeur, version, nomgroupe)
   VALUES ('DOSSIER_DOC_CRISE', 'Emplacement du dossier de stockage des documents CRISE', '/var/remocra/crises', 1, 'Chemins sur disque');
INSERT INTO remocra.param_conf(
       cle, description, valeur, version, nomgroupe)
   VALUES ('CRISE_NOUVEL_EVT_DELAI_MINUTES', 'Nombre de minutes avant de considérer qu''un évènement n''est plus "nouveau"', '20', 1, 'Gestion de crises');



-- Droits sur le module Crise
select setval('remocra.type_droit_id_seq',id,false) from (select max(id)+1 as id from remocra.type_droit) as compteur;
insert into remocra.type_droit(code, nom, description, categorie, version) values 
 ('CRISE_R', 'crise_R', 'Lister les crises', 'Module CRISE', '1'),
 ('CRISE_C', 'crise_C', 'Créer une nouvelle crise', 'Module CRISE', '1'),
 ('CRISE_U', 'crise_U', 'Modifier les informations associées à une crise', 'Module CRISE', '1'),
 ('CRISE_D', 'crise_D', 'Supprimer une crise', 'Module CRISE', '1');



-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise CASCADE;
CREATE TABLE remocra.type_crise(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL UNIQUE
);
COMMENT ON TABLE remocra.type_crise IS 'Gestion de crise : Type de crise géré par REMOCRA';
COMMENT ON COLUMN remocra.type_crise.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_crise.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_crise.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_crise.nom IS 'Libellé du type de crise';

INSERT INTO remocra.type_crise(code,nom) VALUES ('INONDATION','Inondation');
INSERT INTO remocra.type_crise(code,nom) VALUES ('CANICULE','Canicule');
INSERT INTO remocra.type_crise(code,nom) VALUES ('INDUSTRIEL','Accident industriel');
INSERT INTO remocra.type_crise(code,nom) VALUES ('ATTENTAT','Attentat');
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_statut CASCADE;
CREATE TABLE remocra.type_crise_statut(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL UNIQUE
);
COMMENT ON TABLE remocra.type_crise_statut IS 'Gestion de crise : Statut associé à une crise. Le statut conditionne les actions réalisables via REMOCRA';
COMMENT ON COLUMN remocra.type_crise_statut.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_crise_statut.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_crise_statut.code IS 'Code du statut. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_crise_statut.nom IS 'Libellé du statut';

INSERT INTO remocra.type_crise_statut(code,nom) VALUES ('EN_COURS','En cours');
INSERT INTO remocra.type_crise_statut(code,nom) VALUES ('TERMINE','Terminée');
INSERT INTO remocra.type_crise_statut(code,nom) VALUES ('FUSIONNE','Fusionnée');
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_contexte CASCADE;
CREATE TABLE remocra.type_crise_contexte(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL UNIQUE
);
COMMENT ON TABLE remocra.type_crise_contexte IS 'Gestion de crise : Type de contexte dans lequel est gérée la crise. Deux contextes cohabitent simultanément pour chaque épisode : "Opérationnel" et "Anticipation". Les données cartographiques associées peuvent varier en fonction du contexte de même que les types d''évenements à renseigner';
COMMENT ON COLUMN remocra.type_crise_contexte.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_crise_contexte.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_crise_contexte.code IS 'Code du contexte. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_crise_contexte.nom IS 'Libellé du contexte';

INSERT INTO remocra.type_crise_contexte(code,nom) VALUES ('OPERATIONNEL','Opérationnel');
INSERT INTO remocra.type_crise_contexte(code,nom) VALUES ('ANTICIPATION','Anticipation');
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.crise CASCADE;
CREATE TABLE remocra.crise(
	id bigserial NOT NULL PRIMARY KEY,
	nom character varying NOT NULL,
	description character varying,
	activation timestamp with time zone NOT NULL,
	cloture timestamp with time zone,
	statut bigint NOT NULL,
	type_crise bigint NOT NULL,
        carte character varying,
        redefinition timestamp with time zone,
        crise_parente bigint,
	CONSTRAINT crise_type_crise_statut FOREIGN KEY (statut) REFERENCES remocra.type_crise_statut (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_type_crise FOREIGN KEY (type_crise) REFERENCES remocra.type_crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
        CONSTRAINT crise_crise_parente FOREIGN KEY (crise_parente) REFERENCES remocra.crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE SET NULL
);
CREATE INDEX crise_statut_idx ON remocra.crise USING btree (statut);
CREATE INDEX crise_type_idx ON remocra.crise USING btree (type_crise);

COMMENT ON TABLE remocra.crise IS 'Gestion de crise : Episode de crise pour lequel des informations ont été saisies dans REMOCRA';
COMMENT ON COLUMN remocra.crise.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise.nom IS 'Nom de la crise';
COMMENT ON COLUMN remocra.crise.description IS 'Description permettant de fournir des indications sur le contexte de la crise';
COMMENT ON COLUMN remocra.crise.activation IS 'Date et heure d''activation de la crise';
COMMENT ON COLUMN remocra.crise.cloture IS 'Date et heure de désactivation de la crise';
COMMENT ON COLUMN remocra.crise.statut IS 'Identifiant du statut associé';
COMMENT ON COLUMN remocra.crise.type_crise IS 'Identifiant du type de crise associé';
COMMENT ON COLUMN remocra.crise.carte IS 'Groupe de couches complémentaires à carte.json';
COMMENT ON COLUMN remocra.crise.redefinition IS 'Date et heure de modification de la crise';
COMMENT ON COLUMN remocra.crise.crise_parente IS 'Crise parente lors de la fusion';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.crise_commune CASCADE;
CREATE TABLE remocra.crise_commune(
	crise bigint NOT NULL,
	commune bigint NOT NULL,
	CONSTRAINT crise_commune_pkey PRIMARY KEY (crise, commune),
	CONSTRAINT crise_commune_crise_fk FOREIGN KEY (crise) REFERENCES remocra.crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_commune_commune_fk FOREIGN KEY (commune) REFERENCES remocra.commune (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX crise_commune_crise_idx ON remocra.crise_commune USING btree (crise);
CREATE INDEX crise_commune_commune_idx ON remocra.crise_commune USING btree (commune);

COMMENT ON TABLE remocra.crise_commune IS 'Gestion de crise : commune associée à un épisode de crise. Permet de définir un territoire impacté. Les mises à jours de communes sont traçées sous formes d''évènements spécifiques';
COMMENT ON COLUMN remocra.crise_commune.crise IS 'Identifiant de la crise associée';
COMMENT ON COLUMN remocra.crise_commune.commune IS 'Identifiant de la commune associée';


-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_categorie_evenement CASCADE;
CREATE TABLE remocra.type_crise_categorie_evenement(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL UNIQUE
);
COMMENT ON TABLE remocra.type_crise_categorie_evenement IS 'Gestion de crise : Catégorie d''évènement (Réseau routier, Hébergement, Secours à la personne, Zones inondées, etc. ';
COMMENT ON COLUMN remocra.type_crise_categorie_evenement.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_crise_categorie_evenement.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_crise_categorie_evenement.code IS 'Code de la catégorie. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_crise_categorie_evenement.nom IS 'Libellé de la catégorie de l''évènement';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_nature_evenement CASCADE;
CREATE TABLE remocra.type_crise_nature_evenement(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL,
	type_geometrie character varying CHECK(type_geometrie IN('POINT','LINESTRING','POLYGON')),
	categorie_evenement bigint NOT NULL,
	CONSTRAINT type_crise_nature_evenement_categorie_evenement_fk FOREIGN KEY (categorie_evenement) REFERENCES remocra.type_crise_categorie_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX type_crise_nature_evenement_categorie_evenement_idx ON remocra.type_crise_nature_evenement USING btree (categorie_evenement);

COMMENT ON TABLE remocra.type_crise_nature_evenement IS 'Gestion de crise : Nature de l''évènement attaché à une catégorie. Ex : "Route barrée" pour la catégorie "Réseau routier"';
COMMENT ON COLUMN remocra.type_crise_nature_evenement.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_crise_nature_evenement.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_crise_nature_evenement.code IS 'Code de la nature. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_crise_nature_evenement.nom IS 'Libellé de la nature';
COMMENT ON COLUMN remocra.type_crise_nature_evenement.type_geometrie IS 'Type de géométrie. Si non renseigné, type d''évènement non géolocalisable';
COMMENT ON COLUMN remocra.type_crise_nature_evenement.categorie_evenement IS 'Catégorie d''évènement associée';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_evenement_crise CASCADE;
CREATE TABLE remocra.type_crise_evenement_crise(
	categorie_evenement bigint NOT NULL,
	type_crise bigint NOT NULL,
	CONSTRAINT type_crise_evenement_crise_pkey PRIMARY KEY (type_crise, categorie_evenement),
	CONSTRAINT type_crise_evenement_crise_type_crise_fk FOREIGN KEY (type_crise) REFERENCES remocra.type_crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT type_crise_evenement_crise_categorie_evenement_fk FOREIGN KEY (categorie_evenement) REFERENCES remocra.type_crise_categorie_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX type_crise_evenement_crise_type_crise_idx ON remocra.type_crise_evenement_crise USING btree (type_crise);
CREATE INDEX type_crise_evenement_crise_categorie_evenement_idx ON remocra.type_crise_evenement_crise USING btree (categorie_evenement);

COMMENT ON TABLE remocra.type_crise_evenement_crise IS 'Catégorie d''évènement mobilisable pour un type de crise';
COMMENT ON COLUMN remocra.type_crise_evenement_crise.type_crise IS 'Identifiant du type de crise';
COMMENT ON COLUMN remocra.type_crise_evenement_crise.categorie_evenement IS 'Identifiant de la catégorie d''évènement';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_propriete_evenement CASCADE;
CREATE TABLE remocra.type_crise_propriete_evenement(
	id bigserial NOT NULL PRIMARY KEY,
	nom character varying NOT NULL,
	type_valeur character varying NOT NULL,
	obligatoire boolean NOT NULL,
	source_sql character varying,
	source_sql_valeur character varying,
	source_sql_libelle character varying,
	formulaire_etiquette character varying NOT NULL,
	formulaire_type_controle character varying NOT NULL,
	formulaire_num_ordre bigint NOT NULL,
	formulaire_valeur_defaut character varying,
	nature_evenement bigint NOT NULL,
	CONSTRAINT type_crise_propriete_evenement_nature_evenement_fk FOREIGN KEY (nature_evenement) REFERENCES remocra.type_crise_nature_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX type_crise_propriete_evenement_nature_evenement ON remocra.type_crise_propriete_evenement USING btree (nature_evenement);

COMMENT ON TABLE remocra.type_crise_propriete_evenement IS 'Propriété complémentaire pouvant être renseignée lors de la création ou de la modification d''un évènement';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.nom IS 'Nom de la propriété. Sans espace ni caractère spécial';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.type_valeur IS 'Type de valeur attendue';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.obligatoire IS 'La saisie d''une valeur pour le paramètre est obligatoire';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.source_sql IS 'Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA pour fournir les informations facilitant ou limitant la saisie de valeurs pour l''utilisateur';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.source_sql_valeur IS 'Colonne de la requête SQL éventuelle contenant la valeur du paramètre';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.source_sql_libelle IS 'Colonne de la requête SQL éventuelle contenant le libellé associé à la valeur du paramètre';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.formulaire_etiquette IS 'Etiquette associée au champ de saisie';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.formulaire_type_controle IS 'Type de contrôle associé au champ de saisie';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.formulaire_num_ordre IS 'Position d''affichage du champ de saisie dans le formulaire';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.formulaire_valeur_defaut IS 'Valeur par défaut proposée dans le champ de saisie';
COMMENT ON COLUMN remocra.type_crise_propriete_evenement.nature_evenement IS 'Nature d''évènement associée';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.crise_suivi_message_modele CASCADE;
CREATE TABLE remocra.crise_suivi_message_modele(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL UNIQUE,
	corps character varying NOT NULL,
	objet character varying NOT NULL,
	importance bigint,
	tags character varying
);

COMMENT ON TABLE remocra.crise_suivi_message_modele IS 'Gestion de crise : modèle de message à utiliser pour le suivi de crise. Utilisé notament par REMOCRA dans le cadre de la création automatique de messages suite à des actions utilisateurs';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.actif IS 'Sélectionnable dans l''interface ou exploitable par le système';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.code IS 'Code permettant une utilisation applicative ou lors d''échanges';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.corps IS 'Modèle de message à appliquer. Peut contenir des variables sous la forme [NOM_DE_VARIABLE] exploitées par le système';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.objet IS 'Titre de message à appliquer. Peut contenir des  variables sous la forme [NOM_DE_VARIABLE] exploitées par le système';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.importance IS 'Importance par défaut dans la table de suivi';
COMMENT ON COLUMN remocra.crise_suivi_message_modele.tags IS 'Liste des tags associés par défaut';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.type_crise_evenement_droit CASCADE;
CREATE TABLE remocra.type_crise_evenement_droit(
	categorie_evenement bigint NOT NULL,
	profil_droit bigint NOT NULL,
	CONSTRAINT type_crise_evenement_droit_pkey PRIMARY KEY (categorie_evenement, profil_droit),
	CONSTRAINT type_crise_evenement_droit_categorie_evenement_fk FOREIGN KEY (categorie_evenement) REFERENCES remocra.type_crise_categorie_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT type_crise_evenement_droit_droit_fk FOREIGN KEY (profil_droit) REFERENCES remocra.profil_droit (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX type_crise_evenement_droit_categorie_evenement_idx ON remocra.type_crise_evenement_droit USING btree (categorie_evenement);
CREATE INDEX type_crise_evenement_droit_profil_droit_idx ON remocra.type_crise_evenement_droit USING btree (profil_droit);

COMMENT ON TABLE remocra.type_crise_evenement_droit IS 'Profil de droit autorisé pour accéder à la catégorie d''évènement en création';
COMMENT ON COLUMN remocra.type_crise_evenement_droit.categorie_evenement IS 'Identifiant de la catégorie';
COMMENT ON COLUMN remocra.type_crise_evenement_droit.profil_droit IS 'Identifiant du profil de droit autorisé';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.crise_evenement CASCADE;
CREATE TABLE remocra.crise_evenement(
	id bigserial NOT NULL PRIMARY KEY,
	geometrie geometry,
	nom character varying NOT NULL,
	description character varying,
	constat timestamp with time zone NOT NULL,
	redefinition timestamp with time zone,
	cloture timestamp with time zone,
	origine character varying,
	importance integer,
	tags character varying,
	crise bigint NOT NULL,
	nature_evenement bigint NOT NULL,
	CONSTRAINT crise_evenement_crise_fk FOREIGN KEY (crise) REFERENCES remocra.crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_evenement_nature_evenement_fk FOREIGN KEY (nature_evenement) REFERENCES remocra.type_crise_nature_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT enforce_dims_geometrie CHECK (ndims(geometrie) = 2),
	CONSTRAINT enforce_srid_geometrie CHECK (srid(geometrie) = 2154)
);
CREATE INDEX crise_evenement_geometrie_idx ON remocra.crise_evenement USING gist (geometrie);
CREATE INDEX crise_evenement_crise_idx ON remocra.crise_evenement USING btree (crise);
CREATE INDEX crise_evenement_nature_evenement_idx ON remocra.crise_evenement USING btree (nature_evenement);

COMMENT ON TABLE remocra.crise_evenement IS 'Evènement associé à une crise. Route inondée sur la RD 84 à la position...';
COMMENT ON COLUMN remocra.crise_evenement.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise_evenement.nom IS 'Géometrie de localisation éventuelle. Exprimée en Lambert 93';
COMMENT ON COLUMN remocra.crise_evenement.nom IS 'Titre de l''évènement';
COMMENT ON COLUMN remocra.crise_evenement.description IS 'Information générale sur l''évènement (cause, autre information utile, etc.)';
COMMENT ON COLUMN remocra.crise_evenement.constat IS 'Date et heure de constat du phénomène ou de l''action';
COMMENT ON COLUMN remocra.crise_evenement.redefinition IS 'Date et heure de modification de l''évènement';
COMMENT ON COLUMN remocra.crise_evenement.cloture IS 'Date et heure de fin de vie de l''évènement';
COMMENT ON COLUMN remocra.crise_evenement.origine IS 'Origine de l''information : habitant, service de gendarmerie, twitter, etc.';
COMMENT ON COLUMN remocra.crise_evenement.importance IS 'Niveau d''importance permettant de filtrer facilement';
COMMENT ON COLUMN remocra.crise_evenement.tags IS 'Tags permettant qualifier facilement un évènement';
COMMENT ON COLUMN remocra.crise_evenement.crise IS 'Crise associée';
COMMENT ON COLUMN remocra.crise_evenement.nature_evenement IS 'Nature d''évènement associée';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.crise_evenement_complement CASCADE;
CREATE TABLE remocra.crise_evenement_complement(
	id bigserial NOT NULL PRIMARY KEY,
	valeur_formatee character varying,
	valeur_source character varying,
	evenement bigint NOT NULL,
	propriete_evenement bigint NOT NULL,
	CONSTRAINT crise_evenement_complement_evenement_fk FOREIGN KEY (evenement) REFERENCES remocra.crise_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_evenement_complement_type_evenement_propriete_fk FOREIGN KEY (propriete_evenement) REFERENCES remocra.type_crise_propriete_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_evenement_complement_evenement_propriete_uniq UNIQUE (evenement,propriete_evenement)
);
CREATE INDEX crise_evenement_complement_evenement_idx ON remocra.crise_evenement_complement USING btree (evenement);
CREATE INDEX crise_evenement_complement_propriete_evenement_idx ON remocra.crise_evenement_complement USING btree (propriete_evenement);

COMMENT ON TABLE remocra.crise_evenement_complement IS 'Information complémentaire (attribut) attachée à un événement d''une nature spécifique';
COMMENT ON COLUMN remocra.crise_evenement_complement.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise_evenement_complement.valeur_formatee IS 'Valeur affichée dans les formulaires et exploitée dans les exports';
COMMENT ON COLUMN remocra.crise_evenement_complement.valeur_source IS 'Valeur de l''identifiant dans le cas ou une source de données SQL a été définie pour la propriété';
COMMENT ON COLUMN remocra.crise_evenement_complement.evenement IS 'Evènement associé';
COMMENT ON COLUMN remocra.crise_evenement_complement.propriete_evenement IS 'Propriété d''évènement associée';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.crise_suivi CASCADE;
CREATE TABLE remocra.crise_suivi(
	id bigserial NOT NULL PRIMARY KEY,
	origine character varying,
	objet character varying NOT NULL,
	message character varying,
	creation timestamp with time zone NOT NULL,
	importance integer,
	tags character varying,
	crise bigint NOT NULL,
	evenement bigint,
	CONSTRAINT crise_suivi_crise_fk FOREIGN KEY (crise) REFERENCES remocra.crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_suivi_evenement_fk FOREIGN KEY (evenement) REFERENCES remocra.crise_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX crise_suivi_crise_idx ON remocra.crise_suivi USING btree (crise);
CREATE INDEX crise_suivi_evenement_idx ON remocra.crise_suivi USING btree (evenement);

COMMENT ON TABLE remocra.crise_suivi IS 'Message de suivi lié à une crise ou à un évènement. Le message peut être créé directement et manuellement par un utilisateur ou de manière indirecte par le système suite à une action dans REMOCRA. Ex : mise à jour des attributs d''un évènement, ajout d''un document, création d''une carte horodatée, etc.';
COMMENT ON COLUMN remocra.crise_suivi.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise_suivi.origine IS 'Origine du message : auteur, source d''information';
COMMENT ON COLUMN remocra.crise_suivi.message IS 'Objet du message';
COMMENT ON COLUMN remocra.crise_suivi.message IS 'Texte du message';
COMMENT ON COLUMN remocra.crise_suivi.creation IS 'Date et heure du message';
COMMENT ON COLUMN remocra.crise_suivi.importance IS 'Niveau d''importance permettant de filtrer facilement';
COMMENT ON COLUMN remocra.crise_suivi.tags IS 'Tags permettant qualifier facilement un message';
COMMENT ON COLUMN remocra.crise_suivi.crise IS 'Crise associée';
COMMENT ON COLUMN remocra.crise_suivi.evenement IS 'Evènement éventuellement associé';

--////////////////////////////////////////////////////////////

DROP TABLE IF EXISTS remocra.crise_document CASCADE;
CREATE TABLE remocra.crise_document(
	id bigserial NOT NULL PRIMARY KEY,
	sous_type character varying NOT NULL,
	document bigint NOT NULL,
	crise bigint NOT NULL,
	evenement bigint,
	geometrie geometry,
	CONSTRAINT crise_document_document_fk FOREIGN KEY (document) REFERENCES remocra.document (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_document_crise_fk FOREIGN KEY (crise) REFERENCES remocra.crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT crise_document_evenement_fk FOREIGN KEY (evenement) REFERENCES remocra.crise_evenement (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX crise_document_document_idx ON remocra.crise_document USING btree (document);
CREATE INDEX crise_document_crise_idx ON remocra.crise_document USING btree (crise);
CREATE INDEX crise_document_evenement_idx ON remocra.crise_document USING btree (evenement);

COMMENT ON TABLE remocra.crise_document IS 'Document associé à une crise de manière générale ou à un évènemement survenu lors d''une crise';
COMMENT ON COLUMN remocra.crise_document.id IS 'Identifiant interne autogénéré';
COMMENT ON COLUMN remocra.crise_document.sous_type IS 'Type de document pour la thématique crise. Ex : carte horodatée, photo, vidéo, etc.';
COMMENT ON COLUMN remocra.crise_document.document IS 'Référence au document';
COMMENT ON COLUMN remocra.crise_document.crise IS 'Crise associée';
COMMENT ON COLUMN remocra.crise_document.evenement IS 'Evènement éventuellement associé';
COMMENT ON COLUMN remocra.crise_document.geometrie IS 'Géometrie associée à la carte horodatée';





-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_modele CASCADE;
CREATE TABLE remocra.processus_etl_modele(
	id bigserial NOT NULL PRIMARY KEY,
	categorie character varying NOT NULL,
	code character varying NOT NULL UNIQUE,
	libelle character varying NOT NULL,
	description character varying,
	pdi_type character varying NOT NULL CHECK(pdi_type IN('T','J')),
	pdi_chemin character varying NOT NULL,
	pdi_nom character varying NOT NULL,
	priorite bigint NOT NULL DEFAULT 1 CHECK(priorite BETWEEN 1 AND 5),
	message_succes_objet character varying,
	message_succes_corps character varying,
	message_echec_objet character varying,
	message_echec_corps character varying
);
COMMENT ON TABLE remocra.processus_etl_modele IS 'Modèle de processus ETL PDI mobilisable sous forme de tâche planifiée ou à la demande d''un utilisateur via l''interface REMOCRA';
COMMENT ON COLUMN remocra.processus_etl_modele.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.processus_etl_modele.categorie IS 'Libellé de catégorie permettant de regrouper ou de filtrer les processus selon un thème';
COMMENT ON COLUMN remocra.processus_etl_modele.code IS 'Code permettant d''identifier de manière unique et pérenne un modèle de processus ETL PDI';
COMMENT ON COLUMN remocra.processus_etl_modele.libelle IS 'Libellé affiché en liste déroulante lors du choix du modèle de processus';
COMMENT ON COLUMN remocra.processus_etl_modele.description IS 'Description longue du modèle de requête';
COMMENT ON COLUMN remocra.processus_etl_modele.pdi_type IS 'Type de processus ETL : T -> Transormation ou J -> Tâche';
COMMENT ON COLUMN remocra.processus_etl_modele.pdi_chemin IS 'Chemin vers la tâche ou la transformation dans le référentiel "base de données" ou vers le dossier de l''espace disque dans le cas d''un fichier KTR ou KJB';
COMMENT ON COLUMN remocra.processus_etl_modele.pdi_nom IS 'Nom de la tâche ou de la transformation dans le référentiel "base de données" ou nom du fichier sans le chemin dans le cas d''un fichier KTR ou KJB';
COMMENT ON COLUMN remocra.processus_etl_modele.message_succes_objet IS 'Objet du mail en cas de succès du processus. Laisser NULL pour empêcher la notification.';
COMMENT ON COLUMN remocra.processus_etl_modele.priorite IS 'Niveau de priorité d''exécution par défaut. De 1 : Pas prioritaire urgent à 5 : Très urgent';
COMMENT ON COLUMN remocra.processus_etl_modele.message_succes_corps IS 'Corps du mail en cas de succès du processus.  Laisser NULL pour empêcher la notification. TODO: variable pour lien vers téléchargement de document';
COMMENT ON COLUMN remocra.processus_etl_modele.message_echec_objet IS 'Objet du mail en cas d''échec du processus. Laisser NULL pour empêcher la notification';
COMMENT ON COLUMN remocra.processus_etl_modele.message_echec_corps IS 'Corps du mail en cas d''échec du processus. Laisser NULL pour empêcher la notification';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_statut CASCADE;
CREATE TABLE remocra.processus_etl_statut(
	id bigserial NOT NULL PRIMARY KEY,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL UNIQUE
);
COMMENT ON TABLE remocra.processus_etl_statut IS 'Statut associé à une demande de processus ETL';
COMMENT ON COLUMN remocra.processus_etl_statut.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.processus_etl_statut.code IS 'Code du statut permettant de caractériser l''état d''un processus';
COMMENT ON COLUMN remocra.processus_etl_statut.nom IS 'Libellé du statut';

INSERT INTO remocra.processus_etl_statut(code,nom) VALUES ('A','En attente');
INSERT INTO remocra.processus_etl_statut(code,nom) VALUES ('S','Terminé');
INSERT INTO remocra.processus_etl_statut(code,nom) VALUES ('E','En erreur');
INSERT INTO remocra.processus_etl_statut(code,nom) VALUES ('C','En cours');
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_modele_parametre CASCADE;
CREATE TABLE remocra.processus_etl_modele_parametre(
	id bigserial NOT NULL PRIMARY KEY,
	nom character varying NOT NULL,
	type_valeur character varying NOT NULL,
	obligatoire boolean NOT NULL,
	source_sql character varying,
	source_sql_valeur character varying,
	source_sql_libelle character varying,
	formulaire_etiquette character varying NOT NULL,
	formulaire_type_controle character varying NOT NULL,
	formulaire_num_ordre bigint NOT NULL,
	formulaire_valeur_defaut character varying,
	modele bigint NOT NULL,
	CONSTRAINT processus_etl_modele_parametre_processus_etl_modele FOREIGN KEY (modele) REFERENCES remocra.processus_etl_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_modele_parametre_modele_idx ON remocra.processus_etl_modele_parametre USING btree (modele);

COMMENT ON TABLE remocra.processus_etl_modele_parametre IS 'Paramètre associé à un modèle de requête';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.nom IS 'Nom du paramètre. Sans espace ni caractère accentué. De préférence en majuscules';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.type_valeur IS 'Type de valeur attendue par la requête SQL';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.obligatoire IS 'La saisie d''une valeur pour le paramètre est obligatoire';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.source_sql IS 'Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA pour fournir les informations facilitant ou limitant la saisie de valeurs pour l''utilisateur';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.source_sql_valeur IS 'Colonne de la requête SQL éventuelle contenant la valeur du paramètre';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.source_sql_libelle IS 'Colonne de la requête SQL éventuelle contenant le libellé associé à la valeur du paramètre';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.formulaire_etiquette IS 'Etiquette associée au champ de saisie';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.formulaire_type_controle IS 'Type de contrôle associé au champ de saisie';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.formulaire_num_ordre IS 'Position d''affichage du champ de saisie dans le formulaire';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.formulaire_valeur_defaut IS 'Valeur par défaut proposée dans le champ de saisie';
COMMENT ON COLUMN remocra.processus_etl_modele_parametre.modele IS 'Modèle de processus associé';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_modele_droit CASCADE;
CREATE TABLE remocra.processus_etl_modele_droit(
	modele bigint NOT NULL,
	profil_droit bigint NOT NULL,
	CONSTRAINT processus_etl_modele_droit_pkey PRIMARY KEY (modele, profil_droit),
	CONSTRAINT processus_etl_modele_droit_profil_droit FOREIGN KEY (profil_droit) REFERENCES remocra.profil_droit (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT processus_etl_modele_droit_modele FOREIGN KEY (modele) REFERENCES remocra.processus_etl_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_modele_droit_modele_idx ON remocra.processus_etl_modele_droit USING btree (modele);
CREATE INDEX processus_etl_modele_droit_profil_droit_idx ON remocra.processus_etl_modele_droit USING btree (profil_droit);

COMMENT ON TABLE remocra.processus_etl_modele_droit IS 'Profil de droit autorisé pour exécuter un processus ETL';
COMMENT ON COLUMN remocra.processus_etl_modele_droit.modele IS 'Identifiant du modèle de processus ETL';
COMMENT ON COLUMN remocra.processus_etl_modele_droit.profil_droit IS 'Identifiant du profil de droit autorisé';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl CASCADE;
CREATE TABLE remocra.processus_etl (
	id bigserial NOT NULL PRIMARY KEY,
	modele bigint NOT NULL,
	statut bigint NOT NULL,
	utilisateur bigint NOT NULL,
	priorite integer NOT NULL CHECK(priorite BETWEEN 1 AND 5),
	demande timestamp without time zone NOT NULL,
	execution timestamp without time zone,
        code character varying NOT NULL DEFAULT md5(((('document-processus-etl-'::text || (now())::text) || '-'::text) || floor(((random() * (10000)::double precision) + (1)::double precision)))),
	CONSTRAINT processus_etl_modele FOREIGN KEY (modele) REFERENCES remocra.processus_etl_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT processus_etl_statut FOREIGN KEY (statut) REFERENCES remocra.processus_etl_statut (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT processus_etl_utilisateur FOREIGN KEY (utilisateur) REFERENCES remocra.utilisateur (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_modele_idx ON remocra.processus_etl USING btree (modele);
CREATE INDEX processus_etl_statut_idx ON remocra.processus_etl USING btree (statut);
CREATE INDEX processus_etl_utilisateur_idx ON remocra.processus_etl USING btree (utilisateur);

COMMENT ON TABLE remocra.processus_etl IS 'Demande d''exécution d''un processus ETL dans le cadre d''une tâche planifiée ou d''une action d''un utilisateur';
COMMENT ON COLUMN remocra.processus_etl.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.processus_etl.modele IS 'Modèle de processus associé';
COMMENT ON COLUMN remocra.processus_etl.statut IS 'Etat de la demande';
COMMENT ON COLUMN remocra.processus_etl.utilisateur IS 'Utilisateur demandeur. Dans le cas d''une tâche planifiée, identifiant correspondant à l''utilisateur mentionné dans param_conf';
COMMENT ON COLUMN remocra.processus_etl.priorite IS 'Niveau de priorité. De 1 : Pas prioritaire urgent à 5 : Très urgent';
COMMENT ON COLUMN remocra.processus_etl.demande IS 'Date et heure de création de la demande';
COMMENT ON COLUMN remocra.processus_etl.execution IS 'Date et heure de fin d''exécution du processus';
COMMENT ON COLUMN remocra.processus_etl.code IS 'Code interne';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_parametre CASCADE;
CREATE TABLE remocra.processus_etl_parametre (
	processus bigint NOT NULL,
	parametre bigint NOT NULL,
	valeur character varying NOT NULL,
	CONSTRAINT processus_etl_parametre_pk PRIMARY KEY (processus, parametre),
	CONSTRAINT processus_etl_parametre_processus FOREIGN KEY (processus) REFERENCES remocra.processus_etl (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT processus_etl_parametre_parametre FOREIGN KEY (parametre) REFERENCES remocra.processus_etl_modele_parametre (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_parametre_processus_idx ON remocra.processus_etl_parametre USING btree (processus);
CREATE INDEX processus_etl_parametre_parametre_idx ON remocra.processus_etl_parametre USING btree (parametre);

COMMENT ON TABLE remocra.processus_etl_parametre IS 'Valeur de paramètre renseigné lors d''une demande de processus';
COMMENT ON COLUMN remocra.processus_etl_parametre.processus IS 'Demande de processus associée';
COMMENT ON COLUMN remocra.processus_etl_parametre.parametre IS 'Paramètre de processus associé';
COMMENT ON COLUMN remocra.processus_etl_parametre.valeur IS 'Valeur du paramètre';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_document CASCADE;
CREATE TABLE remocra.processus_etl_document (
	id bigserial NOT NULL PRIMARY KEY,
	document bigint NOT NULL,
	processus bigint NOT NULL,
	code character varying NOT NULL DEFAULT md5(((('document-processus-etl-destinataire-'::text || (now())::text) || '-'::text) || floor(((random() * (10000)::double precision) + (1)::double precision)))),
	nom_destinataire character varying NOT NULL,
	type_destinataire character varying NOT NULL,
	id_destinataire character varying,
	accuse timestamp without time zone,
	CONSTRAINT processus_etl_document_document_fk FOREIGN KEY (document) REFERENCES remocra.document (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT processus_etl_document_processus FOREIGN KEY (processus) REFERENCES remocra.processus_etl (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_document_document_idx ON remocra.processus_etl_document USING btree (document);
CREATE INDEX processus_etl_document_processus_idx ON remocra.processus_etl_document USING btree (processus);

COMMENT ON TABLE remocra.processus_etl_document IS 'Document généré par un processus ETL et mis à disposition d''un destinataire';
COMMENT ON COLUMN remocra.processus_etl_document.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.processus_etl_document.document IS 'Référence au document';
COMMENT ON COLUMN remocra.processus_etl_document.processus IS 'Référence au processus à l''origine de création du document';
COMMENT ON COLUMN remocra.processus_etl_document.code IS 'Code unique exploité pour l''accusé de téléchargement';
COMMENT ON COLUMN remocra.processus_etl_document.nom_destinataire IS 'Indication sur le destinataire du document';
COMMENT ON COLUMN remocra.processus_etl_document.type_destinataire IS 'Indication sur le type de destinataire du courrier. Organisme = ORGANISME, utilisateur = UTILISATEUR, autre = AUTRE';
COMMENT ON COLUMN remocra.processus_etl_document.id_destinataire IS 'Identifiant du destinataire du document dans la base Remocra quand le type = ORGANISME ou UTILISATEUR';
COMMENT ON COLUMN remocra.processus_etl_document.accuse IS 'Date et heure d''accusé de téléchargement';
-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_planification CASCADE;
CREATE TABLE remocra.processus_etl_planification(
	id bigserial NOT NULL PRIMARY KEY,
	modele bigint NOT NULL,
	expression character varying NOT NULL,
        categorie character varying NOT NULL,
        objet_concerne bigint NOT NULL,
	CONSTRAINT processus_etl_planification_modele FOREIGN KEY (modele) REFERENCES remocra.processus_etl_modele (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_planification_modele_idx ON remocra.processus_etl_planification USING btree (modele);

COMMENT ON TABLE remocra.processus_etl_planification IS 'Planification d''un processus ETL. Une demande de processus est automatiquement créée par le planificateur';
COMMENT ON COLUMN remocra.processus_etl_planification.modele IS 'Identifiant du modèle de processus ETL';
COMMENT ON COLUMN remocra.processus_etl_planification.expression IS 'Expression de type CRON';
COMMENT ON COLUMN remocra.processus_etl_planification.categorie IS 'Libellé de catégorie permettant de regrouper ou de filtrer les processus selon un thème';
COMMENT ON COLUMN remocra.processus_etl_planification.objet_concerne IS 'Identifiant de l''objet associé à la planification (crise/hydrant...)';

-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.processus_etl_planification_parametre CASCADE;
CREATE TABLE remocra.processus_etl_planification_parametre (
	processus_etl_planification bigint  NOT NULL,
	parametre bigint NOT NULL,
	valeur character varying NOT NULL,
	CONSTRAINT processus_etl_planification_parametre_pk PRIMARY KEY (processus_etl_planification, parametre),
	CONSTRAINT processus_etl_planification_parametre_processus_etl_planification FOREIGN KEY (processus_etl_planification) REFERENCES remocra.processus_etl_planification (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT processus_etl_planification_parametre_parametre FOREIGN KEY (parametre) REFERENCES remocra.processus_etl_modele_parametre (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX processus_etl_planification_parametre_processus_etl_planification_idx ON remocra.processus_etl_planification_parametre USING btree (processus_etl_planification);
CREATE INDEX processus_etl_planification_parametre_parametre_idx ON remocra.processus_etl_planification_parametre USING btree (parametre);

COMMENT ON TABLE remocra.processus_etl_planification_parametre IS 'Valeur de paramètre par défaut à utiliser lors d''une demande de processus planifié';
COMMENT ON COLUMN remocra.processus_etl_planification_parametre.processus_etl_planification IS 'Processus planifié associé';
COMMENT ON COLUMN remocra.processus_etl_planification_parametre.parametre IS 'Paramètre de processus planifié associé';
COMMENT ON COLUMN remocra.processus_etl_planification_parametre.valeur IS 'Valeur du paramètre';

--////////////////////////////////////////////////////////////////////////////////////

DROP TABLE IF EXISTS remocra.repertoire_lieu CASCADE;
CREATE TABLE remocra.repertoire_lieu (
  id bigserial NOT NULL, -- Identifiant interne auto-généré
  code character varying NOT NULL UNIQUE,
  libelle character varying NOT NULL,
  source_sql character varying NOT NULL,
  source_sql_valeur character varying NOT NULL,
  source_sql_libelle character varying NOT NULL,
  CONSTRAINT repertoire_lieu_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE remocra.repertoire_lieu IS 'Répertoire de lieu. Un répertoire de lieu constitue une source de données géographique dont l''ensemble de données est représenté par une instruction SQL permettant de se localiser facilement dans REMOCRA';
COMMENT ON COLUMN remocra.repertoire_lieu.id IS 'Identifiant interne auto-généré';
COMMENT ON COLUMN remocra.repertoire_lieu.code IS 'Code permettant d''identifier de manière unique et pérenne le repertoire. Ex : COMMUNE, LIEUX_DIT, HYDROGRAPHIE, CADASTRE';
COMMENT ON COLUMN remocra.repertoire_lieu.libelle IS 'Nom du répertoire de lieux : Ex Communes, Cours d''eau, Lieux-dits, Quartiers, Hameaux, etc.';
COMMENT ON COLUMN remocra.repertoire_lieu.source_sql IS 'Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA';
COMMENT ON COLUMN remocra.repertoire_lieu.source_sql_valeur IS 'Colonne de la requête SQL contenant la position du lieu';
COMMENT ON COLUMN remocra.repertoire_lieu.source_sql_libelle IS 'Colonne de la requête SQL contenant le libellé associé à la position du lieu. La recherche s''effectue sur les valeurs contenues dans cette colonne sous la forme LIKE%mon_texte%';
---///////////////////////////////////////////////////////////////////////////////////////

DROP TABLE IF EXISTS remocra.crise_repertoire_lieu CASCADE;
CREATE TABLE remocra.crise_repertoire_lieu(
  crise bigint NOT NULL, 
  repertoire_lieu bigint NOT NULL,
  CONSTRAINT crise_repertoire_lieu_pkey PRIMARY KEY (crise, repertoire_lieu),
  CONSTRAINT crise_repertoire_lieu_repertoire_lieu_fk FOREIGN KEY (repertoire_lieu) REFERENCES remocra.repertoire_lieu (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT crise_repertoire_lieu_crise_fk FOREIGN KEY (crise) REFERENCES remocra.crise (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
COMMENT ON TABLE remocra.crise_repertoire_lieu IS 'Gestion de crise : répertoire de lieux mobilisable dans le cadre d''une action "Zoomer sur..". Associé à un épisode de crise';
COMMENT ON COLUMN remocra.crise_repertoire_lieu.crise IS 'Identifiant de la crise associée';
COMMENT ON COLUMN remocra.crise_repertoire_lieu.repertoire_lieu IS 'Identifiant du répertoire de lieu associé';

INSERT INTO remocra.repertoire_lieu (code,libelle,source_sql,source_sql_valeur,source_sql_libelle) VALUES ('COMMUNE','Communes', 'SELECT id, nom, St_SetSRID(CAST(CAST(geometrie AS box2D) AS geometry),2154) AS geometrie FROM remocra.commune ORDER BY nom','geometrie','nom');
INSERT INTO remocra.repertoire_lieu (code,libelle,source_sql,source_sql_valeur,source_sql_libelle) VALUES ('CADASTRE','Cadastre', 'SELECT p.id, (c.insee||'' ''|| s.numero ||'' '' ||p.numero) AS parcelle, St_SetSRID(CAST(CAST(geometrie AS box2D) AS geometry),2154) AS geometrie FROM remocra.cadastre_parcelle p JOIN remocra.cadastre_section s ON (s.id = p.section) JOIN remocra.commune c ON(c.id = s.commune) ORDER BY parcelle','geometrie','parcelle');
INSERT INTO remocra.repertoire_lieu (code,libelle,source_sql,source_sql_valeur,source_sql_libelle) VALUES ('PEI','Points d''eau', 'SELECT id, numero, St_SetSRID(CAST(CAST(geometrie AS box2D) AS geometry),2154) AS geometrie FROM remocra.hydrant ORDER BY numero','geometrie','numero');


-- ////////////////////////////////////////////////////////////////////////////////////
DROP TABLE IF EXISTS remocra.ogc_source CASCADE;
CREATE TABLE remocra.ogc_source(
	id bigserial NOT NULL PRIMARY KEY,
	url character varying NOT NULL
);

COMMENT ON TABLE remocra.ogc_source IS 'Serveur de données géographique interrogeable à la norme OGC';
COMMENT ON COLUMN remocra.ogc_source.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.ogc_source.url IS 'Url d''accès au service. Doit fournir des informations en retour d''une requête de type GetCapabilities sur un service de type WMS ou WMTS';

DROP TABLE IF EXISTS remocra.ogc_service CASCADE;
CREATE TABLE remocra.ogc_service(
	id bigserial NOT NULL PRIMARY KEY,
	type_service character varying NOT NULL CHECK(type_service IN('WMS','WMTS')),
	nom character varying NOT NULL,
	description character varying,
	ogc_source bigint NOT NULL,
	CONSTRAINT ogc_source_fk FOREIGN KEY (ogc_source) REFERENCES remocra.ogc_source (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX ogc_service_ogc_source_idx ON remocra.ogc_service USING btree (ogc_source);

COMMENT ON TABLE remocra.ogc_service IS 'Service de données mobilisable sur un serveur OGC';
COMMENT ON COLUMN remocra.ogc_service.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.ogc_service.type_service IS 'Type de service OGC proposé par le serveur OGC';
COMMENT ON COLUMN remocra.ogc_service.nom IS 'Titre associé aux métadonnées du service';
COMMENT ON COLUMN remocra.ogc_service.description IS 'Résumé associé aux métadonnées du service';
COMMENT ON COLUMN remocra.ogc_service.ogc_source IS 'Serveur OGC associé';

DROP TABLE IF EXISTS remocra.ogc_couche CASCADE;
CREATE TABLE remocra.ogc_couche(
	id bigserial NOT NULL PRIMARY KEY,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL,
	titre character varying NOT NULL,
	definition character varying NOT NULL,
	ogc_service bigint NOT NULL,
	CONSTRAINT ogc_service_fk FOREIGN KEY (ogc_service) REFERENCES remocra.ogc_service (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX ogc_couche_ogc_service_idx ON remocra.ogc_couche USING btree (ogc_service);

COMMENT ON TABLE remocra.ogc_couche IS 'Couche de données mobilisable sur un serveur de données OGC pour un protocole (service) donné';
COMMENT ON COLUMN remocra.ogc_couche.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.ogc_couche.code IS 'Code permettant d''identifier dans REMOCRA un layer de manière unique basé sur l''URL, le type de service et le nom de la couche';
COMMENT ON COLUMN remocra.ogc_couche.nom IS 'Nom de la couche dans les URLs';
COMMENT ON COLUMN remocra.ogc_couche.nom IS 'Titre associé aux métadonnées de la couche';
COMMENT ON COLUMN remocra.ogc_couche.definition IS 'Définition JSON REMOCRA de la couche pour le type de service';
COMMENT ON COLUMN remocra.ogc_couche.ogc_service IS 'Service OGC associé';


--////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION remocra.trg_crise() RETURNS trigger AS
$BODY$
DECLARE

	r_new record;
	r_old record;

	r_statut record;
	r_modele_message record;

	v_statut character varying;
	v_message_count integer;
	v_code_message character varying := null;
	v_message character varying := null;
	v_communes character varying;
	v_crise_parente character varying;
	v_date_message timestamp with time zone := null;
    
    
BEGIN

	IF (TG_OP = 'UPDATE') THEN

		r_new = NEW;
		r_old = OLD;

		-- Statut de la crise
		SELECT code INTO v_statut FROM remocra.type_crise_statut WHERE id = r_new.statut;
			
		-- Code du modèle de message type en fonction du statut et des dates
		-- Codes pour activation ou redéfinition de crise
		IF (v_statut = 'EN_COURS') THEN
			-- Si pas de date de redéfinition, forcément une activation de crise.
			-- pas réalisé sur INSERT car attente des assocations crise/communes

			--Récupération du modèle de message
			SELECT count(*) INTO v_message_count FROM remocra.crise_suivi WHERE crise = r_new.id;
			
			IF (r_new.redefinition IS NULL AND v_message_count = 0) THEN 
				v_code_message := 'CRISE_CREATION';
				v_date_message := r_new.activation;
				
			ELSIF ((r_new.redefinition IS NOT NULL AND r_old.redefinition IS NULL) OR (r_new.redefinition <> r_old.redefinition)) THEN
			-- Attention : dans l'application, ne changer la date de redefinition que si le territoire change
				v_code_message := 'CRISE_REDEFINITION';
				v_date_message := r_new.redefinition;
				
			END IF;
		-- Code pour clôture de crise
		ELSIF (v_statut = 'FUSIONNE' AND r_old.crise_parente IS NULL) THEN
			v_code_message := 'CRISE_FUSION';
			v_date_message := r_new.cloture;
			
		-- Code pour clôture de crise
		ELSIF (v_statut = 'TERMINE' AND r_old.cloture IS NULL) THEN
			v_code_message := 'CRISE_CLOTURE';
			v_date_message := r_new.cloture;
		END IF;

		IF (v_code_message IS NOT NULL) THEN

			--Récupération du modèle de message
			SELECT objet,corps,importance,tags INTO r_modele_message FROM remocra.crise_suivi_message_modele WHERE code = v_code_message;

			IF (v_statut = 'FUSIONNE') THEN

				-- Crise parente
				SELECT
					nom INTO v_crise_parente
				FROM
					remocra.crise
				WHERE
					id =  r_new.crise_parente;

				-- Préparation du message
				v_message := replace(r_modele_message.corps,'[CRISE_PARENTE]', v_crise_parente);

			ELSE
			
				-- Communes concernées par la crise
				SELECT
					array_to_string(array_agg(c.nom),', ') INTO v_communes
				FROM
					remocra.crise_commune cc
					JOIN (SELECT id, nom FROM remocra.commune ORDER BY nom) c ON (c.id = cc.commune)
				WHERE
					crise =  r_new.id
				GROUP BY
					cc.crise;

				-- Préparation du message
				v_message := replace(r_modele_message.corps,'[COMMUNES]', v_communes);
					
			END IF;
					
			--Insertion du message
			INSERT INTO remocra.crise_suivi	(
				origine,
				crise,
				objet,
				message,
				creation,
				importance,
				tags
			) VALUES (
				'Gestionnaire de crise',
				r_new.id,
				r_modele_message.objet,
				v_message,
				v_date_message,
				r_modele_message.importance,
				r_modele_message.tags
			);

		END IF;

	END IF;
   
    RETURN r_new;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER trig_aui AFTER UPDATE ON remocra.crise FOR EACH ROW EXECUTE PROCEDURE remocra.trg_crise();

--////////////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION remocra.trg_crise_document() RETURNS trigger AS
$BODY$
DECLARE

	r_new record;
	r_old record;

	r_modele_message record;
	r_document record;
	r_evenement record;
	v_message character varying := null;
BEGIN

	IF (TG_OP = 'INSERT') THEN

		r_new = NEW;

		-- Récupération du modèle de message en fonction de l'association du document à la crise ou à un évènement spécifique
		IF (r_new.evenement IS NULL) THEN
			SELECT objet,corps,importance,tags INTO r_modele_message FROM remocra.crise_suivi_message_modele WHERE code = 'CRISE_DOCUMENT_AJOUT';
		ELSE
			SELECT objet,corps,importance,tags INTO r_modele_message FROM remocra.crise_suivi_message_modele WHERE code = 'EVT_DOCUMENT_AJOUT';

			-- Récupération de l'évènement concerné
				SELECT
					date::timestamp with time zone AS association,
					base.url || doc.code AS url
				INTO
					r_document
				FROM
					remocra.document doc,
					(SELECT replace(valeur || '/crise/evenements/','//','/') AS url FROM remocra.param_conf WHERE cle = 'PDI_URL_SITE') AS base
				WHERE
					doc.id = r_new.document;
			
			
		END IF;

		-- Récupération des infos du document
		SELECT
			date::timestamp with time zone AS association,
			base.url || doc.code AS url
		INTO
			r_document
		FROM
			remocra.document doc,
			(SELECT replace(valeur || '/telechargement/document/','//','/') AS url FROM remocra.param_conf WHERE cle = 'PDI_URL_SITE') AS base
		WHERE
			doc.id = r_new.document;

		--Contextualisation du message
		v_message:= replace(r_modele_message.corps,'[DOCUMENT_TYPE]',r_new.sous_type);
		v_message:= replace(v_message,'[DOCUMENT_URL]',r_document.url);

		--Insertion du message
		INSERT INTO remocra.crise_suivi	(
			origine,
			crise,
			objet,
			message,
			creation,
			importance,
			tags
		) VALUES (
			'Gestionnaire de crise',
			r_new.crise,
			r_modele_message.objet,
			v_message,
			r_document.association,
			r_modele_message.importance,
			r_modele_message.tags
		);

	ELSIF (TG_OP = 'DELETE') THEN

		

	END IF;
   
    RETURN r_new;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER trig_au AFTER INSERT OR DELETE ON remocra.crise_document FOR EACH ROW EXECUTE PROCEDURE remocra.trg_crise_document();

--/////////////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION remocra.trg_crise_evenement() RETURNS trigger AS
$BODY$
DECLARE

	r_new record;
	r_old record;

	r_modele_message record;
	r_complement record;

	v_communes character varying;
	v_complement character varying;
	v_nature character varying;
	v_message_count integer;
	v_code_message character varying := null;
	v_message character varying := null;
	v_date_message timestamp with time zone := null;
    
BEGIN

	IF (TG_OP = 'UPDATE') THEN

		r_new = NEW;
		r_old = OLD;

		--Comptage du nombre de messages
		SELECT count(*) INTO v_message_count FROM remocra.crise_suivi WHERE evenement = r_new.id;
		
		-- Si 0 : Nouvel évènement
		IF (v_message_count = 0) THEN 
		
			v_code_message := 'EVT_CREATION';
			v_date_message := r_new.constat;
			
		-- Sinon, mise à jour ou clôture 
		ELSE
		
			-- Si pas de date de clôture => Redéfinition
			IF (r_new.cloture IS NULL) THEN
	
				v_code_message := 'EVT_REDEFINITION';
				v_date_message := r_new.redefinition;
			
			-- Si clôture => Redéfinition
			ELSE

				v_code_message := 'EVT_CLOTURE';
				v_date_message := r_new.cloture;
			
			END IF;
			
		END IF;
		
		-- Si code de message
		IF (v_code_message IS NOT NULL) THEN
		
			--Récupération du modèle de message
			SELECT objet,corps,importance,tags INTO r_modele_message FROM remocra.crise_suivi_message_modele WHERE code = v_code_message;

			--Récupération du libellé de nature
			SELECT nom INTO v_nature FROM remocra.type_crise_nature_evenement WHERE id = r_new.nature_evenement;
			
			--Récupération des communes impactées par la géométrie si géométrie
			--pour l'évènement
			IF (r_new.geometrie IS NOT NULL) THEN
				SELECT
					array_to_string(array_agg(c.nom),', ') INTO v_communes
				FROM
					remocra.crise_evenement ce
					JOIN (SELECT id, nom, geometrie FROM remocra.commune ORDER BY nom) c ON (c.geometrie && ce.geometrie AND st_intersects(c.geometrie, ce.geometrie))
				WHERE
					ce.id =  r_new.id
				GROUP BY
					ce.id;
			ELSE
				v_communes := 'Non localisé';
			END IF;
			
			--Récupération des informations complémentaires
			v_complement := '';
			FOR r_complement IN 
				SELECT
					tcpe.formulaire_etiquette AS clef,
					ec.valeur_formatee AS valeur
				FROM
					remocra.crise_evenement e
					JOIN remocra.type_crise_propriete_evenement tcpe ON (tcpe.nature_evenement = e.nature_evenement)
					LEFT JOIN remocra.crise_evenement_complement ec ON(ec.propriete_evenement = tcpe.id)
				WHERE
					e.id = r_new.id
					
				ORDER BY
					tcpe.formulaire_num_ordre
			LOOP
				v_complement :=  v_complement || E'\n' || r_complement.clef ||  ' : ' ||  COALESCE(r_complement.valeur,'-');
	
			END LOOP;

			--Contextualisation du message
			v_message:= replace(r_modele_message.corps,'[EVT_NATURE]',v_nature);
			v_message:= replace(v_message,'[EVT_TITRE]',r_new.nom);
			v_message:= replace(v_message,'[EVT_DESCRIPTION]',r_new.description);
			v_message:= replace(v_message,'[EVT_COMMUNES]',v_communes);
			v_message:= replace(v_message,'[EVT_COMPLEMENT]',v_complement);
		
			--Insertion du message
			INSERT INTO remocra.crise_suivi	(
				origine,
				objet,
				message,
				creation,
				importance,
				tags,
				crise,
				evenement
			) VALUES (
				'Gestionnaire de crise',
				r_modele_message.objet,
				v_message,
				v_date_message,
				r_modele_message.importance,
				r_modele_message.tags,
				r_new.crise,
				r_new.id
			);
		
		END IF;

	END IF;
   
    RETURN r_new;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER trig_aui AFTER UPDATE ON remocra.crise_evenement FOR EACH ROW EXECUTE PROCEDURE remocra.trg_crise_evenement();

--///////////////////////////////////////////////////////////////////////////////////////


INSERT INTO remocra.processus_etl_modele (
    categorie,
    code,
    libelle,
    description,
    pdi_type,
    pdi_chemin,
    pdi_nom,
    priorite,
    message_succes_objet,
    message_succes_corps,
    message_echec_objet,
    message_echec_corps
) VALUES(
    'GESTION_CRISE',
    'INTERVENTIONS_VERS_REMOCRA',
    'Synchronisation des interventions',
    'Récupération des interventions du SDIS en lien avec la crise en cours',
    'J',
    '/var/remocra/pdi/traitements_sdis/83/crise',
    'interventions_vers_remocra.kjb',
    5,
    null,
    null,
    'REMOCRA / Gestion de crise / Erreur lors de la synchronisation des interventions',
    'La synchronisation des interventions liées à la crise ${CODE_CRISE} vers REMOCRA a echoué'
);

INSERT INTO remocra.processus_etl_modele_parametre(
    nom,
    type_valeur,
    obligatoire,
    source_sql,
    source_sql_valeur,
    source_sql_libelle,
    formulaire_etiquette,
    formulaire_type_controle,
    formulaire_num_ordre,
    formulaire_valeur_defaut,
    modele
) VALUES(
    'CODE_CRISE',
    'character varying',
    true,
    null,
    null,
    null,
    'Code de CRISE système d''alerte',
    'textfield',
    1,
    null,
    1
);

INSERT INTO remocra.processus_etl_modele (
    categorie,
    code,
    libelle,
    description,
    pdi_type,
    pdi_chemin,
    pdi_nom,
    priorite,
    message_succes_objet,
    message_succes_corps,
    message_echec_objet,
    message_echec_corps
) VALUES(
    'GESTION_CRISE',
    'VIGICRUE_TRONCON_NIVEAU_VIGILANCE',
    'Niveau de vigilance Vigicrue',
    'Récupération des tronçons du réseau de vigilance de Vigicrue et des niveaux de vigilance associés',
    'J',
    '/var/remocra/pdi/traitements_sdis/83/crise',
    'vigicrue_troncons_vigilance.kjb',
    5,
    null,
    null,
    'REMOCRA / Gestion de crise / Erreur lors de la synchronisation des informations Vigicrue',
    'La synchronisation des informations vigicrue (tronçons et niveaux associés) liées à la crise ${CODE_CRISE} vers REMOCRA a échoué'
);

INSERT INTO remocra.processus_etl_modele (
    categorie,
    code,
    libelle,
    description,
    pdi_type,
    pdi_chemin,
    pdi_nom,
    priorite,
    message_succes_objet,
    message_succes_corps,
    message_echec_objet,
    message_echec_corps
) VALUES(
    'GESTION_CRISE',
    'VIGICRUE_STATION_HAUTEUR',
    'Hauteurs d''eau Vigicrue',
    'Récupération des hauteurs d''eau aux stations du réseau de vigilance de Vigicrue',
    'J',
    '/var/remocra/pdi/traitements_sdis/83/crise',
    'vigicrue_stations_hauteur.kjb',
    5,
    null,
    null,
    'REMOCRA / Gestion de crise / Erreur lors de la synchronisation des informations Vigicrue',
    'La synchronisation des informations Vigicrue (hauteurs aux stations) liées à la crise ${CODE_CRISE} vers REMOCRA a echoué'
);

--////////////////////////////////////////////////////////////////////////////////////
TRUNCATE remocra.type_crise CASCADE;
TRUNCATE remocra.type_crise_categorie_evenement CASCADE;

-- ////////////////////////////////////////////////////////////////////////////////////
INSERT INTO remocra.type_crise(code,nom) VALUES ('INONDATION','Inondation');
INSERT INTO remocra.type_crise(code,nom) VALUES ('CANICULE','Canicule');
INSERT INTO remocra.type_crise(code,nom) VALUES ('INDUSTRIEL','Accident industriel');
INSERT INTO remocra.type_crise(code,nom) VALUES ('ATTENTAT','Attentat');
INSERT INTO remocra.type_crise(code,nom) VALUES ('SEISME','Tremblement de terre');
INSERT INTO remocra.type_crise(code,nom) VALUES ('TEMPETE','Tempête');

-- ////////////////////////////////////////////////////////////////////////////////////
INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('CATEGORIE_TEST','Catégorie de test');
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('NATURE_TEST_SANS_GEOM','Evènement sans géométrie', NULL, (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'CATEGORIE_TEST'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('NATURE_TEST_POINT','Evènement ponctuel', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'CATEGORIE_TEST'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('NATURE_TEST_LINESTRING','Evènement linéaire', 'LINESTRING', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'CATEGORIE_TEST'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('NATURE_TEST_POLYGON','Evènement surfacique', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'CATEGORIE_TEST'));

INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('checkbox_att','boolean', true, null,null,null,'Case à cocher','checkbox',1,'true', (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('combo_att','character varying', true, 'SELECT insee,nom FROM remocra.commune ORDER BY nom;', 'insee','nom','Liste déroulante','combo',2,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datefield_att','date', true, null,null, null,'Calendrier','datefield',3,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datetimefield_att','timestamp', true, null,null, null,'Calendrier avec heure','datetimefield',4,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('timefield_att','time', true, null,null, null,'Heure','timefield',5,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_float_att','double precision', true, null,null, null,'Valeur numérique réèlle','numberfield',6,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_int_att','integer', true, null,null, null,'Valeur numérique entière','numberfield',7,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_SANS_GEOM'));


INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('checkbox_att','boolean', true, null,null,null,'Case à cocher','checkbox',1,'true', (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('combo_att','character varying', true, 'SELECT insee,nom FROM remocra.commune ORDER BY nom;', 'insee','nom','Liste déroulante','combo',2,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datefield_att','date', true, null,null, null,'Calendrier','datefield',3,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datetimefield_att','timestamp', true, null,null, null,'Calendrier avec heure','datetimefield',4,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('timefield_att','time', true, null,null, null,'Heure','timefield',5,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_float_att','double precision', true, null,null, null,'Valeur numérique réèlle','numberfield',6,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_int_att','integer', true, null,null, null,'Valeur numérique entière','numberfield',7,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POINT'));


INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('checkbox_att','boolean', true, null,null,null,'Case à cocher','checkbox',1,'true', (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('combo_att','character varying', true, 'SELECT insee,nom FROM remocra.commune ORDER BY nom;', 'insee','nom','Liste déroulante','combo',2,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datefield_att','date', true, null,null, null,'Calendrier','datefield',3,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datetimefield_att','timestamp', true, null,null, null,'Calendrier avec heure','datetimefield',4,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('timefield_att','time', true, null,null, null,'Heure','timefield',5,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_float_att','double precision', true, null,null, null,'Valeur numérique réèlle','numberfield',6,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_int_att','integer', true, null,null, null,'Valeur numérique entière','numberfield',7,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_LINESTRING'));

INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('checkbox_att','boolean', true, null,null,null,'Case à cocher','checkbox',1,'true', (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('combo_att','character varying', true, 'SELECT insee,nom FROM remocra.commune ORDER BY nom;', 'insee','nom','Liste déroulante','combo',2,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datefield_att','date', true, null,null, null,'Calendrier','datefield',3,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('datetimefield_att','timestamp', true, null,null, null,'Calendrier avec heure','datetimefield',4,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('timefield_att','time', true, null,null, null,'Heure','timefield',5,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_float_att','double precision', true, null,null, null,'Valeur numérique réèlle','numberfield',6,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('numberfield_int_att','integer', true, null,null, null,'Valeur numérique entière','numberfield',7,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'NATURE_TEST_POLYGON'));


/*INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('METEOROLOGIE','Météorologie');
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('METEOROLOGIE_CHGT_NIVEAU_METEO_FRANCE','Changement de niveau de vigilance Météo France', null, (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'METEOROLOGIE'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('niveau_vigilance','character varying', true, 'SELECT
	code,
	libelle
FROM (
SELECT ''VERT'' AS code, ''Vert'' AS libelle, 1::integer AS position 
UNION
SELECT ''JAUNE'' AS code, ''Jaune'' AS libelle, 2::integer AS position 
UNION
SELECT ''ORANGE'' AS code, ''Orange'' AS libelle, 3::integer AS position 
UNION
SELECT ''ROUGE'' AS code, ''Rouge'' AS libelle, 4::integer AS position
) AS niveaux ORDER BY position;','code','libelle','Niveau de vigilance','combo',1,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'METEOROLOGIE_CHGT_NIVEAU_METEO_FRANCE'));*/

INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('HYDROLOGIE','Hydrologie');

INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('HYDROLOGIE_EMBACLE','Embâcle', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'HYDROLOGIE'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('deblaiement','boolean', true, null,null,null,'Déblaiement nécessaire ?','checkbox',1,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'HYDROLOGIE_EMBACLE'));

INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('HYDROLOGIE_EROSION','Zone d''érosion', 'LINESTRING', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'HYDROLOGIE'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('renforcement','boolean', true, null,null,null,'Renforcement nécessaire ?','checkbox',1,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'HYDROLOGIE_EROSION'));

INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('HYDROLOGIE_ZONE_INONDEE','Zone inondée', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'HYDROLOGIE'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('pop_impactee','integer', true, null,null,null,'Population impactée','numberfield',1,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'HYDROLOGIE_ZONE_INONDEE'));

INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('HYDROLOGIE_VAGUE_SUBMERSIVE','Vague submersive', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'HYDROLOGIE'));
INSERT INTO remocra.type_crise_propriete_evenement(nom, type_valeur, obligatoire, source_sql, source_sql_valeur, source_sql_libelle ,formulaire_etiquette, formulaire_type_controle, formulaire_num_ordre, formulaire_valeur_defaut, nature_evenement)
VALUES ('pop_impactee','integer', true, null,null,null,'Population impactée','numberfield',1,null, (SELECT id FROM remocra.type_crise_nature_evenement WHERE code = 'HYDROLOGIE_VAGUE_SUBMERSIVE'));

INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('RESEAU_TRANSPORT','Réseaux de transport');

INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_TRANSPORT_ROUTE_ACCIDENT','Accident route / autoroute', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_TRANSPORT_ROUTE_BARREE','Route barrée', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_TRANSPORT_ROUTE_DEVIATION','Déviation de route', 'LINESTRING', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_TRANSPORT_FER','Accident voie ferrée', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_TRANSPORT_FER_BARREE','Voie ferrée fermée', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'));

INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('RESEAU_ENERGIE_EAU','Réseaux d''énergie / eau');
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_ENERGIE_EAU_SANS_ELEC','Secteur sans électricité', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_ENERGIE_EAU_SANS_GAZ','Secteur sans gaz', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_ENERGIE_EAU_SANS_EAU','Secteur sans eau', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'));

INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_ENERGIE_RUPTURE_ELEC','Rupture de ligne éléctrique', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_ENERGIE_RUPTURE_GAZ','Rupture canalisation de gaz', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_ENERGIE_RUPTURE_EAU','Rupture canalisation d''eau', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'));

INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('RESEAU_COMMUNICATION','Moyens de communication');
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_COMMUNICATION_SANS_FIXE','Secteur sans téléphone fixe', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'));
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('RESEAU_COMMUNICATION_SANS_TM','Secteur sans téléphone mobile', 'POLYGON', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'));

INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('BIENS_ET_PERSONNES','Biens et personnes');
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('BIENS_ET_PERSONNES_EXPLOSION','Explosion', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'BIENS_ET_PERSONNES'));

INSERT INTO remocra.type_crise_categorie_evenement(code,nom) VALUES ('ENVIRONNEMENT','Environnement');
INSERT INTO remocra.type_crise_nature_evenement (code,nom, type_geometrie, categorie_evenement) VALUES ('ENVIRONNEMENT_POLLUTION_EAU','Point de pollution des eaux', 'POINT', (SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'ENVIRONNEMENT'));

--INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'METEOROLOGIE'), (SELECT id FROM remocra.type_crise WHERE code = 'INONDATION'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'HYDROLOGIE'), (SELECT id FROM remocra.type_crise WHERE code = 'INONDATION'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'), (SELECT id FROM remocra.type_crise WHERE code = 'INONDATION'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'), (SELECT id FROM remocra.type_crise WHERE code = 'INONDATION'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'), (SELECT id FROM remocra.type_crise WHERE code = 'INONDATION'));

--INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'METEOROLOGIE'), (SELECT id FROM remocra.type_crise WHERE code = 'CANICULE'));

INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'BIENS_ET_PERSONNES'), (SELECT id FROM remocra.type_crise WHERE code = 'INDUSTRIEL'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'ENVIRONNEMENT'), (SELECT id FROM remocra.type_crise WHERE code = 'INDUSTRIEL'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'), (SELECT id FROM remocra.type_crise WHERE code = 'INDUSTRIEL'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'), (SELECT id FROM remocra.type_crise WHERE code = 'INDUSTRIEL'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'), (SELECT id FROM remocra.type_crise WHERE code = 'INDUSTRIEL'));

INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'BIENS_ET_PERSONNES'), (SELECT id FROM remocra.type_crise WHERE code = 'ATTENTAT'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'), (SELECT id FROM remocra.type_crise WHERE code = 'ATTENTAT'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'), (SELECT id FROM remocra.type_crise WHERE code = 'ATTENTAT'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'), (SELECT id FROM remocra.type_crise WHERE code = 'ATTENTAT'));

INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'), (SELECT id FROM remocra.type_crise WHERE code = 'SEISME'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'), (SELECT id FROM remocra.type_crise WHERE code = 'SEISME'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'), (SELECT id FROM remocra.type_crise WHERE code = 'SEISME'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'BIENS_ET_PERSONNES'), (SELECT id FROM remocra.type_crise WHERE code = 'SEISME'));

INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'HYDROLOGIE'), (SELECT id FROM remocra.type_crise WHERE code = 'TEMPETE'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_TRANSPORT'), (SELECT id FROM remocra.type_crise WHERE code = 'TEMPETE'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_COMMUNICATION'), (SELECT id FROM remocra.type_crise WHERE code = 'TEMPETE'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'RESEAU_ENERGIE_EAU'), (SELECT id FROM remocra.type_crise WHERE code = 'TEMPETE'));
INSERT INTO remocra.type_crise_evenement_crise (categorie_evenement, type_crise) VALUES ((SELECT id FROM remocra.type_crise_categorie_evenement WHERE code = 'BIENS_ET_PERSONNES'), (SELECT id FROM remocra.type_crise WHERE code = 'TEMPETE'));
-- ////////////////////////////////////////////////////////////////////////////////////

INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('CRISE_CREATION',
'Activation de crise',
E'Territoire concerné : [COMMUNES]',5,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('CRISE_CLOTURE',
'Clôture de crise',
E'Territoire concerné : [COMMUNES]',5,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('CRISE_REDEFINITION',
'Reconfiguration de la crise',
E'Territoire concerné : [COMMUNES]',5,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('CRISE_FUSION',
'Fusion de crise',
E'Crise parente : [CRISE_PARENTE]',5,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('CRISE_DOCUMENT_AJOUT',
'Association d''un document',
E'Type : [DOCUMENT_TYPE]
Lien de téléchargement : [DOCUMENT_URL]',1,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('CRISE_DOCUMENT_SUPPRESSION',
'Suppression d''un document',
E'Type : [DOCUMENT_TYPE]',1,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('EVT_CREATION',
'Création d''évènement',
E'Type [EVT_NATURE]
Titre : [EVT_TITRE]
Description : [EVT_DESCRIPTION]
Territoire concerné : [COMMUNES]
[EVT_COMPLEMENT]',5,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags)VALUES ('EVT_REDEFINITION',
'Redéfinition d''évènement',
E'Type [EVT_NATURE]
Titre : [EVT_TITRE]
Description : [EVT_DESCRIPTION]
Territoire concerné : [COMMUNES]
[EVT_COMPLEMENT]',4,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags)VALUES ('EVT_CLOTURE',
'Clôture d''évènement',
E'Type [EVT_NATURE]
Titre : [EVT_TITRE]
Description : [EVT_DESCRIPTION]
Territoire concerné : [EVT_COMMUNES]
[EVT_COMPLEMENT]',5,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('EVT_DOCUMENT_AJOUT',
'Association d''un document à un évènement',
E'Type : [DOCUMENT_TYPE]
Evènement : [EVENEMENT_URL]
Lien de téléchargement : [DOCUMENT_URL]',1,null);
INSERT INTO remocra.crise_suivi_message_modele(code,objet,corps,importance,tags) VALUES ('EVT_DOCUMENT_SUPPRESSION',
'Suppression d''un document',
E'Type : [DOCUMENT_TYPE]
Evènement : [EVENEMENT_URL]',1,null);


-- Contenu réel du patch fin
--------------------------------------------------

commit;

