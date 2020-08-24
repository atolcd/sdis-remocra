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
    numero_patch := 137;
    description_patch :='Couverture hydraulique';

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

-- =============== Structure ===============
DROP SCHEMA IF EXISTS couverture_hydraulique CASCADE;
CREATE SCHEMA couverture_hydraulique AUTHORIZATION remocra;

SET search_path = remocra, pdi, public, pg_catalog, couverture_hydraulique;

DROP TABLE IF EXISTS couverture_hydraulique.pei CASCADE;
CREATE TABLE couverture_hydraulique.pei (
  id serial PRIMARY KEY,
  identifiant BIGINT NOT NULL,
  geometrie geometry(POINT,2154),
  gros_debit BOOLEAN DEFAULT FALSE,
  type CHARACTER VARYING NOT NULL
);
CREATE INDEX pei_geometrie_idx ON couverture_hydraulique.pei USING gist (geometrie);
CREATE INDEX pei_id_idx ON couverture_hydraulique.pei USING btree (id);
CREATE INDEX pei_id_gros_debit ON couverture_hydraulique.pei USING btree (gros_debit);

COMMENT ON TABLE couverture_hydraulique.pei IS 'Hydrants de la couverture hydraulique';
COMMENT ON COLUMN couverture_hydraulique.pei.identifiant IS 'Identifiant utilisé dans la table remocra.hydrant ou remocra.etude_hydrant_projet';
COMMENT ON COLUMN couverture_hydraulique.pei.type IS 'HYDRANT ou PROJET selon si l''hydrant est un hydrant existant ou un hydrant projet';

DROP TABLE IF EXISTS couverture_hydraulique.reseau CASCADE;
CREATE TABLE couverture_hydraulique.reseau (
  id serial PRIMARY KEY,
  geometrie geometry(LINESTRING,2154),
  source integer,
  destination integer,
  pei_troncon integer,
  traversable boolean NOT NULL DEFAULT TRUE,
  sens_unique boolean NOT NULL DEFAULT FALSE,
  niveau INTEGER NOT NULL DEFAULT 0,
  etude BIGINT,
  CONSTRAINT fk_etude FOREIGN KEY (etude)
    REFERENCES remocra.etude (id) MATCH SIMPLE
);
CREATE INDEX reseau_geometrie_idx ON couverture_hydraulique.reseau USING gist (geometrie);
CREATE INDEX reseau_pei_troncon_idx ON couverture_hydraulique.reseau USING btree (pei_troncon);
CREATE INDEX reseau_pei_source_idx ON couverture_hydraulique.reseau USING btree (source);
CREATE INDEX reseau_pei_destination_idx ON couverture_hydraulique.reseau USING btree (destination);

COMMENT ON TABLE couverture_hydraulique.reseau IS 'Réseau routier de la couverture hydraulique';
COMMENT ON COLUMN couverture_hydraulique.reseau.source IS 'Sommet source de la voie (déterminé par la création de la topologie)';
COMMENT ON COLUMN couverture_hydraulique.reseau.destination IS 'Sommet de destination de la voie (déterminé par la création de la topologie)';
COMMENT ON COLUMN couverture_hydraulique.reseau.pei_troncon IS 'Identifiant du pei si la voie relie un pei au réseau routier, NULL sinon';
COMMENT ON COLUMN couverture_hydraulique.reseau.traversable IS 'Indique si l''on peut ou non traverser cette voie';
COMMENT ON COLUMN couverture_hydraulique.reseau.sens_unique IS 'Indique si la voie est à sens unique. Le sens est celui de la digitalisation de la géométrie';
COMMENT ON COLUMN couverture_hydraulique.reseau.niveau IS 'Niveau de la voie (ex: -1 pour un tunnel, 1 pour un pont, etc) si celle-ci est au-dessus ou en dessous du réseau routier';

DROP TABLE IF EXISTS couverture_hydraulique.batiments CASCADE;
CREATE TABLE couverture_hydraulique.batiments (
  id serial PRIMARY KEY,
  geometrie geometry(POLYGON,2154),
  etude BIGINT,
  CONSTRAINT fk_etude FOREIGN KEY (etude)
    REFERENCES remocra.etude (id) MATCH SIMPLE
);
CREATE INDEX batiments_geometrie_idx ON couverture_hydraulique.batiments USING gist (geometrie);

