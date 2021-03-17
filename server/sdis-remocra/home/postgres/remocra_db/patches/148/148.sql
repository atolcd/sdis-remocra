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
    numero_patch := 148;
    description_patch :='Calcul de dispo des PENA sans anomalies';

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

-- Adaptation de la fonction de calcul de dispo
CREATE OR REPLACE FUNCTION remocra.trg_calcul_indispo()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_rec record;
        p_id bigint;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;

	if TG_TABLE_NAME = 'hydrant_anomalies' then
        	p_id := p_rec.hydrant;
    	else
        	p_id := p_rec.id;
    	end if;
	

	perform remocra.calcul_indispo(p_id);
  RETURN p_rec;
END;
$function$
;

-- Ajout d'un trigger de dispo sur la table PENA
DROP TRIGGER IF EXISTS trig_indispo ON remocra.hydrant_pena;
CREATE TRIGGER trig_indispo AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE trg_calcul_indispo();

-- Contenu réel du patch fin
--------------------------------------------------

commit;
