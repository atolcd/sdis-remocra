BEGIN;

-- modification du chemin du traitement 'hydrants à numeroter'
update pdi.modele_traitement
set ref_chemin = '/demandes/statistiques_hydrants/hydrants_a_numeroter'
where ref_nom = 'hydrants_a_numeroter';

COMMIT;