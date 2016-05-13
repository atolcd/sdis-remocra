BEGIN;


-- Retours sur le module RCI

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, tracabilite, pdi, public, pg_catalog;

-- Origine et point éclosion obligatoires
alter table remocra.rci alter column origine_alerte set not null;
alter table remocra.rci alter column point_eclosion set not null;

-- Cause inutile
alter table remocra.rci drop column cause;

-- GDH date
alter table remocra.rci drop column gdh;
alter table remocra.rci add column gdh timestamp without time zone;

-- Arrivée Référents
alter table remocra.rci add column arrivee_ddtm_onf varchar;
alter table remocra.rci add column arrivee_sdis varchar;
alter table remocra.rci add column arrivee_gendarmerie varchar;
alter table remocra.rci add column arrivee_police varchar;

-- Paramètre catégorie remplacé par la famille dans le traitement PDI d'extraction
CREATE OR REPLACE VIEW pdi.vue_familles_promethee AS 
 SELECT allin.id, allin.libelle
   FROM (         SELECT (-1) AS id, 'Toutes'::character varying AS libelle, NULL::character varying AS tricol
        UNION 
                 SELECT type_rci_prom_famille.id, type_rci_prom_famille.nom AS libelle, type_rci_prom_famille.nom AS tricol
                   FROM remocra.type_rci_prom_famille) allin
  ORDER BY allin.tricol NULLS FIRST;

ALTER TABLE pdi.vue_familles_promethee
  OWNER TO postgres;

drop view pdi.vue_categorie_promethee;

update pdi.modele_traitement_parametre set form_etiquette='Famille prométhée', form_source_donnee='vue_familles_promethee', nom='FAMILLE_PROMETHEE_ID' where 
form_etiquette='Catégorie prométhée' and form_source_donnee='vue_categorie_promethee' and nom='CATEGORIE_PROMETHEE_ID' and idmodele=(select idmodele from modele_traitement where ref_chemin='/demandes/rci' and ref_nom='export_xls');


COMMIT;
