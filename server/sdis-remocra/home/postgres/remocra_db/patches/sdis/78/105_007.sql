begin;


UPDATE remocra_referentiel.synchronisation_sig
SET sql_query_after_synchro = 'TRUNCATE remocra.voie;
INSERT INTO remocra.voie (
    geometrie,
    mot_classant,
    nom,
    source,
    commune
) SELECT
    st_multi(fusion.geometrie),
    fusion.mot_classant,
    fusion.nom,
    ''ROUTE''::character varying AS source,
    fusion.commune
FROM
(SELECT
    st_linemerge(st_union(r.geometrie)) AS geometrie,
    upper(translate(r.nom,
        ''âãäåÁÂÃÄÅèééêëÈÉÉÊËìíîïìÌÍÎÏÌóôõöÒÓÔÕÖùúûüÙÚÛÜ'',
        ''aaaaAAAAAeeeeeEEEEEiiiiiIIIIIooooOOOOOuuuuUUUU'')) AS nom,
    r.nom_abrege_maj as mot_classant,
    c.id AS commune
FROM
    remocra_referentiel.route r
    JOIN remocra.commune c ON(c.insee = r.code_insee)
WHERE
    r.nom IS NOT NULL AND
    r.nom_abrege_maj IS NOT NULL AND
    r.code_insee IS NOT NULL AND
    r.geometrie IS NOT NULL
GROUP BY
    r.nom,
    r.nom_abrege_maj,
    c.id) AS fusion;'
WHERE schema_table_name = 'CARTOSIG.ROUTE';


commit;
