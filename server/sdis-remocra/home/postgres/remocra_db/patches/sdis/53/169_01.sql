BEGIN;
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_53(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
    p_anomalie_id integer;
    p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN
    select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;
    -- Suppression des anomalies débit/pression des règles communes
    delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF_NC', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF'));

    -- Ajout des anomalies DEBIT

   -- NULL OU Q <= 15 ===> INDISPO
    if (p_rec.debit is null
        OR p_rec.debit <= 15)
    then
        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);

    -- 15 m3 / h   Q  ≤ 59 m3 / h ===> NON_CONFORME
    elsif (p_rec.debit > 15 AND p_rec.debit < 60)
    then
        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    --  Q > 1000 m3 / h ===> Trop elevée
    elsif (p_rec.debit > 1000 )
    then
        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    end if;

        -- Ajout anomalies PRESSION
    if (p_rec.pression is null OR p_rec.pression < 1) then
        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    -- P 14 bar ===> Trop élevé
    ELSIF (p_rec.pression > 14) then
        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_TROP_ELEVEE';
        insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    end if;
    perform remocra.calcul_indispo(p_rec.id);
END;
$function$
;


CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_53()
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
	perform remocra.calcul_debit_pression_53(p_rec.id);
    RETURN p_rec;
END;
$function$
;
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;
DROP TRIGGER IF EXISTS trig_debit_pression_53 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_53 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_53();

COMMIT;