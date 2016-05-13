-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
-- * Suppression  pistes de sous type "ARCHIVE"
-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
DELETE FROM remocra_referentiel.piste WHERE sous_type = 'ARCHIVE';

-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
-- * Affectation des communes aux tronçons de routes et de pistes si non renseigné
-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

-- Affectation des codes insee et des nom de commune aux tronçons de piste si insee inconnu
UPDATE remocra_referentiel.piste p SET numero_insee = ac.numero_insee, commune = ac.commune FROM
(SELECT
    DISTINCT ON(p.gc_key)
    p.gc_key,
    c.insee::integer AS numero_insee,
    c.nom AS commune
FROM
    (SELECT gc_key,geometrie FROM remocra_referentiel.piste WHERE st_isvalid(geometrie) AND (numero_insee IS NULL OR numero_insee NOT IN(SELECT insee::integer FROM remocra.commune))) p
    JOIN remocra.commune c ON(p.geometrie && c.geometrie AND St_Intersects(p.geometrie,c.geometrie))
ORDER BY
    p.gc_key,
    St_length(St_Intersection(p.geometrie,c.geometrie)) DESC) AS ac
WHERE
    p.gc_key = ac.gc_key;

-- Affectation des codes insee et des nom de commune aux tronçons de routes si insee inconnu
UPDATE remocra_referentiel.res_route r SET numero_insee = ac.numero_insee, commune = ac.commune FROM
(SELECT
    DISTINCT ON(r.gc_key)
    r.gc_key,
    c.insee::integer AS numero_insee,
    c.nom AS commune
FROM
    (SELECT gc_key,geometrie FROM remocra_referentiel.res_route WHERE st_isvalid(geometrie) AND (numero_insee IS NULL OR numero_insee NOT IN(SELECT insee::integer FROM remocra.commune))) r
    JOIN remocra.commune c ON(r.geometrie && c.geometrie AND St_Intersects(r.geometrie,c.geometrie))
ORDER BY
    r.gc_key,
    St_length(St_Intersection(r.geometrie,c.geometrie)) DESC) AS ac
WHERE
    r.gc_key = ac.gc_key;



-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
-- * Consolidation des voies à partir des tronçons de routes et des tronçons de pistes
-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

TRUNCATE remocra.voie;

-- Voies issus des tronçons de routes
INSERT INTO remocra.voie (
    geometrie,
    mot_classant,
    nom,
    source,
    commune
) SELECT
    st_union(st_multi(geometrie)),
    COALESCE(mot_classant,''),
    COALESCE(nom,''),
    'ROUTE'::character varying AS source,
    commune
FROM
(SELECT DISTINCT ON(r.geometrie)
    r.geometrie,
    r.mot_classant,
    r.com_nme AS nom,
    c.id AS commune
FROM
    remocra_referentiel.res_route r
    --JOIN remocra.commune c ON r.commune = c.nom
    JOIN remocra.commune c ON r.numero_insee::text = c.insee
WHERE
    st_isvalid(r.geometrie)
    AND st_geometrytype(r.geometrie) = 'ST_LineString'

) AS troncons_uniques
GROUP BY
    mot_classant,
    nom,
    commune;

-- Voies issus des tronçons de pistes
INSERT INTO remocra.voie (
    geometrie,
    mot_classant,
    nom,
    source,
    commune
) SELECT
    st_union(st_multi(geometrie)),
    ''::character varying AS mot_classant,
    COALESCE(nom,''),
    'PISTE'::character varying AS source,
    commune
FROM
(SELECT DISTINCT ON(p.geometrie)
    p.geometrie,
    p.com_nme AS nom,
    c.id AS commune
FROM
    remocra_referentiel.piste p
    --JOIN remocra.commune c ON p.commune = c.nom
    JOIN remocra.commune c ON p.numero_insee::text = c.insee
WHERE
    st_isvalid(p.geometrie)
    AND st_geometrytype(p.geometrie) = 'ST_LineString'
    --AND NOT st_intersects(p.geometrie,st_buffer(st_setsrid(st_makepoint(959862.39999999863,6262653.7000000002),2154),10))
    --AND NOT st_intersects(p.geometrie,st_buffer(st_setsrid(st_makepoint(982755.42105262703,6260914.5263157887),2154),10))

) AS troncons_uniques
GROUP BY
    mot_classant,
    nom,
    commune;

