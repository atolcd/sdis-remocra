begin;

-- Function: remocra.trg_calcul_indispo()

-- DROP FUNCTION remocra.trg_calcul_indispo();

CREATE OR REPLACE FUNCTION remocra.trg_calcul_indispo()
  RETURNS trigger AS
$BODY$
DECLARE
	p_rec record;
        p_id bigint;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;

	if TG_TABLE_NAME = 'hydrant_anomalies' then
        	p_id := p_rec.hydrant;
    	else
        	p_id := p_rec.id;
    	end if;
	

	perform remocra.calcul_indispo(p_id);
  RETURN p_rec;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.trg_calcul_indispo()
  OWNER TO postgres;
  

-- Trigger: trig_indispo on remocra.hydrant_pena

-- DROP TRIGGER trig_indispo ON remocra.hydrant_pena;

CREATE TRIGGER trig_indispo
  AFTER INSERT OR UPDATE
  ON remocra.hydrant_pena
  FOR EACH ROW
  EXECUTE PROCEDURE remocra.trg_calcul_indispo();


commit;

