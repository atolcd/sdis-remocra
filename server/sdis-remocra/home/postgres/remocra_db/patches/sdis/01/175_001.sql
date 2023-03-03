----------------------------------
--------------PIBI----------------
----------------------------------
BEGIN;
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_01(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$

DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
    p_diametre_id integer;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;
	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in
	(
	    select id
	    from remocra.type_hydrant_anomalie
	    where code
	    IN(
            'PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE',
            'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE',
            'DEBIT_INSUFF_NC', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF'
	    )
    );


    -- CONTROLE DEBIT POUR LES DIAM80 et DIAM70
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM80' OR code LIKE 'DIAM70';
	-- Ajout des anomalies DEBIT

   -- Q ≥ 30 m3 / h ===> pas d'anomalie
	if (p_rec.debit >= 30 AND p_rec.diametre = p_diametre_id)
	then
	    perform remocra.calcul_indispo(p_rec.id);
	--  Q  < 30 m3 / h ou non renseigné ===> INDISPO
	elsif ((p_rec.debit < 30 or p_rec.debit is null) AND p_rec.diametre = p_diametre_id)
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;



  -- CONTROLE DEBIT POUR LES DIAM100
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre thd WHERE thd.code LIKE 'DIAM100';
	-- Ajout des anomalies DEBIT

   -- Q ≥ 60 m3 / h ===> pas d'anomalie
	if (p_rec.debit >= 60 AND p_rec.diametre = p_diametre_id)
	then perform remocra.calcul_indispo(p_rec.id);

	-- 30 m3 / h ≤  Q  ≤ 59 m3 / h ===> NON_CONFORME
	elsif ((p_rec.debit >= 30 AND p_rec.debit < 60) AND p_rec.diametre = p_diametre_id)
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	--  Q < 30 m3 / h ou non renseigné ===> INDISPO
	elsif ((p_rec.debit < 30 or p_rec.debit is null)AND p_rec.diametre = p_diametre_id)
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;



    -- CONTROLE DEBIT POUR LES DIAM150
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM150';
	-- Ajout des anomalies DEBIT

   -- Q ≥ 120 m3 / h ===> pas d'anomalie
	if (p_rec.debit >= 120 AND p_rec.diametre = p_diametre_id)
	then
	    perform remocra.calcul_indispo(p_rec.id);
	-- 30 m3 / h ≤  Q  ≤ 119 m3 / h ===> NON_CONFORME
	elsif ((p_rec.debit >= 30 AND p_rec.debit < 120) AND p_rec.diametre = p_diametre_id)
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	--  Q < 30 m3 / h ou non renseigné ===> INDISPO
	elsif ((p_rec.debit is null OR p_rec.debit < 30) AND p_rec.diametre = p_diametre_id)
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant, anomalies) values (p_rec.id, p_anomalie_id);
	end if;

	perform remocra.calcul_indispo(p_rec.id);
END;
$function$
;


CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_01()
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
	perform remocra.calcul_debit_pression_01(p_rec.id);
    RETURN p_rec;
END;
$function$
;
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;
DROP TRIGGER IF EXISTS trig_debit_pression_01 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_01 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_01();



--------------------------
---------PENA-------------
--------------------------
CREATE
OR REPLACE FUNCTION remocra.calcul_volume_01(id_hydrant bigint) RETURNS VOID LANGUAGE plpgsql AS $FUNCTION$ DECLARE p_anomalie_id INTEGER;

p_rec remocra.hydrant_pena % ROWTYPE;

BEGIN
    -- Suppression des anomalies sur le volume
    delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in
    (
        select id
        from remocra.type_hydrant_anomalie
        where code = 'VOLUME_INSUFF'
    );


    SELECT * INTO p_rec FROM remocra.hydrant_pena WHERE id = id_hydrant;

    -- Volume < 30 ===> indispo
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
OR REPLACE FUNCTION remocra.trg_calcul_volume_01() RETURNS TRIGGER LANGUAGE plpgsql AS $FUNCTION$ DECLARE p_rec RECORD;

BEGIN
    IF (TG_OP = 'DELETE')
    THEN
        p_rec = OLD;
    ELSE
        p_rec = NEW;
    END IF;

    perform remocra.calcul_volume_01(p_rec.id);

    RETURN p_rec;

END;

$FUNCTION$;

DROP TRIGGER IF EXISTS trig_volume_01 ON remocra.hydrant_pena;

CREATE TRIGGER trig_volume_01
AFTER
INSERT
    OR
UPDATE
    ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_volume_01();

COMMIT;