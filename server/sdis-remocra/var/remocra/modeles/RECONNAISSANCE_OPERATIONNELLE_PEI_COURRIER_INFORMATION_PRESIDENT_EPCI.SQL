WITH territoire AS (
	SELECT
		org.nom,
		zc.geometrie
	FROM
		(SELECT	nom,zone_competence FROM remocra.organisme WHERE id = ${ORGANISME_COLLECTIVITE_ID} AND actif) AS org
		JOIN remocra.zone_competence zc ON(zc.id = org.zone_competence)
)
SELECT
	xmlelement(name "courrier",
		xmlattributes(
			to_char(now(),'dd/mm/yyyy') as date,
			''  as "numero"
		),
		xmlconcat(
			responsables.responsable_sdis,
			xmlelement(name "passage",
				xmlelement(name "dateDebut", to_char('${DATE_DEB}'::date,'dd/mm/yyyy')),
				xmlelement(name "dateFin", to_char('${DATE_FIN}'::date,'dd/mm/yyyy'))
			)
		)
	) AS xml
 
FROM
	-- Utilisateur responsable SDIS
	(SELECT
		xmlelement(name "responsable",
			xmlelement(name "nom",u.nom),
			xmlelement(name "prenom",u.prenom),
			xmlelement(name "telephone",u.telephone),
			xmlelement(name "email",u.email)
		)AS responsable_sdis
	FROM
		(SELECT	* FROM remocra.utilisateur WHERE actif AND profil_utilisateur = ${PROFIL_UTILISATEUR_ID}) AS u
		JOIN remocra.organisme org ON(org.id = u.organisme)
		JOIN remocra.zone_competence zc ON(zc.id = org.zone_competence)
		JOIN territoire ON (zc.geometrie && territoire.geometrie)
	ORDER BY
		--Utilisateur dont la zone de compétence recouvre au max
		ST_Area(St_Intersection(zc.geometrie,territoire.geometrie)) DESC LIMIT 1
	) AS responsables
