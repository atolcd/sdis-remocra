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
    numero_patch := 64;
    description_patch := 'Obligations légales de débroussaillement';

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
 -- Section cadastrale
DROP TABLE IF EXISTS remocra.cadastre_section CASCADE;
CREATE TABLE remocra.cadastre_section(
	id bigserial NOT NULL PRIMARY KEY,
	geometrie geometry NOT NULL,	
	numero character varying NOT NULL,
	commune bigint NOT NULL,
	CONSTRAINT uk_cadastre_section_numero UNIQUE (commune,numero),
	CONSTRAINT fk_cadastre_section_commune FOREIGN KEY (commune)
		REFERENCES remocra.commune (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
	CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'MULTIPOLYGON'::text),
	CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);
COMMENT ON TABLE remocra.cadastre_section IS 'Section cadastrale d''une commune';
COMMENT ON COLUMN remocra.cadastre_section.id IS 'Identifiant autogénéré';
COMMENT ON COLUMN remocra.cadastre_section.geometrie IS 'Géometrie de la section cadastrale';
COMMENT ON COLUMN remocra.cadastre_section.numero IS 'Numéro d''identification de la section';
COMMENT ON COLUMN remocra.cadastre_section.commune IS 'Identifiant de correspondance avec la commune rattachée à une section';

CREATE INDEX cadastre_section_geometrie_idx
  ON remocra.cadastre_section
  USING gist
  (geometrie);

-- Parcelle cadastrale
DROP TABLE IF EXISTS remocra.cadastre_parcelle CASCADE;
CREATE TABLE remocra.cadastre_parcelle(
	id bigserial NOT NULL PRIMARY KEY,
	geometrie geometry NOT NULL,
	numero character varying NOT NULL,
	section bigint NOT NULL,
	CONSTRAINT cadastre_parcelle_numero UNIQUE (section,numero),
	CONSTRAINT fk_cadastre_parcelle_section FOREIGN KEY (section)
		REFERENCES remocra.cadastre_section (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
	CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'MULTIPOLYGON'::text),
	CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);
COMMENT ON TABLE remocra.cadastre_parcelle IS 'Parcelle cadastrale d''une commune';
COMMENT ON COLUMN remocra.cadastre_parcelle.id IS 'Identifiant autogénéré';
COMMENT ON COLUMN remocra.cadastre_parcelle.geometrie IS 'Géometrie de la parcelle cadastrale';
COMMENT ON COLUMN remocra.cadastre_parcelle.numero IS 'Numéro d''identification de la parcelle';
COMMENT ON COLUMN remocra.cadastre_parcelle.section IS 'Identifiant de correspondance avec la section rattachée à une parcelle';

CREATE INDEX cadastre_parcelle_geometrie_idx
  ON remocra.cadastre_parcelle
  USING gist
  (geometrie);