COMMENT ON TABLE couverture_hydraulique.batiments IS 'Représentation schématique des bâtiments d''une étude';

DROP TABLE IF EXISTS couverture_hydraulique.sommet CASCADE;
CREATE TABLE couverture_hydraulique.sommet (
  id serial PRIMARY KEY,
  geometrie geometry(POINT,2154) NOT NULL UNIQUE,
  etude BIGINT,
  CONSTRAINT fk_etude FOREIGN KEY (etude)
    REFERENCES remocra.etude (id) MATCH SIMPLE
);
CREATE INDEX sommet_geometrie_idx ON couverture_hydraulique.sommet USING gist (geometrie);
CREATE INDEX sommet_id_idx ON couverture_hydraulique.sommet USING btree (id);

COMMENT ON TABLE couverture_hydraulique.sommet IS 'Sommet utilisée lors du parcours de graph. Chaque sommet réprésente un début, une fin ou une intersection entre des voies du réseau routier. ';

DROP TABLE IF EXISTS couverture_hydraulique.couverture_hydraulique_pei;
CREATE TABLE couverture_hydraulique.couverture_hydraulique_pei(
  distance integer,
  pei integer,
  geometrie geometry(POLYGON,2154) CHECK (st_srid(geometrie) = 2154),
  etude BIGINT,
  PRIMARY KEY (pei, etude, distance),
  CONSTRAINT fk_etude FOREIGN KEY (etude)
    REFERENCES remocra.etude (id) MATCH SIMPLE
);

CREATE INDEX couverture_hydraulique_pei_distance ON couverture_hydraulique.couverture_hydraulique_pei using btree(distance);

COMMENT ON TABLE couverture_hydraulique.couverture_hydraulique_pei IS 'Couverture hydraulique d''un pei';
COMMENT ON COLUMN couverture_hydraulique.couverture_hydraulique_pei.distance IS 'La distance de parcours du graph en mètre depuis le pei (50, 100, etc)';
COMMENT ON COLUMN couverture_hydraulique.couverture_hydraulique_pei.pei IS 'L''identifiant du pei';

DROP TABLE IF EXISTS couverture_hydraulique.couverture_hydraulique_zonage;
CREATE TABLE couverture_hydraulique.couverture_hydraulique_zonage(
  label character varying,
  geometrie geometry(POLYGON,2154) CHECK (st_srid(geometrie) = 2154),
  etude BIGINT,
  PRIMARY KEY (label, etude),
  CONSTRAINT fk_etude FOREIGN KEY (etude)
    REFERENCES remocra.etude (id) MATCH SIMPLE
);

COMMENT ON TABLE couverture_hydraulique.couverture_hydraulique_zonage IS 'Couverture hydraulique résultante de la simulation. Il s''agit de la couverture totale issue de toutes les couvertures hydrauliques de la table couverture_hydraulique.couverture_hydraulique_pei';
COMMENT ON COLUMN couverture_hydraulique.couverture_hydraulique_zonage.label IS 'Label de la géométrie calculée (risque courant, 50m, 250m, etc)';

DROP TABLE IF EXISTS couverture_hydraulique.voiesLaterales;
CREATE TABLE couverture_hydraulique.voiesLaterales (
   voie integer,
   angle float,
   gauche boolean,
   droite boolean,
   traversable boolean
);

COMMENT ON TABLE couverture_hydraulique.voiesLaterales IS 'Résultante de la fonction couverture_hydraulique.voiesLaterales()';
COMMENT ON COLUMN couverture_hydraulique.voiesLaterales.voie IS 'Voie voisine de la voie actuelle';
COMMENT ON COLUMN couverture_hydraulique.voiesLaterales.angle IS 'Angle que forme la voie avec la voie actuelle';
COMMENT ON COLUMN couverture_hydraulique.voiesLaterales.gauche IS 'Indique si la voie est celle se situant le plus à gauche';
COMMENT ON COLUMN couverture_hydraulique.voiesLaterales.droite IS 'Indique si la voie est celle se situant le plus à droite';
COMMENT ON COLUMN couverture_hydraulique.voiesLaterales.traversable IS 'Indique si la voie est traversable';

