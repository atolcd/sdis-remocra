begin;

ALTER TABLE remocra.hydrant_pibi DISABLE TRIGGER trig_debit_pression;

DROP TRIGGER trig_debit_pression_78 ON remocra.hydrant_pibi;

commit;
