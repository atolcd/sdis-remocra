/*
  Ajustement du calcul de dispo pour les PIBI pour prendre en compte ceux sans anomalie qui ont un debit correcte ou inconnu
*/

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_89(id_hydrant bigint)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN
	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anomalies débit/pression des règles communes
	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('DEBIT_INF_15', 'DEBIT_15_30_NC'));

	-- Ajout des anomalies
	if (p_rec.debit is null OR p_rec.debit >= 30) then -- cas de creation et reception sans anomalies
		perform remocra.calcul_indispo(p_rec.id);
	elsif (p_rec.debit < 15) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INF_15';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	elsif (p_rec.debit < 30) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_15_30_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
END;
$function$
;
