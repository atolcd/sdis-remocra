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
    numero_patch := 141;
    description_patch :='Fix boucle infine parcours graphe couverture hydraulique';

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


/**
  * VUE DES ZONES DE COMPETENCE DES ORGANISMES
  * Permet de précalculer les organismes dont la zone de compétence est contenue ou superposée avec la zone de compétence
d'autres organismes
  */
CREATE TABLE remocra.zone_competence_organisme
(
  organisme_id bigint,
  organisme_contenu_id bigint
);
ALTER TABLE remocra.zone_competence_organisme
    OWNER to remocra;

COMMENT ON TABLE remocra.zone_competence_organisme IS 'Vue des zones de compétence des organismes';
COMMENT ON COLUMN remocra.zone_competence_organisme.organisme_id IS 'Organisme principal';
COMMENT ON COLUMN remocra.zone_competence_organisme.organisme_contenu_id IS 'Organisme dont la zone de compétence est contenue ou
superposée avec la zone de compétence de l''organisme princial';

CREATE INDEX zone_competence_organisme_organisme_idx
    ON remocra.zone_competence_organisme USING btree
    (organisme_id)
    TABLESPACE pg_default;

CREATE INDEX zone_competence_organisme_organisme_contenu_idx
    ON remocra.zone_competence_organisme USING btree
    (organisme_contenu_id)
    TABLESPACE pg_default;

INSERT INTO remocra.zone_competence_organisme
SELECT DISTINCT o1.id, o2.id
FROM remocra.organisme o1
JOIN remocra.zone_competence zc1 ON zc1.id = o1.zone_competence
LEFT JOIN remocra.organisme o2 ON o2.id IS NOT NULL
LEFT JOIN remocra.zone_competence zc2 ON zc2.id = o2.zone_competence
WHERE (st_contains(st_buffer(zc1.geometrie,0), st_buffer(zc2.geometrie,0))
OR st_overlaps(st_buffer(zc1.geometrie,0), st_buffer(zc2.geometrie,0)));


