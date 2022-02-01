BEGIN;

-- Fonction de zonage spécifique
CREATE OR REPLACE FUNCTION couverture_hydraulique.couverture_hydraulique_zonage_21(
	idetude integer)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE

BEGIN
 RETURN 1;
END
$BODY$;

ALTER FUNCTION couverture_hydraulique.couverture_hydraulique_zonage_21(integer)
    OWNER TO remocra;
    
-- Nettoyage des hydrants suivis par la couverture hydraulique
DELETE FROM couverture_hydraulique.pei;

INSERT INTO couverture_hydraulique.pei(identifiant, geometrie, type)
SELECT id, geometrie, 'HYDRANT' from remocra.hydrant;

COMMIT;