-- Anomalies
DROP TABLE IF EXISTS remocra.type_oldeb_categorie_anomalie CASCADE;
CREATE TABLE remocra.type_oldeb_categorie_anomalie (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_categorie_anomalie IS 'Groupement de types d''anomalies  dans le cadre des obligations légales de débroussaillement';

DROP TABLE IF EXISTS remocra.type_oldeb_anomalie CASCADE;
CREATE TABLE remocra.type_oldeb_anomalie (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL,
	categorie bigint NOT NULL,
	CONSTRAINT fk_type_oldeb_anomalie_categorie FOREIGN KEY (categorie)
		REFERENCES remocra.type_oldeb_categorie_anomalie (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.type_oldeb_anomalie IS 'Type d''anomalie constatée lors d''une visite liée à une obligation légale de débroussaillement';

-- Caractéristiques
DROP TABLE IF EXISTS remocra.type_oldeb_categorie_caracteristique CASCADE;
CREATE TABLE remocra.type_oldeb_categorie_caracteristique (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_categorie_caracteristique IS 'Groupement de caractéristiques permettant de qualifier une parcelle soumise à une obligation légale de débroussaillement';

DROP TABLE IF EXISTS remocra.type_oldeb_caracteristique CASCADE;
CREATE TABLE remocra.type_oldeb_caracteristique (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL,
	categorie bigint NOT NULL,
	CONSTRAINT fk_type_oldeb_caracteristique_categorie FOREIGN KEY (categorie)
		REFERENCES remocra.type_oldeb_categorie_caracteristique (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.type_oldeb_caracteristique IS 'Caractéristique permettant de qualifier une parcelle soumise à une obligation légale de débroussaillement';

-- Type de suite
DROP TABLE IF EXISTS remocra.type_oldeb_suite CASCADE;
CREATE TABLE remocra.type_oldeb_suite (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_suite IS 'Type de suite donnée après une visite liée à une obligation légale de débroussaillement';

-- Type de résidence
DROP TABLE IF EXISTS remocra.type_oldeb_residence CASCADE;
CREATE TABLE remocra.type_oldeb_residence (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_residence IS 'Type de résidence pour le proprietaire d''une parcelle batie sousmise à une obligation légale de débroussaillement';

-- Type d''acces
DROP TABLE IF EXISTS remocra.type_oldeb_acces CASCADE;
CREATE TABLE remocra.type_oldeb_acces (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_acces IS 'Type de voirie permettant l''accès à une parcelle sousmise à une obligation légale de débroussaillement';

-- Etat du débroussaillement
DROP TABLE IF EXISTS remocra.type_oldeb_debroussaillement CASCADE;
CREATE TABLE remocra.type_oldeb_debroussaillement (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_debroussaillement IS 'Etat du débroussaillement lié à une parcelle (ou un accès à une parcelle) sousmise à une obligation légale de débroussaillement';

-- Avis suite à une visite
DROP TABLE IF EXISTS remocra.type_oldeb_avis CASCADE;
CREATE TABLE remocra.type_oldeb_avis (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_avis IS 'Avis donné à la parcelle sousmise à une obligation légale de débroussaillement suite à une visite';

-- Type d'action suite à une visite
DROP TABLE IF EXISTS remocra.type_oldeb_action CASCADE;
CREATE TABLE remocra.type_oldeb_action (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_action IS 'Type d''action à engager suite à une visite sur une parcelle sousmise à une obligation légale de débroussaillement';

-- Type de zone urbanisme
DROP TABLE IF EXISTS remocra.type_oldeb_zone_urbanisme CASCADE;
CREATE TABLE remocra.type_oldeb_zone_urbanisme (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_oldeb_zone_urbanisme IS 'Type de zone de la parcelle dans le document d''urbanisme lié à la commune';

-- Obligation
DROP TABLE IF EXISTS remocra.oldeb CASCADE;
CREATE TABLE remocra.oldeb(
	id bigserial NOT NULL PRIMARY KEY,
	geometrie geometry NOT NULL,
	section character varying NOT NULL,
	parcelle character varying NOT NULL,
	num_voie character varying,
	voie character varying,
	lieu_dit character varying,
	volume integer NOT NULL DEFAULT 0,
	largeur_acces integer,
	portail_electrique boolean NOT NULL DEFAULT false,
	code_portail character varying,
	actif boolean DEFAULT true,	
	commune bigint NOT NULL,
	zone_urbanisme bigint,
	acces bigint,
	CONSTRAINT uk_oldeb_commune_section_parcelle UNIQUE (commune,section,parcelle),
	CONSTRAINT fk_oldeb_commune FOREIGN KEY (commune)
		REFERENCES remocra.commune (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_zone_urbanisme FOREIGN KEY (zone_urbanisme)
		REFERENCES remocra.type_oldeb_zone_urbanisme (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_acces FOREIGN KEY (acces)
		REFERENCES remocra.type_oldeb_acces (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
	CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'MULTIPOLYGON'::text),
	CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);
COMMENT ON TABLE remocra.oldeb IS 'Informations relatives à une parcelle sousmise à une obligation légale de débroussaillement';
COMMENT ON COLUMN remocra.oldeb.geometrie IS 'Géometrie de l''oldeb';
COMMENT ON COLUMN remocra.oldeb.parcelle IS 'Numéro d''identification de la parcelle rattachée à l''oldeb';
COMMENT ON COLUMN remocra.oldeb.section IS 'Numero de section rattachée à l''oldeb';
COMMENT ON COLUMN remocra.oldeb.actif IS 'Determine si l''oldeb est actif ou non';
COMMENT ON COLUMN remocra.oldeb.commune IS 'Identifiant de correspondance avec la commune rattachée à l''oldeb';

CREATE INDEX oldeb_geometrie_idx
  ON remocra.oldeb
  USING gist
  (geometrie);

--Caracteristiques
DROP TABLE IF EXISTS remocra.oldeb_caracteristique CASCADE;
CREATE TABLE remocra.oldeb_caracteristique(
	oldeb bigint NOT NULL,
	caracteristique bigint NOT NULL,
	CONSTRAINT pk_oldeb_caracteristique PRIMARY KEY (oldeb, caracteristique),
	CONSTRAINT fk_oldeb_caracteristique_oldeb FOREIGN KEY (oldeb)
		REFERENCES remocra.oldeb (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_caracteristique_caracteristique FOREIGN KEY (caracteristique)
		REFERENCES remocra.type_oldeb_caracteristique (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_caracteristique IS 'Caractéristique liée à une parcelle soumise à une obligation légale de débroussaillement';




-- Locataire
DROP TABLE IF EXISTS remocra.oldeb_locataire CASCADE;
CREATE TABLE remocra.oldeb_locataire(
	id bigserial NOT NULL PRIMARY KEY,
	organisme boolean NOT NULL DEFAULT false,
	raison_sociale character varying,
	civilite character varying NOT NULL,
	nom character varying NOT NULL,
	prenom character varying NOT NULL,
	telephone character varying,
	email character varying,
	oldeb bigint NOT NULL,
	CONSTRAINT chk_oldeb_locataire_civilite CHECK (civilite IN('M','MME')),
	CONSTRAINT uk_oldeb_locataire_oldeb UNIQUE (oldeb),
	CONSTRAINT fk_oldeb_locataire_oldeb FOREIGN KEY (oldeb)
		REFERENCES remocra.oldeb (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_locataire IS 'Locataire d''une parcelle soumise à une obligation légale de débroussaillement';

-- Propriétaires et propriétés
DROP TABLE IF EXISTS remocra.oldeb_proprietaire CASCADE;
CREATE TABLE remocra.oldeb_proprietaire(
	id bigserial NOT NULL PRIMARY KEY,
	organisme boolean NOT NULL DEFAULT false,
	raison_sociale character varying,
	civilite character varying NOT NULL,
	nom character varying NOT NULL,
	prenom character varying NOT NULL,
	telephone character varying,
	email character varying,
	num_voie character varying,
	voie character varying,
	lieu_dit character varying,
	code_postal character varying NOT NULL,
	ville character varying NOT NULL,
	pays character varying NOT NULL,
	CONSTRAINT chk_oldeb_proprietaire_civilite CHECK (civilite IN('M','Mme'))
);
COMMENT ON TABLE remocra.oldeb_proprietaire IS 'Propriétaire de parcelles soumises à obligation légale de débroussaillement';

DROP TABLE IF EXISTS remocra.oldeb_propriete CASCADE;
CREATE TABLE remocra.oldeb_propriete(
        id bigserial NOT NULL PRIMARY KEY,
	oldeb bigint NOT NULL,
	proprietaire bigint NOT NULL,
	residence bigint NOT NULL,
	CONSTRAINT fk_oldeb_propriete_oldeb FOREIGN KEY (oldeb)
		REFERENCES remocra.oldeb (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_propriete_proprietaire FOREIGN KEY (proprietaire)
		REFERENCES remocra.oldeb_proprietaire (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_propriete_residence FOREIGN KEY (residence)
		REFERENCES remocra.type_oldeb_residence (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_propriete IS 'Lien entre un propriétaire et une parcelle soumise à une obligation légale de débroussaillement';

-- Visites
DROP TABLE IF EXISTS remocra.oldeb_visite CASCADE;
CREATE TABLE remocra.oldeb_visite(
	id bigserial NOT NULL PRIMARY KEY,
	code character varying NOT NULL default md5(now() || 'oldeb_visite_document' || random()),
	date_visite timestamp without time zone,
	agent character varying NOT NULL,
	observation character varying,
	oldeb bigint NOT NULL,
	utilisateur bigint,
	debroussaillement_parcelle bigint NOT NULL,
	debroussaillement_acces bigint NOT NULL,
	avis bigint NOT NULL,
	action bigint NOT NULL,
	CONSTRAINT oldeb_visite_code_key UNIQUE (code),
	CONSTRAINT fk_oldeb_visite_oldeb FOREIGN KEY (oldeb)
		REFERENCES remocra.oldeb (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_visite_utilisateur FOREIGN KEY (utilisateur)
		REFERENCES remocra.utilisateur (id) MATCH SIMPLE
		ON UPDATE CASCADE ON DELETE SET NULL,
	CONSTRAINT fk_oldeb_visite_debroussaillement_parcelle FOREIGN KEY (debroussaillement_parcelle)
		REFERENCES remocra.type_oldeb_debroussaillement (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_visite_debroussaillement_acces FOREIGN KEY (debroussaillement_acces)
		REFERENCES remocra.type_oldeb_debroussaillement (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_visite_avis FOREIGN KEY (avis)
		REFERENCES remocra.type_oldeb_avis (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_visite_action FOREIGN KEY (action)
		REFERENCES remocra.type_oldeb_action (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_visite IS 'Visite de contrôle réalisée sur une parcelle soumise à une obligation légale de débroussaillement';

DROP TABLE IF EXISTS remocra.oldeb_visite_anomalie CASCADE;
CREATE TABLE remocra.oldeb_visite_anomalie(
	visite bigint NOT NULL,
	anomalie bigint NOT NULL,
	CONSTRAINT pk_visite_anomalie PRIMARY KEY (visite, anomalie),
	CONSTRAINT fk_oldeb_visite_anomalie_visite FOREIGN KEY (visite)
		REFERENCES remocra.oldeb_visite (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_visite_anomalie_anomalie FOREIGN KEY (anomalie)
		REFERENCES remocra.type_oldeb_anomalie (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_visite_anomalie IS 'Anomalie constatée lors d''une visite de contrôle réalisée sur une parcelle soumise à une obligation légale de débroussaillement';

DROP TABLE IF EXISTS remocra.oldeb_visite_suite CASCADE;
CREATE TABLE remocra.oldeb_visite_suite (
	id bigserial NOT NULL PRIMARY KEY,
	visite bigint NOT NULL,
	suite bigint NOT NULL,
	date_suite timestamp without time zone,
	observation character varying,
	CONSTRAINT oldeb_visite_suite_visite FOREIGN KEY (visite)
		REFERENCES remocra.oldeb_visite (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT type_oldeb_visite_suite_suite FOREIGN KEY (suite)
		REFERENCES remocra.type_oldeb_suite (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_visite_suite IS 'Suite donnée à une visite liée à une obligation légale de débroussaillement';


-- Document associé à une visite
DROP TABLE IF EXISTS remocra.oldeb_visite_document CASCADE;
CREATE TABLE remocra.oldeb_visite_document(
	id bigserial NOT NULL PRIMARY KEY,
	document bigint NOT NULL,
	visite bigint NOT NULL,
	CONSTRAINT fk_oldeb_visite_document_document FOREIGN KEY (document)
		REFERENCES remocra.document (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_oldeb_visite_document_visite FOREIGN KEY (visite)
		REFERENCES remocra.oldeb_visite (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.oldeb_visite_document IS 'Document associé à une visite de contrôle réalisée sur une parcelle soumise à une obligation légale de débroussaillement';


INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DOC_OLDEBVISITE', 'Emplacement du dossier de stockage des documents de la visite ', '/var/remocra/oldebvisite', 1);


-- Soundex version françisée
CREATE OR REPLACE FUNCTION remocra.soundex_fr(text)
RETURNS text AS
	$BODY$
	
		DECLARE
		
			st text;
			st2 text;
			i  int2;
			ch char;
			
		BEGIN
			st2:='';
		 
			-- on transforme les voyelles et on passe en majuscule
			st:=translate($1,'aàäâeéèêëiïîoôöuùûücç','AAAAEEEEEIIIOOOUUUUCC');
			st:=upper(st);
		 
			-- on enleve les espaces
			FOR i IN 1..length(st) LOOP
				IF substring(st,i,1)<>' ' THEN
					st2:=st2||substring(st,i,1);
				END IF;
			END LOOP;
		 
			st:='';
		 
			FOR i IN 1..length(st2) LOOP
				IF NOT (substring(st2,i,1) IN ('A','E','I','O','U','Y','H','W')) THEN
					st:=st||substring(st2,i,1);
				END IF;
			END LOOP;
		 
			st2:=substring(st,1,1);
		 
			FOR i IN 2..length(st) LOOP
				ch:=substring(st,i,1);
			  IF ch IN ('B','P') THEN st2:=st2||'1';
			   ELSIF ch IN ('C','K','Q') THEN st2:=st2||'2';
				ELSIF ch IN ('D','T') THEN st2:=st2||'3';
				 ELSIF ch='L' THEN st2:=st2||'4';
				  ELSIF ch IN ('M','N') THEN st2:=st2||'5';
				   ELSIF ch='R' THEN st2:=st2||'6';
					ELSIF ch IN ('G','J') THEN st2:=st2||'7';
					 ELSIF ch IN ('S','X','Z') THEN st2:=st2||'8';
					  ELSIF ch IN ('F','V') THEN st2:=st2||'9';
					   END IF;
			END LOOP;
		 
			st:=substring(st2,1,1);
			FOR i IN 1..length(st2) LOOP
				IF substring(st2,i,1) <> substring(st,length(st),1) THEN
					st:=st||substring(st2,i,1);
				END IF;
			END LOOP;
		 
			IF length(st)<4 THEN
				FOR i IN length(st)..4 LOOP
					st:=st||'0';
				END LOOP;
			ELSIF length(st)>4 THEN
				st:=substring(st,1,4);
			END IF;
		 
			return st;
		END;
	$BODY$
LANGUAGE plpgsql VOLATILE STRICT;
COMMENT ON FUNCTION remocra.soundex_fr(text) IS 'Calcule le code soundex d''une chaîne en prenant en compte des spécificités liées à la langue française';
-- Distance de Jaro Winkler
CREATE OR REPLACE FUNCTION remocra.jaro_winkler(str1_in text, str2_in text)
RETURNS double precision AS
	$BODY$
		DECLARE
			str1 text;
			str2 text;
			len_str1 integer;
			len_str2 integer;
			swap_len integer;
			max_len integer;
			m integer;
			i integer;
			j integer;
			f integer;
			l integer;
			tr double precision := 0;
			a1 text;
			a2 text;
			swap_str text;
			f1 boolean[];
			f2 boolean[];
			wcd double precision;
			wrd double precision;
			wtr double precision;
			common double precision := 0;
			jaro_value double precision := 0;
		BEGIN
			str1 := str1_in;
			str2 := str2_in;
			len_str1 := LENGTH(str1);
			len_str2 := LENGTH(str2);
			IF len_str1 > len_str2
			THEN
				swap_len := len_str2;
				len_str2 := len_str1;
				len_str1 := swap_len;
				swap_str := str1;
				str1 := str2;
				str2 := swap_str;
			END IF;
			max_len := len_str2;
			FOR i IN 1 .. len_str1
			LOOP
			   f1:=  array_append(f1,false);
			END LOOP;
			FOR j IN 1 .. len_str2
			LOOP
			   f2:= array_append(f2,false);
			END LOOP;
			m := ROUND((max_len / 2) - 1);
			FOR i IN 1 .. len_str1
			LOOP
				a1 := SUBSTR(str1, i, 1);
			
				IF m >= i
				THEN
					f := 1;
					l := i + m;
				ELSE
					f := i - m;
					l := i + m;
				END IF;
			
				IF l > max_len
				THEN
					l := max_len;
				END IF;
			
				FOR j IN f .. l
				LOOP
					a2 := SUBSTR(str2, j, 1);
					IF (a2 = a1)
					  AND (f2[j] = FALSE)
					THEN
						common := common + 1;
						f1[i] := TRUE;
						f2[j] := TRUE;
					   EXIT;
					END IF;
				END LOOP; -- j
			END LOOP; -- i
			l := 1;
			FOR i IN 1 .. len_str1
			LOOP
			
				IF f1[i]
				THEN
					FOR j IN l .. len_str2
					LOOP
						IF f2[j]
						THEN
							l := j + 1;
							a1 := SUBSTR(str1, i, 1);
							a2 := SUBSTR(str2, j, 1);
							IF a1 <> a2
							THEN
								tr := tr + 0.5;
							END IF;
							EXIT;
						END IF;
					END LOOP; -- j
				END IF;
			END LOOP; -- i
			wcd := 1/3::double precision;
			wrd := 1/3::double precision;
			wtr := 1/3::double precision;
			IF common <> 0
			THEN
				jaro_value := wcd * common / len_str1 + wrd * common / len_str2 +
							wtr * (common - tr) / common;
			END IF;
			RETURN jaro_value;
		END;
	$BODY$
LANGUAGE plpgsql VOLATILE STRICT;
COMMENT ON FUNCTION remocra.jaro_winkler(text,text) IS 'Calcule la distance de Jaro-Winkler entre deux chaînes de caractères';

-- Droits sur le module Oldeb : OLDEB CRUD -> Profil Administrateur applicatif
select setval('remocra.type_droit_id_seq',id,false) from (select max(id)+1 as id from remocra.type_droit) as compteur;
insert into remocra.type_droit(code, description, nom, version) values ('OLDEB', 'Droit sur le module OLDEB', 'obligation.debroussaillement', 1);
select setval('remocra.droit_id_seq',id,false) from (select max(id)+1 as id from remocra.droit) as compteur;
insert into remocra.droit(droit_create, droit_read, droit_update, droit_delete, "version", profil_droit, type_droit)
  select true, true, true, true, 1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'OLDEB'
  and pd.code in ('SDIS-ADM-APP');

-- Nouvelle thématique OLD
INSERT INTO remocra.thematique (id, nom, actif, code)
VALUES ((SELECT MAX(id)+1 FROM remocra.thematique), 'Obligation légale de débroussaillement', 'true', 'OLD');

-- Initialisation du paramètre "Numéro de traitement PDI pour fiche OLD"
-- Calculé avec id max de pdi_modele_traitement + 1 car pas de séquence
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES
('ID_TRAITEMENT_OLDEB', 'Traitement pour l''impression d''une fiche "Obligation de débroussaillement"', (SELECT MAX(idmodele)+1 FROM pdi.modele_traitement)::text, 1);


-- Vue des communes pour lesquelles des OLD existents
CREATE OR REPLACE VIEW pdi.vue_commune_oldeb AS 
SELECT
	com.id,
	com.nom AS libelle
FROM
	remocra.commune com
	JOIN (SELECT DISTINCT commune FROM remocra.oldeb) com_oldeb ON (com_oldeb.commune = com.id)
ORDER BY
	nom;

-- Référencement du modèle de traitement PDI associé
INSERT INTO pdi.modele_traitement (
	idmodele,
	code,
	description,
	nom,
	ref_chemin,
	ref_nom,
	type,
	message_echec,
	message_succes)
VALUES (
	(SELECT MAX(idmodele)+1 FROM pdi.modele_traitement),
	(SELECT id FROM remocra.thematique WHERE code ='OLD'),
	'Imprime la fiche parcellaire liée à une obligation légale de débroussaillement',
	'Impression d''une fiche "obligation légale de débroussaillement"',
	'/demandes/generation_courriers/obligation_legale_debroussaillement',
	'generer_fiche_parcellaire',
	'J',
	3,
	1);

-- Paramètre du traitement : commune
INSERT INTO pdi.modele_traitement_parametre (
	idparametre,
	form_etiquette,
	form_num_ordre,
	form_obligatoire,
	form_source_donnee,
	form_type_valeur,
	nom, idmodele)
VALUES (
	(SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre),
	'Commune',
	1,
	'true',
	'vue_commune_oldeb',
	'combo',
	'COMMUNE_ID',
	(SELECT MAX(idmodele) FROM pdi.modele_traitement));

-- Paramètre du traitement : section cadastrale
INSERT INTO pdi.modele_traitement_parametre (
	idparametre,
	form_etiquette,
	form_num_ordre,
	form_obligatoire,
	form_source_donnee,
	form_type_valeur,
	nom, idmodele)
VALUES (
	(SELECT MAX(idparametre)+2 FROM pdi.modele_traitement_parametre),
	'Numéro de section cadastrale',
	2,
	'true',
	null,
	'textfield',
	'NUM_SECTION',
	(SELECT MAX(idmodele) FROM pdi.modele_traitement));

-- Paramètre du traitement : parcelle cadastrale
INSERT INTO pdi.modele_traitement_parametre (
	idparametre,
	form_etiquette,
	form_num_ordre,
	form_obligatoire,
	form_source_donnee,
	form_type_valeur,
	nom, idmodele)
VALUES (
	(SELECT MAX(idparametre)+3 FROM pdi.modele_traitement_parametre),
	'Numéro de parcelle cadastrale',
	3,
	'true',
	null,
	'textfield',
	'NUM_PARCELLE',
	(SELECT MAX(idmodele) FROM pdi.modele_traitement));

--jeu de données référentiels

-- Table type_oldeb_acces
TRUNCATE remocra.type_oldeb_acces CASCADE;
INSERT INTO remocra.type_oldeb_acces (code,nom) VALUES('CHC','Chemin communal');
INSERT INTO remocra.type_oldeb_acces (code,nom) VALUES('CHP','Chemin privé');
INSERT INTO remocra.type_oldeb_acces (code,nom) VALUES('IMP','Impasse');
INSERT INTO remocra.type_oldeb_acces (code,nom) VALUES('NR','Non renseigné');
INSERT INTO remocra.type_oldeb_acces (code,nom) VALUES('RD','Route départementale');

-- Table type_oldeb_action
TRUNCATE remocra.type_oldeb_action CASCADE;
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('CERTIFICAT','Certificat de conformité');
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('MISE_DEMEURE','Mise en demeure');
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('VERBALISATION','Verbalisation');
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('VISITE_SIX_MOIS','Repasser dans 6 mois');
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('VISITE_UN_AN','Repasser dans 1 an');
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('COURRIER_MAIRIE_1','1er courrier Mairie');
INSERT INTO remocra.type_oldeb_action (code,nom) VALUES('COURRIER_MAIRIE_2','2nd courrier Mairie');




-- Table type_oldeb_categorie_anomalie
TRUNCATE remocra.type_oldeb_categorie_anomalie CASCADE;
INSERT INTO remocra.type_oldeb_categorie_anomalie (code,nom) VALUES('ACCES','Accès au terrain');
INSERT INTO remocra.type_oldeb_categorie_anomalie (code,nom) VALUES('PARCELLE','Terrain');

-- Table type_oldeb_categorie_caracteristique
TRUNCATE remocra.type_oldeb_categorie_caracteristique CASCADE;
INSERT INTO remocra.type_oldeb_categorie_caracteristique (code,nom) VALUES('ANIMAUX','Animaux');
INSERT INTO remocra.type_oldeb_categorie_caracteristique (code,nom) VALUES('BATIMENTS','Bâtiments');
INSERT INTO remocra.type_oldeb_categorie_caracteristique (code,nom) VALUES('DIVERS','Divers');
INSERT INTO remocra.type_oldeb_categorie_caracteristique (code,nom) VALUES('HYDROCARBURE','Hydrocarbures');
INSERT INTO remocra.type_oldeb_categorie_caracteristique (code,nom) VALUES('EAU','Ressources en eau et équipements');
INSERT INTO remocra.type_oldeb_categorie_caracteristique (code,nom) VALUES('RISQUE_HUMAIN','Risque humain');

-- Table type_oldeb_avis
TRUNCATE remocra.type_oldeb_avis CASCADE;
INSERT INTO remocra.type_oldeb_avis (code,nom) VALUES('SATISFAISANT','Satisfaisant');
INSERT INTO remocra.type_oldeb_avis (code,nom) VALUES('NON_SATISFAISANT','Non satisfaisant');
INSERT INTO remocra.type_oldeb_avis (code,nom) VALUES('A_COMPLETER','A compléter');

-- Table type_oldeb_debroussaillement
TRUNCATE remocra.type_oldeb_debroussaillement CASCADE;
INSERT INTO remocra.type_oldeb_debroussaillement (code,nom) VALUES('AV','A voir');
INSERT INTO remocra.type_oldeb_debroussaillement (code,nom) VALUES('N','Non');
INSERT INTO remocra.type_oldeb_debroussaillement (code,nom) VALUES('O','Oui');
INSERT INTO remocra.type_oldeb_debroussaillement (code,nom) VALUES('PA','Partiel');

-- Table type_oldeb_residence
TRUNCATE remocra.type_oldeb_residence CASCADE;
INSERT INTO remocra.type_oldeb_residence (code,nom) VALUES('A','Autre');
INSERT INTO remocra.type_oldeb_residence (code,nom) VALUES('C','Cabanon');
INSERT INTO remocra.type_oldeb_residence (code,nom) VALUES('P','Principale');
INSERT INTO remocra.type_oldeb_residence (code,nom) VALUES('S','Secondaire');

-- Table type_oldeb_suite
TRUNCATE remocra.type_oldeb_suite CASCADE;
INSERT INTO remocra.type_oldeb_suite (code,nom) VALUES('CONTRAVENTION','Contravention');
INSERT INTO remocra.type_oldeb_suite (code,nom) VALUES('DECISION_TRIBUNAL','Décision tribunal');
INSERT INTO remocra.type_oldeb_suite (code,nom) VALUES('MISE_DEMEURE','Mise en demeure');
INSERT INTO remocra.type_oldeb_suite (code,nom) VALUES('PV_INEXECUTION','PV d''inexécution');
INSERT INTO remocra.type_oldeb_suite (code,nom) VALUES('TA_MAIRIE','TA transmis au maire');

-- Table type_oldeb_zone_urbanisme
TRUNCATE remocra.type_oldeb_zone_urbanisme CASCADE;
INSERT INTO remocra.type_oldeb_zone_urbanisme (code,nom) VALUES('A','Zone agricole (A)');
INSERT INTO remocra.type_oldeb_zone_urbanisme (code,nom) VALUES('N','Zone naturelle / forestière (N) ');
INSERT INTO remocra.type_oldeb_zone_urbanisme (code,nom) VALUES('U','Zone urbaine (U)');
INSERT INTO remocra.type_oldeb_zone_urbanisme (code,nom) VALUES('AU','Zone à urbaniser (AU)');

-- Table type_oldeb_anomalie
TRUNCATE remocra.type_oldeb_anomalie CASCADE;
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_001','Arbres à moins de 3 m des constructions',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_002','Arbustes maintenus sous les arbres conservés',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_003','Bouquets d''arbres supérieurs à 15 m de diamètre ',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_004','Bouquets d''arbustes supérieurs à 3 m de diamètre',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_005','Haie à moins de 3 m des constructions et de plus de 15 m de long',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_006','Herbe non tenue rase',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_007','Houppiers des arbres à moins de 3 m entre eux',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_009','Non ratissage de la litière et des feuilles dans les 20 m',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_010','Non réalisation au delà des limites de la parcelle',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_008','Non élagage des arbres conservés à 2,5 m ou 2/3 de leur hauteur',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_011','Présence de bois morts, rémanents de coupe',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_ACCES_001','Voirie dégradée',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'ACCES'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_PARCELLE_012','Végétation à moins de 4 m au dessus de la plate-forme et sur l''emprise',(SELECT id FROM remocra.type_oldeb_categorie_anomalie AS toca WHERE toca.code = 'PARCELLE'));
INSERT INTO remocra.type_oldeb_anomalie (code,nom,categorie) VALUES
('ANO_ACCES_002','Gabarit de 4m non réalisé',(SELECT id FROM remocra.type_oldeb_categorie_anomalie WHERE code = 'ACCES'));
-- Table type_oldeb_caracteristique
TRUNCATE remocra.type_oldeb_caracteristique CASCADE;
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('BGA','Bouteille gaz extérieure',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'HYDROCARBURE'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('BO','Bovins',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'ANIMAUX'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('CA','Caprins',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'ANIMAUX'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('CHARPENTE','Charpente apparente',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'BATIMENTS'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('CFA','Cuve fuel aérienne',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'HYDROCARBURE'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('CFE','Cuve fuel enterrée',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'HYDROCARBURE'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('CGA','Cuve gaz aérienne',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'HYDROCARBURE'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('CGE','Cuve gaz enterrée ',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'HYDROCARBURE'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('EQ','Equins',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'ANIMAUX'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('MOTOPOMPE','Motopompe',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'EAU'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('OV','Ovins',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'ANIMAUX'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('M','Personne malade',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'RISQUE_HUMAIN'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('MR','Personne à mobilité réduite',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'RISQUE_HUMAIN'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('AGE','Personne âgée',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'RISQUE_HUMAIN'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('PISCINE','Piscine',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'EAU'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('ACCES_PISCINE','Piscine accessible aux véhicules',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'EAU'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('STOCKAGE_BOIS','Stockage de bois',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'DIVERS'));
INSERT INTO remocra.type_oldeb_caracteristique (code,nom,categorie) VALUES
('TOITURE','Toiture sale',(SELECT id FROM remocra.type_oldeb_categorie_caracteristique AS tocc WHERE tocc.code = 'BATIMENTS'));



--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

