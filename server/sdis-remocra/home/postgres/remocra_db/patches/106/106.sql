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
    numero_patch := 106;
    description_patch := 'Fix Trigger crise_evenement';

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




-- Contenu réel du patch fin
--------------------------------------------------

commit;

