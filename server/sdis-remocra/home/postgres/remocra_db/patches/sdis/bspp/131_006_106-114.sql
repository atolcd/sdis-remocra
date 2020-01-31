begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;


-- Décalage du suivi
alter table remocra.suivi_patches drop constraint suivi_patches_pkey;
update remocra.suivi_patches set numero=numero+9 where numero>105;
alter table remocra.suivi_patches add constraint suivi_patches_pkey PRIMARY KEY (numero);


--------------------------------------------------
-- 106
insert into remocra.suivi_patches(numero, description) values(106, 'Fix Trigger crise_evenement');

-- Function: remocra.trg_crise_evenement()

-- DROP FUNCTION remocra.trg_crise_evenement();

CREATE OR REPLACE FUNCTION remocra.trg_crise_evenement()
  RETURNS trigger AS
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
			v_communes := '';
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
					JOIN remocra.crise_evenement_complement ec ON(ec.evenement = e.id)
					LEFT JOIN remocra.type_crise_propriete_evenement tcpe ON (tcpe.id = ec.propriete_evenement)
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.trg_crise_evenement()
  OWNER TO postgres;

-- 106
--------------------------------------------------





--------------------------------------------------
-- 107
insert into remocra.suivi_patches(numero, description) values(107, 'Ajout de l''auteur de crise');

ALTER TABLE remocra.crise ADD COLUMN auteur_crise BIGINT;
ALTER TABLE remocra.crise ADD constraint crise_utilisateur foreign key (auteur_crise)
    references remocra.utilisateur (id) match simple
    on update no action on delete no action;

ALTER TABLE remocra.crise_evenement ADD COLUMN auteur_evenement BIGINT;
ALTER TABLE remocra.crise_evenement ADD constraint crise_evenement_utilisateur foreign key (auteur_evenement)
    references remocra.utilisateur (id) match simple
    on update no action on delete no action;

ALTER TABLE remocra.crise_suivi ADD COLUMN auteur CHARACTER VARYING;

-- Function: remocra.trg_crise()

-- DROP FUNCTION remocra.trg_crise();

CREATE OR REPLACE FUNCTION remocra.trg_crise()
  RETURNS trigger AS
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
        v_auteur character varying := null;
	v_date_message timestamp with time zone := null;
    
    
