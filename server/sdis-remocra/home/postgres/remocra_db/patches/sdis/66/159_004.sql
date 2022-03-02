begin

UPDATE remocra.type_hydrant_anomalie
SET code = REPLACE(code, '_NC', '')
WHERE code NOT IN ('DEBIT_INSUFF_NC', 'ABORDSDGAGER_NC')
	AND code LIKE '%_NC';

commit;