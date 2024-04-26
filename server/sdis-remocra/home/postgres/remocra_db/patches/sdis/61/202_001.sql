-- Mise à niveau des anomalies pression_dyn
---- Insert de l'anomalie manquante
INSERT INTO remocra.type_hydrant_anomalie (actif, code, commentaire, nom, "version", critere)
select true, 'PRESSION_DYN_INSUFF_NC','Anomalie système, ne pas modifier','Pression dynamique non conforme (< 1 bar)',1,NULL
WHERE 'PRESSION_DYN_INSUFF_NC' NOT IN (SELECT code FROM remocra.type_hydrant_anomalie);
---- Changement de nom pour l'ancienne
UPDATE remocra.type_hydrant_anomalie
	SET nom='Pression dynamique insuffisante'
	WHERE code = 'PRESSION_DYN_INSUFF';
---- Assignation des indices de blocage
WITH value_to_insert AS (
	SELECT
		0 AS val_indispo_terrestre,
		tha.id AS anomalie,
		thn.id AS nature
	FROM remocra.type_hydrant_anomalie tha
		JOIN remocra.TYPE_hydrant_nature thn ON thn.type_hydrant = (SELECT id FROM remocra.type_hydrant WHERE code = 'PIBI')
	WHERE tha.code = 'PRESSION_DYN_INSUFF_NC'
)
INSERT INTO remocra.type_hydrant_anomalie_nature (val_indispo_terrestre, anomalie, nature)
SELECT * FROM value_to_insert;

-- Modification de la méthode de calcul:
-- DROP FUNCTION remocra.calcul_debit_pression_61(int8); -- Si nécessaire lors de l'exécution du patch

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_61(id_hydrant bigint)
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
    	delete from remocra.hydrant_anomalies where hydrant=id_hydrant and anomalies in
    	(
    	    select id
    	    from remocra.type_hydrant_anomalie
    	    where code
    	    IN(
                'PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE',
                'PRESSION_DYN_INSUFF_NC', 'PRESSION_DYN_TROP_ELEVEE',
                'DEBIT_INSUFF_NC', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF'
    	    )
        );

        --------------------- Pour les débits
        -- DIAM 80
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM80';

        if ((p_rec.debit < 30 or p_rec.debit is null)  AND p_rec.diametre = p_diametre_id)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        elsif (p_rec.debit > 90  AND p_rec.diametre = p_diametre_id)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;

        --DIAM 100
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM100';

    	if ((p_rec.debit < 30 or p_rec.debit is null)  AND p_rec.diametre = p_diametre_id)
    	then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
    		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    	elsif (p_rec.debit < 60  AND p_rec.diametre = p_diametre_id)
    	then
    		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
    		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    	elsif (p_rec.debit > 130  AND p_rec.diametre = p_diametre_id)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    	end if;

        --DIAM 150
        SELECT id into p_diametre_id FROM remocra.type_hydrant_diametre WHERE code LIKE 'DIAM150';

    	if ((p_rec.debit < 60 or p_rec.debit is null)  AND p_rec.diametre = p_diametre_id)
    	then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
    		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    	elsif (p_rec.debit < 120  AND p_rec.diametre = p_diametre_id)
    	then
    		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
    		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    	elsif (p_rec.debit > 180  AND p_rec.diametre = p_diametre_id)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
    	end if;

        --------------------- Pour les pressions
        if (p_rec.pression_dyn < 1)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_INSUFF_NC';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        elsif (p_rec.pression_dyn > 8)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_TROP_ELEVEE';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;

        if (p_rec.pression < 1)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        elsif (p_rec.pression > 8)
        then
            select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_TROP_ELEVEE';
            insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
        end if;

	perform remocra.calcul_indispo(p_rec.id);
END;
$function$
;
