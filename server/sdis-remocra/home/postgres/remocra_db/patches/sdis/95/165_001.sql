BEGIN;
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_95(id_hydrant bigint)
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
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF_NC', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF_NC', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NORME_NC'));

    -- CONTROLE DEBIT POUR LES DIAM100
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre thd WHERE thd.code LIKE 'DIAM100';
	-- Ajout des anomalies DEBIT

   -- Q ≥ 60 m3 / h ===> pas d'anomalie
	if (p_rec.debit is null
		OR (p_rec.debit >= 60 AND p_rec.diametre = p_diametre_id))
	then perform remocra.calcul_indispo(p_rec.id);

	-- 30 m3 / h ≤  Q  ≤ 59 m3 / h ===> NON_CONFORME
	elsif ((p_rec.debit >= 30 AND p_rec.debit < 60) AND p_rec.diametre = p_diametre_id)
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	--  Q ≤ 29 m3 / h ===> INDISPO
	elsif ((p_rec.debit < 30 AND p_rec.diametre = p_diametre_id))
	then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;

    -- CONTROLE DEBIT POUR LES DIAM80
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM80';
	-- Ajout des anomalies DEBIT

   -- Q ≥ 30 m3 / h ===> pas d'anomalie
	if (p_rec.debit is null 
		OR (p_rec.debit >= 30 AND p_rec.diametre = p_diametre_id)) 
	then 
	    perform remocra.calcul_indispo(p_rec.id);
	-- 15 m3 / h ≤  Q  ≤ 30 m3 / h ===> NON_CONFORME
	elsif ((p_rec.debit >= 15 AND p_rec.debit < 30) AND p_rec.diametre = p_diametre_id)
	then 
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	--  Q < 15 m3 / h ===> INDISPO
	elsif ((p_rec.debit < 15 AND p_rec.diametre = p_diametre_id)) 
	then 
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;

    -- CONTROLE DEBIT POUR LES DIAM150
    SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM150';
	-- Ajout des anomalies DEBIT

   -- Q ≥ 120 m3 / h ===> pas d'anomalie
	if (p_rec.debit is null 
		OR (p_rec.debit >= 120 AND p_rec.diametre = p_diametre_id))
	then 
	    perform remocra.calcul_indispo(p_rec.id);
	-- 60 m3 / h ≤  Q  ≤ 119 m3 / h ===> NON_CONFORME
	elsif ((p_rec.debit >= 60 AND p_rec.debit < 120) AND p_rec.diametre = p_diametre_id)
	then 
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	--  Q < 60 m3 / h ===> INDISPO
	elsif ((p_rec.debit < 60 AND p_rec.diametre = p_diametre_id)) 
	then 
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
    -- Ajout anomalies PRESSION
    if (p_rec.pression is null OR p_rec.pression >= 1) then -- cas de creation et reception sans anomalies
	    perform remocra.calcul_indispo(p_rec.id);
	else
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
	perform remocra.calcul_indispo(p_rec.id);
END;
$function$
;


CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_95()
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
	perform remocra.calcul_debit_pression_95(p_rec.id);
    RETURN p_rec;
END;
$function$
;
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;
DROP TRIGGER IF EXISTS trig_debit_pression_95 ON remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_95 AFTER
INSERT
    OR
UPDATE
    ON
    remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_95();

COMMIT;