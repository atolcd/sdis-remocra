WITH territoire AS (
	SELECT
		org.nom,
		zc.geometrie
	FROM
		(SELECT	nom,zone_competence FROM remocra.organisme WHERE id = ${ORGANISME_ID} AND actif) AS org
		JOIN remocra.zone_competence zc ON(zc.id = org.zone_competence)
),
der_etat_dispo AS (
	-- Première date de bascule en indisponibilité après la
	-- dernière date de disponibilité totale ou partielle connue
	SELECT
		h.id_hydrant,
		MIN(h.date_operation) AS date_operation
	FROM
		tracabilite.hydrant h
		JOIN (--Dernière date de disponibilite totale ou partielle connue 
			SELECT
				h.id_hydrant,
				max(h.date_operation) AS date_operation
			FROM
				tracabilite.hydrant h
			WHERE
				dispo_terrestre <> 'INDISPO' AND dispo_terrestre IS NOT NULL
			GROUP BY
				h.id_hydrant) dispo ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation)
	WHERE
		dispo_terrestre = 'INDISPO'
	GROUP BY
		h.id_hydrant
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
				nouv_non_dispo.pe_non_disponibles,
				nonDispo.pe_non_disponibles
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
	
	-- Points d'eau nouvellement non disponibles
	(SELECT
		xmlelement(name "nouveauxNonDisponibles",
			xmlagg(
				xmlelement(name "pe",
					xmlelement(name "commune",indispo.nom_commune),
					xmlelement(name "numero",indispo.numero),
					xmlelement(name "type",indispo.nature),
					xmlelement(name "voie",indispo.voie),
					xmlelement(name "date_deb_indispo",to_char(indispo.date_operation,'dd/mm/yyyy'))
				)
			)
		) AS pe_non_disponibles
	FROM
	   (SELECT commune.nom AS nom_commune, pe.numero, nature.nom AS nature, pe.voie, pe.date_operation
		FROM territoire,
		(SELECT
			DISTINCT ON (h_indispo.id_hydrant)
			h_indispo.id_hydrant,
			h_indispo.commune,
			h_indispo.geometrie,
			h_indispo.numero,
			h_indispo.voie,
			h_indispo.nature,
			der_etat_dispo.date_operation
		FROM
		-- Hydrants en état d'indisponibilité
		(SELECT
			id AS id_hydrant,
			commune,
			geometrie,
			numero,
			voie,
			nature
		FROM
			remocra.hydrant h
		WHERE
			dispo_terrestre = 'INDISPO'
		) AS h_indispo
		JOIN der_etat_dispo ON(der_etat_dispo.id_hydrant = h_indispo.id_hydrant)
		WHERE der_etat_dispo.date_operation >= now() - interval '${INTERVAL_INDISPO} hours') AS pe
		LEFT JOIN remocra.type_hydrant_nature nature ON(nature.id = pe.nature)
		LEFT JOIN remocra.commune ON (commune.id = pe.commune)
	 WHERE
		pe.geometrie && territoire.geometrie
		AND ST_Within(pe.geometrie,territoire.geometrie) ORDER BY commune.nom, nature.nom) indispo) AS nouv_non_dispo,

	-- Points d'eau indisponnibles
	(SELECT
		xmlelement(name "nonDisponibles",
			xmlagg(
				xmlelement(name "pe",
					xmlelement(name "commune",indispo.nom_commune),
					xmlelement(name "numero",indispo.numero),
					xmlelement(name "type",indispo.nature),
					xmlelement(name "voie",indispo.voie),
					xmlelement(name "date_deb_indispo",to_char(indispo.date_operation,'dd/mm/yyyy'))
				)
			)
		) AS pe_non_disponibles
	FROM
		(SELECT commune.nom AS nom_commune, pe.numero, nature.nom AS nature, pe.voie, der_etat_dispo.date_operation
		FROM territoire
		,remocra.hydrant pe
		LEFT JOIN der_etat_dispo ON der_etat_dispo.id_hydrant = pe.id
		LEFT JOIN remocra.type_hydrant_nature nature ON(nature.id = pe.nature)
		LEFT JOIN remocra.commune ON (commune.id = pe.commune)
	 WHERE
		pe.geometrie && territoire.geometrie
		AND ST_Within(pe.geometrie,territoire.geometrie)
		AND dispo_terrestre = 'INDISPO' ORDER BY commune.nom, nature.nom)AS indispo) AS nonDispo