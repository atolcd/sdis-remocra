SET search_path = remocra, tracabilite, public, pg_catalog;

BEGIN;


/*----------------------------------------------------------------------------------------
 * Script de mise à jour du champ date_modification de la table hydrant (le 27/01/2013 : 6min)
 *
 *   - Pour chaque hydrant, on prend la date d'opération la plus récente lue dans la table de traçabilité
 *
 *----------------------------------------------------------------------------------------*/


-- Réaliser les vérifications ci-dessous au préalable et sauvegarder la table hydrant.

/*

-- On s'assure que tous les hydrants sont tracés :
select count(*) from remocra.hydrant where numero not in (select numero from tracabilite.hydrant);
-- Le 27/01/2013 à 11h12 :  0


-- On s'assure que toutes les dates d'opération sont passées
select count(*) from tracabilite.hydrant where date_operation > now();
-- Le 27/01/2013 à 11h13 : 0

*/


-- Désactivation temporaire du trigger de traçabilité des hydrants car on ne souhaite pas tracer les modifications liées à ce script (pas une modification métier)
ALTER TABLE remocra.hydrant DISABLE TRIGGER trig_aui;

-- Mise à jour des dates
update remocra.hydrant rh set date_modification = (
  select max(date_operation) from tracabilite.hydrant th where th.numero = rh.numero
);

-- Réactivation du trigger de traçabilité des hydrants
ALTER TABLE remocra.hydrant ENABLE TRIGGER trig_aui;


COMMIT;

