BEGIN;
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_91(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
    p_diametre_id integer;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code = 'DIAM150';
	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));
	-- Ajout des anomalies DEBIT
	if (p_rec.debit is null OR (p_rec.debit >= 30 AND p_rec.diametre != p_diametre_id) OR (p_rec.debit >= 90 AND p_rec.diametre = p_diametre_id)) then -- cas de creation et reception sans anomalies
	    perform remocra.calcul_indispo(p_rec.id);
	elsif (p_rec.debit < 30 OR (p_rec.debit < 90 AND p_rec.diametre = p_diametre_id)) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
    -- Ajout anomalies PRESSION
    if (p_rec.pression is null OR p_rec.pression >= 1) then -- cas de creation et reception sans anomalies
	    perform remocra.calcul_indispo(p_rec.id);
	elsif (p_rec.pression < 1 ) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
END;
$function$
;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_91()
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
	perform remocra.calcul_debit_pression_91(p_rec.id);
    RETURN p_rec;
END;
$function$
;
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;
DROP TRIGGER IF EXISTS trig_debit_pression_91 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_91 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_91();

-- MAJ param conf
UPDATE remocra.param_conf
SET valeur = '91'
WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';

UPDATE remocra.param_conf
SET valeur = '91'
WHERE cle = 'HYDRANT_NUMEROTATION_METHODE';
COMMIT;