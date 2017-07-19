
-- ----------
-- Réinitialise le rôle remocra et ses droits
-- ----------

BEGIN;

-- ----------
-- 1 - Rôle remocra
-- ----------
CREATE FUNCTION createremocraroleonce12489() RETURNS void AS $$
BEGIN
   IF NOT EXISTS (
      SELECT *
      FROM   pg_catalog.pg_user
      WHERE  usename = 'remocra') THEN

      CREATE ROLE remocra LOGIN
        PASSWORD 'remocra1453'
        NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;
      COMMENT ON ROLE remocra IS 'Rôle utilisé par l''application REMOcRA';

   END IF;

END;
$$ LANGUAGE plpgsql;

select createremocraroleonce12489();
DROP FUNCTION createremocraroleonce12489();



-- ----------
-- 2 - On supprime/ajoute tous les droits
-- ----------
REVOKE ALL ON DATABASE postgres FROM remocra;
REVOKE ALL ON DATABASE template_postgis FROM remocra;

REVOKE ALL ON DATABASE remocra FROM remocra;
GRANT ALL PRIVILEGES ON DATABASE remocra_ref_pdi TO remocra;



-- ----------
-- 3 - On ajoute le droit d'utilisation sur les schémas
-- ----------
grant usage on schema pdi to remocra;
grant usage on schema public to remocra;
grant usage on schema remocra to remocra;
grant usage on schema remocra_referentiel to remocra;
grant usage on schema sdis_referentiel to remocra;
grant usage on schema tracabilite to remocra;


-- ----------
-- 3 - On ajoute le droit de sélection sur toutes les relations
-- ----------

-- On passe par un fichier temporaire
\t
\o /tmp/droits.sql
select 'GRANT SELECT ON '||schemaname||'.'||tablename||' to remocra;' from pg_tables
where tablename not like 'pg_%' and schemaname in ('pdi', 'public', 'remocra', 'remocra_referentiel', 'sdis_referentiel', 'tracabilite')
order by schemaname, tablename;
select 'GRANT SELECT ON SEQUENCE '||n.nspname||'.'||c.relname||' to remocra;' from pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
where c.relkind = 'S' and n.nspname in ('pdi', 'public', 'remocra', 'remocra_referentiel', 'sdis_referentiel', 'tracabilite')
order by n.nspname, c.relname;
\o
\t
\i /tmp/droits.sql
\! rm /tmp/droits.sql 



-- ----------
-- 4 - ON ajoute les droits de modification sur le minimum de relations
-- ----------
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE pdi.modele_message TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE pdi.modele_traitement TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE pdi.modele_traitement_parametre TO remocra;

GRANT SELECT,UPDATE ON TABLE remocra.email_modele TO remocra;
GRANT SELECT ON SEQUENCE remocra.email_modele_id_seq TO remocra;
GRANT UPDATE ON SEQUENCE remocra.email_modele_id_seq TO remocra;

GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.zone_competence TO remocra;
GRANT SELECT ON SEQUENCE remocra.zone_competence_id_seq TO remocra;
GRANT UPDATE ON SEQUENCE remocra.zone_competence_id_seq TO remocra;

-- Utilisateurs
GRANT SELECT,UPDATE ON TABLE remocra.utilisateur TO remocra;

-- COMMUNES
GRANT UPDATE ON TABLE remocra.commune TO remocra;

-- ALERTES
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_alerte_elt TO remocra;
GRANT ALL ON SEQUENCE remocra.type_alerte_elt_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.sous_type_alerte_elt TO remocra;
GRANT ALL ON SEQUENCE remocra.sous_type_alerte_elt_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_alerte_ano TO remocra;
GRANT ALL ON SEQUENCE remocra.type_alerte_ano_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.alerte TO remocra;
GRANT ALL ON SEQUENCE remocra.alerte_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.alerte_document TO remocra;
GRANT ALL ON SEQUENCE remocra.alerte_document_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.alerte_elt TO remocra;
GRANT ALL ON SEQUENCE remocra.alerte_elt_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.alerte_elt_ano TO remocra;
GRANT ALL ON SEQUENCE remocra.alerte_elt_ano_id_seq TO remocra;

-- HYDRANTS
GRANT EXECUTE ON FUNCTION remocra.remocra.calcul_debit_pression(bigint) TO remocra;
GRANT EXECUTE ON FUNCTION tracabilite.update_hydrant_anomalies(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint) TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.hydrant_anomalies TO remocra;
GRANT UPDATE ON TABLE remocra.hydrant TO remocra;
GRANT UPDATE ON TABLE remocra.hydrant_pibi TO remocra;
GRANT UPDATE ON TABLE remocra.hydrant_pena TO remocra;
GRANT UPDATE ON TABLE remocra.tournee TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.zone_speciale TO remocra;
GRANT ALL ON SEQUENCE remocra.zone_speciale_id_seq TO remocra;

GRANT INSERT,UPDATE ON TABLE tracabilite.hydrant TO remocra;
GRANT ALL ON SEQUENCE tracabilite.hydrant_id_seq TO remocra;

-- MARQUES / MODELES
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_hydrant_marque TO remocra;
GRANT ALL ON SEQUENCE remocra.type_hydrant_marque_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_hydrant_modele TO remocra;
GRANT ALL ON SEQUENCE remocra.type_hydrant_modele_id_seq TO remocra;

-- OLDEB
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_acces TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_acces_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_action TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_action_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_categorie_anomalie TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_categorie_anomalie_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_categorie_caracteristique TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_categorie_caracteristique_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_avis TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_avis_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_debroussaillement TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_debroussaillement_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_residence TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_residence_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_suite TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_suite_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_zone_urbanisme TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_zone_urbanisme_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_anomalie TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_anomalie_id_seq TO remocra;
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE remocra.type_oldeb_caracteristique TO remocra;
GRANT ALL ON SEQUENCE remocra.type_oldeb_caracteristique_id_seq TO remocra;

-- RCI
GRANT UPDATE ON TABLE remocra.rci TO remocra;

COMMIT;
