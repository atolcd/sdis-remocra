-- DROP FUNCTION remocra.calcul_debit_pression_58(int8);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_58(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
p_anomalie_id INTEGER;
P_rec remocra.hydrant_pibi % rowtype;
p_code_type_hydrant_nature VARCHAR;
BEGIN
    select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;
    -- Suppression des anciennes anomalies
        delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (
            select id
            from remocra.type_hydrant_anomalie
            where critere IS NULL
            AND code <> 'INDISPONIBILITE_TEMP');
    -- Récupération du type_hydrant_nature id
        select thn.code into p_code_type_hydrant_nature from remocra.hydrant h left join remocra.type_hydrant_nature thn on (thn.id=h.nature) where h.id = id_hydrant;
    -- Définition des règles de calcul_debit_pression_58
    IF FOUND THEN
        -- Pression
        ---- Pression insuffisante
        IF(p_rec.pression IS NULL OR p_rec.pression < 1) THEN
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_INSUFF';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
        ---- Pression trop élévée
        IF(p_rec.pression > 8) THEN
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_TROP_ELEVEE_NC';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
        -- Débit
        ---- Débit insuffisant
        if (p_rec.debit IS NULL OR p_rec.debit < 15) then
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'DEBIT_INSUFF';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        elseif (p_rec.debit < 30) then
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'DEBIT_INSUFF_NC';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
    END IF;

    perform remocra.calcul_indispo(p_rec.id);
END;
$function$
;


--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
--@@@@@@@@@@@@@@@@@@@@@@@ calcul_volume_58 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
------ Insertion des anomalies
INSERT INTO remocra.type_hydrant_anomalie (actif, code, commentaire, nom, "version", critere)
SELECT true, 'VOLUME_INSUFF', 'Anomalie système, ne pas modifier', 'Volume insuffisant', 1, null
WHERE NOT EXISTS (
    SELECT 1 FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF'
);

INSERT INTO remocra.type_hydrant_anomalie (actif, code, commentaire, nom, "version", critere)
SELECT true, 'VOLUME_INSUFF_NC', 'Anomalie système, ne pas modifier', 'Volume insuffisant (non conforme)', 1, null
WHERE NOT EXISTS (
    SELECT 1 FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF_NC'
);
------ Ajout des valeurs fonctionnelles
DELETE FROM remocra.type_hydrant_anomalie_nature WHERE anomalie IN (SELECT id FROM remocra.type_hydrant_anomalie WHERE code IN ('VOLUME_INSUFF','VOLUME_INSUFF_NC'));

INSERT INTO remocra.type_hydrant_anomalie_nature (val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, "version", anomalie, nature)
SELECT
    NULL, NULL, 5, NULL, (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF'), tha.id
FROM remocra.type_hydrant_nature tha
    JOIN remocra.type_hydrant th on tha.type_hydrant = th.id AND th.code = 'PENA';

INSERT INTO remocra.type_hydrant_anomalie_nature (val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, "version", anomalie, nature)
SELECT
    NULL, NULL, 0, NULL, (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF_NC'), tha.id
FROM remocra.type_hydrant_nature tha
    JOIN remocra.type_hydrant th on tha.type_hydrant = th.id AND th.code = 'PENA';

------ Ajout de la méthode de calcul_volume_58
-- DROP FUNCTION remocra.calcul_volume_58(int8);
CREATE OR REPLACE FUNCTION remocra.calcul_volume_58(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
p_anomalie_id INTEGER;
p_rec remocra.hydrant_pena % ROWTYPE;
nature_pena text;
BEGIN
    select * into p_rec from remocra.hydrant_pena where id = id_hydrant;
    -- Suppression des anciennes anomalies
    delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (
        select id
        from remocra.type_hydrant_anomalie
        where critere IS NULL
        AND code <> 'INDISPONIBILITE_TEMP');
    select thn.code into nature_pena from remocra.hydrant_pena pena join remocra.hydrant h on pena.id = h.id join remocra.type_hydrant_nature thn on h.nature = thn.id WHERE pena.id = id_hydrant;
    -- Définition des règles de calcul_volume_58
    IF FOUND THEN
        -- Si capacite est convertible en float alors détail des rêgle de calcul
        if(p_rec.capacite ~ '^[0-9]+(\.[0-9]+)?$') then
            if(p_rec.capacite IS NULL OR p_rec.capacite::float < 15) then
                SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF';
                INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
            elseif (p_rec.capacite::float < 30) then
                SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF_NC';
                INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
            END IF;
        -- Sinon, INDISPO
        else
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'VOLUME_INSUFF';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
    END IF;

    PERFORM remocra.calcul_indispo(id_hydrant);
END;
$function$;

-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Gestion des appels de fonctions @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
---- Ajout fonction appel calcul_valume_58
Create or replace function remocra.trg_calcul_volume_58()
 returns TRIGGER
 language plpgsql
as $function$
declare p_rec RECORD;
BEGIN
    if (TG_OP = 'DELETE') then
        p_rec = OLD;
    else
        p_rec = NEW;
    end if;
    perform remocra.calcul_volume_58(p_rec.id);
    RETURN p_rec;
END;
$function$;
---- Ajout Trigger calcul_volume_58 à la table hydrant_pena
DROP TRIGGER IF EXISTS trg_calcul_volume_58 ON remocra.hydrant_pena;
CREATE TRIGGER trg_calcul_volume_58
AFTER INSERT OR UPDATE ON remocra.hydrant_pena
    FOR EACH ROW
        EXECUTE PROCEDURE remocra.trg_calcul_volume_58();

-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Remise en état des anomalies avec la nouvelle regle de calcul @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
SELECT remocra.calcul_debit_pression_58(id) FROM remocra.hydrant_pibi;
ALTER TABLE remocra.hydrant_pibi ENABLE TRIGGER trig_debit_pression_58;

SELECT remocra.calcul_volume_58(id) FROM remocra.hydrant_pena;
ALTER TABLE remocra.hydrant_pena ENABLE TRIGGER trg_calcul_volume_58;