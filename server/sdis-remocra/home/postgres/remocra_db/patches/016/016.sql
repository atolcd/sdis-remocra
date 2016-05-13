CREATE SCHEMA tracabilite AUTHORIZATION postgres;
-- drop TABLE tracabilite.hydrant;

DROP TABLE IF EXISTS tracabilite.hydrant;

CREATE TABLE tracabilite.hydrant
(
  id serial not null,
  num_transac bigint,
  nom_operation character varying,
  date_operation timestamp with time zone,
  id_hydrant bigint,
  numero character varying,
  geometrie geometry,
  insee character varying,
  commune character varying,
  lieu_dit character varying,
  voie character varying,
  carrefour character varying,
  complement character varying,
  agent1 character varying,
  agent2 character varying,
  date_recep timestamp without time zone,
  date_reco timestamp without time zone,
  date_contr timestamp without time zone,
  date_verif timestamp without time zone,
  dispo_terrestre character varying,
  dispo_hbe character varying,
  nature character varying,
  type_hydrant character varying,
  anomalies character varying[],
  observation character varying,
  organisme character varying,
  hbe boolean,
  positionnement character varying,
  materiau character varying,
  vol_constate character varying,
  diametre character varying,
  debit integer,
  debit_max integer,
  pression double precision,
  pression_dyn double precision,
  marque character varying,
  modele character varying,
  CONSTRAINT hydrant_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tracabilite.hydrant
  OWNER TO postgres;

 DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant;
DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant_pibi;
DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant_pena;
DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant_anomalies;
DROP TRIGGER IF EXISTS trig_bd ON remocra.hydrant_pibi;
DROP TRIGGER IF EXISTS trig_bd ON remocra.hydrant_pena;
DROP TRIGGER IF EXISTS trig_bd ON remocra.hydrant_anomalies;

CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(p_id_hydrant bigint, p_operation varchar)
  RETURNS void
AS $BODY$
BEGIN
    insert into tracabilite.hydrant (num_transac, nom_operation, date_operation,
    id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco, date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, organisme,
    hbe, positionnement, materiau, vol_constate,
    diametre, debit, debit_max, pression, pression_dyn, marque, modele)
    SELECT
      -- traca
      txid_current() ,p_operation,  now() as date_operation,
      -- hydrant
      h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, array_agg(anomalie.nom), h.observation, o.nom,
      -- pena
      pena.hbe, p.nom, mat.nom, v.nom,
      -- pibi
      d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom
      FROM remocra.hydrant h
      -- hydrant
      JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
      JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
      LEFT JOIN remocra.commune c on (h.commune = c.id)
      LEFT JOIN remocra.organisme o on (h.organisme = o.id)
      -- pibi
      LEFT JOIN remocra.hydrant_pibi pibi on (pibi.id = h.id)
      LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
      LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
      LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
      -- pena
      LEFT JOIN remocra.hydrant_pena pena on (pena.id = h.id)
      LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
      LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
      LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
      -- anomalies
      LEFT JOIN (remocra.hydrant_anomalies ha JOIN remocra.type_hydrant_anomalie a on (ha.anomalies = a.id)) anomalie on (anomalie.hydrant = h.id)
      WHERE h.id = p_id_hydrant
      GROUP BY h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, h.observation, o.nom,
    pena.hbe, p.nom, mat.nom, v.nom,
    d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom;

END;
$BODY$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(p_id_hydrant bigint, p_operation varchar, p_num_transac bigint)
  RETURNS void
AS $BODY$
BEGIN
    update tracabilite.hydrant
    SET numero =h.numero, geometrie = h.geometrie, insee = h.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom, type_hydrant = th.nom,
    agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre, dispo_hbe = h.dispo_hbe,
    observation = h.observation, organisme = o.nom
    FROM remocra.hydrant h
    JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
    JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
    LEFT JOIN remocra.commune c on (h.commune = c.id)
    LEFT JOIN remocra.organisme o on (h.organisme = o.id)
    WHERE h.id = p_id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$BODY$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_pibi(p_id_pibi bigint, p_operation varchar, p_num_transac bigint)
  RETURNS void
AS $BODY$
BEGIN
    update tracabilite.hydrant
    SET  diametre = d.nom, debit = pibi.debit,  debit_max = pibi.debit_max, pression = pibi.pression, pression_dyn = pibi.pression_dyn, marque = m.nom, modele = mod.nom
    FROM remocra.hydrant_pibi pibi
    LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
    LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
    LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
    WHERE pibi.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pibi;
END;
$BODY$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_pena(p_id_pena bigint, p_operation varchar, p_num_transac bigint)
  RETURNS void
AS $BODY$
BEGIN
    update tracabilite.hydrant
    SET hbe = pena.hbe, materiau = mat.nom, vol_constate = v.nom, positionnement = p.nom
    FROM remocra.hydrant_pena pena
    LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
    LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
    LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
    WHERE pena.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pena;
END;
$BODY$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_anomalies(p_id_hydrant bigint, p_operation varchar, p_num_transac bigint)
  RETURNS void
AS $BODY$
BEGIN
    update tracabilite.hydrant
    SET anomalies = (select array_agg(a.nom) from remocra.hydrant_anomalies ha JOIN remocra.type_hydrant_anomalie a on (ha.anomalies = a.id) where ha.hydrant = p_id_hydrant)
    WHERE num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$BODY$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION tracabilite.trg_hydrant() RETURNS TRIGGER
AS $BODY$
DECLARE
    p_id bigint;
    p_operation varchar;
    p_rec record;
BEGIN
    if (TG_OP = 'DELETE') then
        p_rec = OLD;
    else
        p_rec = NEW;
    end if;

    if TG_TABLE_NAME = 'hydrant_anomalies' then
        p_id := p_rec.hydrant;
    else
        p_id := p_rec.ID;
    end if;
    select nom_operation into p_operation from tracabilite.hydrant where num_transac = txid_current() and id_hydrant = p_id;
    if FOUND then
        if (TG_WHEN = 'AFTER' AND p_operation != 'DELETE') then
        if TG_TABLE_NAME = 'hydrant' then
            perform tracabilite.update_hydrant(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_pibi' then
            perform tracabilite.update_hydrant_pibi(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_pena' then
            perform tracabilite.update_hydrant_pena(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_anomalies' then
            perform tracabilite.update_hydrant_anomalies(p_id, TG_OP::varchar, txid_current());
        end if;
        end if;
    else
        perform tracabilite.insert_hydrant(p_id, TG_OP::varchar);
    end if;
    RETURN p_rec;
END;
$BODY$ LANGUAGE plpgsql;

CREATE TRIGGER trig_aui AFTER UPDATE OR INSERT ON remocra.hydrant FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_aui AFTER UPDATE OR INSERT ON remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_aui AFTER UPDATE OR INSERT ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_aui AFTER UPDATE OR INSERT OR DELETE ON remocra.hydrant_anomalies FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_bd BEFORE DELETE ON remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_bd BEFORE DELETE ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_bd BEFORE DELETE ON remocra.hydrant_anomalies FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();