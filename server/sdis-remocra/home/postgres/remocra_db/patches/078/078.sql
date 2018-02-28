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
    numero_patch := 78;
    description_patch := 'Ajout du modèle de traitement PEI non receptionnés';

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


--Vue des organismes de Remocra pour les demandes de traitement
CREATE OR REPLACE VIEW pdi.vue_organisme AS 
   SELECT o.id, o.nom || ' (' || tyo.nom || ')' AS libelle, o.nom AS tricol
   FROM remocra.organisme o
      JOIN remocra.type_organisme tyo ON o.type_organisme = tyo.id
   WHERE o.actif = true
  ORDER BY o.tricol NULLS FIRST;

ALTER TABLE pdi.vue_organisme
  OWNER TO postgres;


 -- Traitement de génération du tableau des hydrants non réceptionnés
INSERT INTO pdi.modele_traitement(
            idmodele, code, description, nom, ref_chemin, ref_nom, type, 
            message_echec, message_succes)
    VALUES ((SELECT MAX(idmodele)+1 FROM pdi.modele_traitement), 1, 'Génère un tableau avec la liste des PEI non réceptionnés', 'Etat des PEI non réceptionnés', '/demandes/statistiques_hydrants', 'etat_hydrant_non_receptionne', 'T', 
            3, 1);


--Paramètre du traitement de génération du tableau des hydrants non réceptionnés
INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Organisme', '1', true, 'vue_organisme',
            'combo', null, 'ORGANISME_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));

INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Profil utilisateur à notifer', 2, true, 'vue_profil_sdis',
            'combo', null, 'PROFIL_UTILISATEUR_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));


-- Contenu réel du patch fin
--------------------------------------------------

commit;

