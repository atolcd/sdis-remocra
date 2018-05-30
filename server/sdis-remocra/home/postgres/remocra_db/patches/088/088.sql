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
    numero_patch := 88;
    description_patch := 'Correction trigger de traça';

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


CREATE OR REPLACE FUNCTION tracabilite.trg_hydrant()
  RETURNS trigger AS
$BODY$
DECLARE
    p_id bigint;
    p_operation varchar;
    p_rec record;
BEGIN
    if (TG_OP = 'DELETE') then
        p_rec = OLD;
    else
        p_rec = NEW;
    end if;

    if TG_TABLE_NAME = 'hydrant_anomalies' then
        p_id := p_rec.hydrant;
    else
        p_id := p_rec.ID;
    end if;
    select nom_operation into p_operation from tracabilite.hydrant where num_transac = txid_current() and id_hydrant = p_id;
    if FOUND then
        if (TG_WHEN = 'AFTER' AND p_operation != 'DELETE') then
        if TG_TABLE_NAME = 'hydrant' then
            perform tracabilite.update_hydrant(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_pibi' then
            perform tracabilite.update_hydrant_pibi(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_pena' then
            perform tracabilite.update_hydrant_pena(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_anomalies' then
            perform tracabilite.update_hydrant_anomalies(p_id, TG_OP::varchar, txid_current());
        end if;
        end if;
    else
    if(TG_OP = 'UPDATE' OR TG_TABLE_NAME = 'hydrant_anomalies' OR ((TG_TABLE_NAME = 'hydrant_pibi' OR TG_TABLE_NAME = 'hydrant_pena') AND TG_OP = 'INSERT')) then
        p_operation = 'UPDATE';
    else
        p_operation = TG_OP::varchar;
    end if;
        perform tracabilite.insert_hydrant(p_id, p_operation);
    end if;
    RETURN p_rec;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.trg_hydrant()
  OWNER TO postgres;


-- Contenu réel du patch fin
--------------------------------------------------

commit;
