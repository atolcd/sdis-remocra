
-- Mise à jour des modèles de courriers PDI

BEGIN;
UPDATE pdi.modele_traitement SET nom = 'ROI - Courrier de rapport', description = 'Génère un courrier de rapport suite à une ROI' WHERE nom = 'Reconnaissance opérationnelle initiale PEI - Courrier de rapport';
UPDATE pdi.modele_traitement SET nom = 'ROP - Courrier d''information préalable', description = 'Génère un courrier d''information préalable à une ROP' WHERE nom = 'Reconnaissance opérationnelle PEI - Courrier d''information préalable';
UPDATE pdi.modele_traitement SET nom = 'ROP - Courrier de rapport', description = 'Génère un courrier de rapport suite à une ROP' WHERE nom = 'Reconnaissance opérationnelle PEI - Courrier de rapport';

CREATE OR REPLACE VIEW pdi.vue_commune_ou_interco_one AS 
SELECT
	id,
	libelle
FROM
(
-- Organisme "Communes" actifs (hors délegation DECI)
SELECT
	org.id,
	org.nom::text AS libelle
FROM
	remocra.organisme org
	JOIN ( SELECT
			type_organisme.id,
			type_organisme.code AS type
		FROM
			remocra.type_organisme
          WHERE
		type_organisme.code = 'COMMUNE') torg ON (torg.id = org.type_organisme)
WHERE
	org.actif
	AND COALESCE(org.email_contact, ''::character varying)::text <> ''::text
	AND org.code NOT IN(SELECT organisme_commune FROM remocra.delegation_deci)
UNION
-- Organismes "EPCI" actifs ayant délegation DECI
SELECT
	org.id,
	org.nom::text AS libelle
FROM
	remocra.organisme org
WHERE
	org.actif
	AND COALESCE(org.email_contact, ''::character varying)::text <> ''::text
	AND org.code IN(SELECT organisme_epci FROM remocra.delegation_deci)
) AS deci
ORDER BY
	libelle;
COMMENT ON VIEW pdi.vue_commune_ou_interco_one IS 'Organismes chargés du service public DECI de type "commune" ou autre si la commune est présente dans la table remocra.delecgation_deci';

CREATE OR REPLACE VIEW pdi.vue_modele_courrier_rapport_roi AS 
SELECT
	courrier_modele.id,
	courrier_modele.libelle
FROM
	remocra.courrier_modele
WHERE
	courrier_modele.categorie::text = 'COURRIER_RAPPORT_ROI'::text
ORDER BY
	courrier_modele.libelle;
COMMENT ON VIEW pdi.vue_modele_courrier_rapport_roi IS 'Modèles de courriers de compte rendu à destination du service public DECI suite à une ROI';

CREATE OR REPLACE VIEW pdi.vue_pei_cr_roi AS 
SELECT
	p.id,
	((p.numero::text || ' ('::text) || c.nom::text) || ')'::text AS libelle
FROM
	remocra.hydrant p
	JOIN remocra.commune c ON (c.id = p.commune)
WHERE
	date_recep IS NOT NULL AND date_recep >= (now() - interval '365 days')
	AND date_reco IS NULL
	AND date_contr IS NULL
ORDER BY
	p.numero;
COMMENT ON VIEW pdi.vue_pei_cr_roi IS 'PEI ayant fait l''objet d''une ROI dans les 365 derniers jours et pour lesquels aucune ROP ou CT n''a été réalisé';

UPDATE pdi.modele_traitement_parametre SET form_source_donnee = 'vue_pei_cr_roi' WHERE nom = 'PEI_ID' AND idmodele
IN (SELECT idmodele FROM pdi.modele_traitement WHERE nom = 'ROI - Courrier de rapport');

COMMIT;
