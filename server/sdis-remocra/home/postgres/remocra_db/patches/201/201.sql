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
    numero_patch := 201;
    description_patch := 'Met à jour la méthode trouver la plus proche PEI';

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

CREATE OR REPLACE FUNCTION couverture_hydraulique.plus_proche_pei(geomclic geometry, distance_max_parcours integer, idreseauimporte integer)
 RETURNS TABLE(pei integer, chemin text, dist double precision)
 LANGUAGE plpgsql
AS $function$
DECLARE
  jonction record;
  distance_max_au_reseau integer := 100;
  distanceParcourue double precision; -- Distance actuelle parcourue sur le graph
  distancePlusProche double precision; -- Distance avec le pei le plus proche trouvé
  tronconPeiProche integer; -- ID de la voie servant de jonction entre le pei le plus proche et le graph
  plusProchePredecesseur integer;
  predecesseur record;
  plusProchePei integer;
  chemin geometry;

  -- Variables du parcours de graph
  noeudsAVisiter integer[];
  noeudsVisites integer[];
  noeudCourant integer;
  voisinRecord record;

BEGIN

  -- Jonction des PEI existants dans un certain rayon au graph
  PERFORM couverture_hydraulique.inserer_jonction_pei(p.id, distance_max_au_reseau, idReseauImporte, false)
  FROM couverture_hydraulique.pei p
  WHERE ST_DISTANCE(p.geometrie, geomClic) <= distance_max_parcours;

  -- Distance du clic au réseau routier
  SELECT * INTO jonction FROM (
    SELECT t.id AS troncon_id,
      ST_ClosestPoint(t.geometrie, geomClic) AS jonction_geometrie,
      ST_LineLocatePoint(t.geometrie, geomClic) AS jonction_fraction,
      t.geometrie AS voie_geometrie,
      t.source AS voie_source,
      t.destination AS voie_destination,
    t.geometrie AS voie_geometrie,
      ST_Distance(t.geometrie, geomClic) AS distance
    FROM couverture_hydraulique.reseau t
    WHERE
        ST_Dwithin(geomClic, t.geometrie, distance_max_au_reseau)
      AND t.etude IS NOT DISTINCT FROM idReseauImporte
      AND t.pei_troncon IS NULL
      ORDER BY
        distance ASC
  ) AS t;

  -- Jonction réalisable: on commence le parcours
  -- On autorise les jonctions aux fractions 0 et 1 (strict début et fin de la géométrie)
  IF jonction.jonction_geometrie IS NOT NULL THEN

   CREATE TEMP TABLE temp_chemins(
     noeud integer,
     distance double precision,
     predecesseur integer,
     geometrie geometry
   );
   INSERT INTO temp_chemins VALUES
   (jonction.voie_source, jonction.distance + jonction.jonction_fraction * ST_Length2D(jonction.voie_geometrie), NULL),
   (jonction.voie_destination, jonction.distance + (1-jonction.jonction_fraction) * ST_Length2D(jonction.voie_geometrie), NULL);

   noeudsAVisiter = array_append(noeudsAVisiter, jonction.voie_source);
   noeudsAVisiter = array_append(noeudsAVisiter, jonction.voie_destination);

   WHILE cardinality(noeudsAVisiter) > 0 LOOP
     SELECT noeudsAVisiter[1] into noeudCourant;
   noeudsVisites = array_append(noeudsVisites, noeudCourant);

   SELECT distance INTO distanceParcourue FROM temp_chemins WHERE noeud = noeudCourant;

   -- Pour toutes les voies voisines
   FOR voisinRecord IN (SELECT * FROM (
        (SELECT id, destination, source, ST_LENGTH(geometrie) as distance, geometrie, pei_troncon, traversable FROM couverture_hydraulique.reseau WHERE source = noeudCourant AND etude IS NOT DISTINCT FROM idReseauImporte)
         UNION
        (SELECT id, source as destination, source as source, ST_LENGTH(geometrie) as distance, ST_REVERSE(geometrie), pei_troncon, traversable FROM couverture_hydraulique.reseau WHERE destination = noeudCourant AND etude IS NOT DISTINCT FROM idReseauImporte)
      ) as R) LOOP

      -- On a trouvé un PEI, on vérifie sa distance
    IF (voisinRecord.pei_troncon IS NOT NULL) THEN
      IF (distancePlusProche IS NULL OR distancePlusProche > voisinRecord.distance + distanceParcourue) THEN
        distancePlusProche = voisinRecord.distance + distanceParcourue;
      tronconPeiProche = voisinRecord.id;
      plusProchePredecesseur = noeudCourant;
      plusProchePei = voisinRecord.pei_troncon;
      END IF;
    ELSE
      -- Si on a trouvé un chemin plus rapide pour aller à ce noeud, on le remplace
      IF (SELECT distance FROM temp_chemins WHERE noeud = voisinRecord.destination) > voisinRecord.distance + distanceParcourue THEN
        DELETE FROM temp_chemins WHERE noeud = voisinRecord.destination;
        INSERT INTO temp_chemins(noeud, distance, predecesseur, geometrie) VALUES
    (voisinRecord.destination, voisinRecord.distance + distanceParcourue, noeudCourant, voisinRecord.geometrie);
      -- Si il n'existe pas encore de chemin
      ELSIF (SELECT distance FROM temp_chemins WHERE noeud = voisinRecord.destination) IS NULL THEN
        INSERT INTO temp_chemins(noeud, distance, predecesseur, geometrie) VALUES
        (voisinRecord.destination, voisinRecord.distance + distanceParcourue, noeudCourant, voisinRecord.geometrie);
      END IF;

      -- Si le noeud n'est pas encore marqué comme visité, qu'on ne l'a pas visité et que l'on n'a pas encore atteint la distance max, on le rajoute à parcourir
      IF NOT noeudsVisites @> ARRAY[voisinRecord.destination] AND NOT noeudsAVisiter @> ARRAY[voisinRecord.destination] AND voisinRecord.distance + distanceParcourue < distance_max_parcours THEN
      noeudsAVisiter = array_append(noeudsAVisiter, voisinRecord.destination);
      END IF;
    END IF;

    END LOOP;
     noeudsAVisiter = array_remove(noeudsAVisiter, noeudCourant);
   END LOOP;

   -- Reconstitution du chemin
   IF tronconPeiProche IS NOT NULL THEN
       chemin = couverture_hydraulique.safe_union(chemin, ST_MakeLine(geomClic, jonction.jonction_geometrie));
     WHILE plusProchePredecesseur IS NOT NULL LOOP
       SELECT * INTO predecesseur FROM temp_chemins WHERE noeud = plusProchePredecesseur;
       plusProchePredecesseur = predecesseur.predecesseur;
       chemin = couverture_hydraulique.safe_union(chemin, predecesseur.geometrie);
     END LOOP;

   -- Troncon clic - réseau routier
     IF predecesseur.noeud = jonction.voie_source THEN
       chemin = couverture_hydraulique.safe_union(chemin, ST_LineSubstring(jonction.voie_geometrie,0,jonction.jonction_fraction));
     ELSIF predecesseur.noeud = jonction.voie_destination THEN
       chemin = couverture_hydraulique.safe_union(chemin, ST_LineSubstring(jonction.voie_geometrie,jonction.jonction_fraction, 1));
     END IF;

   -- Troncon réseau routier - PEI
     chemin = couverture_hydraulique.safe_union(chemin, (select geometrie from couverture_hydraulique.reseau where id = tronconPeiProche));
   END IF;

   -- Retrait jonctions PEI
   PERFORM couverture_hydraulique.retirer_jonction_pei(id)
   FROM couverture_hydraulique.pei p
   WHERE ST_DISTANCE(p.geometrie, geomClic) <= distance_max_parcours;

   DROP TABLE temp_chemins;
   RETURN QUERY
   SELECT plusProchePei, ST_AsText(chemin), distancePlusProche;

  ELSE
   -- Retrait jonctions PEI
   PERFORM couverture_hydraulique.retirer_jonction_pei(id)
   FROM couverture_hydraulique.pei p
   WHERE ST_DISTANCE(p.geometrie, geomClic) <= distance_max_parcours;

   RETURN QUERY
   SELECT -1, NULL, 0::DOUBLE PRECISION;
  END IF;
END
$function$
;


-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
