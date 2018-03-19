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
    numero_patch := 82;
    description_patch := 'Ajout d''un paramètre de configuration d''intervalle pour le modèle de traitement de mise en indispo des PEI';

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

INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire, 
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom, 
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Intervalle de temps de sélection des nouveaux PEI indisponibles', 5, true, 
            '', 'textfield', '24', 'INTERVAL_INDISPO', 
            (SELECT idmodele FROM pdi.modele_traitement WHERE nom = 'Mise en indisponibilité d''un PEI - courrier d''information'));
			
-- Contenu réel du patch fin
--------------------------------------------------

commit;