/**
  * FIX FONCTION INSERTION DE JONCTION PEI
  * JOINTURE POSSIBLE SUR UN SOMMET
  * JOINTURES IMPOSSIBLES SUR GEOMETRIES DE VOIES PROBLEMATIQUES
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.inserer_jonction_pei(
	pei_id integer,
	distance_max_au_reseau integer,
	idetude integer)
    RETURNS integer
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
  DECLARE

    jonction record;
    voie1 record;
    voie2 record;
    voie2Id integer;
    voieJonctionId integer;
    sommetJonction integer;
    sommetPei integer;

  BEGIN
    --Récupère le troncon le + proche du PEI et calcule de point de jonction sur le troncon
    SELECT INTO jonction
      ST_ClosestPoint(t.geometrie, p.geometrie) AS jonction_geometrie,
	  ST_LineLocatePoint(t.geometrie, p.geometrie) AS jonction_fraction,
      p.id AS pei_id,
      p.geometrie AS pei_geometrie,
      t.id AS troncon_id,
      t.geometrie AS troncon_geometrie
    FROM
      (SELECT * FROM couverture_hydraulique.pei WHERE id = pei_id) AS p
      CROSS JOIN LATERAL
      (SELECT DISTINCT ON (p.id)
        t.id,
        t.geometrie,
        ST_Distance(t.geometrie, p.geometrie) AS distance
      FROM
        couverture_hydraulique.reseau t
      WHERE
        ST_Dwithin(p.geometrie,t.geometrie,distance_max_au_reseau)
	    AND t.etude IS NOT DISTINCT FROM idEtude
	    AND t.pei_troncon IS NULL
      ORDER BY
        p.id,
        distance ASC
      ) AS t;
    -- Si le point de jonction n'est pas null et qu'il ne corresponds pas à une extremité existante du tronçon,
    -- procède à la découpe du troncon initial en 2.
    IF jonction.jonction_geometrie IS NOT NULL AND jonction.jonction_fraction >= 0.00001 AND jonction.jonction_fraction < 1 THEN
	  --Mise à jour de géométrie du troncon à découper
      UPDATE couverture_hydraulique.reseau t SET
        geometrie = ST_LineSubstring(t.geometrie,0,jonction.jonction_fraction)
      WHERE
        id = jonction.troncon_id;

      -- Insertion du complément
      INSERT INTO couverture_hydraulique.reseau (
        geometrie, etude
      ) VALUES (
        ST_LineSubstring(jonction.troncon_geometrie,jonction.jonction_fraction,1),
		idEtude
      ) RETURNING id INTO voie2Id;

      -- Insertion de la jonction entre le PEI et le réseau
      INSERT INTO couverture_hydraulique.reseau (
        geometrie,
        pei_troncon,
		etude
      ) VALUES (
        St_MakeLine(jonction.pei_geometrie, jonction.jonction_geometrie),
        pei_id,
		idEtude
      ) RETURNING id INTO voieJonctionId;

      -- Création du sommet entre les 3 voies et du sommet sur le PEI, modification des sommets source/destination en adéquation
      SELECT * into voie1 from couverture_hydraulique.reseau where id = jonction.troncon_id;
      SELECT * into voie2 from couverture_hydraulique.reseau where id = voie2Id;
      INSERT INTO couverture_hydraulique.sommet(geometrie) VALUES(jonction.jonction_geometrie) RETURNING id INTO sommetJonction;
      INSERT INTO couverture_hydraulique.sommet(geometrie) VALUES(jonction.pei_geometrie) RETURNING id INTO sommetPei;

      UPDATE couverture_hydraulique.reseau SET destination = voie1.destination WHERE id = voie2.id;
      UPDATE couverture_hydraulique.reseau SET destination = sommetJonction WHERE id = voie1.id OR id = voieJonctionId;
      UPDATE couverture_hydraulique.reseau SET source = sommetJonction WHERE id = voie2.id;
      UPDATE couverture_hydraulique.reseau SET source = sommetPei WHERE id = voieJonctionId;

	ELSIF jonction.jonction_geometrie IS NOT NULL AND (jonction.jonction_fraction < 0.00001 OR jonction.jonction_fraction = 1) THEN
	  SELECT id INTO sommetJonction FROM couverture_hydraulique.sommet order by st_distance(geometrie, jonction.jonction_geometrie) limit 1;
	  INSERT INTO couverture_hydraulique.sommet(geometrie) VALUES(jonction.pei_geometrie) RETURNING id INTO sommetPei;
	  INSERT INTO couverture_hydraulique.reseau (
        geometrie,
        pei_troncon,
		etude,
		source,
		destination
       ) VALUES (
        St_MakeLine(jonction.pei_geometrie, jonction.jonction_geometrie),
        pei_id,
		idEtude,
		sommetPei,
		sommetJonction
       );
    END IF;

    RETURN 1;
  END;
$BODY$;

/**
  * FIX PARCOURS DU GRAPH
  * GESTION DU CAS OU POSTGIS RETOURNE UN BUFFER EN MULTIPOLYGON AU LIEU D'UN POLYGON
  * RETRAIT BOUCLE INFINIE CAUSEE PAR UN MAUVAIS SUIVI DES NOEUDS DEJA VISITES
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.parcours_couverture_hydraulique(
	depart integer,
	idetude integer,
	idreseauimporte integer)
    RETURNS integer
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
DECLARE
  tabDistances int[] := ARRAY[50,100, 250, 300];
  dist integer;
  noeudsAVisiter integer[];
  noeudsVisites integer[];
  noeudCourant integer;
  debutChemin boolean;
  voisinRecord record;
  courantRecord record;
  t record;
  distanceParcourue double precision;
  voieCourante integer;
  bufferSize integer;

  voieGauche integer;
  voieDroite integer;
  buffer geometry;
  blade geometry;
  bladeSommets geometry;
  splitResult geometry;
  bufferSommets geometry;
  bufferEndPoint geometry;
  bufferSide character varying;
  bufferEndCap character varying;
BEGIN
  TRUNCATE ONLY couverture_hydraulique.temp_distances;
  FOREACH dist in ARRAY tabDistances LOOP
    noeudsAVisiter = array[]::integer[];
    noeudsVisites = array[]::integer[];
    debutChemin = true;
    distanceParcourue = 0;
    bufferSize = 50;

    -- Premier parcourt depuis ce PEI: on récupère le premier noeud à visite
    IF tabDistances[1] = dist THEN
      DELETE FROM couverture_hydraulique.temp_distances where start = depart;
      SELECT source into noeudCourant from couverture_hydraulique.reseau where pei_troncon = depart;
      debutChemin = true;
      noeudsAVisiter = array_append(noeudsAVisiter, noeudCourant);

    -- Parcours suivants: on reprend une partie des données (le parcours N contient le parcours N-1)
    ELSE
      noeudsAVisiter = (SELECT ARRAY(
        SELECT DISTINCT sommet FROM couverture_hydraulique.temp_distances WHERE voie IN (
          SELECT DISTINCT voieprecedente
          FROM couverture_hydraulique.temp_distances
          WHERE distance = tabDistances[(array_position(tabDistances, dist)-1)]
        )
      )::int[]);

      DELETE FROM couverture_hydraulique.temp_distances where start = depart AND distance =  tabDistances[(array_position(tabDistances, dist)-1)];

    END IF;

    -- Pour tous les noeuds à visiter
    WHILE cardinality(noeudsAVisiter) > 0 LOOP
      SELECT noeudsAVisiter[1] into noeudCourant;
      noeudsVisites = array_append(noeudsVisites, noeudCourant);
      SELECT * INTO courantRecord FROM couverture_hydraulique.temp_distances WHERE start = depart AND sommet = noeudCourant ORDER BY distance LIMIT 1;

      -- Si c'est la voie de départ, il n'y a aucune occurence dans la table temp_distances. On récupère néanmoins son ID afin de pouvoir déterminer les voies partant à sa gauche et à sa droite
      IF courantRecord IS NULL THEN SELECT id INTO voieCourante FROM couverture_hydraulique.reseau WHERE pei_troncon = depart; END IF;
 noeudCourant, idReseauImporte;
      PERFORM couverture_hydraulique.voiesLaterales(COALESCE(courantRecord.voie, voieCourante), noeudCourant, idReseauImporte);
      SELECT voie FROM couverture_hydraulique.voiesLaterales WHERE gauche INTO voieGauche;
      SELECT voie FROM couverture_hydraulique.voiesLaterales WHERE droite INTO voieDroite;

      -- Pour tous les noeuds voisins
      FOR voisinRecord IN (SELECT * FROM (
        (SELECT id, destination, source, ST_LENGTH(geometrie) as distance, geometrie, pei_troncon, traversable FROM couverture_hydraulique.reseau WHERE source = noeudCourant AND (id IN (voieGauche, voieDroite) OR (voieGauche IS NULL AND voieDroite IS NULL)) AND etude IS NOT DISTINCT FROM idReseauImporte)
         UNION
        (SELECT id, source as destination, source as source, ST_LENGTH(geometrie) as distance, ST_REVERSE(geometrie), pei_troncon, traversable FROM couverture_hydraulique.reseau WHERE destination = noeudCourant AND NOT sens_unique AND (id IN (voieGauche, voieDroite) OR (voieGauche IS NULL AND voieDroite IS NULL)) AND etude IS NOT DISTINCT FROM idReseauImporte)
      ) as R) LOOP

        -- On prend les voies étant un troncon pei seulement lors du premier parcours. Lors des suivants, ces troncons sont ignorés
        CONTINUE WHEN (voisinRecord.pei_troncon > 0 AND debutChemin = FALSE) OR voisinRecord.id = courantRecord.voie;

        -- Si la voie est trop longue, on n'en parcourt qu'une partie
        IF voisinRecord.distance + COALESCE(courantRecord.distance, 0) > dist AND COALESCE(courantRecord.distance, 0) < dist THEN
          distanceParcourue = COALESCE(courantRecord.distance, 0) + voisinRecord.distance;
          Select ST_LineSubstring(voisinRecord.geometrie, 0,
            (CASE
              WHEN distanceParcourue <= dist THEN 1
              ELSE (1 -((distanceParcourue - dist)/voisinRecord.distance))
            END)
          )::geometry(LineString,2154) INTO voisinRecord.geometrie;

          SELECT ST_PointN(
            voisinRecord.geometrie,
            generate_series(2, ST_NPoints(voisinRecord.geometrie))
          ) INTO bufferEndPoint;

          distanceParcourue = dist;

        ELSE
          distanceParcourue = COALESCE(courantRecord.distance, 0) + voisinRecord.distance;
          SELECT geometrie INTO bufferEndPoint FROM couverture_hydraulique.sommet WHERE id = (CASE WHEN voisinRecord.source != noeudCourant THEN voisinRecord.source ELSE voisinRecord.destination END) AND debutChemin = false;
        END IF;

        -- Le chemin pour aller au noeud n'existe pas en mémoire ou il en existe déjà un plus long => on remplace par le chemin courant
        SELECT * INTO t FROM couverture_hydraulique.temp_distances WHERE start = depart AND sommet = voisinRecord.destination and voie = voisinRecord.id;
        IF t IS NULL AND distanceParcourue <= dist THEN

          /** =============================================== Tracé du buffer de la voie ==================================================== **/

    -- On détermine de quel côté tracer le buffer
          IF voisinRecord.traversable THEN
            bufferSide = 'both';
            bufferEndCap = 'round';
          ELSIF voieGauche = voieDroite THEN
            bufferSide = courantRecord.side;
            bufferEndCap = 'round';
          ELSIF voisinRecord.id = voieGauche THEN
            bufferSide = 'left';
            bufferEndCap = 'flat';
          ELSIF voisinRecord.id = voieDroite THEN
            bufferSide = 'right';
            bufferEndCap = 'flat';
          END IF;

          buffer = ST_BUFFER(voisinRecord.geometrie, bufferSize, CONCAT('side=', bufferSide, ' endcap=', bufferEndCap));

          -- Si le buffer traverse une voie non traversable, on retire la partie en trop
          SELECT ST_UNION(geometrie) INTO blade FROM (SELECT geometrie FROM couverture_hydraulique.reseau WHERE pei_troncon IS NULL AND NOT traversable AND id != voisinRecord.id AND ST_INTERSECTS(buffer, geometrie) AND etude IS NOT DISTINCT FROM idReseauImporte) AS R;
          IF blade IS NOT NULL THEN
            splitResult = ST_SPLIT(buffer, blade);
            IF ST_NUMGEOMETRIES(splitResult) > 1 THEN
              SELECT geom INTO buffer FROM (
                SELECT (st_dump(st_split(buffer, blade))).geom
              ) AS R ORDER BY st_distance(ST_LineInterpolatePoint(voisinRecord.geometrie, 0.5), geom) LIMIT 1;
            END IF;
          END IF;
          /** =============================================================================================================================== **/

          /** ================================== Ajout des buffer de destination des voies ===================================== **/
          IF bufferSide != 'both' THEN
            bufferSommets = ST_BUFFER(bufferEndPoint,bufferSize);
            SELECT ST_UNION(geometrie) INTO bladeSommets FROM (SELECT geometrie FROM couverture_hydraulique.reseau WHERE ST_INTERSECTS(geometrie, bufferSommets) AND pei_troncon IS NULL AND NOT traversable) AS R;

            IF bladeSommets IS NOT NULL THEN
              SELECT ST_UNION(buffer, geom) INTO buffer FROM (
                SELECT (st_dump(st_split(bufferSommets, bladeSommets))).geom
              ) AS R ORDER BY ST_AREA(ST_INTERSECTION(geom, buffer))/ST_AREA(geom) DESC LIMIT 1;
            END IF;
          END IF;
          /** =============================================================================================================================== **/

          DELETE FROM couverture_hydraulique.temp_distances WHERE start = depart AND voie = voisinRecord.id AND  sommet = voisinRecord.destination;
	  -- Si le buffer est un MultiPolygon, on le cast en Polygon
	  IF ST_GeometryType(buffer) = 'ST_MultiPolygon' THEN
	    SELECT (ST_Dump(buffer)).geom INTO buffer;
	  END IF;
          INSERT INTO couverture_hydraulique.temp_distances (start, sommet, voie, distance, geometrie, voiePrecedente, traversable, side)
            SELECT depart, voisinRecord.destination, voisinRecord.id, distanceParcourue, buffer, courantRecord.voie, voisinRecord.traversable, bufferSide;
        END IF;

        -- Si coût inférieur à la limite de recherche et le noeud n'a jamais été visité
        IF NOT noeudsVisites @> ARRAY[voisinRecord.destination] AND distanceParcourue < dist THEN
          noeudsAVisiter = array_append(noeudsAVisiter, voisinRecord.destination);
        END IF;

        debutChemin = FALSE;
      END LOOP; -- Fin parcourt des voies voisines
      noeudsAVisiter = array_remove(noeudsAVisiter, noeudCourant);
    END LOOP; -- Fin tant que des sommets restent à visiter

    -- Récupération de la géométrie de la couverture hydraulique
    DELETE FROM couverture_hydraulique.couverture_hydraulique_pei WHERE distance = dist AND pei = depart;
    INSERT INTO couverture_hydraulique.couverture_hydraulique_pei (distance, pei, geometrie, etude)
    SELECT dist, depart, st_union(geometrie), idEtude FROM couverture_hydraulique.temp_distances WHERE start = depart;

  END LOOP; -- Fin for pour chaque distance
  DELETE FROM couverture_hydraulique.voiesLaterales;
  DELETE FROM couverture_hydraulique.temp_distances;
  RETURN 1;
