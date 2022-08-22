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
    numero_patch := 169;
    description_patch := 'Reprise trigger changement dispo';

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

CREATE OR REPLACE FUNCTION remocra.trg_date_changement_dispo_terrestre()
    RETURNS trigger
    LANGUAGE plpgsql
  AS $function$
    DECLARE
    p_rec_old remocra.hydrant%ROWTYPE;
    p_rec_new remocra.hydrant%ROWTYPE;
    BEGIN
	  p_rec_new = NEW;
	  IF (TG_OP = 'UPDATE') THEN
      	p_rec_old = OLD;
		-- Si la dispo a changé on met la date du jour
		  if (p_rec_old IS NULL OR p_rec_old.dispo_terrestre != p_rec_new.dispo_terrestre) then
			p_rec_new.date_changement_dispo_terrestre = NOW();
		  end if;
	  ELSE -- En cas d'insertion
	  	p_rec_new.date_changement_dispo_terrestre = NOW();
	  END IF;
      RETURN p_rec_new;
    END;
  $function$
  ;
  
DROP TRIGGER IF EXISTS trig_date_changement_dispo_terrestre ON remocra.hydrant;
  CREATE TRIGGER trig_date_changement_dispo_terrestre BEFORE
    INSERT
    OR
    UPDATE
    ON
    remocra.hydrant FOR EACH ROW EXECUTE PROCEDURE remocra.trg_date_changement_dispo_terrestre();

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
