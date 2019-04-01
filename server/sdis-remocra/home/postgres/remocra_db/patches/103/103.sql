begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 103;
    description_patch := 'Correction trigger de traça';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(
    p_id_hydrant bigint,
    p_operation character varying,
    p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET nom_operation = p_operation, numero =h.numero, geometrie = h.geometrie, insee = c.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom, type_hydrant = th.nom,
    agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre, dispo_hbe = h.dispo_hbe,
    observation = h.observation, auteur_modification = CASE WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur') THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
    FROM remocra.hydrant h
    JOIN remocra.organisme o on (h.organisme = o.id)
    JOIN remocra.utilisateur u on (h.utilisateur_modification = u.id)
    WHERE h.id = p_id_hydrant) ELSE o.nom END, domaine = dom.nom
    FROM remocra.hydrant h
    JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
    JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
    LEFT JOIN remocra.commune c on (h.commune = c.id)
    LEFT JOIN remocra.organisme o on (h.organisme = o.id)
    LEFT JOIN remocra.type_hydrant_domaine dom on (h.domaine = dom.id)
    WHERE h.id = p_id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.update_hydrant(bigint, character varying, bigint)
  OWNER TO postgres;


CREATE OR REPLACE FUNCTION tracabilite.trg_hydrant()
  RETURNS trigger AS
$BODY$
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
        if (TG_TABLE_NAME = 'hydrant' AND TG_OP = 'DELETE') then
            perform tracabilite.update_hydrant(p_id, TG_OP::varchar, txid_current());
        elsif (TG_WHEN = 'AFTER' AND p_operation != 'DELETE') then
            if TG_TABLE_NAME = 'hydrant' then
                perform tracabilite.update_hydrant(p_id, p_operation, txid_current());
            elsif TG_TABLE_NAME = 'hydrant_pibi' then
                perform tracabilite.update_hydrant_pibi(p_id, TG_OP::varchar, txid_current());
            elsif TG_TABLE_NAME = 'hydrant_pena' then
                perform tracabilite.update_hydrant_pena(p_id, TG_OP::varchar, txid_current());
            elsif TG_TABLE_NAME = 'hydrant_anomalies' then
                perform tracabilite.update_hydrant_anomalies(p_id, TG_OP::varchar, txid_current());
            end if;
        end if;
    else
        if(TG_OP = 'UPDATE' OR TG_TABLE_NAME = 'hydrant_anomalies' OR ((TG_TABLE_NAME = 'hydrant_pibi' OR TG_TABLE_NAME = 'hydrant_pena') AND TG_OP = 'INSERT')) then
            p_operation = 'UPDATE';
        else
            p_operation = TG_OP::varchar;
        end if;
        perform tracabilite.insert_hydrant(p_id, p_operation);
    end if;
    RETURN p_rec;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tracabilite.trg_hydrant()
  OWNER TO postgres;

DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant_anomalies;
DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant_pena;
DROP TRIGGER IF EXISTS trig_bd ON remocra.hydrant_pena;
DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant_pibi;
DROP TRIGGER IF EXISTS trig_bd ON remocra.hydrant_pibi;
DROP TRIGGER IF EXISTS trig_aui ON remocra.hydrant;
DROP TRIGGER IF EXISTS trig_bd ON remocra.hydrant;

CREATE TRIGGER trig_aui AFTER INSERT OR UPDATE ON remocra.hydrant_anomalies FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_aui AFTER INSERT OR UPDATE ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_bd BEFORE DELETE ON remocra.hydrant_pena FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_aui AFTER INSERT OR UPDATE ON remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_bd BEFORE DELETE ON remocra.hydrant_pibi FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_aui AFTER INSERT OR UPDATE ON remocra.hydrant FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();
CREATE TRIGGER trig_bd BEFORE DELETE ON remocra.hydrant FOR EACH ROW EXECUTE PROCEDURE tracabilite.trg_hydrant();

-- Script correctif des lignes en UPDATE qui auraient du être en DELETE dans la table tracabilite.hydrant
UPDATE tracabilite.hydrant SET nom_operation = 'DELETE' WHERE id IN (
  SELECT id_to_update FROM (
  SELECT id_hydrant, MAX(id) AS id_to_update FROM tracabilite.hydrant
  WHERE id_hydrant NOT IN (SELECT id FROM remocra.hydrant)
  GROUP BY id_hydrant) as hydrant_delete
);


-- Contenu réel du patch fin
--------------------------------------------------

commit;