DROP TABLE IF EXISTS couverture_hydraulique.temp_distances;
CREATE TABLE couverture_hydraulique.temp_distances(
 sommet integer, -- Sommet à atteindre
 voie integer, -- En passant par cette voie
 distance double precision, -- Distance minimum 
 geometrie geometry(POLYGON,2154),
 side varchar default null,
 traversable boolean default true,
 voiePrecedente integer,
 start integer
);

COMMENT ON TABLE couverture_hydraulique.temp_distances IS 'Table permettant de stocker les informations nécessaire au parcours de graph; basé sur l''algorithme de Dijkstra';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.sommet IS 'Identifiant du sommet que l''on atteint';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.voie IS 'Identifiant de la voie que l''on emprunte';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.distance IS 'Distance parcourue depuis le départ en empruntant cette voie';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.geometrie IS 'Buffer de la couverture hydraulique de la voie empruntée';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.side IS 'Indique sur quel côté de la voie tracer le buffer si la voie n''est pas traversable (renseigné par l''algo de calcul de la couverture hydraulique';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.traversable IS 'Indique si la voie est traversable';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.voiePrecedente IS 'Identifiant de la voie empruntée pour arriver à cette voie. En remontant les prédéceseurs, on peut reconsituer le chemin le plus court';
COMMENT ON COLUMN couverture_hydraulique.temp_distances.start IS 'Identifiant du PEI depuis lequel on effectue notre parcours';

-- =============== Fonctions de la couverture hydraulique ===============

