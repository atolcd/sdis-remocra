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
    numero_patch := 83;
    description_patch := 'Schéma prevarisc et paramètres de synchronisation';

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

-- Création du schéma prevarisc contenant vues et tables nécsessaire à la synchronisation
DROP SCHEMA IF EXISTS prevarisc CASCADE;
CREATE SCHEMA prevarisc;

CREATE TABLE prevarisc.adressecommune (
	numinsee_commune character varying PRIMARY KEY,
	libelle_commune character varying NOT NULL
);
COMMENT ON TABLE prevarisc.adressecommune
  IS 'Table tampon pour mise à jour des communes de Prevarisc';
COMMENT ON COLUMN prevarisc.adressecommune.numinsee_commune IS 'Numéro INSEE de la commune';
COMMENT ON COLUMN prevarisc.adressecommune.libelle_commune IS 'Nom de la commune';

CREATE TABLE prevarisc.adresserue (
	id_rue bigint PRIMARY KEY,
	libelle_rue character varying NOT NULL,
	numinsee_commune character varying NOT NULL
);
COMMENT ON TABLE prevarisc.adresserue
  IS 'Table tampon pour mise à jour des adresse de Prevarisc';
COMMENT ON COLUMN prevarisc.adresserue.id_rue IS 'Identifiant de la voie';
COMMENT ON COLUMN prevarisc.adresserue.libelle_rue IS 'Nom de la voie';
COMMENT ON COLUMN prevarisc.adresserue.numinsee_commune IS 'Numéro INSEE de la commune';

CREATE TABLE prevarisc.etablissement (
	id_etablissement integer PRIMARY KEY,
	nom character varying NOT NULL,
	longitude double precision,
	latitude double precision,
	rue character varying NOT NULL,
	insee character varying NOT NULL,
	date_maj date
);
COMMENT ON TABLE prevarisc.etablissement
  IS 'Table tampon des établissement pour mise à jour des alertes dans Remocra';
COMMENT ON COLUMN prevarisc.etablissement.id_etablissement IS 'Identifiant de l''établissement';
COMMENT ON COLUMN prevarisc.etablissement.nom IS 'Nom de l''établissement';
COMMENT ON COLUMN prevarisc.etablissement.longitude IS 'Longitude de l''établissement';
COMMENT ON COLUMN prevarisc.etablissement.latitude IS 'Latitude de l''établissement';
COMMENT ON COLUMN prevarisc.etablissement.rue IS 'Nom de la rue de l''établissement';
COMMENT ON COLUMN prevarisc.etablissement.insee IS 'Numéro INSEE de la commune de l''établissement';
COMMENT ON COLUMN prevarisc.etablissement.date_maj IS 'Date de mise à jour de l''établissement';

CREATE OR REPLACE VIEW prevarisc.remocra_rue AS 
 SELECT c.insee AS numinsee_commune, v.nom AS libelle_rue
   FROM remocra.voie v
   JOIN remocra.commune c ON c.id = v.commune;

ALTER TABLE prevarisc.remocra_rue
  OWNER TO postgres;
  
CREATE OR REPLACE VIEW prevarisc.remocra_commune AS 
 SELECT commune.insee AS numinsee_commune, commune.nom AS libelle_commune
   FROM remocra.commune;

ALTER TABLE prevarisc.remocra_commune
  OWNER TO postgres;

SELECT pg_catalog.setval('remocra.type_alerte_elt_id_seq', (select max(id) from remocra.type_alerte_elt));
INSERT INTO remocra.type_alerte_elt (code,nom) VALUES('PREVARISC','Elément de Prevarisc');
SELECT pg_catalog.setval('remocra.sous_type_alerte_elt_id_seq', (select max(id) from remocra.sous_type_alerte_elt));
INSERT INTO remocra.sous_type_alerte_elt (code,nom,type_geom,type_alerte_elt) VALUES('PREVARISC_ETAB','Etablissement',0, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'PREVARISC'));

-- Paramètre pour la synchronisation avec Prevarisc
INSERT INTO remocra.param_conf(
            cle, description, valeur, version, nomgroupe)
    VALUES ('PDI_PREVARISC_UTILISATEUR_ID', 'Identifiant de l''utilisateur utilisé comme créateur des alertes venant de Prevarisc', COALESCE((SELECT id FROM remocra.utilisateur WHERE identifiant = 'remocra-adm-app')::text, ''), 1, 'Synchro SIG');
	
INSERT INTO remocra.param_conf(
            cle, description, valeur, version, nomgroupe)
    VALUES ('PDI_FTP_FICHIER_REMOCRA_VERS_PREVARISC', 'Nom du fichier ZIP généré par Remocra pour mettre à jour Prevarisc', 'REMOCRA_VERS_PREVARISC.ZIP', 1, 'Synchro SIG');
	
INSERT INTO remocra.param_conf(
            cle, description, valeur, version, nomgroupe)
    VALUES ('PDI_FTP_DOSSIER_PREVARISC', 'Sous dossier FTP contenant les fichiers d''import et d''export', 'PREVARISC', 1, 'Synchro SIG');
	
INSERT INTO remocra.param_conf(
            cle, description, valeur, version, nomgroupe)
    VALUES ('PDI_FTP_FICHIER_PREVARISC_VERS_REMOCRA', 'Nom du fichier ZIP généré depuis Prevarisc pour mettre à jour Remocra', 'PREVARISC_VERS_REMOCRA.ZIP', 1, 'Synchro SIG');
			
-- Contenu réel du patch fin
--------------------------------------------------

commit;
