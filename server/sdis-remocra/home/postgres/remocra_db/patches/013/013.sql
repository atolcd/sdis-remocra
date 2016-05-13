SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;

INSERT INTO sous_type_alerte_elt (id, actif, code, nom, type_geom, type_alerte_elt) VALUES (14, true, 'HYDRANT', 'Hydrant', 0, (select id from type_alerte_elt where code = 'AUTRE'));

SELECT pg_catalog.setval('sous_type_alerte_elt_id_seq', 14, true);

COMMIT;

