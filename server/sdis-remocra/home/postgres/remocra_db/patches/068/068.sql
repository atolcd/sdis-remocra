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
    numero_patch := 68;
    description_patch := 'Regroupement des paramètres';

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

-- Renommage du paramètre dont l'utilisateur a été généralisée depuis sa mise en place
update remocra.param_conf set cle='TOLERANCE_VOIES_METRES' where cle='PERMIS_TOLERANCE_VOIES_METRES';


-- Info de regroupement (nom de groupe)
ALTER TABLE remocra.param_conf add column nomgroupe varchar not null default 'Général';


-- Regroupement (rejouable)
update remocra.param_conf set nomgroupe = 'Général';
update remocra.param_conf set nomgroupe = 'Chemins sur disque' where
  lower(cle) like '%dossier%' or lower(cle) like '%chemin%' or lower(cle) like '%fichier_parametrage%';
update remocra.param_conf set nomgroupe = 'Cartographie' where
  lower(cle) like '%_ign%' or lower(cle) like '%coordonnees%' or lower(cle) like '%orientation%' or lower(cle) like '%wms%' or lower(cle) like '%_tolerance_%';
update remocra.param_conf set nomgroupe = 'Courriels et courriers' where
  lower(cle) like '%_smtp_%' or lower(cle) like '%mail%' or lower(cle) like '%notification%'
  or lower(cle) like '%pdi_pdf_%';
update remocra.param_conf set nomgroupe = 'Synchro SIG' where
  lower(cle) like '%pdi_ftp%' or lower(cle) like '%oracle%' or lower(cle) like '%nom_schema_synchro%'
  or lower(cle) like '%export_sdis%' or lower(cle) like '%import_extranet%' or lower(cle) like '%pdi_postgresql_nom_schema%';
update remocra.param_conf set nomgroupe = 'Permis' where
  lower(cle) like 'permis%';
update remocra.param_conf set nomgroupe = 'Risques' where
  lower(cle) like '%pdi_impa%';
update remocra.param_conf set nomgroupe = 'Métadonnées' where
  lower(cle) like '%metadata%';
update remocra.param_conf set nomgroupe = 'Points d''eau' where
  lower(cle) like 'hydrant%' or lower(cle) like '%citerne%';
update remocra.param_conf set nomgroupe = 'Risques' where
  lower(cle) like '%_imap_%';
update remocra.param_conf set nomgroupe = 'Traitements et purge' where
  lower(cle) like '%_traitement_%' or lower(cle) like '%_purge_%';
update remocra.param_conf set nomgroupe = 'Authentification vers un autre service' where
  lower(cle) like '%jwt_%';


--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

