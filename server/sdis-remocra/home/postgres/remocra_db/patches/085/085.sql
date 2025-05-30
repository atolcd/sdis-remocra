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
    numero_patch := 85;
    description_patch := 'Reprise doc paramètres de conf';

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


update remocra.param_conf set description='Règle de calcul des <i>numéros internes</i> des PEI (ex : 77, 83)' where cle='HYDRANT_NUMEROTATION_INTERNE_METHODE';
update remocra.param_conf set description='Règle de calcul des <i>numéros globaux</i> des PEI (ex : 77, 83)' where cle='HYDRANT_NUMEROTATION_METHODE';
update remocra.param_conf set description='Symbologie des PEI (ex : GEN, 77, 83)' where cle='HYDRANT_SYMBOLOGIE_METHODE';


-- Contenu réel du patch fin
--------------------------------------------------

commit;

