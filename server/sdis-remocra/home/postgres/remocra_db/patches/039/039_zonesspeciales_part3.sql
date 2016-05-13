SET search_path = remocra, tracabilite, public, pg_catalog;

BEGIN;


/*----------------------------------------------------------------------------------------
 * Script de mise en place des zones spéciales Partie 3 :
 *   
 *   - Recalcule les numéros des hydrants situés en zones spéciales et met à jour les références aux zones spéciales des hydrants
 *
 *----------------------------------------------------------------------------------------*/


-- --------------------------------------------------------------------
-- Procédure permettant d'initialiser les hydrants en zones spéciales
--  + numérotation : 'PI/BI/PN' 'CODE_ZONE' Numéro > 90 000
-- --------------------------------------------------------------------
CREATE OR REPLACE FUNCTION remocra.init_numero_hydrant_zone_speciale()
	RETURNS void as
$BODY$
DECLARE
	p_hydrant record;
	p_numero_interne record;

	num_interne integer;
	v_code_zone varchar;
	v_nature  varchar;

	-- Curseur du numéro interne
	c_numero_interne CURSOR(zone_speciale integer,type_hydrant varchar) IS
	SELECT
		COALESCE(min(h.numero_interne),99999) as numero
	FROM
		remocra.hydrant h
		JOIN remocra.type_hydrant_nature n ON (h.nature = n.id)
	WHERE
		h.zone_speciale = zone_speciale
		and n.type_hydrant = (select id from remocra.type_hydrant where code = type_hydrant)
		and h.numero_interne > 90000;
	-- Curseur des hydrants à numéroter des zones spéciales
	c_hydrants_a_numeroter CURSOR IS
	SELECT
		h.id hydrant_id,
		h.code as type_h, -- PIBI/PENA
		(CASE h.code WHEN 'PENA' THEN 'PN' ELSE n.code END) as nature,
		zs.id zone_speciale,
		zs.code code_zone
	FROM
		remocra.hydrant h
		JOIN remocra.zone_speciale zs ON (h.zone_speciale = zs.id)
		JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
	WHERE h.zone_speciale is not null
	AND split_part(numero,' ',2) != zs.code
	ORDER BY zs.code, split_part(numero,' ',1); -- PI/BI/PN
BEGIN
-- On parcours tous les hydrants des zones speciales
	num_interne := 0;
	v_code_zone := '';
	v_nature := '';

	FOR p_hydrant in c_hydrants_a_numeroter LOOP
	   IF(v_code_zone != p_hydrant.code_zone) THEN
			OPEN c_numero_interne (p_hydrant.zone_speciale,p_hydrant.type_h);
			FETCH c_numero_interne into p_numero_interne;
			CLOSE c_numero_interne;

			v_code_zone := p_hydrant.code_zone;
			v_nature := p_hydrant.nature;

			IF(p_numero_interne.numero != 99999) THEN
				num_interne := num_interne - 1;
			ELSE
				num_interne := 99999;
			END IF;
	   ELSE
			IF(v_nature != p_hydrant.nature) THEN
				v_nature := p_hydrant.nature;
				OPEN c_numero_interne (p_hydrant.zone_speciale,p_hydrant.type_h);
				FETCH c_numero_interne into p_numero_interne;
				CLOSE c_numero_interne;
				IF(p_numero_interne.numero != 99999) THEN
					num_interne := num_interne - 1;
				ELSE
					num_interne := 99999;
				END IF;
			ELSE
				num_interne := num_interne - 1;
			END IF;
	   END IF;

	   RAISE NOTICE 'Type Hydrant (%) Code Zone (%) Numero (%)',v_nature, v_code_zone , num_interne;

           -- MISE à jour de l'hydrant
           UPDATE remocra.hydrant
           SET
				numero_interne =  num_interne,
				numero =  v_nature||' '||v_code_zone||' '||num_interne
           WHERE
                id = p_hydrant.hydrant_id;

	END LOOP;
	RAISE NOTICE 'Mise à jour des hydrants en zone speciale effectuée avec succès !';

END;
$BODY$
 LANGUAGE plpgsql VOLATILE;


-- --------------------------------------------------------------------
-- Mise à jour champ zone_speciale de la table hydrant
-- --------------------------------------------------------------------
UPDATE remocra.hydrant h
	set zone_speciale = (
		select zs.id id_zone
			from
				remocra.hydrant h1, remocra.zone_speciale zs
			where
					h1.geometrie && zs.geometrie
					and st_distance(h1.geometrie, zs.geometrie)<=0
					and h1.id = h.id
		);
-- --------------------------------------------------------------------
-- Mise à jour des numero et numero_interne des hydrants en zones spéciales
-- --------------------------------------------------------------------
select remocra.init_numero_hydrant_zone_speciale();

DROP FUNCTION IF EXISTS remocra.init_numero_hydrant_zone_speciale();


COMMIT;

