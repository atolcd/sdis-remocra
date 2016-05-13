SET search_path = remocra, tracabilite, pdi, public, pg_catalog;

BEGIN;

-- Désactive les traitements ci-dessous de la rubrique "Extraire, Télécharger, Téléverser"
update pdi.modele_traitement
set code = -1
where ref_nom in ('etat_hydrant_indisponibles','etat_pena_indisponibles');

COMMIT;
