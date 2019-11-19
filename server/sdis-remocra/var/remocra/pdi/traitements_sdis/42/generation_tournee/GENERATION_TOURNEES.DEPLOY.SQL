CREATE OR REPLACE VIEW pdi.vue_organisme_zc AS
 SELECT org.id, org.libelle, org.tricol
   FROM (
     SELECT zc.id, o.nom AS libelle, o.nom AS tricol
     FROM remocra.organisme o
      JOIN remocra.type_organisme tyo ON o.type_organisme = tyo.id
      JOIN remocra.zone_competence zc ON zc.id = o.zone_competence
      WHERE o.actif = true AND tyo.code::text IN ('CIS','CIE','SDIS')) org
  ORDER BY org.tricol NULLS FIRST;

INSERT INTO pdi.modele_traitement VALUES ((SELECT MAX(idmodele) +1 FROM pdi.modele_traitement),1,'Génère toutes les tournées par commune sur la zone de compétence d''un CIS, d''un CIE ou du département','Fiches de tournée ROP par commune en lot','/var/remocra/pdi/traitements_sdis/42/generation_tournee/','generer_fiches_tournee_par_commune.kjb','J',3,1);

INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Territoire de génération des tournées', 1, true, 'vue_organisme_zc', 'combo', null::character varying, 'ZONE_COMPETENCE_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Modèle de  tournée', 2, true, 'vue_modele_courrier_fiche_tournee_commune', 'combo', null::character varying, 'MODELE_COURRIER_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));

INSERT INTO pdi.modele_traitement VALUES ((SELECT MAX(idmodele) +1 FROM pdi.modele_traitement),1,'Génère toutes les tournées par tournée sur la zone de compétence d''un CIS, d''un CIE ou du département','Fiches de tournée ROP par tournée en lot','/var/remocra/pdi/traitements_sdis/42/generation_tournee/','generer_fiches_tournee_par_tournee.kjb','J',3,1);

INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Territoire de génération des tournées', 1, true, 'vue_organisme_zc', 'combo', null::character varying, 'ZONE_COMPETENCE_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
INSERT INTO pdi.modele_traitement_parametre VALUES ((SELECT MAX(idparametre) +1 FROM pdi.modele_traitement_parametre), 'Modèle de  tournée', 2, true, 'vue_modele_courrier_fiche_tournee_tournee', 'combo', null::character varying, 'MODELE_COURRIER_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
