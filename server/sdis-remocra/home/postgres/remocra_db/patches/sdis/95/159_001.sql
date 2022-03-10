BEGIN;

-- MAJ param conf
UPDATE remocra.param_conf
SET valeur = '95'
WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';

UPDATE remocra.param_conf
SET valeur = '95'
WHERE cle = 'HYDRANT_NUMEROTATION_METHODE';

COMMIT;
