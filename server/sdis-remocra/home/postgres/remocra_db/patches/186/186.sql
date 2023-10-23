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
    numero_patch := 186;
    description_patch := 'Crée un paramètre de configuration correspondant à la profondeur de couverture pour la planification DECI + le prend en compte dans les fonctions SQL';

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


INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('PROFONDEUR_COUVERTURE', 'Profondeur de la couverture', '50', 1);

DROP FUNCTION couverture_hydraulique.parcours_couverture_hydraulique(integer, integer, integer, integer[]);
CREATE OR REPLACE FUNCTION couverture_hydraulique.parcours_couverture_hydraulique(
    depart integer,
    idetude integer,
    idreseauimporte integer,
    isodistances integer[],
    profondeurcouverture integer)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
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
  bufferSizeRestreint = 5; -- Buffer pour les voies restreintes (pont, tunnels, etc) via champ niveau != 0

  -- On trie les distances par ordre croissant et on retranche la taille du buffer
  SELECT ARRAY(SELECT unnest-profondeurcouverture FROM unnest(isodistances) ORDER BY unnest) INTO tabDistances;

  FOREACH dist in ARRAY tabDistances LOOP
    noeudsAVisiter = array[]::integer[];
    noeudsVisites = array[]::integer[];
    debutChemin = true;
    distanceParcourue = 0;
    -- Premier parcourt depuis ce PEI: on récupère le premier noeud à visite
    IF tabDistances[1] = dist THEN
      DELETE FROM couverture_hydraulique.temp_distances where start = depart;
      SELECT source into noeudCourant from couverture_hydraulique.reseau where pei_troncon = depart;
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
      DELETE FROM couverture_hydraulique.temp_distances
          where start = depart
          AND distance = tabDistances[(array_position(tabDistances, dist)-1)];
    END IF;
    -- Pour tous les noeuds à visiter
    WHILE cardinality(noeudsAVisiter) > 0 LOOP
      SELECT noeudsAVisiter[1] into noeudCourant;
      noeudsVisites = array_append(noeudsVisites, noeudCourant);
      SELECT * INTO courantRecord FROM couverture_hydraulique.temp_distances
          WHERE start = depart AND sommet = noeudCourant
          ORDER BY distance LIMIT 1;
      -- Si c'est la voie de départ, il n'y a aucune occurence dans la table temp_distances. On récupère néanmoins son ID afin de pouvoir déterminer les voies partant à sa gauche et à sa droite
      IF courantRecord IS NULL THEN
        SELECT id INTO voieCourante FROM couverture_hydraulique.reseau WHERE pei_troncon = depart;
      END IF;

      PERFORM couverture_hydraulique.voiesLaterales(COALESCE(courantRecord.voie, voieCourante), noeudCourant, idReseauImporte);
      SELECT * FROM couverture_hydraulique.voiesLaterales WHERE gauche INTO voieGauche;
      SELECT * FROM couverture_hydraulique.voiesLaterales WHERE droite INTO voieDroite;

      -- Pour tous les noeuds voisins
      FOR voisinRecord IN (SELECT * FROM (
        (SELECT id, destination, source, ST_LENGTH(geometrie) as distance, geometrie, pei_troncon, traversable, niveau
        	FROM couverture_hydraulique.reseau
        	WHERE source = noeudCourant
        	AND (id IN ((SELECT voie FROM couverture_hydraulique.voieslaterales)) OR (voieGauche.voie IS NULL AND voieDroite.voie IS NULL))
        	AND etude IS NOT DISTINCT FROM idReseauImporte)
         UNION
        (SELECT id, source as destination, source as source, ST_LENGTH(geometrie) as distance, ST_REVERSE(geometrie), pei_troncon, traversable, niveau
        	FROM couverture_hydraulique.reseau
        	WHERE destination = noeudCourant
        	AND (id IN ((SELECT voie FROM couverture_hydraulique.voieslaterales)) OR (voieGauche.voie IS NULL AND voieDroite.voie IS NULL))
        	AND etude IS NOT DISTINCT FROM idReseauImporte)
      ) as R ) LOOP

        -- On prend les voies étant un troncon pei seulement lors du premier parcours. Lors des suivants, ces troncons sont ignorés
        CONTINUE WHEN (voisinRecord.pei_troncon > 0 AND debutChemin = FALSE) OR voisinRecord.id = courantRecord.voie;

		SELECT * INTO voisinRecordFromVoieLaterale FROM couverture_hydraulique.voiesLaterales
		    WHERE voie = voisinRecord.id;

        -- Si c'est une voie non accessible (carrefour où l'on doit passer auparavant par des voies gauche et droite non traversables), on stoppe
		CONTINUE WHEN (voisinRecordFromVoieLaterale.accessible IS NOT NULL AND voisinRecordFromVoieLaterale.accessible = FALSE);

		IF(voieGauche IS NOT NULL AND voieDroite IS NOT NULL AND voieGauche.voie != voieDroite.voie) THEN
		  -- Si c'est une voie à gauche, que la voie courante est non traversable et que l'on trace le buffer sur notre droite et que ce
		  -- n'est pas la première voie non traversable rencontrée sur notre droite
		  SELECT * INTO premiereVoieNonTraversableRencontree
		  FROM couverture_hydraulique.voieslaterales
		  WHERE traversable = false
		  ORDER BY ANGLE DESC LIMIT 1;
		  CONTINUE WHEN (voisinRecordFromVoieLaterale.gauche = TRUE
						 and courantRecord.traversable = FALSE
						 AND courantRecord.side = 'right'
						 AND premiereVoieNonTraversableRencontree.voie != voisinRecordFromVoieLaterale.voie);

		  -- Si c'est une voie à droite, que la voie courante est non traversable, que l'on trace le buffer sur notre gauche et que ce
		  -- n'est pas la première voie non traversable rencontrée sur notre gauche
		  SELECT * INTO premiereVoieNonTraversableRencontree
		  FROM couverture_hydraulique.voieslaterales
		  WHERE traversable = FALSE
		  ORDER BY ANGLE LIMIT 1;
		  CONTINUE WHEN (voisinRecordFromVoieLaterale.droite = TRUE
						  and courantRecord.traversable = FALSE
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
          SELECT geometrie INTO bufferEndPoint FROM couverture_hydraulique.sommet
            WHERE id = (
                CASE
                    WHEN voisinRecord.source != noeudCourant THEN voisinRecord.source
                    ELSE voisinRecord.destination
                END)
            AND debutChemin = false;
        END IF;

        -- Le chemin pour aller au noeud n'existe pas en mémoire ou il en existe déjà un plus long => on remplace par le chemin courant
        SELECT * INTO t FROM couverture_hydraulique.temp_distances
            WHERE start = depart AND sommet = voisinRecord.destination and voie = voisinRecord.id;

        IF t IS NULL AND distanceParcourue <= dist THEN
              /** =============================================== Tracé du buffer de la voie ==================================================== **/
              -- On détermine de quel côté tracer le buffer
            IF courantRecord.traversable = false and voisinRecord.traversable = false then
                bufferSide = courantRecord.side;
                bufferEndCap = 'round';
              elsif voisinRecord.traversable THEN
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
            ELSE profondeurcouverture END
          ), CONCAT('side=', bufferSide, ' endcap=', bufferEndCap));

          -- Si le buffer traverse une voie non traversable, on retire la partie en trop
          SELECT ST_UNION(geometrie) INTO blade
            FROM (SELECT geometrie FROM couverture_hydraulique.reseau
                    WHERE pei_troncon IS NULL AND NOT traversable
                    AND niveau = 0 AND id != voisinRecord.id AND ST_INTERSECTS(buffer, geometrie)
                    AND etude IS NOT DISTINCT FROM idReseauImporte) AS R;

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
            bufferSommets = ST_BUFFER(bufferEndPoint,profondeurcouverture);
            SELECT ST_UNION(geometrie) INTO bladeSommets
                FROM (SELECT geometrie FROM couverture_hydraulique.reseau
                        WHERE ST_INTERSECTS(geometrie, bufferSommets) AND pei_troncon IS NULL
                        AND NOT traversable AND niveau = 0) AS R;

            IF bladeSommets IS NOT NULL THEN
              SELECT ST_UNION(buffer, geom) INTO buffer FROM (
                SELECT (st_dump(st_split(bufferSommets, bladeSommets))).geom
              ) AS R ORDER BY ST_AREA(ST_INTERSECTION(geom, buffer))/ST_AREA(geom) DESC LIMIT 1;
            END IF;
          END IF;
          /** =============================================================================================================================== **/
          DELETE FROM couverture_hydraulique.temp_distances
            WHERE start = depart AND voie = voisinRecord.id AND  sommet = voisinRecord.destination;

          -- Si le buffer est un MultiPolygon, on le cast en Polygon
          IF ST_GeometryType(buffer) = 'ST_MultiPolygon' THEN
            SELECT (ST_Dump(buffer)).geom INTO buffer;
          END IF;
          INSERT INTO couverture_hydraulique.temp_distances (start, sommet, voie, distance, geometrie, voiePrecedente, traversable, side)
            SELECT depart, voisinRecord.destination, voisinRecord.id, distanceParcourue, buffer,
                courantRecord.voie, voisinRecord.traversable, bufferSide;
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
$function$
;
ALTER FUNCTION couverture_hydraulique.parcours_couverture_hydraulique(integer, integer, integer, integer[], integer)
    OWNER TO remocra;



