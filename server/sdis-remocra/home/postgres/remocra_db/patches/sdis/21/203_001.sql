-- DROP FUNCTION remocra.calcul_debit_pression_21(int8);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_21(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

DECLARE
    p_anomalie_id integer;
    p_rec remocra.hydrant_pibi%ROWTYPE;
    p_diametre_id integer;
BEGIN

    SELECT * INTO p_rec
    FROM remocra.hydrant_pibi
    WHERE id = id_hydrant;

    -- Suppression des anomalies débit/pression des règles communes
    DELETE FROM remocra.hydrant_anomalies
    WHERE hydrant = id_hydrant
        AND anomalies IN (SELECT id FROM remocra.type_hydrant_anomalie WHERE critere IS NULL and code <> 'INDISPONIBILITE_TEMP');

    -- Anomalies Debit
    ---- Diamètre 150
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code ILIKE 'diam150';
    IF (p_rec.diametre = p_diametre_id) THEN
        IF (p_rec.debit < 108) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        ELSIF (p_rec.debit < 120) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
    end if;
    ---- Diamètre 100
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code ILIKE 'diam100';
    IF (p_rec.diametre = p_diametre_id) THEN
        IF (p_rec.debit < 27) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        ELSIF (p_rec.debit < 54) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
    end if;
    ---- Diamètre 70
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code ILIKE 'diam70';
    IF (p_rec.diametre = p_diametre_id) THEN
        IF (p_rec.debit < 27) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        ELSIF (p_rec.debit < 30) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
    end if;

    perform remocra.calcul_indispo(p_rec.id);

END;
$function$
;


SELECT remocra.calcul_debit_pression_21(id) FROM remocra.hydrant_pibi;
