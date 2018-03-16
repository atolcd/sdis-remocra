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
			xmlelement(name "pe",
				non_dispo.pe_non_disponibles,
				pibi.pe_pibi,
				pena.pe_pena
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
	) AS responsables,
	
	-- Points d'eau non disponibles
	(SELECT
		xmlelement(name "nonDisponibles",
			xmlagg(
				xmlelement(name "pe",
					xmlelement(name "commune",commune.nom),
					xmlelement(name "position",
						xmlattributes(
							st_x(pe.geometrie) AS x,
							st_y(pe.geometrie) AS y
						)
					),
					xmlelement(name "numero",pe.numero),
					xmlelement(name "type",nature.nom),
					xmlelement(name "emplacement",pe.voie),
					xmlelement(name "anomalies",anomalie.anomalies)

				)
			)
		) AS pe_non_disponibles
	FROM
		territoire,
		remocra.hydrant pe
		LEFT JOIN remocra.type_hydrant_nature nature ON(nature.id = pe.nature)
		LEFT JOIN remocra.commune ON (commune.id = pe.commune)
		LEFT JOIN (
		SELECT
			a.hydrant,
			array_to_string(array_agg(ta.nom),', ') AS anomalies
		FROM
			remocra.hydrant_anomalies a
			JOIN remocra.type_hydrant_anomalie ta ON (ta.id = a.anomalies)
		GROUP BY
			a.hydrant) AS anomalie ON(anomalie.hydrant = pe.id)
	 WHERE
		(pe.dispo_terrestre = 'INDISPO' OR pe.dispo_hbe = 'INDISPO')
		AND pe.geometrie && territoire.geometrie
		AND ST_Within(pe.geometrie,territoire.geometrie)) AS non_dispo,

	-- Points d'eau de type pibi	
	(SELECT
		xmlelement(name "pibi",
			xmlagg(
				xmlelement(name "pe",
					xmlelement(name "commune",commune.nom),
					xmlelement(name "position",
						xmlattributes(
							st_x(pe.geometrie) AS x,
							st_y(pe.geometrie) AS y
						)
					),
					xmlelement(name "numero",pe.numero),
					xmlelement(name "type",nature.nom),
					xmlelement(name "voie",pe.voie),
					xmlelement(name "carrefour",pe.voie2),
					xmlelement(name "domaine",type_hydrant_domaine.nom),
					xmlelement(name "diametre",pibi.debit),
					xmlelement(name "debit",pibi.debit),
					xmlelement(name "pressionDynamique",pibi.pression_dyn),
					xmlelement(name "pressionStatique",pibi.pression),
					xmlelement(name "observation",pe.observation),
					xmlelement(name "disponibilite",pe.dispo_terrestre),
					xmlelement(name "anomalies",anomalie.anomalies)
				)
			)
		) AS pe_pibi
	FROM
		territoire,
		remocra.hydrant_pibi pibi
		JOIN remocra.hydrant pe ON (pe.id = pibi.id)
		LEFT JOIN remocra.type_hydrant_nature nature ON(nature.id = pe.nature)
		LEFT JOIN remocra.type_hydrant_domaine ON (type_hydrant_domaine.id = pe.domaine)
		LEFT JOIN remocra.commune ON (commune.id = pe.commune)
		LEFT JOIN (
		SELECT
			a.hydrant,
			array_to_string(array_agg(ta.nom),', ') AS anomalies
		FROM
			remocra.hydrant_anomalies a
			JOIN remocra.type_hydrant_anomalie ta ON (ta.id = a.anomalies)
		GROUP BY
			a.hydrant) AS anomalie ON(anomalie.hydrant = pibi.id)
	 WHERE
		pe.geometrie && territoire.geometrie
		AND ST_Within(pe.geometrie,territoire.geometrie)) AS pibi,
	-- Points d'eau de type pena
	(SELECT
		xmlelement(name "pena",
			xmlagg(
				xmlelement(name "pe",
					xmlelement(name "commune",commune.nom),
					xmlelement(name "position",
						xmlattributes(
							st_x(pe.geometrie) AS x,
							st_y(pe.geometrie) AS y
						)
					),
					xmlelement(name "numero",pe.numero),
					xmlelement(name "type",nature.nom),
					xmlelement(name "voie",pe.voie),
					xmlelement(name "carrefour",pe.voie2),
					xmlelement(name "domaine",type_hydrant_domaine.nom),
					xmlelement(name "volumeConstate",pena.vol_constate),
					xmlelement(name "capacite",pena.capacite),
					xmlelement(name "disponibiliteHBE",pe.dispo_hbe),
					xmlelement(name "observation",pe.observation),
					xmlelement(name "disponibilite",pe.dispo_terrestre),
					xmlelement(name "anomalies",anomalie.anomalies)
				)
			)
		) AS pe_pena
	FROM
		territoire,
		remocra.hydrant_pena pena
		JOIN remocra.hydrant pe ON (pe.id = pena.id)
		LEFT JOIN remocra.type_hydrant_nature nature ON(nature.id = pe.nature)
		LEFT JOIN remocra.type_hydrant_domaine ON (type_hydrant_domaine.id = pe.domaine)
		LEFT JOIN remocra.commune ON (commune.id = pe.commune)
		LEFT JOIN (
		SELECT
			a.hydrant,
			array_to_string(array_agg(ta.nom),', ') AS anomalies
		FROM
			remocra.hydrant_anomalies a
			JOIN remocra.type_hydrant_anomalie ta ON (ta.id = a.anomalies)
		GROUP BY
			a.hydrant) AS anomalie ON(anomalie.hydrant = pena.id)
	WHERE
		pe.geometrie && territoire.geometrie
		AND ST_Within(pe.geometrie,territoire.geometrie)) AS pena