END
$BODY$;


/**
  * FIX RETRAIT JONCTION PEI
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.retirer_jonction_pei(
	id_pei integer)
    RETURNS integer
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
DECLARE
  tronconPei record;
  voie1 record;
  voie2 record;
  geom geometry;
BEGIN
  SELECT * INTO tronconPei FROM couverture_hydraulique.reseau WHERE pei_troncon = id_pei;
  SELECT * INTO voie1 FROM couverture_hydraulique.reseau WHERE destination = tronconPei.destination AND pei_troncon IS NULL;
  SELECT * INTO voie2 FROM couverture_hydraulique.reseau WHERE source = tronconPei.destination AND pei_troncon IS NULL;
  DELETE FROM couverture_hydraulique.sommet WHERE id = tronconPei.source;
  SELECT ST_LINEMERGE(ST_UNION(voie1.geometrie, voie2.geometrie)) INTO geom;

  IF ST_GeometryType(geom) = 'ST_LineString' THEN
    UPDATE couverture_hydraulique.reseau
      SET geometrie = geom,
          destination = voie2.destination
      WHERE id = voie1.id;
	DELETE FROM couverture_hydraulique.reseau WHERE id = voie2.id;
	DELETE FROM couverture_hydraulique.sommet WHERE id = tronconPei.destination;
  END IF;

  DELETE FROM couverture_hydraulique.reseau where pei_troncon = id_pei;
  RETURN 1;
END
$BODY$;
-- Contenu réel du patch fin
--------------------------------------------------

commit;
