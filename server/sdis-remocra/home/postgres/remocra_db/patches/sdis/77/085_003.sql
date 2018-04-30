
begin;


INSERT INTO remocra_sgo.hydrant_nature (code_nature_sgo, code_nature_remocra) VALUES ('09', 'PA_DIAM_IND');

UPDATE remocra_sgo.hydrant_nature SET code_nature_remocra = 'PI_DIAM80' WHERE code_nature_remocra = 'PI_DIAM70';

commit;

