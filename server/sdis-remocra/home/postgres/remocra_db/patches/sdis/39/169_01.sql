--------------------------
---------PIBI-------------
--------------------------
CREATE
OR REPLACE FUNCTION remocra.calcul_debit_pression_39(id_hydrant bigint) RETURNS VOID LANGUAGE plpgsql AS $ FUNCTION $ DECLARE p_anomalie_id INTEGER;

p_rec remocra.hydrant_pibi % ROWTYPE;

BEGIN
SELECT
    * INTO p_rec
FROM
    remocra.hydrant_pibi
WHERE
    id = id_hydrant;

-- Suppression des anomalies débit/pression des règles communes
DELETE FROM
    remocra.hydrant_anomalies
WHERE
    hydrant = p_rec.id
    AND anomalies IN (
        SELECT
            id
        FROM
            remocra.type_hydrant_anomalie
        WHERE
            code IN(
                'PRESSION_INSUFF_NC',
                'PRESSION_INSUFF',
                'PRESSION_TROP_ELEVEE',
                'PRESSION_DYN_INSUFF',
                'PRESSION_DYN_TROP_ELEVEE',
                'DEBIT_TROP_ELEVE',
                'DEBIT_INSUFF_NC',
                'DEBIT_INSUFF'
            )
    );

-- Ajout des anomalies DEBIT
-- D ≥ 60 m3 / h ===> pas d'anomalie
-- 30 m3 / h ≤  D  ≤ 59 m3 / h ===> NON_CONFORME
IF (
    p_rec.debit >= 30
    AND p_rec.debit < 60
) THEN
SELECT
    id INTO p_anomalie_id
FROM
    remocra.type_hydrant_anomalie
WHERE
    code = 'DEBIT_INSUFF_NC';

INSERT INTO
    remocra.hydrant_anomalies (hydrant, anomalies)
VALUES
    (p_rec.id, p_anomalie_id);

--  D ≤ 29 m3 / h OR NULL ===> INDISPO
ELSE IF(
    p_rec.debit < 30
    AND p_rec.debit IS NULL
) THEN
SELECT
    id INTO p_anomalie_id
FROM
    remocra.type_hydrant_anomalie
WHERE
    code = 'DEBIT_INSUFF';

INSERT INTO
    remocra.hydrant_anomalies (hydrant, anomalies)
VALUES
    (p_rec.id, p_anomalie_id);

END IF;

-- Ajout anomalies PRESSION
IF p_rec.pression < 1 THEN
SELECT
    id INTO p_anomalie_id
FROM
    remocra.type_hydrant_anomalie
WHERE
    code = 'PRESSION_INSUFF';

INSERT INTO
    remocra.hydrant_anomalies (hydrant, anomalies)
VALUES
    (p_rec.id, p_anomalie_id);

END IF;

perform remocra.calcul_indispo(p_rec.id);

END;

$ FUNCTION $;

CREATE
OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_39() RETURNS TRIGGER LANGUAGE plpgsql AS $ FUNCTION $ DECLARE p_rec RECORD;

BEGIN IF (TG_OP = 'DELETE') THEN p_rec = OLD;

ELSE p_rec = NEW;

END IF;

perform remocra.calcul_debit_pression_39(p_rec.id);

RETURN p_rec;

END;

$ FUNCTION $;

DROP TRIGGER IF EXISTS trig_debit_pression_39 ON remocra.hydrant_pibi;

CREATE TRIGGER trig_debit_pression_39
AFTER
INSERT
    OR
UPDATE
    ON remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE trg_calcul_debit_pression_39();

--------------------------
---------PENA-------------
--------------------------
CREATE
OR REPLACE FUNCTION remocra.calcul_volume_39(id_hydrant bigint) RETURNS VOID LANGUAGE plpgsql AS $ FUNCTION $ DECLARE p_anomalie_id INTEGER;

p_rec remocra.hydrant_pena % ROWTYPE;

BEGIN

-- Suppression des anomalies sur le volume
DELETE FROM remocra.hydrant_anomalies
WHERE hydrant = p_rec.id
    AND anomalies IN (
        SELECT
            id
        FROM
            remocra.type_hydrant_anomalie
        WHERE
            code IN(
                'VOLUME_INSUFF',
                'VOLUME_INSUFF_NC'
                )
        );

SELECT
    * INTO p_rec
FROM
    remocra.hydrant_pena
WHERE
    id = id_hydrant;

-- Q ≥ 120 m3 ===> pas d'anomalie
--30 m3 ≥ Q ≥ 119 m3 ===> non conforme
IF (
    p_rec.capacite >= 30
    AND p_rec.capacite < 120
) THEN
SELECT
    id INTO p_anomalie_id
FROM
    remocra.type_hydrant_anomalie
WHERE
    code = 'VOLUME_INSUFF_NC';

INSERT INTO
    remocra.hydrant_anomalies (hydrant, anomalies)
VALUES
    (p_rec.id, p_anomalie_id);

-- Q < 30 ===> INDISPO
ELSE IF (p_rec.capacite < 30) THEN
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

$ FUNCTION $;

CREATE
OR REPLACE FUNCTION remocra.trg_calcul_volume_39() RETURNS TRIGGER LANGUAGE plpgsql AS $ FUNCTION $ DECLARE p_rec RECORD;

BEGIN IF (TG_OP = 'DELETE') THEN p_rec = OLD;

ELSE p_rec = NEW;

END IF;

perform remocra.calcul_volume_39(p_rec.id);

RETURN p_rec;

END;

$ FUNCTION $;

DROP TRIGGER IF EXISTS trig_volume_39 ON remocra.hydrant_pena;

CREATE TRIGGER trig_volume_39
AFTER
INSERT
    OR
UPDATE
    ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_volume_39();

COMMIT;