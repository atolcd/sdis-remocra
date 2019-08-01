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
    numero_patch := 107;
    description_patch := 'Ajout de l''auteur de crise';

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


 
-- Contenu réel du patch fin
--------------------------------------------------

commit;

