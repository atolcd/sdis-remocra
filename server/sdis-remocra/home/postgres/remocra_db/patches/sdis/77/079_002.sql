
begin;


ALTER TABLE remocra_sgo.hydrant_tracabilite ALTER COLUMN date_maj_remocra SET DATA TYPE timestamp without time zone;


commit;

