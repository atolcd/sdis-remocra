--DROP FUNCTION remocra.calcul_debit_pression_61(int8); -- Si nécessaire lors de l'exécution du patch

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_61(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

DECLARE
    p_anomalie_id integer;
    p_rec remocra.hydrant_pibi%ROWTYPE;
    p_diametre_id integer;
BEGIN
        select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;
        -- Suppression des anciennes anomalies systeme
        delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (
            select id
            from remocra.type_hydrant_anomalie
            where critere IS NULL
                AND code <> 'INDISPONIBILITE_TEMP'
        );
        -- PRESSION_INSUFF
        if ((p_rec.pression < 1) or (p_rec.pression is null)) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
        -- PRESSION_TROP_ELEVEE
        if (p_rec.pression > 8) then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_TROP_ELEVEE';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;
        -- PRESSION_DYN_TROP_ELEVEE
        if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
            if (p_rec.pression_dyn > 8) then
                select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_TROP_ELEVEE';
                insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
            end if;
        end if;
        -- DEBIT_INSUFF
        if ((p_rec.pression_dyn <= 8) or (p_rec.pression_dyn is null)) then
            if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
                if (p_rec.debit < 30) then
                    select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
                    insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
                end if;
            end if;
        end if;
        if (p_rec.debit is null) then
            if ((p_rec.pression_dyn is null) or (p_rec.pression_dyn < 1)) then
                select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
                insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
            end if;
        end if;
        -- DEBIT_INSUFF_NC
        ---- DIAM 100
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM100';
        if (p_rec.diametre = p_diametre_id) then
            if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
                if ((p_rec.pression_dyn >= 1) and (p_rec.pression_dyn <= 8)) then
                    if ((p_rec.debit >= 30) and (p_rec.debit < 60)) then
                        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
                        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
                    end if;
                end if;
            end if;
        end if;
        ---- DIAM 150
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM150';
        if (p_rec.diametre = p_diametre_id) then
            if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
                if ((p_rec.pression_dyn >= 1) and (p_rec.pression_dyn <= 8)) then
                    if ((p_rec.debit >= 30) and (p_rec.debit < 120)) then
                        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
                        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
                    end if;
                end if;
            end if;
        end if;
        -- DEBIT_TROP_ELEVE
        ---- DIAM 80
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM80';
        if (p_rec.diametre = p_diametre_id) then
            if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
                if ((p_rec.pression_dyn >= 1) and (p_rec.pression_dyn <= 8)) then
                    if (p_rec.debit >= 90) then
                        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
                        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
                    end if;
                end if;
            end if;
        end if;
        ---- DIAM 100
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM100';
        if (p_rec.diametre = p_diametre_id) then
            if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
                if ((p_rec.pression_dyn >= 1) and (p_rec.pression_dyn <= 8)) then
                    if (p_rec.debit >= 150) then
                        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
                        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
                    end if;
                end if;
            end if;
        end if;
        ---- DIAM 150
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM150';
        if (p_rec.diametre = p_diametre_id) then
            if ((p_rec.pression >= 1) and (p_rec.pression <= 8)) then
                if ((p_rec.pression_dyn >= 1) and (p_rec.pression_dyn <= 8)) then
                    if (p_rec.debit >= 270) then
                        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
                        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
                    end if;
                end if;
            end if;
        END IF;

    perform remocra.calcul_indispo(p_rec.id);
END;
$function$;

SELECT remocra.calcul_debit_pression_61(id) FROM remocra.hydrant_pibi;
