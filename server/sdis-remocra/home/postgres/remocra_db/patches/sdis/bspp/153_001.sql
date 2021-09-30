BEGIN;

UPDATE remocra.commune
SET geometrie = (SELECT st_multi(st_intersection(zc.geometrie, st_buffer(c.geometrie, -0.001)))
FROM remocra.commune c
JOIN remocra.zone_competence zc ON zc.id = (SELECT zone_competence FROM remocra.organisme WHERE code = 'BSPP')
WHERE c.id = (SELECT id FROM remocra.commune WHERE nom = 'PIERREFITTE-SUR-SEINE'))
WHERE id = (SELECT id FROM remocra.commune WHERE nom = 'PIERREFITTE-SUR-SEINE');

COMMIT;
