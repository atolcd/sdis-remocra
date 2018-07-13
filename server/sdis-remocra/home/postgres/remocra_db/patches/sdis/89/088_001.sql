--
-- SDIS89
-- 
-- Gestion de deux types d'anomalies sur les PIBI : DEBIT_INF_15 / DEBIT_15_30_NC
--  * Manuellement par l'utilisateur de manière à ce qu'il puisse voir la disponibilité calculée côté client
--  * Automatiquement par un trigger sur la table remocra.hydrant_pibi pour une donnée de qualité
-- 
-- Remarque : les points des anomalies "classiques" liées au débit et à la pression ont été redéfinis à 0 ('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC') 
--

begin;





ALTER TABLE remocra.hydrant DISABLE TRIGGER ALL;
ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER ALL;
ALTER TABLE remocra.hydrant_pena DISABLE TRIGGER ALL;
ALTER TABLE remocra.hydrant_anomalies DISABLE TRIGGER ALL;




-- Ajout d'une catégorie d'anomalies liées aux valeurs de débit
INSERT INTO remocra.type_hydrant_critere(code, nom) VALUES ('DEBIT', 'Débit');

-- Ajout des anomalies
INSERT INTO remocra.type_hydrant_anomalie(code, nom, critere) VALUES ('DEBIT_INF_15', 'Débit inférieur à 15 m3/h', (SELECT id FROM remocra.type_hydrant_critere WHERE code = 'DEBIT'));
INSERT INTO remocra.type_hydrant_anomalie(code, nom, critere) VALUES ('DEBIT_15_30_NC', 'Débit compris entre 15 et 30 m3/h', (SELECT id FROM remocra.type_hydrant_critere WHERE code = 'DEBIT'));



-- Paramétrage des poids
-- Poids anomalie pour "Débit inférieur à 15 m3/h" et PIBI
INSERT INTO remocra.type_hydrant_anomalie_nature (
    val_indispo_admin,
    val_indispo_hbe,
    val_indispo_terrestre,
    version,
    anomalie,
    nature
) SELECT
    0::integer,
    0::integer,
    5::integer,
    1::integer,
    (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'DEBIT_INF_15'),
    n.id
FROM
    (SELECT id FROM remocra.type_hydrant_nature WHERE code IN('PI','BI')) n;
    
-- Poids anomalie pour "Débit compris entre 15 et 30 m3/h" et PIBI    
INSERT INTO remocra.type_hydrant_anomalie_nature (
    val_indispo_admin,
    val_indispo_hbe,
    val_indispo_terrestre,
    version,
    anomalie,
    nature
) SELECT
    0::integer,
    0::integer,
    0::integer,
    1::integer,
    (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'DEBIT_15_30_NC'),
    n.id
FROM
    (SELECT id FROM remocra.type_hydrant_nature WHERE code IN('PI','BI')) n;

-- Aucun poid pour les anomalies débit/pression des règles communes
delete from remocra.type_hydrant_anomalie_nature where anomalie in (select id from remocra.type_hydrant_anomalie where code in ('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));





-- Executé après remocra.calcul_debit_pression car :
-- "SQL specifies that multiple triggers should be fired in time-of-creation order. PostgreSQL uses name order, which was judged to be more convenient."
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression_89(id_hydrant bigint)
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
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('DEBIT_INF_15', 'DEBIT_15_30_NC'));

	-- Ajout des anomalies
	if (p_rec.debit < 15) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INF_15';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	elsif (p_rec.debit < 30) then
		select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_15_30_NC';
		insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id, p_anomalie_id);
	end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.calcul_debit_pression_89(bigint)
  OWNER TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_89(bigint) TO public;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_89(bigint) TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression_89(bigint) TO remocra;



CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression_89()
  RETURNS trigger AS
$BODY$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_debit_pression_89(p_rec.id);

    RETURN p_rec;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.trg_calcul_debit_pression_89()
  OWNER TO postgres;



DROP TRIGGER if exists trig_debit_pression_89 on remocra.hydrant_pibi;
CREATE TRIGGER trig_debit_pression_89
  AFTER INSERT OR UPDATE
  ON remocra.hydrant_pibi
  FOR EACH ROW
  EXECUTE PROCEDURE remocra.trg_calcul_debit_pression_89();





-- Suppression des anomalies PIBI des règles communes
select remocra.calcul_debit_pression_89(id) from remocra.hydrant_pibi;
select remocra.calcul_indispo(id) from remocra.hydrant_pibi;

ALTER TABLE remocra.hydrant ENABLE TRIGGER ALL;
ALTER TABLE remocra.hydrant_pibi ENABLE TRIGGER ALL;
ALTER TABLE remocra.hydrant_pena ENABLE TRIGGER ALL;
ALTER TABLE remocra.hydrant_anomalies ENABLE TRIGGER ALL;





alter table remocra.hydrant_pibi disable trigger trig_debit_pression;





commit;

