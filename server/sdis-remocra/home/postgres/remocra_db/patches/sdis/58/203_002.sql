-- Création fonction spécifique calcul_debit_pression_58
create or replace function remocra.calcul_debit_pression_58(id_hydrant bigint)
 returns void
 language plpgsql
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
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_TROP_ELEVEE';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
        -- Pression dynamique
        ---- Pression dynamique insuffisante
        IF(p_rec.pression_dyn IS NULL OR p_rec.pression_dyn < 1) THEN
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_DYN_INSUFF';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
		---- Pression dynamique trop élévée
		IF(p_rec.pression_dyn > 8) THEN
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_DYN_TROP_ELEVEE';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
        -- Débit
        ---- Débit insuffisant
        if (p_rec.debit IS NULL OR p_rec.debit < 30) then
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'DEBIT_INSUFF';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
    END IF;
END;
$function$;
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Gestion des appels de fonctions @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
---- Ajout fonction appel calul_debit_pression_58
Create or replace function remocra.trg_calcul_debit_pression_58()
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
	perform remocra.calcul_debit_pression_58(p_rec.id);
    RETURN p_rec;
END;
$function$;
---- Ajout Trigger calcul_debit_pression_58 à la table hydrant_pibi
DROP TRIGGER IF EXISTS trig_debit_pression_58 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_58
AFTER INSERT OR UPDATE ON remocra.hydrant_pibi
    FOR EACH ROW
        EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_58();
---- Désactivation du trigger de calcul_debit_pression initial
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Remise en état des anomalies avec la nouvelle regle de calcul @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
SELECT remocra.calcul_debit_pression_58(id) FROM remocra.hydrant_pibi;
ALTER TABLE remocra.hydrant_pibi ENABLE TRIGGER trig_debit_pression_58;