DROP FUNCTION couverture_hydraulique.couverture_hydraulique_zonage(integer, int[]);
CREATE OR REPLACE FUNCTION couverture_hydraulique.couverture_hydraulique_zonage(
    idetude integer,
    isodistances integer[],
    profondeurCouverture integer)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
  tabDistances int[] := ARRAY[50, 100, 250, 300];
  dist integer;
  grosDebits int[];
  couverture_pei record;
  couverture_voisin record;
  couverture_risque_courant_faible geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_risque_courant_ordinaire geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_risque_important geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_risque_particulier geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_distance geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
BEGIN

  SELECT ARRAY(SELECT unnest-profondeurCouverture FROM unnest(isodistances) ORDER BY unnest) INTO tabDistances;

  -- Tracé des zones d'isodistances (fonctionnement commun à tous les SDIS)
  FOREACH dist in ARRAY tabDistances LOOP
    DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = dist || 'm' AND etude IS NOT DISTINCT FROM idEtude;
	FOR couverture_pei IN (SELECT * FROM couverture_hydraulique.couverture_hydraulique_pei WHERE distance = dist AND etude IS NOT DISTINCT FROM idEtude) LOOP
		couverture_distance = couverture_hydraulique.safe_union(couverture_distance, couverture_pei.geometrie);
	END LOOP;
	INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
	VALUES(dist || 'm', idEtude, couverture_distance);
  END LOOP;

  -- S'il existe une fonction spécifique, on la joue. Sinon, on reprends le calcul de risque par défaut
  IF (select exists(
	  select * from pg_proc where proname = (select 'couverture_hydraulique_zonage_' || valeur from remocra.param_conf where cle = 'PDI_CODE_SDIS')
      )
  ) THEN
  	EXECUTE CONCAT('SELECT * FROM couverture_hydraulique.', (
	  select proname from pg_proc where proname = (select 'couverture_hydraulique_zonage_' || valeur from remocra.param_conf where cle = 'PDI_CODE_SDIS')), '(', '1', ');');
  ELSE
	  -- Tracé du risque courant faible
	  -- Conditions: 1 PEI de 60m3/h sur 150m (buffer compris)
	  FOR couverture_pei IN (SELECT chp.geometrie, chp.pei
		FROM couverture_hydraulique.couverture_hydraulique_pei chp
		JOIN couverture_hydraulique.pei p ON p.id = chp.pei
		LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
		WHERE chp.distance = 100 AND (hp.id IS NULL OR NOT p.gros_debit) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
		couverture_risque_courant_faible = couverture_hydraulique.safe_union(couverture_risque_courant_faible, couverture_pei.geometrie);
	  END LOOP;

	  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_faible' AND etude IS NOT DISTINCT FROM idEtude;
	  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
	  VALUES('risque_courant_faible', idEtude, couverture_risque_courant_faible);

	  -- Tracé du risque courant ordinaire
	  -- Conditions: 2 PEI de 60 m3/h, intersection sur distances 150m et 350m (buffer compris)
	  FOR couverture_pei in (SELECT chp.geometrie, chp.pei
		FROM couverture_hydraulique.couverture_hydraulique_pei chp
		JOIN couverture_hydraulique.pei p ON p.id = chp.pei
		WHERE chp.distance = 100 AND (p.type = 'PROJET' OR NOT p.gros_debit) AND etude IS NOT DISTINCT FROM idEtude) loop
		  FOR couverture_voisin in (SELECT chp2.geometrie, chp2.pei
			FROM couverture_hydraulique.couverture_hydraulique_pei chp2
			JOIN couverture_hydraulique.pei p ON p.id = chp2.pei
			WHERE chp2.distance = 300 AND (p.type = 'PROJET' OR NOT p.gros_debit) AND chp2.pei != couverture_pei.pei AND etude IS NOT DISTINCT FROM idEtude) loop
			  couverture_risque_courant_ordinaire = couverture_hydraulique.safe_union(couverture_risque_courant_ordinaire, ST_INTERSECTION(couverture_pei.geometrie, couverture_voisin.geometrie));
		  end loop;
	  end loop;

	  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_ordinaire' AND etude IS NOT DISTINCT FROM idEtude;
	  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
	  VALUES('risque_courant_ordinaire', idEtude, couverture_risque_courant_ordinaire);

	  -- Tracé du risque courant important
	  -- Conditions: A ce stade, identique au risque courant ordinaire
	  raise info 'risque courant important';
	  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_important' AND etude IS NOT DISTINCT FROM idEtude;
	  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie) VALUES ('risque_courant_important', idEtude, couverture_risque_courant_ordinaire);

	  -- Tracé du risque particulier
	  -- Conditions: Intersection distances 100m et 300m (buffer compris), au moins un des deux PEI gros débit (débit >= 150 m3/h)
		-- Etape 1 : couverture 50m d'un gros débit avec une couverture 250m
	  FOR couverture_pei IN (SELECT chp.geometrie, chp.pei
		FROM couverture_hydraulique.couverture_hydraulique_pei chp
		JOIN couverture_hydraulique.pei p on p.id = chp.pei
		WHERE chp.distance = 50 --AND p.type = 'HYDRANT'
		   AND (p.gros_debit) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
		FOR couverture_voisin IN (SELECT *
			FROM couverture_hydraulique.couverture_hydraulique_pei
			WHERE distance = 250 AND etude IS NOT DISTINCT FROM idEtude AND pei != couverture_pei.pei AND ST_DISTANCE(geometrie, couverture_pei.geometrie) <= 1000) LOOP
		  couverture_risque_particulier = couverture_hydraulique.safe_union(couverture_risque_particulier, ST_INTERSECTION(couverture_pei.geometrie, couverture_voisin.geometrie));
		END LOOP;
	  END LOOP;

		-- Etape 2 : couverture 250m d'un gros débit avec une couverture 50m
	  FOR couverture_pei IN (SELECT chp.geometrie, chp.pei
		FROM couverture_hydraulique.couverture_hydraulique_pei chp
		JOIN couverture_hydraulique.pei p on p.id = chp.pei
		WHERE chp.distance = 250 --AND p.type = 'HYDRANT'
		   AND (p.gros_debit) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
		FOR couverture_voisin IN (SELECT *
			FROM couverture_hydraulique.couverture_hydraulique_pei
			WHERE distance = 50 AND etude IS NOT DISTINCT FROM idEtude AND pei != couverture_pei.pei AND ST_DISTANCE(geometrie, couverture_pei.geometrie) <= 1000) LOOP
		  couverture_risque_particulier = couverture_hydraulique.safe_union(couverture_risque_particulier, ST_INTERSECTION(couverture_pei.geometrie, couverture_voisin.geometrie));
		END LOOP;
	  END LOOP;

	  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_particulier' AND etude IS NOT DISTINCT FROM idEtude;
	  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie) VALUES ('risque_particulier', idEtude, couverture_risque_particulier);
  END IF;
  RETURN 1;
END
$function$

;
ALTER FUNCTION couverture_hydraulique.couverture_hydraulique_zonage(integer, int[], integer)
    OWNER TO remocra;

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
