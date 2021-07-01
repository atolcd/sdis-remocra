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
    numero_patch := 144;
    description_patch := 'Pei plus proche: gestion jonctions à un sommet';

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

DROP FUNCTION couverture_hydraulique.inserer_jonction_pei(integer, integer, integer);
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
    IF jonction.jonction_geometrie IS NOT NULL AND jonction.jonction_fraction >= 0.00001 AND jonction.jonction_fraction < 0.99999 THEN
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

DROP FUNCTION couverture_hydraulique.trg_hydrant_projet_couverture() CASCADE;
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
  r_old = OLD;
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
