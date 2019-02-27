/*
 Creation des vues nécessaires à l'envoie des courriers d'info de ROI
 ou de suppression de PEI dans les dernières X heures
*/

CREATE OR REPLACE VIEW pdi.vue_modele_courrier_rapport_roi_suppression AS
 SELECT courrier_modele.id, courrier_modele.libelle
   FROM remocra.courrier_modele
  WHERE courrier_modele.categorie::text = 'DECI_INFO_ROI_SUPPRESSION'::text
  ORDER BY courrier_modele.libelle;

ALTER TABLE pdi.vue_modele_courrier_rapport_roi_suppression
  OWNER TO postgres;


/*
 Création du traitement pour le courrier de rapport de ROI ou de suppression
 de PEI dans les dernières X heures
*/
INSERT INTO pdi.modele_traitement VALUES ((SELECT MAX(idmodele) +1 FROM pdi.modele_traitement), 1, 'Génère un document listant les PEI dont la ROI est récente et les PEI dernièrement supprimés', 'Document de suivi de ROI ou de suppression de PEI', '/demandes/generation_courriers/deci_info_roi_suppression', 'generer_et_notifier_info_roi_suppression', 'J', 3, 1);

INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Ancienneté des changements en H', 1, true, '', 'textfield', 24::character varying, 'PERIODE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Modèle de document', 2, true, 'vue_modele_courrier_rapport_roi_suppression', 'combo', null::character varying, 'MODELE_COURRIER_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Profil d''utilisateur à notifier', 3, true, 'vue_profil_sdis', 'combo', null::character varying, 'PROFIL_UTILISATEUR_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
