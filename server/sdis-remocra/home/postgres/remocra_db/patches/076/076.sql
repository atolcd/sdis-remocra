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
    numero_patch := 76;
    description_patch := 'Droit TRAITEMENTS.C';

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


-- Nouveau type de droit "Traitements.C"
select setval('remocra.type_droit_id_seq', id, false) from (select max(id)+1 as id from remocra.type_droit) as compteur;
insert into remocra.type_droit(code, description, nom, version) values ('TRAITEMENTS', 'Créer des traitements', 'traitements', 1);

-- Pour iso-fonctionnalité : affectation aux profils qui ont le droit Referentiel.C
select setval('remocra.droit_id_seq', id, false) from (select max(id)+1 as id from remocra.droit) as compteur;
insert into remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
 select 'TRUE', 'FALSE', 'FALSE', 'FALSE', 1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'TRAITEMENTS'
  and pd.id in (select d.profil_droit from remocra.droit d where (d.type_droit = (select td.id from remocra.type_droit td where td.code  = 'REFERENTIELS')  and d.droit_create = true));




-- Contenu réel du patch fin
--------------------------------------------------

commit;

