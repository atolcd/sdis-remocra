begin;
/*
*  Patch pour la synchro avec le SGO
*/
-- MAJ vue des PEI pour exploitation dans le SGO
drop view if exists remocra_sgo.hydrant;
CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
  WITH remocra_hydrant_nature AS (
  SELECT h.id,
  CASE WHEN thn.code = 'CI_FIXE' AND thp.code = 'AIR_LIBRE' THEN 'CI_ALIBRE'
    WHEN thn.code = 'CI_FIXE' AND thp.code = 'ENTERRE' THEN 'CI_ENTERRE'
    ELSE thn.code END AS code
  FROM remocra.hydrant h
    LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
    LEFT JOIN remocra.type_hydrant_positionnement thp ON thp.id = hpe.positionnement
    LEFT JOIN remocra.type_hydrant_nature thn ON thn.id = h.nature
)
  SELECT h.id AS ID_HYDRANT,
    CASE WHEN h.dispo_terrestre = 'DISPO' THEN 0 WHEN h.dispo_terrestre = 'INDISPO' THEN 1 WHEN h.dispo_terrestre = 'NON_CONFORME' THEN 2 ELSE 3 END AS INDISPONIBLE,
    u.nom || ' ' || u.prenom AS MODIFIE_PAR,
    anomalie.anomalies AS ANOMALIES,
    hpe.capacite AS CAPACITE,
    hpi.pression AS PRESSION,
    hd.code_sgo AS DIAMETRE,
    null::integer AS DIAMETRE_CANALISATION,
    h.voie2 AS ACCES,
    h.voie AS SITUATION,
    hpi.gest_reseau AS GESTIONNAIRE,
    hdo.code_sgo AS CODE_DOMAINE,
    hpi.debit AS DEBIT,
    GREATEST(h.date_recep, h.date_contr, h.date_reco) AS DATE_VERIFICATION,
    h.gest_point_eau AS PROPRIETAIRE,
    carr.nom AS CARROYAGE,
    c.insee AS CODE_INSEE,
    ST_AsText(h.geometrie) AS GEOMETRIE,
    h.numero_interne AS NUMERO,
    hn.code_sgo AS SOUS_TYPE
  FROM remocra.hydrant h
    LEFT JOIN remocra.utilisateur u ON u.id = h.utilisateur_modification
    LEFT JOIN (
      SELECT
        a.hydrant,
        array_to_string(array_agg(ta.code),';') AS anomalies
      FROM
        remocra.hydrant_anomalies a
        JOIN remocra.type_hydrant_anomalie ta ON (ta.id = a.anomalies)
      GROUP BY
        a.hydrant) AS anomalie ON anomalie.hydrant = h.id
    LEFT JOIN remocra.hydrant_pena hpe ON hpe.id = h.id
    LEFT JOIN remocra.hydrant_pibi hpi ON hpi.id = h.id
    LEFT JOIN remocra.type_hydrant_diametre thd ON thd.id = hpi.diametre
    LEFT JOIN remocra.type_hydrant_domaine thdo ON thdo.id = h.domaine
    LEFT JOIN remocra_referentiel.carroyage_deci carr ON ST_Contains(carr.geometrie, h.geometrie) AND carr.type_carroyage = '333'
    LEFT JOIN remocra.commune c ON c.id = h.commune
    LEFT JOIN remocra_sgo.hydrant_diametre hd ON hd.code_remocra = thd.code
    LEFT JOIN remocra_hydrant_nature rhn ON rhn.id = h.id
    LEFT JOIN remocra_sgo.hydrant_domaine hdo ON hdo.code_remocra = thdo.code
    LEFT JOIN remocra_sgo.hydrant_nature hn ON hn.code_remocra = rhn.code;
COMMENT ON VIEW remocra_sgo.hydrant IS 'Etat des points d''eau en vue d''une exploitation par le SGO';

GRANT SELECT ON TABLE remocra_sgo.hydrant TO impi;

commit;