BEGIN

	IF (TG_OP = 'UPDATE') THEN

		r_new = NEW;
		r_old = OLD;

		-- Statut de la crise
		SELECT code INTO v_statut FROM remocra.type_crise_statut WHERE id = r_new.statut;

                -- auteur de la crise
                IF (r_new.auteur_crise is not null) THEN 
                SELECT nom || ' ' || prenom  INTO v_auteur FROM remocra.utilisateur WHERE id = r_new.auteur_crise;
                END IF;

			
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
                                auteur,
				crise,
				objet,
				message,
				creation,
				importance,
				tags
			) VALUES (
                                v_auteur,
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.trg_crise()
  OWNER TO postgres;


-- Function: remocra.trg_crise_evenement()

-- DROP FUNCTION remocra.trg_crise_evenement();

CREATE OR REPLACE FUNCTION remocra.trg_crise_evenement()
  RETURNS trigger AS
$BODY$
DECLARE

	r_new record;
	r_old record;

	r_modele_message record;
	r_complement record;

        v_auteur character varying;
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
                
                -- auteur de l'évènement
                IF (r_new.auteur_evenement is not null) THEN 
                SELECT  nom || ' ' || prenom  INTO v_auteur FROM remocra.utilisateur WHERE id = r_new.auteur_evenement;
                END IF;
		
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
			v_communes := '';
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
					JOIN remocra.crise_evenement_complement ec ON(ec.evenement = e.id)
					LEFT JOIN remocra.type_crise_propriete_evenement tcpe ON (tcpe.id = ec.propriete_evenement)
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
                                auteur,
				origine,
				objet,
				message,
				creation,
				importance,
				tags,
				crise,
				evenement
			) VALUES (
                                v_auteur,
				r_new.origine,
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.trg_crise_evenement()
  OWNER TO postgres;

-- 107
--------------------------------------------------





--------------------------------------------------
-- 108
insert into remocra.suivi_patches(numero, description) values(108, 'Ajout de la colonne contexte');

ALTER TABLE remocra.crise_evenement ADD COLUMN contexte CHARACTER VARYING NOT NULL DEFAULT 'ANTICIPATION';

-- 108
--------------------------------------------------





--------------------------------------------------
-- 109
insert into remocra.suivi_patches(numero, description) values(109, 'Mise à jour de la carte crise (Operationnel /anticipation)');

ALTER TABLE remocra.crise RENAME COLUMN carte TO carte_ant;
ALTER TABLE remocra.crise ADD COLUMN carte_op CHARACTER VARYING ;

COMMENT ON COLUMN remocra.crise.carte_ant IS 'Groupe de couches complémentaires à carte.json (anticipation)'; 
COMMENT ON COLUMN remocra.crise.carte_op IS 'Groupe de couches complémentaires à carte.json (operationnel)'; 

-- 109
--------------------------------------------------





--------------------------------------------------
-- 110
insert into remocra.suivi_patches(numero, description) values(110, 'Gestion des intervention');

CREATE TABLE remocra.type_moyen
(
  id bigserial NOT NULL,
  actif boolean DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  CONSTRAINT type_moyen_pkey PRIMARY KEY (id),
  CONSTRAINT type_moyen_code_key UNIQUE (code),
  CONSTRAINT type_moyen_nom_key UNIQUE (nom)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_moyen
  OWNER TO postgres;
COMMENT ON TABLE remocra.type_moyen
  IS 'Type de moyen déployé, lors d''une intervention, pour filtre dans REMOcRA';
COMMENT ON COLUMN remocra.type_moyen.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_moyen.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_moyen.code IS 'Code du moyen. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_moyen.nom IS 'Libellé du type de moyen';


INSERT INTO remocra.type_moyen (code, nom) VALUES
  ('ENGIN', 'Engin'), ('HUMAIN', 'Humain');

CREATE TABLE remocra.intervention
(
  id bigserial NOT NULL,
  code character varying,
  code_type character varying,
  libelle_type character varying,
  priorite integer,
  date_creation timestamp without time zone,
  date_modification timestamp without time zone,
  date_cloture timestamp without time zone,
  cloture character varying,
  num_voie character varying,
  voie character varying,
  commune bigint,
  geometrie geometry,
  CONSTRAINT intervention_pkey PRIMARY KEY (id),
  CONSTRAINT fk_intervention_commune FOREIGN KEY (commune)
    REFERENCES remocra.commune (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
  CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POINT'::text),
  CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);

COMMENT ON TABLE remocra.intervention
  IS 'Table de synchronisation des interventions pour utilisation dans REMOcRA';
COMMENT ON COLUMN remocra.intervention.id IS 'Identifiant autogénéré';
COMMENT ON COLUMN remocra.intervention.code IS 'Code unique de l''intervention';
COMMENT ON COLUMN remocra.intervention.code_type IS 'Code du type de l''intervention';
COMMENT ON COLUMN remocra.intervention.libelle_type IS 'Libellé du type de l''intervention';
COMMENT ON COLUMN remocra.intervention.priorite IS 'Priorité de l''intervention';
COMMENT ON COLUMN remocra.intervention.date_creation IS 'Date de création de l''intervention';
COMMENT ON COLUMN remocra.intervention.date_modification IS 'Date de modification de l''intervention';
COMMENT ON COLUMN remocra.intervention.date_cloture IS 'Date de clotûre de l''intervention';
COMMENT ON COLUMN remocra.intervention.num_voie IS 'Numéro de la voie du lieu de l''intervention';
COMMENT ON COLUMN remocra.intervention.voie IS 'Nom de la voie du lieu de l''intervention';
COMMENT ON COLUMN remocra.intervention.commune IS 'Commune de l''intervention';
COMMENT ON COLUMN remocra.intervention.geometrie IS 'Géométrie de l''intervention';

CREATE INDEX intervention_geometrie_idx
  ON remocra.intervention
  USING gist
  (geometrie);

CREATE TABLE remocra.moyen
(
  id bigserial NOT NULL,
  type bigint,
  nom character varying,
  intervention bigint,
  geometrie geometry,
  CONSTRAINT engin_pkey PRIMARY KEY (id),
  CONSTRAINT fk_moyen_type_moyen FOREIGN KEY (type)
    REFERENCES remocra.type_moyen (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_moyen_intervention FOREIGN KEY (intervention)
    REFERENCES remocra.intervention (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
  CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POINT'::text),
  CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);

COMMENT ON TABLE remocra.moyen
  IS 'Table de synchronisation des moyens pour utilisation dans REMOcRA en lien avec les interventions';
COMMENT ON COLUMN remocra.moyen.id IS 'Identifiant autogénéré';
COMMENT ON COLUMN remocra.moyen.nom IS 'Nom du moyen';
COMMENT ON COLUMN remocra.moyen.intervention IS 'Intervention à laquelle est rattachée le moyen';
COMMENT ON COLUMN remocra.moyen.geometrie IS 'Dernier emplacement connu du moyen';

CREATE INDEX moyen_geometrie_idx
  ON remocra.moyen
  USING gist
      (geometrie);

CREATE TABLE remocra.crise_intervention
(
  crise bigint,
  intervention bigint,
  CONSTRAINT fk_crise_intervention_crise FOREIGN KEY (crise)
    REFERENCES remocra.crise (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_crise_intervention_intervention FOREIGN KEY (intervention)
    REFERENCES remocra.intervention (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

COMMENT ON TABLE remocra.crise_intervention
  IS 'Table de liaison entre les crises et les interventions';
COMMENT ON COLUMN remocra.crise_intervention.crise IS 'Identifiant de la crise';
COMMENT ON COLUMN remocra.crise_intervention.intervention IS 'Identifiant de l''intervention';


CREATE TABLE remocra.crise_evenement_intervention
(
  crise_evenement bigint,
  intervention bigint,
  CONSTRAINT fk_crise_evenement_intervention_evenement FOREIGN KEY (crise_evenement)
    REFERENCES remocra.crise_evenement (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_crise_intervention_intervention FOREIGN KEY (intervention)
    REFERENCES remocra.intervention (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

COMMENT ON TABLE remocra.crise_evenement_intervention
  IS 'Table de liaison entre des évènement d''une crises et des interventions';
COMMENT ON COLUMN remocra.crise_evenement_intervention.crise_evenement IS 'Identifiant de l''evenement de la crise';
COMMENT ON COLUMN remocra.crise_evenement_intervention.intervention IS 'Identifiant de l''intervention';

CREATE TABLE remocra.crise_indicateur
(
  id bigserial NOT NULL,
  libelle character varying NOT NULL,
  code character varying NOT NULL,
  source_sql character varying NOT NULL,
  CONSTRAINT crise_indicateur_pkey PRIMARY KEY (id),
  CONSTRAINT crise_indicateur_code_key UNIQUE (code)
);

COMMENT ON TABLE remocra.crise_indicateur
  IS 'Table de stockage de la requête pour récupérer les données des indicateurs d''une crise';
COMMENT ON COLUMN remocra.crise_indicateur.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise_indicateur.libelle IS 'Libellé de la requête pour description';
COMMENT ON COLUMN remocra.crise_indicateur.code IS 'Code unique de la requête';
COMMENT ON COLUMN remocra.crise_indicateur.source_sql IS 'Requête de sélection des données pour les indicateurs';

INSERT INTO remocra.crise_indicateur(libelle, code, source_sql) VALUES ('Indicateurs de crise', 'CRISE_INDICATEUR', 'SELECT
    cast(xmlelement(name "indicateurs",xmlagg(xml_groupe)) AS text)
FROM
(
SELECT
    xmlelement(name "groupe",
        xmlelement(name "nom","Moyens"),
        XMLAGG(xml_indicateur)
    ) AS xml_groupe
FROM
(SELECT
    xmlelement(name "indicateur",
            xmlelement(name clef,"Indicateur" || i),
            xmlelement(name valeur,i*i)
    ) AS xml_indicateur

FROM
    generate_series(1,10,1) AS i) AS groupe_1
UNION ALL
SELECT
    xmlelement(name "groupe",
        xmlelement(name "nom","Interventions"),
        XMLAGG(xml_indicateur)
    ) AS xml_groupe
FROM

(SELECT
    xmlelement(name "indicateur",
            xmlelement(name clef,"Indicateur" || i),
            xmlelement(name valeur,i*i)
    ) AS xml_indicateur

FROM
    generate_series(1,5,1) AS i) AS groupe_2
) as total');

-- 110
--------------------------------------------------





--------------------------------------------------
-- 111
insert into remocra.suivi_patches(numero, description) values(111, 'Ajout du paramètre des exports nouvelle gestion ETL');

INSERT INTO remocra.param_conf(
       cle, description, valeur, version, nomgroupe)
   VALUES ('PDI_CHEMIN_PROCESSUS_ETL_DOCUMENT', 'Emplacement du dossier de stockage des fichiers créés par l''ETL à envoyé aux utilisateurs', '/var/remocra/pdi/documents', 1, 'Chemins sur disque');

-- 111
--------------------------------------------------





--------------------------------------------------
-- 112
insert into remocra.suivi_patches(numero, description) values(112, 'Ajout des tables Vigicru');

CREATE TABLE remocra_referentiel.vigicrue_station (
    id serial NOT NULL,
    geometrie geometry,
    code character varying NOT NULL,
    libelle character varying NOT NULL,
    CONSTRAINT vigicrue_station_pkey PRIMARY KEY (id),
    CONSTRAINT vigicrue_station_srid CHECK ((public.st_srid(geometrie) = 2154))
);



CREATE TABLE remocra_referentiel.vigicrue_station_mesure (
    id serial NOT NULL,
    dateheure timestamp without time zone NOT NULL,
    hauteur integer NOT NULL,
    idstation integer NOT NULL,
    CONSTRAINT vigicrue_station_mesure_pkey PRIMARY KEY (id),
    CONSTRAINT fk_vigicrue_station_idstation FOREIGN KEY (idstation) REFERENCES remocra_referentiel.vigicrue_station(id) ON UPDATE CASCADE ON DELETE CASCADE
);



CREATE TABLE remocra_referentiel.vigicrue_troncon (
    id serial NOT NULL,
    geometrie geometry,
    code character varying NOT NULL,
    libelle character varying NOT NULL,
    CONSTRAINT vigicrue_troncon_pkey PRIMARY KEY (id),
    CONSTRAINT vigicrue_troncon_srid CHECK ((public.st_srid(geometrie) = 2154))
);


CREATE TABLE remocra_referentiel.vigicrue_troncon_niveau (
    id serial NOT NULL,
    dateheure timestamp without time zone NOT NULL,
    niveau integer NOT NULL,
    idtroncon integer NOT NULL,
    CONSTRAINT vigicrue_troncon_niveau_pkey PRIMARY KEY (id),
    CONSTRAINT fk_vigicrue_troncon_idtroncon FOREIGN KEY (idtroncon) REFERENCES remocra_referentiel.vigicrue_troncon(id) ON UPDATE CASCADE ON DELETE CASCADE
);



CREATE INDEX vigicrue_station_geometrie_idx ON remocra_referentiel.vigicrue_station USING gist (geometrie);

CREATE INDEX vigicrue_troncon_geometrie_idx ON remocra_referentiel.vigicrue_troncon USING gist (geometrie);

-- 112
--------------------------------------------------





--------------------------------------------------
-- 113
insert into remocra.suivi_patches(numero, description) values(113, 'Ajout du droit de zoom sur un lieu');

-- Droits de zoom sur un lieu
insert into remocra.type_droit(code, description, nom, version,categorie) values ('ZOOM_LIEU_R', 'zoomer sur un lieu', 'zoom.lieu_R', 1, 'Général');

-- 113
--------------------------------------------------





--------------------------------------------------
-- 114
insert into remocra.suivi_patches(numero, description) values(114, 'Ajout d''un paramètre pour définir si les zones de compétence des CIS et CIE contiennent exactement les zones de communes');

INSERT INTO remocra.param_conf (cle, description, valeur, nomgroupe) 
	VALUES ('PDI_ZC_CONTAINS', 'Les zones de compétence des CIS et CIE contiennent strictement les communes (1 = oui, 0 = non)', '1', 'Traitements et purge');

-- 114
--------------------------------------------------

commit;
