CREATE OR REPLACE FUNCTION remocra.nextnumerointerne(idhydrant bigint, idtypehydrant bigint, idnaturedeci bigint,
                                                     idcommune bigint, idzonespeciale bigint)
    RETURNS bigint
    LANGUAGE plpgsql
AS
$function$
DECLARE
    returned bigint;
BEGIN
    SELECT n
    INTO returned
    FROM (SELECT generate_series(startnum, endnum)
          FROM (SELECT CASE
                           WHEN (codetype = 'PIBI' AND codenaturedeci = 'PUBLIC') THEN 1
                           WHEN (codetype = 'PIBI') THEN 500
                           ELSE 800
                           END AS startnum,
                       CASE
                           WHEN (codetype = 'PIBI' AND codenaturedeci = 'PUBLIC') THEN 499
                           WHEN (codetype = 'PIBI') THEN 799
                           ELSE 99999
                           END AS endnum
                FROM (SELECT th.code AS codetype, thnd.code AS codenaturedeci
                      FROM remocra.type_hydrant th
                               CROSS JOIN remocra.type_hydrant_nature_deci thnd
                      WHERE thnd.id = idnaturedeci
                        AND th.id = idtypehydrant) AS codes) AS nums) AS gs(n)
    EXCEPT
    SELECT numero_interne
    FROM remocra.hydrant h
             JOIN remocra.type_hydrant_nature thn ON (h.nature = thn.id)
             JOIN remocra.type_hydrant th ON (thn.type_hydrant = th.id)
             JOIN remocra.type_hydrant_nature_deci thnd ON (h.nature_deci = thnd.id)
    WHERE (CASE WHEN idhydrant IS NOT NULL THEN h.id != idhydrant ELSE TRUE END)
      AND (CASE WHEN idcommune IS NOT NULL THEN h.commune = idcommune ELSE TRUE END)
      AND (CASE WHEN idzonespeciale IS NOT NULL THEN h.zone_speciale = idzonespeciale ELSE TRUE END)
      AND (CASE WHEN idtypehydrant IS NOT NULL THEN th.id = idtypehydrant ELSE TRUE END)
      AND (CASE WHEN idnaturedeci IS NOT NULL AND thn.code = 'PIBI' THEN thnd.id = idnaturedeci ELSE TRUE END)
    ORDER BY 1
    LIMIT 1;

    RETURN returned;
END;
$function$
;
