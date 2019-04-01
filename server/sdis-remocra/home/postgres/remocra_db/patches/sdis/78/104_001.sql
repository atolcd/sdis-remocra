begin;

UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'DEBIT_INSUFF';
UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'DEBIT_INSUFF_NC';
UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'DEBIT_TROP_ELEVE';
UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'PRESSION_DYN_INSUFF';
UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'PRESSION_DYN_TROP_ELEVEE';
UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'PRESSION_INSUFF';
UPDATE remocra.type_hydrant_anomalie set nom = '', critere = null where code  = 'PRESSION_TROP_ELEVEE';

 
INSERT INTO remocra.type_hydrant_domaine (actif, code, nom, version) values (TRUE, 'AUTOROUTE', 'Autoroute', 1);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_78(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));

	-- Ajout des anomalies (VOIR pour ajouter la pression <1 bar)
	if (p_rec.debit < 60) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
        if (p_rec.pression_dyn < 1) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION remocra.calcul_debit_pression_78(bigint) OWNER TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_78(bigint) TO public;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_78(bigint) TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_78(bigint) TO remocra;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_78() RETURNS trigger AS
$BODY$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_debit_pression_78(p_rec.id);

    RETURN p_rec;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION remocra.trg_calcul_debit_pression_78() OWNER TO postgres;
CREATE TRIGGER trig_debit_pression_78 AFTER INSERT OR UPDATE ON remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_78();


commit;
