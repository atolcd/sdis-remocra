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
    numero_patch := 75;
    description_patch := 'Ajou des droits vérification et reception';

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

-- Droits sur la réception d'hydrant : Reception CRUD 
select setval('remocra.type_droit_id_seq',id,false) from (select max(id)+1 as id from remocra.type_droit) as compteur;
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_RECEPTION', 'Droit sur la réception des hydrants', 'hydrants.reception', 1);

-- Affectation de droits de réception à tous les utilisateurs qui ont déjà le droit de création d'hydrant
select setval('remocra.droit_id_seq',id,false) from (select max(id)+1 as id from remocra.droit) as compteur;
insert into remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
 select 'TRUE','TRUE','TRUE','TRUE',1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'HYDRANTS_RECEPTION'
  and pd.id in (select d.profil_droit from remocra.droit d where (d.type_droit = (select td.id from remocra.type_droit td where td.code  = 'HYDRANTS')  and d.droit_create = true));


-- Droits sur la déplacement d'hydrant : Deplacement CRUD 
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_DEPLACEMENT', 'Droit sur le déplacement des hydrants', 'hydrants.deplacement', 1);

-- Affectation de droits de déplacement à tous les utilisateurs qui ont déjà le droit de suppression d'hydrant
insert into remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
 select 'TRUE','TRUE','TRUE','TRUE',1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'HYDRANTS_DEPLACEMENT'
  and pd.id in (select d.profil_droit from remocra.droit d where (d.type_droit = (select td.id from remocra.type_droit td where td.code  = 'HYDRANTS')  and d.droit_delete = true));



-- Droits sur la vérification d'hydrant : Verification CRUD -> Profil Administrateur applicatif
insert into remocra.type_droit(code, description, nom, version) values ('HYDRANTS_VERIFICATION', 'Droit sur la vérification des hydrants', 'hydrants.verification', 1);
insert into remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
  select 'TRUE','TRUE','TRUE','TRUE',1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'HYDRANTS_VERIFICATION'
  and pd.code in ('SDIS-ADM-APP');

-- Droits sur l'export d'atlas 
insert into remocra.type_droit(code, description, nom, version) values ('DFCI_EXPORTATLAS', 'Droit sur l''export d''atlas', 'dfci.exportatlas', 1);

-- Affectation de droits d'export d'atlas
insert into remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
 select 'TRUE','FALSE','FALSE','FALSE',1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'DFCI_EXPORTATLAS'
  and pd.id in (select d.profil_droit from remocra.droit d where (d.type_droit = (select td.id from remocra.type_droit td where td.code  = 'DFCI')  and d.droit_read = true));

-- Contenu réel du patch fin
--------------------------------------------------

commit;


