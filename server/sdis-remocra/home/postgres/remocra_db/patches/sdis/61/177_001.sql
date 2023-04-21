BEGIN;
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_61(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;
	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in
	(
	    select id
	    from remocra.type_hydrant_anomalie
	    where code
	    IN(
            'DEBIT_INSUFF_NC','DEBIT_INSUFF'
	    )
    );

    -- Si Q < X => INDISPO
	if (p_rec.debit < 10 or p_rec.debit is null) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    -- Si 30 > Q >= X => NON CONFORME
	elsif (p_rec.debit < 30 and p_rec.debit >= 10) then
	    select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
    	insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
	perform remocra.calcul_indispo(p_rec.id);
END;
$function$;



CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_61()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_debit_pression_61(p_rec.id);
    RETURN p_rec;
END;
$function$
;
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;
DROP TRIGGER IF EXISTS trig_debit_pression_61 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_61 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_61();


COMMIT;




--------------------------
---------PENA-------------
--------------------------
CREATE
OR REPLACE FUNCTION remocra.calcul_volume_61(id_hydrant bigint) RETURNS VOID LANGUAGE plpgsql AS $FUNCTION$ DECLARE p_anomalie_id INTEGER;

p_rec remocra.hydrant_pena % ROWTYPE;

BEGIN
    -- Suppression des anomalies sur le volume
    delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in
    (
        select id
        from remocra.type_hydrant_anomalie
        where code
        IN(
            'VOLUME_INSUFF', 'VOLUME_INSUFF_NC'
        )
    );
    SELECT * INTO p_rec FROM remocra.hydrant_pena WHERE id = id_hydrant;

    -- Volume < X ===> indispo
    IF (p_rec.capacite IS NOT NULL AND p_rec.capacite <> ''  AND p_rec.capacite::int < 30)THEN
        SELECT
            id INTO p_anomalie_id
        FROM
            remocra.type_hydrant_anomalie
        WHERE
            code = 'VOLUME_INSUFF';
        INSERT INTO
            remocra.hydrant_anomalies (hydrant, anomalies)
        VALUES
            (p_rec.id, p_anomalie_id);
    END IF;

    perform remocra.calcul_indispo(p_rec.id);

END;

$FUNCTION$;

CREATE
OR REPLACE FUNCTION remocra.trg_calcul_volume_61() RETURNS TRIGGER LANGUAGE plpgsql AS $FUNCTION$ DECLARE p_rec RECORD;

BEGIN
    IF (TG_OP = 'DELETE')
    THEN
        p_rec = OLD;
    ELSE
        p_rec = NEW;
    END IF;

    perform remocra.calcul_volume_61(p_rec.id);

    RETURN p_rec;

END;

$FUNCTION$;

DROP TRIGGER IF EXISTS trig_volume_61 ON remocra.hydrant_pena;

CREATE TRIGGER trig_volume_61
AFTER
INSERT
    OR
UPDATE
    ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_volume_61();

