
begin;


INSERT INTO remocra_sgo.hydrant_nature (code_nature_sgo, code_nature_remocra) VALUES ('09', 'PA_DIAM_IND');

UPDATE remocra_sgo.hydrant_nature SET code_nature_remocra = 'PI_DIAM80' WHERE code_nature_remocra = 'PI_DIAM70';

ALTER TABLE remocra_sgo.hydrant_tracabilite ADD COLUMN carroyage character varying;
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.carroyage IS 'Identifiant du carré de 200 de la table remocra_referentiel.carre_200';

commit;

