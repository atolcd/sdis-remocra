BEGIN;

UPDATE remocra.type_rci_degre_certitude
SET nom = '1. Inconnue'
WHERE code = 'INCONNUE';

UPDATE remocra.type_rci_degre_certitude
SET nom = '2. Probable'
WHERE code = 'PROBABLE';

UPDATE remocra.type_rci_degre_certitude
SET nom = '3. Supposée'
WHERE code = 'SUPPOSE';

UPDATE remocra.type_rci_degre_certitude
SET nom = '4. Certaine'
WHERE code = 'CERTAINE';

COMMIT;