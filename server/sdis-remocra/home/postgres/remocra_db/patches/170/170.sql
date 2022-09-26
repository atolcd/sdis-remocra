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
    numero_patch := 170;
    description_patch := 'Couverture hydraulique : reprise calcul des voies traversables et en sens unique';

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

ALTER TABLE couverture_hydraulique.voieslaterales
ADD COLUMN accessible BOOLEAN DEFAULT TRUE;

COMMENT ON COLUMN couverture_hydraulique.voieslaterales.accessible is 'Voie accessible depuis le point de jonction (non accessible si entre 
les voies gauche et droite si ces dernières sont non traversables)';

CREATE OR REPLACE FUNCTION couverture_hydraulique.inserer_jonction_pei(
	pei_id integer,
	distance_max_au_reseau integer,
	idetude integer)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
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
    IF jonction.jonction_geometrie IS NOT NULL AND jonction.jonction_fraction >= 0.00001 AND jonction.jonction_fraction < 0.99999 THEN
	  --Mise à jour de géométrie du troncon à découper
      UPDATE couverture_hydraulique.reseau t SET
        geometrie = ST_LineSubstring(t.geometrie,0,jonction.jonction_fraction)
      WHERE
        id = jonction.troncon_id;

      -- Insertion du complément
	  SELECT * INTO voie1 FROM couverture_hydraulique.reseau WHERE id = jonction.troncon_id;
      INSERT INTO couverture_hydraulique.reseau (
        geometrie, etude, traversable, sens_unique, niveau
      ) VALUES (
        ST_LineSubstring(jonction.troncon_geometrie,jonction.jonction_fraction,1),
		idEtude,
		voie1.traversable,
		voie1.sens_unique,
		voie1.niveau
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

ALTER FUNCTION couverture_hydraulique.inserer_jonction_pei(integer, integer, integer)
    OWNER TO remocra;
    
CREATE OR REPLACE FUNCTION couverture_hydraulique.voieslaterales(
	depart integer,
	matchingpoint integer,
	idetude integer)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
  voieCourante record;
  voieVoisine record;
  geometrieCourante geometry;
  geometrieVoisine geometry;
BEGIN

  TRUNCATE ONLY couverture_hydraulique.voiesLaterales;
  SELECT * INTO voieCourante FROM couverture_hydraulique.reseau WHERE id = depart AND etude IS NOT DISTINCT FROM idEtude;
  -- On ne prend qu'une toute petite section de la voie courante (5%) pour éviter les formes exotiques
  SELECT ST_LineSubstring(
    (CASE WHEN voieCourante.destination != matchingPoint THEN st_reverse(voieCourante.geometrie) ELSE voieCourante.geometrie END),
    0.95, 1)::geometry(LineString,2154) INTO geometrieCourante;

  -- Pour chaque voie voisine (reliée à notre croisement)
  FOR voieVoisine IN (SELECT * FROM (
    (SELECT id, destination, source, ST_LENGTH(geometrie) AS distance, geometrie, pei_troncon, r.traversable FROM couverture_hydraulique.reseau r WHERE source = matchingPoint and pei_troncon IS NULL AND id != depart AND etude IS NOT DISTINCT FROM idEtude) 
     UNION
    (SELECT id, source as destination, source as source, ST_LENGTH(geometrie) as distance, geometrie, pei_troncon, r.traversable FROM couverture_hydraulique.reseau r WHERE destination = matchingPoint AND pei_troncon IS NULL AND id != depart AND etude IS NOT DISTINCT FROM idEtude)
  ) as R) LOOP

    IF depart != voieVoisine.id THEN
      -- On ne prend qu'une toute petite section de la voie voisine (5%)
      Select ST_LineSubstring(
        (CASE when voieVoisine.source != matchingPoint THEN st_reverse(voieVoisine.geometrie) ELSE voieVoisine.geometrie END),
         0.05, 1)::geometry(LineString,2154) INTO geometrieVoisine;
     -- On calcule l'angle et on stocke le résultat
     INSERT INTO couverture_hydraulique.voiesLaterales (voie, angle, gauche, droite, traversable, accessible) 
     VALUES(voieVoisine.id, ST_ANGLE(ST_StartPoint(geometrieCourante), ST_EndPoint(geometrieCourante), ST_StartPoint(geometrieVoisine)), false, false, voieVoisine.traversable, true);

    END IF;
  END LOOP;

  -- On marque les voies de gauche et de droite
  UPDATE couverture_hydraulique.voiesLaterales SET gauche = TRUE WHERE angle = (SELECT min(angle) FROM couverture_hydraulique.voiesLaterales);
  UPDATE couverture_hydraulique.voiesLaterales SET droite = TRUE WHERE angle = (SELECT max(angle) FROM couverture_hydraulique.voiesLaterales);
  
  /*
   On marque toutes les voies non accessibles
   Les voies non accessibles sont celles situées entre les voies de gauche et de droite si ces dernières sont non traversables (on ne traverse pas le carrefour)
  */
  IF (SELECT COUNT(*) FROM couverture_hydraulique.voiesLaterales) >= 3 
    AND (SELECT traversable FROM couverture_hydraulique.voiesLaterales WHERE gauche = TRUE) = FALSE
	AND (SELECT traversable FROM couverture_hydraulique.voiesLaterales WHERE droite = TRUE) = FALSE
  THEN
    UPDATE couverture_hydraulique.voiesLaterales
	SET accessible = FALSE
	WHERE angle > (SELECT angle FROM couverture_hydraulique.voieslaterales WHERE gauche = true)
    AND angle < (SELECT angle FROM couverture_hydraulique.voiesLaterales WHERE droite = true);
  END IF;
END
$BODY$;

ALTER FUNCTION couverture_hydraulique.voieslaterales(integer, integer, integer)
    OWNER TO remocra;
    
CREATE OR REPLACE FUNCTION couverture_hydraulique.parcours_couverture_hydraulique(
	depart integer,
	idetude integer,
	idreseauimporte integer,
	isodistances integer[])
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
  tabDistances int[];
  dist integer;
  noeudsAVisiter integer[];
  noeudsVisites integer[];
  noeudCourant integer;
  debutChemin boolean;
  voisinRecord record;
  voisinRecordFromVoieLaterale record;
  premiereVoieNonTraversableRencontree record;
  courantRecord record;
  t record;
  distanceParcourue double precision;
  voieCourante integer;
  bufferSize integer;
  bufferSizeRestreint integer;
  voieGauche record;
  voieDroite record;
  buffer geometry;
  blade geometry;
  bladeSommets geometry;
  splitResult geometry;
  bufferSommets geometry;
  bufferEndPoint geometry;
  bufferSide character varying;
  bufferEndCap character varying;
  recordCouverture record;
BEGIN
  TRUNCATE ONLY couverture_hydraulique.temp_distances;
  bufferSize = 50; -- Buffer par défaut
  bufferSizeRestreint = 5; -- Buffer pour les voies restreintes (pont, tunnels, etc) via champ niveau != 0
  
  -- On trie les distances par ordre croissant et on retranche la taille du buffer
  SELECT ARRAY(SELECT unnest-bufferSize FROM unnest(isodistances) ORDER BY unnest) INTO tabDistances;
  FOREACH dist in ARRAY tabDistances LOOP
    noeudsAVisiter = array[]::integer[];
    noeudsVisites = array[]::integer[];
    debutChemin = true;
    distanceParcourue = 0;
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
    PERFORM couverture_hydraulique.voiesLaterales(COALESCE(courantRecord.voie, voieCourante), noeudCourant, idReseauImporte);
      SELECT * FROM couverture_hydraulique.voiesLaterales WHERE gauche INTO voieGauche;
      SELECT * FROM couverture_hydraulique.voiesLaterales WHERE droite INTO voieDroite;
	  
      -- Pour tous les noeuds voisins
      FOR voisinRecord IN (SELECT * FROM (
        (SELECT id, destination, source, ST_LENGTH(geometrie) as distance, geometrie, pei_troncon, traversable, niveau FROM couverture_hydraulique.reseau WHERE source = noeudCourant AND (id IN ((SELECT voie FROM couverture_hydraulique.voieslaterales)) OR (voieGauche.voie IS NULL AND voieDroite.voie IS NULL)) AND etude IS NOT DISTINCT FROM idReseauImporte)
         UNION
        (SELECT id, source as destination, source as source, ST_LENGTH(geometrie) as distance, ST_REVERSE(geometrie), pei_troncon, traversable, niveau FROM couverture_hydraulique.reseau WHERE destination = noeudCourant AND NOT sens_unique AND (id IN ((SELECT voie FROM couverture_hydraulique.voieslaterales)) OR (voieGauche.voie IS NULL AND voieDroite.voie IS NULL)) AND etude IS NOT DISTINCT FROM idReseauImporte)
      ) as R) LOOP
	  
        -- On prend les voies étant un troncon pei seulement lors du premier parcours. Lors des suivants, ces troncons sont ignorés
        CONTINUE WHEN (voisinRecord.pei_troncon > 0 AND debutChemin = FALSE) OR voisinRecord.id = courantRecord.voie;
		
		SELECT * INTO voisinRecordFromVoieLaterale FROM couverture_hydraulique.voiesLaterales WHERE voie = voisinRecord.id;
        -- Si c'est une voie non accessible (carrefour où l'on doit passer auparavant par des voies gauche et droite non traversables), on stoppe
		CONTINUE WHEN (voisinRecordFromVoieLaterale.accessible IS NOT NULL AND voisinRecordFromVoieLaterale.accessible = FALSE);
		IF(voieGauche IS NOT NULL AND voieDroite IS NOT NULL AND voieGauche.voie != voieDroite.voie) THEN
		  -- Si c'est une voie à gauche, que la voie courante est non traversable et que l'on trace le buffer sur notre droite et que ce
		  -- n'est pas la première voie non traversable rencontrée sur notre droite
		  SELECT * INTO premiereVoieNonTraversableRencontree
		  FROM couverture_hydraulique.voieslaterales
		  WHERE traversable = FALSE
		  ORDER BY ANGLE DESC LIMIT 1;
		  CONTINUE WHEN (voisinRecordFromVoieLaterale.gauche = TRUE 
						 AND courantRecord.traversable = FALSE 
						 AND courantRecord.side = 'right'
						 AND premiereVoieNonTraversableRencontree.voie != voisinRecordFromVoieLaterale.voie);
		
		  -- Si c'est une voie à droite, que la voie courante est non traversable, que l'on trace le buffer sur notre gauche et que ce
		  -- n'est pas la première voie non traversable rencontrée sur notre gauche
		  SELECT * INTO premiereVoieNonTraversableRencontree
		  FROM couverture_hydraulique.voieslaterales
		  WHERE traversable = FALSE
		  ORDER BY ANGLE LIMIT 1;
		  CONTINUE WHEN (voisinRecordFromVoieLaterale.droite = TRUE 
						 AND courantRecord.traversable = FALSE 
						 AND courantRecord.side = 'left'
						 AND premiereVoieNonTraversableRencontree.voie != voisinRecordFromVoieLaterale.voie);
		END IF;

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
          ELSIF voieGauche.voie = voieDroite.voie THEN
            bufferSide = courantRecord.side;
            bufferEndCap = 'round';
          ELSIF voisinRecord.id = voieGauche.voie THEN
            --bufferSide = 'left';
			bufferSide = CASE
			  WHEN courantRecord.side = 'both' THEN 'left'
			  ELSE courantRecord.side
			END;
            bufferEndCap = 'flat';
          ELSIF voisinRecord.id = voieDroite.voie THEN
            --bufferSide = 'right';
			bufferSide = CASE
			  WHEN courantRecord.side = 'both' THEN 'right'
			  ELSE courantRecord.side
			END;
            bufferEndCap = 'flat';
          END IF;
		  
		  buffer = ST_BUFFER(voisinRecord.geometrie, (
		    CASE WHEN voisinRecord.niveau != 0 THEN bufferSizeRestreint
		    ELSE bufferSize END
		  ), CONCAT('side=', bufferSide, ' endcap=', bufferEndCap));

          -- Si le buffer traverse une voie non traversable, on retire la partie en trop
          SELECT ST_UNION(geometrie) INTO blade FROM (SELECT geometrie FROM couverture_hydraulique.reseau WHERE pei_troncon IS NULL AND NOT traversable AND niveau = 0 AND id != voisinRecord.id AND ST_INTERSECTS(buffer, geometrie) AND etude IS NOT DISTINCT FROM idReseauImporte) AS R;
          IF blade IS NOT NULL THEN
            splitResult = ST_SPLIT(buffer, blade);
            IF ST_NUMGEOMETRIES(splitResult) > 1 THEN
              SELECT geom INTO buffer FROM (
                SELECT (st_dump(st_split(buffer, blade))).geom
              ) AS R ORDER BY st_distance(ST_LineInterpolatePoint(voisinRecord.geometrie, 0.001), geom) LIMIT 1;
            END IF;
          END IF;
          /** =============================================================================================================================== **/

		  /** ================================== Ajout des buffer de destination des voies ===================================== **/
          IF bufferSide != 'both' THEN
            bufferSommets = ST_BUFFER(bufferEndPoint,bufferSize);
            SELECT ST_UNION(geometrie) INTO bladeSommets FROM (SELECT geometrie FROM couverture_hydraulique.reseau WHERE ST_INTERSECTS(geometrie, bufferSommets) AND pei_troncon IS NULL AND NOT traversable AND niveau = 0) AS R;
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
    SELECT dist, depart, idEtude, ST_Union(geometrie) as geometrie INTO recordCouverture 
      FROM couverture_hydraulique.temp_distances WHERE start = depart;
	  
	-- Si MultiPolygon, on cherche à récupérer un Polygon
	IF ST_GeometryType(recordCouverture.geometrie) = 'ST_MultiPolygon' THEN
	  recordCouverture.geometrie = ST_BUFFER(ST_BUFFER(recordCouverture.geometrie, 0.001), -0,001);
    END IF;
	
	-- Si la condition n'est pas validée à ce stade, l'erreur provient très probablement du jeu de données
	IF ST_GeometryType(recordCouverture.geometrie) = 'ST_Polygon' THEN
      INSERT INTO couverture_hydraulique.couverture_hydraulique_pei (distance, pei, etude, geometrie)
      VALUES(recordCouverture.dist, recordCouverture.depart, recordCouverture.idEtude, recordCouverture.geometrie);
	END IF;
  END LOOP; -- Fin for pour chaque distance
  
  DELETE FROM couverture_hydraulique.voiesLaterales;
  DELETE FROM couverture_hydraulique.temp_distances;
  
  RETURN 1;
END
$BODY$;
ALTER FUNCTION couverture_hydraulique.parcours_couverture_hydraulique(integer, integer, integer, integer[])
    OWNER TO remocra;

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
