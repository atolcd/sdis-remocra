-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Mise à plat des anomalies systeme et ajout des anomalies manquantes @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    ---- Suppression de l'assignation de l'anomalie aux PEI
    Delete from remocra.hydrant_anomalies where anomalies in (
        select id from remocra.type_hydrant_anomalie
        where critere IS NULL
            AND code <> 'INDISPONIBILITE_TEMP');
    ---- Suppression des indices fonctionnels
    DELETE FROM remocra.type_hydrant_anomalie_nature WHERE anomalie IN (
	select id from remocra.type_hydrant_anomalie
        where critere IS NULL
            AND code <> 'INDISPONIBILITE_TEMP');
    ---- Suppression des anomalies
    DELETE FROM remocra.type_hydrant_anomalie
    where critere IS NULL
        AND code <> 'INDISPONIBILITE_TEMP';
    ---- Insertion des anomalies
    INSERT INTO remocra.type_hydrant_anomalie (actif, code, commentaire, nom, "version", critere)
        VALUES
            (true, 'DEBIT_INSUFF','Anomalie système, ne pas modifier','Débit insuffisant',1,null),
            (true, 'DEBIT_INSUFF_NC','Anomalie système, ne pas modifier','Débit insuffisant (non conforme)',1,null),
            (true, 'DEBIT_TROP_ELEVE','Anomalie système, ne pas modifier','Débit trop élevé',1,null),
            (true, 'DEBIT_TROP_ELEVE_NC','Anomalie système, ne pas modifier','Débit trop élevé (non conforme)',1,null),
            (true, 'PRESSION_DYN_INSUFF','Anomalie système, ne pas modifier','Pression dynamique insuffisante',1,null),
            (true, 'PRESSION_DYN_INSUFF_NC','Anomalie système, ne pas modifier','Pression dynamique insuffisante (non conforme)',1,null),
            (true, 'PRESSION_DYN_TROP_ELEVEE','Anomalie système, ne pas modifier','Pression dynamique trop élevée',1,null),
            (true, 'PRESSION_DYN_TROP_ELEVEE_NC','Anomalie système, ne pas modifier','Pression dynamique trop élevée (non conforme)',1,null),
            (true, 'PRESSION_INSUFF','Anomalie système, ne pas modifier','Pression statique insuffisante',1,null),
            (true, 'PRESSION_INSUFF_NC','Anomalie système, ne pas modifier','Pression statique insuffisante (non conforme)',1,null),
            (true, 'PRESSION_TROP_ELEVEE','Anomalie système, ne pas modifier','Pression statique trop élevée',1,null),
            (true, 'PRESSION_TROP_ELEVEE_NC','Anomalie système, ne pas modifier','Pression statique trop élevée (non conforme)',1,null);
    ---- Insertion des indices fonctionnels (toutes les anomalies système)
    WITH value_to_insert AS (
		SELECT
			5 AS val_indispo_terrestre,
			tha.id AS anomalie,
			thn.id AS nature
		FROM remocra.type_hydrant_anomalie tha
			JOIN remocra.TYPE_hydrant_nature thn ON (thn.code = 'PI' or thn.code = 'BI')
		WHERE critere IS NULL
    )
    INSERT INTO remocra.type_hydrant_anomalie_nature (val_indispo_terrestre, anomalie, nature)
    SELECT * FROM value_to_insert;
    ---- Update des indices fonctionnels (anomalies non conforme)
	UPDATE remocra.type_hydrant_anomalie_nature
	SET val_indispo_terrestre = 0
	WHERE anomalie IN (
		SELECT id
		FROM remocra.type_hydrant_anomalie
		WHERE code LIKE '%_NC'
	);

-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Création fonction spécifique calcul_debit_pression_78 @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
create or replace function remocra.calcul_debit_pression_78(id_hydrant bigint)
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
    -- Aucune mise en indisponibilité automatique, simplement indiquer que le PIBI est non conforme.
	-- mise en non conformité uniquement dans le cas d'un-e débit/pression insuffisant-e.
    IF FOUND THEN
        -- Pression statique non conforme
        IF(p_rec.pression < 1 AND p_rec.pression > 0) THEN
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_INSUFF_NC';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
        -- Pression dynamique non conforme
        IF(p_rec.pression_dyn < 1 AND p_rec.pression_dyn > 0) THEN
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'PRESSION_DYN_INSUFF_NC';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        END IF;
        -- Débit non conforme
        IF(p_rec.debit < 45 AND p_rec.debit > 0) then
            SELECT id INTO p_anomalie_id FROM remocra.type_hydrant_anomalie WHERE code = 'DEBIT_INSUFF_NC';
            INSERT INTO remocra.hydrant_anomalies (hydrant,anomalies) VALUES (p_rec.id,p_anomalie_id);
        end if;
    end if;
end;
$function$;

-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Gestion des appels de fonctions @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
---- Ajout fonction appel calul_debit_pression_78
Create or replace function remocra.trg_calcul_debit_pression_78()
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
	perform remocra.calcul_debit_pression_78(p_rec.id);
    RETURN p_rec;
END;
$function$;

---- Ajout Trigger calcul_debit_pression_78 à la table hydrant_pibi
DROP TRIGGER IF EXISTS trig_debit_pression_78 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_78
AFTER INSERT OR UPDATE ON remocra.hydrant_pibi
    FOR EACH ROW
        EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_78();

---- Désactivation du trigger de calcul_debit_pression initial
alter table remocra.hydrant_pibi
disable trigger trig_debit_pression;

-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
-- @@ Remise en état des anomalies avec la nouvelle regle de calcul @@
-- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
select remocra.calcul_debit_pression_78(id) FROM remocra.hydrant_pibi;