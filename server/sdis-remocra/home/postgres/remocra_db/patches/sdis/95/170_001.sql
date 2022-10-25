CREATE TABLE remocra_sgo.anomalies(
  code_anomalie_sgo VARCHAR NOT NULL,
  code_anomalie_remocra VARCHAR NOT NULL,
  libelle_anomalie_remocra VARCHAR NOT NULL
);

COMMENT ON COLUMN remocra_sgo.anomalies.code_anomalie_sgo IS 'Code de l''anomalie dans le sgo';
COMMENT ON COLUMN remocra_sgo.anomalies.code_anomalie_remocra IS 'Code de l''anomalie dans remocra';
COMMENT ON COLUMN remocra_sgo.anomalies.libelle_anomalie_remocra IS 'Libellé de l''anomalie dans remocra';

INSERT INTO remocra_sgo.anomalies
SELECT '', code, nom
FROM remocra.type_hydrant_anomalie;

CREATE OR REPLACE VIEW remocra_sgo.hydrant_anomalies
AS SELECT
  h.numero AS "NOM_POINT_EAU",
  code_anomalie_sgo AS "CODE_ANOMALIE"
FROM remocra.hydrant_anomalies ha
JOIN remocra.hydrant h ON h.id = ha.hydrant
JOIN remocra.type_hydrant_anomalie tha ON tha.id = ha.anomalies
JOIN remocra_sgo.anomalies a ON a.code_anomalie_remocra = tha.code;

GRANT SELECT ON remocra_sgo.hydrant_anomalies TO systeletl;
GRANT UPDATE (code_anomalie_sgo) ON TABLE remocra_sgo.hydrant_anomalies TO systeletl;