/**
  * Ajout d'un jonction lié à un PEI
  * @param pei_id L'identifiant du pei (table couverture_hydraulique.pei)
  * @param distance_max_au_reseau La distance maximum en mètres entre le pei et le réseau routier le plus proche. Au-delà, le pei ne sera pas relié au réseau routier
  * @param idEtude L'identifiant de l'étude courante
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.inserer_jonction_pei(pei_id integer, distance_max_au_reseau integer, idEtude integer) RETURNS INT AS
$BODY$
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
      ORDER BY
        p.id,
        distance ASC
      ) AS t;

    -- Si le point de jonction n'est pas null et qu'il ne corresponds pas à une extremité existante du tronçon,
    -- procède à la découpe du troncon initial en 2.
    IF jonction.jonction_geometrie IS NOT NULL AND jonction.jonction_fraction > 0 AND jonction.jonction_fraction < 1 THEN

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
    END IF;

    RETURN 1;
  END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION couverture_hydraulique.retirer_jonction_pei (id_pei integer)
RETURNS INT AS $BODY$
DECLARE
  tronconPei record;
  voie1 record;
  voie2 record;
BEGIN
  SELECT * INTO tronconPei FROM couverture_hydraulique.reseau WHERE pei_troncon = id_pei;
  SELECT * INTO voie1 FROM couverture_hydraulique.reseau WHERE destination = tronconPei.destination AND pei_troncon IS NULL;
  SELECT * INTO voie2 FROM couverture_hydraulique.reseau WHERE source = tronconPei.destination AND pei_troncon IS NULL;
  DELETE FROM couverture_hydraulique.sommet WHERE id = tronconPei.source;
  UPDATE couverture_hydraulique.reseau 
    SET geometrie = ST_LINEMERGE(ST_UNION(voie1.geometrie, voie2.geometrie)),
        destination = voie2.destination
    WHERE id = voie1.id;
  DELETE FROM couverture_hydraulique.reseau WHERE id = voie2.id;
  DELETE FROM couverture_hydraulique.sommet WHERE id = tronconPei.destination;
  DELETE FROM couverture_hydraulique.reseau where pei_troncon = id_pei;
  RETURN 1;
END
$BODY$
LANGUAGE plpgsql VOLATILE;

-- Création de la topologie
CREATE OR REPLACE FUNCTION couverture_hydraulique.creation_topologie (idEtude integer)
RETURNS void AS $BODY$
DECLARE
  voie record;
  debut geometry;
  fin geometry;
  sommet_id integer;
  t record;
BEGIN
  FOR voie IN (SELECT * FROM couverture_hydraulique.reseau WHERE etude IS NOT DISTINCT FROM idEtude) LOOP
    debut = ST_StartPoint(voie.geometrie);
    fin = ST_EndPoint(voie.geometrie);

    -- On sélectionne le sommet de la topologie correspondant au croisement au début de la voie pour alimenter le champ 'source'
    SELECT * INTO t FROM couverture_hydraulique.sommet WHERE ST_WITHIN(geometrie, ST_EXPAND(debut, 0.2)) AND etude IS NOT DISTINCT FROM idEtude LIMIT 1;
    IF t IS NULL THEN -- Si le sommet n'existe pas, on le créé
      INSERT INTO couverture_hydraulique.sommet(geometrie, etude) VALUES (debut, idEtude) RETURNING id INTO sommet_id;
      UPDATE couverture_hydraulique.reseau SET source = sommet_id WHERE id = voie.id;
    ELSE
      UPDATE couverture_hydraulique.reseau SET source = t.id WHERE id = voie.id;
    END IF;

    -- On sélectionne le sommet de la topologie correspondant au croisement à la fin de la voie pour alimenter le champ 'destination'
    SELECT * INTO t FROM couverture_hydraulique.sommet WHERE ST_WITHIN(geometrie, ST_EXPAND(fin, 0.2)) AND etude IS NOT DISTINCT FROM idEtude LIMIT 1;
    IF t IS NULL THEN -- Si le sommet n'existe pas, on le créé
      INSERT INTO couverture_hydraulique.sommet(geometrie, etude) VALUES (fin, idEtude) RETURNING id INTO sommet_id;
      UPDATE couverture_hydraulique.reseau SET destination = sommet_id WHERE id = voie.id;
    ELSE
      UPDATE couverture_hydraulique.reseau SET destination = t.id WHERE id = voie.id;
    END IF;
  END LOOP;
END
$BODY$
LANGUAGE plpgsql VOLATILE;

-- Fonction d'intersection permettant de gérer les Topology Exception qui pourraient survenir
CREATE OR REPLACE FUNCTION couverture_hydraulique.safe_isect(geom_a geometry, geom_b geometry)
RETURNS geometry AS
$$
BEGIN
    RETURN ST_Intersection(geom_a, geom_b);
    EXCEPTION
        WHEN OTHERS THEN
            BEGIN
                RETURN ST_Intersection(ST_Buffer(geom_a, 0.0000001), ST_Buffer(geom_b, 0.0000001));
                EXCEPTION
                    WHEN OTHERS THEN
                        RETURN ST_GeomFromText('POLYGON EMPTY');
    END;
END
$$
LANGUAGE 'plpgsql' STABLE STRICT;

/**
  * Fonction d'union permettant de gérer les Topology Exception qui pourraient survenir
  * Si l'union ne peut se faire, la première géométrie est renvoyée
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.safe_union(
  geom_a geometry = NULL,
  geom_b geometry = NULL)
    RETURNS geometry
    LANGUAGE 'plpgsql'

    COST 100
    STABLE
    CALLED ON NULL INPUT
AS $BODY$
BEGIN
    IF geom_a IS NOT NULL AND geom_b IS NOT NULL THEN
  RETURN ST_UNION(geom_a, geom_b);
  ELSE
    RETURN COALESCE(geom_a, geom_b);
  END IF;
    EXCEPTION
        WHEN OTHERS THEN
            BEGIN
                RETURN ST_UNION(ST_Buffer(geom_a, 0.0000001), ST_Buffer(geom_b, 0.0000001));
                EXCEPTION
                    WHEN OTHERS THEN
                        RETURN geom_a;
    END;
END
$BODY$;

/**
  * Fonction permettant de déterminer, lorsque l'on arrive à une intersection, les voies situées à notre gauche et à notre droite
  * @param depart L'identifiant de la voie de départ
  * @param matchingPoint L'identifiant du sommet correspondant au croisement
  * @param idEtude L'identifiant de l'étude actuelle
  * La fonction calcule l'angle entre notre position et les voies voisines et stocke le résultat dans la table voiesLaterales. Un booléen indique la voie de gauche et de droite
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.voiesLaterales (depart integer, matchingPoint integer, idEtude integer)
RETURNS void AS $BODY$
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
    (SELECT id, source as destination, source as source, ST_LENGTH(geometrie) as distance, geometrie, pei_troncon, r.traversable FROM couverture_hydraulique.reseau r WHERE destination = matchingPoint AND NOT sens_unique AND pei_troncon IS NULL AND id != depart AND etude IS NOT DISTINCT FROM idEtude)
  ) as R) LOOP

    IF depart != voieVoisine.id THEN
      -- On ne prend qu'une toute petite section de la voie voisine (5%)
      Select ST_LineSubstring(
        (CASE when voieVoisine.source != matchingPoint THEN st_reverse(voieVoisine.geometrie) ELSE voieVoisine.geometrie END),
         0.05, 1)::geometry(LineString,2154) INTO geometrieVoisine;
     -- On calcule l'angle et on stocke le résultat
     INSERT INTO couverture_hydraulique.voiesLaterales (voie, angle, gauche, droite, traversable) 
     VALUES(voieVoisine.id, ST_ANGLE(ST_StartPoint(geometrieCourante), ST_EndPoint(geometrieCourante), ST_StartPoint(geometrieVoisine)), false, false, voieVoisine.traversable);

    END IF;
  END LOOP;

  -- On marque les voies de gauche et de droite
  UPDATE couverture_hydraulique.voiesLaterales SET gauche = TRUE WHERE angle = (SELECT min(angle) FROM couverture_hydraulique.voiesLaterales);
  UPDATE couverture_hydraulique.voiesLaterales SET droite = TRUE WHERE angle = (SELECT max(angle) FROM couverture_hydraulique.voiesLaterales);
END
$BODY$
LANGUAGE plpgsql VOLATILE;

/**
  * CALCUL DE LA COUVERTURE HYDRAULIQUE D'UN PEI
  * @param depart Identifiant du pei de départ
  * @param idEtude Identifiant de l'étude courante
  * @idReseauImporte Indique si on se base sur le réseau routier commun (idReseauImporte = null) ou sur le réseau routier importé
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.parcours_couverture_hydraulique (depart integer, idEtude integer, idReseauImporte integer)
RETURNS INT AS $BODY$
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

      SELECT * INTO courantRecord FROM couverture_hydraulique.temp_distances WHERE start = depart AND sommet = noeudCourant ORDER BY distance LIMIT 1;

      -- Si c'est la voie de départ, il n'y a aucune occurence dans la table temp_distances. On récupère néanmoins son ID afin de pouvoir déterminer les voies partant à sa gauche et à sa droite
      IF courantRecord IS NULL THEN SELECT id INTO voieCourante FROM couverture_hydraulique.reseau WHERE pei_troncon = depart; END IF;

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
$BODY$
LANGUAGE plpgsql VOLATILE;

/**
  * ZONAGE DES MULTIPLES COUVERTURES CALCULEES
  * Cette fonction effetcure le tracé (zonage) de la couverture hdyraulique et des risques, en prenant en compte le tracé des pei et leurs spécificités
  */
