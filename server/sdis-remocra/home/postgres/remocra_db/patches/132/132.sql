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
    numero_patch := 132;
    description_patch :='Fix patch requête modèle/ repertoire lieu';

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

update remocra.repertoire_lieu set source_sql='SELECT p.id, (c.insee||'' ''|| s.numero ||'' '' ||p.numero) AS parcelle, St_SetSRID(CAST(CAST(p.geometrie AS box2D) AS geometry),2154) AS geometrie FROM remocra.cadastre_parcelle p JOIN remocra.cadastre_section s ON (s.id = p.section) JOIN remocra.commune c ON(c.id = s.commune) ORDER BY parcelle'
where code = 'CADASTRE';


-- Contenu réel du patch fin
--------------------------------------------------

commit;
