begin;


-- Function: remocra.calcul_debit_pression_bspp(bigint)

-- DROP FUNCTION remocra.calcul_debit_pression_bspp(bigint);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_bspp(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));
	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('DEBIT_EGAL_0'));
	
        -- Ajout d'anomalie pour le calcul d'indispo (Le trigger de calcul d'indispo ne se déclenche pas if 0 row is affected)
        select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_EGAL_0';
	insert into remocra.hydrant_anomalies values (p_rec.id,p_anomalie_id);

	-- Ajout des anomalies
	if (p_rec.debit is null or p_rec.debit != 0) then
		delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies = p_anomalie_id;
	end if;
	
	-- Si débit strictement inférieur à 60 => indisponibilité
	if p_rec.debit is not null and p_rec.debit < 60 then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
	end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.calcul_debit_pression_bspp(bigint)
  OWNER TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_bspp(bigint) TO public;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_bspp(bigint) TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_bspp(bigint) TO remocra;

commit;
