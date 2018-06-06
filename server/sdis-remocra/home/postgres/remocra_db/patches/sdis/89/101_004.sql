/*
  Modification pour choix CS dans génération courrier d'information de rop
*/

-- Ajout d'un paramètre de choix du CS
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'CS', 1, true, 'vue_organisme_cis', 'combo', null::character varying, 'ORGANISME_CIS_ID', (SELECT idmodele FROM pdi.modele_traitement WHERE nom = 'ROP - Courrier d''information préalable'));

-- Modification de la vue des CS pour prendre en compte le code de 89
CREATE OR REPLACE VIEW pdi.vue_organisme_cis AS
 SELECT org.id, org.libelle, org.tricol
   FROM (         SELECT (-1) AS id, 'Toutes' AS libelle, NULL::unknown AS tricol
        UNION
                 SELECT o.id, o.nom AS libelle, o.nom AS tricol
                   FROM remocra.organisme o
              JOIN remocra.type_organisme tyo ON o.type_organisme = tyo.id
             WHERE o.actif = true AND tyo.code::text = 'SDIS_CIS'::text) org
  ORDER BY org.tricol NULLS FIRST;

ALTER TABLE pdi.vue_organisme_cis
  OWNER TO postgres;


/*
  Modification pour choix du PEI et modèle de courrier pour le courrier de rapport d'une ROI
*/

-- création de la vue des PEI ayant fait lobjet d''une ROI dans les 365 derniers jours et pour lesquels aucune ROP ou CT n'a été réalisé
CREATE OR REPLACE VIEW pdi.vue_pei_cr_roi AS
 SELECT p.id, ((p.numero::text || ' ('::text) || c.nom::text) || ')'::text AS libelle
   FROM remocra.hydrant p
   JOIN remocra.commune c ON c.id = p.commune
  WHERE p.date_recep IS NOT NULL AND p.date_recep >= (now() - '365 days'::interval) AND p.date_reco IS NULL AND p.date_contr IS NULL
  ORDER BY p.numero;

ALTER TABLE pdi.vue_pei_cr_roi
  OWNER TO postgres;
COMMENT ON VIEW pdi.vue_pei_cr_roi
  IS 'PEI ayant fait l''objet d''une ROI dans les 365 derniers jours et pour lesquels aucune ROP ou CT n''a été réalisé';

-- Ajout du paramètre
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Point d''eau', 1, true, 'vue_pei_cr_roi', 'combo', null::character varying, 'PEI_ID', (SELECT idmodele FROM pdi.modele_traitement WHERE nom = 'ROI - Courrier de rapport'));

-- MAJ de la source de données pour le paramètre de sélection des courriers
UPDATE pdi.modele_traitement_parametre SET form_source_donnee='vue_modele_courrier_rapport_roi' WHERE idparametre = (SELECT idparamete FROM pdi.modele_traitement_parametre WHERE nom = 'MODELE_COURRIER_ID' AND idmodele = (SELECT idmodele FROM pdi.modele_traitement WHERE nom = 'ROI - Courrier de rapport'));
