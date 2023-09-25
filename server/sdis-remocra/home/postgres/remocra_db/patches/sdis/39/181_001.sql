CREATE OR REPLACE FUNCTION remocra.calcul_volume_39(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

DECLARE
p_anomalie_id INTEGER;
p_rec1 remocra.hydrant_pena % ROWTYPE;

nature_pena text;
BEGIN
-- Suppression des anomalies sur le volume
DELETE
FROM
    remocra.hydrant_anomalies
WHERE hydrant = id_hydrant
    AND anomalies IN (
        SELECT id
        FROM
            remocra.type_hydrant_anomalie
        WHERE code IN ( 'VOLUME_INSUFF', 'VOLUME_INSUFF_NC' )
);

select * into p_rec1
from remocra.hydrant_pena pena
WHERE pena.id = id_hydrant;

select thn.code into nature_pena from remocra.hydrant_pena pena
    join remocra.hydrant h on pena.id = h.id
    join remocra.type_hydrant_nature thn on h.nature = thn.id
WHERE pena.id = id_hydrant;

-- Q ≥ 120 m3 ===> pas d'anomalie
-- 119 m3 ≥ Q ≥ 60 m3 ===> non conforme
    if( p_rec1.capacite is not null
        and p_rec1.capacite <> ''
        and p_rec1.capacite::int >= 60
        and p_rec1.capacite::int < 120
    ) then
        select id
        into p_anomalie_id
        from remocra.type_hydrant_anomalie
        where code = 'VOLUME_INSUFF_NC';

        INSERT INTO remocra.hydrant_anomalies (hydrant, anomalies)
        VALUES (id_hydrant, p_anomalie_id);

    -- Q < 60 ===> INDISPO
    ELSEIF ( p_rec1.capacite IS NOT NULL
        AND p_rec1.capacite <> ''
        and p_rec1.capacite::int < 60
    ) THEN
        SELECT id INTO p_anomalie_id
        FROM remocra.type_hydrant_anomalie
        WHERE code = 'VOLUME_INSUFF';

        INSERT INTO remocra.hydrant_anomalies (hydrant, anomalies)
        VALUES (id_hydrant, p_anomalie_id);
    -- Uniquement pour les aires artificielles
    -- Si volume null ou vide ou insuff => indispo
    ELSEIF( p_rec1.illimitee IS NOT TRUE
        AND (nature_pena = 'A'
            AND (p_rec1.capacite IS NULL
                OR p_rec1.capacite = ''
                or p_rec1.capacite::int < 60)
            )
    ) THEN
        SELECT id INTO p_anomalie_id
        FROM remocra.type_hydrant_anomalie
        WHERE code = 'VOLUME_INSUFF';

        INSERT INTO remocra.hydrant_anomalies (hydrant, anomalies)
        VALUES (id_hydrant, p_anomalie_id);
    END IF;

PERFORM remocra.calcul_indispo(id_hydrant);

END;

$function$
;
