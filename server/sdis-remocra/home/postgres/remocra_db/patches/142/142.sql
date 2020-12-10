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
    numero_patch := 142;
    description_patch :='Couverture hydraulique: modification règles de calcul des risques';

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
  grosDebits int[];
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
  
  -- Récupération des PEI gros débit (jumelés et diamètre nominal de 150)
  grosDebits = (SELECT ARRAY(SELECT DISTINCT p.id
  FROM couverture_hydraulique.couverture_hydraulique_pei chp
  JOIN couverture_hydraulique.pei p on p.id = chp.pei
  LEFT JOIN remocra.etude_hydrant_projet ehp ON ehp.id = p.identifiant AND p.type = 'PROJET'
  LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
  LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = COALESCE(hp.diametre, ehp.diametre_nominal)
  WHERE (thd.code = 'DIAM150' OR hp.jumele IS NOT NULL) AND chp.etude IS NOT DISTINCT FROM idEtude
  )::int[]);
  
  -- Tracé du risque courant faible
  -- Conditions: 1 PEI de 60m3/h sur 150m (buffer compris)
  FOR couverture_pei IN (SELECT chp.geometrie, chp.pei
	FROM couverture_hydraulique.couverture_hydraulique_pei chp
	JOIN couverture_hydraulique.pei p ON p.id = chp.pei
	LEFT JOIN remocra.hydrant_pibi hp ON hp.id = p.identifiant AND p.type = 'HYDRANT'
	WHERE chp.distance = 100 AND (hp.id IS NULL OR NOT grosDebits @> ARRAY[hp.id::int]) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
	couverture_risque_courant_faible = couverture_hydraulique.safe_union(couverture_risque_courant_faible, couverture_pei.geometrie);
  END LOOP;
  DELETE FROM couverture_hydraulique.couverture_hydraulique_zonage WHERE label = 'risque_courant_faible' AND etude IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverture_hydraulique.couverture_hydraulique_zonage(label, etude, geometrie)
  VALUES('risque_courant_faible', idEtude, couverture_risque_courant_faible);

  -- Tracé du risque courant ordinaire
  -- Conditions: 2 PEI de 60 m3/h, intersection sur distances 150m et 350m (buffer compris)
  FOR couverture_pei IN (SELECT chp.geometrie, chp.pei
	FROM couverture_hydraulique.couverture_hydraulique_pei chp
	JOIN couverture_hydraulique.pei p ON p.id = chp.pei
	WHERE chp.distance = 100 AND (p.type = 'PROJET' OR NOT grosDebits @> ARRAY[p.identifiant::int]) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
	FOR couverture_voisin IN (SELECT chp.geometrie, chp.pei
		FROM couverture_hydraulique.couverture_hydraulique_pei chp
		JOIN couverture_hydraulique.pei p ON p.id = chp.pei
		WHERE chp.distance = 300 AND (p.type = 'PROJET' OR NOT grosDebits @> ARRAY[p.identifiant::int]) AND chp.etude IS NOT DISTINCT FROM idEtude AND pei != couverture_pei.pei AND ST_DISTANCE(chp.geometrie, couverture_pei.geometrie) <= 1000) LOOP
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
  FOR couverture_pei IN (SELECT chp.geometrie, chp.pei
	FROM couverture_hydraulique.couverture_hydraulique_pei chp
	JOIN couverture_hydraulique.pei p on p.id = chp.pei
	WHERE chp.distance = 50 AND p.type = 'HYDRANT' AND (grosDebits @> ARRAY[p.id::int]) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
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
	WHERE chp.distance = 250 AND p.type = 'HYDRANT' AND (grosDebits @> ARRAY[p.id::int]) AND chp.etude IS NOT DISTINCT FROM idEtude) LOOP
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

--select couverture_hydraulique.couverture_hydraulique_zonage(34);
-- Contenu réel du patch fin
--------------------------------------------------

commit;
