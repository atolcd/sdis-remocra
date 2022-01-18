BEGIN;
-- Requête pour fiche résumé
DELETE FROM remocra.hydrant_resume
WHERE code IN ('RESUME_PIBI', 'RESUME_PENA');

INSERT INTO remocra.hydrant_resume (libelle,code,source_sql) VALUES
('Requête personnalisée PIBI', 'RESUME_PIBI', 'WITH etat_indispo AS (
  SELECT
    h.id_hydrant as id_hydrant,
    MIN(h.date_operation) AS date_indispo
  FROM
    tracabilite.hydrant h
    JOIN (
      SELECT
        h.id_hydrant,
        max(h.date_operation) AS date_operation
      FROM
        tracabilite.hydrant h
      WHERE
        dispo_terrestre <> ''INDISPO'' AND dispo_terrestre IS NOT NULL
      GROUP BY
        h.id_hydrant) dispo ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation)
  WHERE
    dispo_terrestre = ''INDISPO''
  GROUP BY
    h.id_hydrant
)
SELECT CAST(xmlelement(name "data",
  xmlconcat(
    hydrant.hydrant_data,
    anomalies.anomalies_data,
    cstc.cstc_data,
    tournees.tournees_data,
    indispoTemp.indispo_data
  )
) as TEXT) as xml
  FROM
  (SELECT xmlelement(name "hydrant",
      xmlelement(name "adresse", h.adresse),
      xmlelement(name "commune", h.commune),
      xmlelement(name "complement", h.complement),
      xmlelement(name "debit_renforce", h.debit_renforce),
      xmlelement(name "dispo_terrestre", h.dispo_terrestre),
      xmlelement(name "nature", h.nature),
      xmlelement(name "diametre", diametre),
      xmlelement(name "lieuDit", lieu_dit),
      xmlelement(name "dateIndispo", date_indispo)
  ) as hydrant_data
  FROM
    (SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie || CASE WHEN h.en_face = TRUE THEN '' (En face)'' else '''' end) AS adresse,
      c.nom AS commune,
      h.complement,
      hp.debit_renforce,
      h.dispo_terrestre,
      th.code AS nature,
      thd.nom AS diametre,
      h.lieu_dit,
      eind.date_indispo
    FROM
      remocra.hydrant h
      JOIN remocra.commune c on h.commune=c.id
      JOIN remocra.hydrant_pibi hp on hp.id=h.id
      LEFT JOIN remocra.type_hydrant_diametre thd on thd.id=hp.diametre
      JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
      JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
      LEFT JOIN etat_indispo eind on (eind.id_hydrant = h.id)
    WHERE   h.id=${HYDRANT_ID})
    AS h) as hydrant,
  (SELECT
    XMLAGG(xmlelement(name "anomalie",
      xmlelement(name "nom", a.nom),
      xmlelement(name "indispo", a.indispo))
    )as anomalies_data
  FROM
    (SELECT tha.nom AS nom,
      than.val_indispo_terrestre AS indispo
    FROM   remocra.hydrant_anomalies ha
      JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
      JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
      JOIN remocra.hydrant h on h.id=ha.hydrant
    WHERE   ha.hydrant=${HYDRANT_ID}
      AND than.nature = h.nature)
    as a) as anomalies,
  (SELECT
    XMLAGG(xmlelement(name "cstc",
      xmlelement(name "nom", c.nom),
      xmlelement(name "tournee", c.tournee))
    )as cstc_data
  FROM
    (SELECT o.nom AS nom, t.nom AS tournee
    FROM remocra.organisme o
      JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
      LEFT JOIN remocra.tournee t on t.affectation=o.id
      LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
    WHERE typeO.code = ''CASERNE''
      AND ht.hydrant=${HYDRANT_ID}
    ORDER BY o.nom) as c) as cstc,
  (SELECT
   xmlelement(name "tournees",
    XMLAGG(xmlelement(name "tournee",
      xmlelement(name "nomOrg", tour.nomOrg),
      xmlelement(name "nomTournee", tour.nomTournee))
  ))as tournees_data
  FROM
     (SELECT o.nom AS nomOrg, t.nom AS nomTournee
    FROM remocra.organisme o
      JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
      LEFT JOIN remocra.tournee t on t.affectation=o.id
      LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
    WHERE ht.hydrant=${HYDRANT_ID}
    ORDER BY o.nom) as tour) as tournees,
  (
   SELECT
    XMLAGG(xmlelement(name "indispoTemp",
      xmlelement(name "dateDebut", to_char(it.date_debut, ''dd/mm/yyyy'')),
      xmlelement(name "dateFin", to_char(it.date_fin, ''dd/mm/yyyy'')),
      xmlelement(name "motif", it.motif))
    ) as indispo_data
    FROM (
      select hith.hydrant,
       hit.date_debut,
       hit.date_fin,
       hit.motif
     FROM remocra.hydrant_indispo_temporaire hit
     join remocra.hydrant_indispo_temporaire_hydrant hith on (hit.id = hith.indisponibilite)
     where hit.date_debut < now() and (hit.date_fin > now() or hit.date_fin is null)
     AND hith.hydrant=${HYDRANT_ID}
    ) as it
  ) as indispoTemp;');


INSERT INTO remocra.hydrant_resume (libelle,code,source_sql) VALUES
('Requête personnalisée PENA', 'RESUME_PENA', 'WITH etat_indispo AS (
  SELECT
    h.id_hydrant as id_hydrant,
    MIN(h.date_operation) AS date_indispo
  FROM
    tracabilite.hydrant h
    JOIN (
      SELECT
        h.id_hydrant,
        max(h.date_operation) AS date_operation
      FROM
        tracabilite.hydrant h
      WHERE
        dispo_terrestre <> ''INDISPO'' AND dispo_terrestre IS NOT NULL
      GROUP BY
        h.id_hydrant) dispo ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation)
  WHERE
    dispo_terrestre = ''INDISPO''
  GROUP BY
    h.id_hydrant
)
SELECT CAST(xmlelement(name "data",
  xmlconcat(
    hydrant.hydrant_data,
    anomalies.anomalies_data,
    cstc.cstc_data,
    tournees.tournees_data,
    indispoTemp.indispo_data
  )
) as TEXT) as xml
  FROM
  (SELECT xmlelement(name "hydrant",
      xmlelement(name "adresse", h.adresse),
      xmlelement(name "commune", h.commune),
      xmlelement(name "complement", h.complement),
      xmlelement(name "dispo_terrestre", h.dispo_terrestre),
      xmlelement(name "nature", h.nature),
      xmlelement(name "capacite", h.capacite),
      xmlelement(name "aspirations", h.aspirations),
      xmlelement(name "lieuDit", lieu_dit),
      xmlelement(name "dateIndispo", date_indispo)
  ) as hydrant_data
  FROM
    (SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie || CASE WHEN h.en_face = TRUE THEN '' (En face)'' else '''' end) AS adresse,
      c.nom AS commune,
      h.complement,
      h.dispo_terrestre,
      th.code AS nature,
      CASE WHEN thn.code = ''CI_FIXE'' AND hp.illimitee THEN ''Illimitée''
        WHEN thn.code = ''CI_FIXE''  AND ((NOT hp.illimitee OR hp.illimitee IS NULL) AND hp.capacite IS NOT NULL AND CAST(NULLIF(TRIM(hp.capacite), '''') AS Integer) IS NOT NULL AND CAST(NULLIF(TRIM(hp.capacite), '''') AS Integer) > -1 ) then hp.capacite|| ''m3''
        ELSE NULL
      END as capacite,
      (SELECT COUNT(ha)
        FROM remocra.hydrant h
        JOIN remocra.hydrant_pena hp on hp.id=h.id
        JOIN remocra.hydrant_aspiration ha on h.id=ha.pena
        WHERE h.id=${HYDRANT_ID}) AS aspirations,
      h.lieu_dit,
      eind.date_indispo
    FROM remocra.hydrant h
      JOIN remocra.commune c on h.commune=c.id
      JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
      JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
      -- PENA
      JOIN remocra.hydrant_pena hp on hp.id=h.id
      LEFT JOIN etat_indispo eind on (eind.id_hydrant = h.id)
    WHERE   h.id=${HYDRANT_ID})
    AS h) as hydrant,
  (SELECT
    XMLAGG(xmlelement(name "anomalie",
      xmlelement(name "nom", a.nom),
      xmlelement(name "indispo", a.indispo))
    )as anomalies_data
  FROM
    (SELECT tha.nom AS nom,
      than.val_indispo_terrestre AS indispo
    FROM   remocra.hydrant_anomalies ha
      JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
      JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
      JOIN remocra.hydrant h on h.id=ha.hydrant
    WHERE   ha.hydrant=${HYDRANT_ID}
      AND than.nature = h.nature)
    as a) as anomalies,
  (SELECT
    XMLAGG(xmlelement(name "cstc",
      xmlelement(name "nom", c.nom),
      xmlelement(name "tournee", c.tournee))
    )as cstc_data
  FROM
    (SELECT o.nom AS nom, t.nom AS tournee
    FROM remocra.organisme o
      JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
      LEFT JOIN remocra.tournee t on t.affectation=o.id
      LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
    WHERE typeO.code = ''CASERNE''
      AND ht.hydrant=${HYDRANT_ID}
    ORDER BY o.nom) as c) as cstc,
  (SELECT
   xmlelement(name "tournees",
    XMLAGG(xmlelement(name "tournee",
      xmlelement(name "nomOrg", tour.nomOrg),
      xmlelement(name "nomTournee", tour.nomTournee))
    ))as tournees_data
  FROM
     (SELECT o.nom AS nomOrg, t.nom AS nomTournee
    FROM remocra.organisme o
      JOIN remocra.type_organisme typeO on typeO.id=o.type_organisme
      LEFT JOIN remocra.tournee t on t.affectation=o.id
      LEFT JOIN remocra.hydrant_tournees ht on ht.tournees=t.id
    WHERE ht.hydrant=${HYDRANT_ID}
    ORDER BY o.nom) as tour) as tournees,
  (
   SELECT
    XMLAGG(xmlelement(name "indispoTemp",
      xmlelement(name "dateDebut", to_char(it.date_debut, ''dd/mm/yyyy'')),
      xmlelement(name "dateFin", to_char(it.date_fin, ''dd/mm/yyyy'')),
      xmlelement(name "motif", it.motif))
    ) as indispo_data
    FROM (
      select hith.hydrant,
       hit.date_debut,
       hit.date_fin,
       hit.motif
     from remocra.hydrant_indispo_temporaire hit
     join remocra.hydrant_indispo_temporaire_hydrant hith on (hit.id = hith.indisponibilite)
     where hit.date_debut < now() and (hit.date_fin > now() or hit.date_fin is null)
     AND hith.hydrant=${HYDRANT_ID}
    ) as it
  ) as indispoTemp;');

COMMIT;