CREATE OR REPLACE FUNCTION couverture_hydraulique.couverture_hydraulique_zonage(
  idetude integer)
    RETURNS integer
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
AS $BODY$
DECLARE
  tabDistances int[] := ARRAY[50, 100, 250, 300];
  dist integer;
  couverture_pei record;
  couverture_voisin record;
  couverture_risque_courant_faible geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_risque_courant_ordinaire geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_risque_important geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_risque_particulier geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
  couverture_distance geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), 2154);
BEGIN
  -- Tracé des zones 50, 100, 250m et 300 m
  FOREACH dist in ARRAY tabDistances LOOP
    DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = dist || 'm' AND etude IS NOT DISTINCT FROM idEtude;
  FOR couverture_pei IN (SELECT * FROM couverture_hydraulique.couverture_hydraulique_pei WHERE distance = dist AND etude IS NOT DISTINCT FROM idEtude) LOOP
    couverture_distance = couverture_hydraulique.safe_union(couverture_distance, couverture_pei.geometrie);
  END LOOP;
  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
  VALUES(dist || 'm', idEtude, couverture_distance);
  END LOOP;

  -- Tracé du risque courant faible
  -- Conditions: 1 PEI de 60m3/h sur 150m (buffer compris)
  FOR couverture_pei IN (SELECT chp.*, hp.debit, ehp.debit
  FROM couverture_hydraulique.couverture_hydraulique_pei chp
  JOIN couverture_hydraulique.pei p ON p.id = chp.pei
  LEFT JOIN remocra.etude_hydrant_projet ehp ON ehp.id = p.identifiant AND p.type = 'PROJET'
  LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
  WHERE chp.distance = 100 AND COALESCE(ehp.debit, hp.debit) >= 60 AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
  couverture_risque_courant_faible = couverture_hydraulique.safe_union(couverture_risque_courant_faible, couverture_pei.geometrie);
  END LOOP;
  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_faible' AND etude IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
  VALUES('risque_courant_faible', idEtude, couverture_risque_courant_faible);

  -- Tracé du risque courant ordinaire
  -- Conditions: 2 PEI de 60 m3/h, intersection sur distances 150m et 350m (buffer compris)
  FOR couverture_pei IN (SELECT chp.*, hp.debit, ehp.debit
  FROM couverture_hydraulique.couverture_hydraulique_pei chp
  JOIN couverture_hydraulique.pei p ON p.id = chp.pei
  LEFT JOIN remocra.etude_hydrant_projet ehp ON ehp.id = p.identifiant AND p.type = 'PROJET'
  LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
  WHERE chp.distance = 100 AND COALESCE(ehp.debit, hp.debit) >= 60 AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
  FOR couverture_voisin IN (SELECT chp.*, hp.debit, ehp.debit
    FROM couverture_hydraulique.couverture_hydraulique_pei chp
    JOIN couverture_hydraulique.pei p ON p.id = chp.pei
    LEFT JOIN remocra.etude_hydrant_projet ehp ON ehp.id = p.identifiant AND p.type = 'PROJET'
    LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
    WHERE chp.distance = 300 AND COALESCE(ehp.debit, hp.debit) >= 60 AND chp.etude IS NOT DISTINCT FROM idEtude AND pei != couverture_pei.pei AND ST_DISTANCE(chp.geometrie, couverture_pei.geometrie) <= 1000) LOOP
    couverture_risque_courant_ordinaire = couverture_hydraulique.safe_union(couverture_risque_courant_ordinaire, ST_INTERSECTION(couverture_pei.geometrie, couverture_voisin.geometrie));
  END LOOP;
  END LOOP;
  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_ordinaire' AND etude IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
  VALUES('risque_courant_ordinaire', idEtude, couverture_risque_courant_ordinaire);


  -- Tracé du risque courant important
  -- Conditions: A ce stade, identique au risque courant ordinaire
  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_important' AND etude IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie) VALUES ('risque_courant_important', idEtude, couverture_risque_courant_ordinaire);

  -- Tracé du risque particulier
  -- Conditions: Intersection distances 100m et 300m (buffer compris), au moins un des deux PEI gros débit (débit >= 150 m3/h)
    -- Etape 1 : couverture 50m d'un gros débit avec une couverture 250m
  FOR couverture_pei IN (SELECT chp.*, hp.debit, ehp.debit
  FROM couverture_hydraulique.couverture_hydraulique_pei chp
  JOIN couverture_hydraulique.pei p on p.id = chp.pei
  LEFT JOIN remocra.etude_hydrant_projet ehp ON ehp.id = p.identifiant AND p.type = 'PROJET'
  LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
  LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = COALESCE(hp.diametre, ehp.diametre_nominal)
  WHERE chp.distance = 50 AND thd.code = 'DIAM150' AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
  FOR couverture_voisin IN (SELECT *
    FROM couverture_hydraulique.couverture_hydraulique_pei
    WHERE distance = 250 AND etude IS NOT DISTINCT FROM idEtude AND pei != couverture_pei.pei AND ST_DISTANCE(geometrie, couverture_pei.geometrie) <= 1000) LOOP
    couverture_risque_particulier = couverture_hydraulique.safe_union(couverture_risque_particulier, ST_INTERSECTION(couverture_pei.geometrie, couverture_voisin.geometrie));
  END LOOP;
  END LOOP;

  -- Etape 2 : couverture 250m d'un gros débit avec une couverture 50m
  FOR couverture_pei IN (SELECT chp.*, hp.debit, ehp.debit
  FROM couverture_hydraulique.couverture_hydraulique_pei chp
  JOIN couverture_hydraulique.pei p on p.id = chp.pei
  LEFT JOIN remocra.etude_hydrant_projet ehp ON ehp.id = p.identifiant AND p.type = 'PROJET'
  LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
  LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = COALESCE(hp.diametre, ehp.diametre_nominal)
  WHERE chp.distance = 250 AND thd.code = 'DIAM150' AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
  FOR couverture_voisin IN (SELECT *
    FROM couverture_hydraulique.couverture_hydraulique_pei
    WHERE distance = 50 AND etude IS NOT DISTINCT FROM idEtude AND pei != couverture_pei.pei AND ST_DISTANCE(geometrie, couverture_pei.geometrie) <= 1000) LOOP
    couverture_risque_particulier = couverture_hydraulique.safe_union(couverture_risque_particulier, ST_INTERSECTION(couverture_pei.geometrie, couverture_voisin.geometrie));
  END LOOP;
  END LOOP;


  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_particulier' AND etude IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie) VALUES ('risque_particulier', idEtude, couverture_risque_particulier);
  RETURN 1;
END
$BODY$;

-- Remontée des hydrants existants dans la table pei
INSERT INTO couverture_hydraulique.pei (identifiant, geometrie, type)
SELECT id, geometrie, 'HYDRANT' FROM remocra.hydrant;

-- Remontée des hydrants projets existants dans la table pei
INSERT INTO couverture_hydraulique.pei (identifiant, geometrie, type)
SELECT id, geometrie, 'PROJET' FROM remocra.etude_hydrant_projet;

-- =============== Fonctions trigger ===============
CREATE OR REPLACE FUNCTION couverture_hydraulique.trg_hydrant_couverture()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
DECLARE
  r_new record;
  r_old record;
BEGIN
  r_new = NEW;
  r_old = OLD;
  IF (TG_OP = 'UPDATE') THEN
    UPDATE couverture_hydraulique.pei SET geometrie = r_new.geometrie WHERE identifiant = r_new.id AND type = 'HYDRANT';

  ELSEIF (TG_OP = 'INSERT') THEN
    INSERT INTO couverture_hydraulique.pei (identifiant, geometrie, type) VALUES(r_new.id, r_new.geometrie, 'HYDRANT');

  ELSE
    DELETE FROM couverture_hydraulique.pei WHERE identifiant = r_old.id AND type = 'HYDRANT';
  END IF;

    RETURN r_new;
END;
$BODY$;

ALTER FUNCTION couverture_hydraulique.trg_hydrant_couverture()
    OWNER TO remocra;

CREATE TRIGGER trig_hydrant_couverture
    AFTER INSERT OR UPDATE OR DELETE
    ON remocra.hydrant
    FOR EACH ROW
    EXECUTE PROCEDURE couverture_hydraulique.trg_hydrant_couverture();

CREATE OR REPLACE FUNCTION couverture_hydraulique.trg_hydrant_projet_couverture()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
DECLARE
  r_new record;
  r_old record;
BEGIN
  r_new = NEW;
  IF (TG_OP = 'UPDATE') THEN
    UPDATE couverture_hydraulique.pei SET geometrie = r_new.geometrie WHERE identifiant = r_new.id AND type = 'PROJET';

  ELSEIF (TG_OP = 'INSERT') THEN
    INSERT INTO couverture_hydraulique.pei (identifiant, geometrie, type) VALUES(r_new.id, r_new.geometrie, 'PROJET');

  ELSE
    DELETE FROM couverture_hydraulique.pei WHERE identifiant = r_old.id AND type = 'PROJET';
  END IF;

    RETURN r_new;
END;
$BODY$;

ALTER FUNCTION couverture_hydraulique.trg_hydrant_projet_couverture()
    OWNER TO remocra;


CREATE TRIGGER trig_hydrant_projet_couverture
    AFTER INSERT OR UPDATE OR DELETE
    ON remocra.etude_hydrant_projet
    FOR EACH ROW
    EXECUTE PROCEDURE couverture_hydraulique.trg_hydrant_projet_couverture();




-- Contenu réel du patch fin
--------------------------------------------------

commit;
