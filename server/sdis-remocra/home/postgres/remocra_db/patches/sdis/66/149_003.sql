begin;

GRANT USAGE ON SCHEMA remocra_sgo TO sgo_template;
GRANT SELECT ON TABLE remocra_sgo.hydrant_disponibilite TO sgo_template;
GRANT SELECT ON TABLE remocra_sgo.hydrant_nature TO sgo_template;
GRANT SELECT ON TABLE remocra_sgo.hydrant TO sgo_template;

commit;