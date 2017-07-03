SELECT
	xmlelement(name "old",
		-- Parcelle
		xmlelement(name "parcelle",
			-- Identification
			xmlelement(name "identification",
				xmlelement(name "commune",xmlattributes(com.insee AS "insee"),com.nom),
				xmlelement(name "section",o.section),
				xmlelement(name "parcelle",o.parcelle),
				xmlelement(name "zoneUrbanisme",zurb.nom)
			),
			-- Localisation et acces
			xmlelement(NAME "localisation",
				xmlelement(name "adresse",
					xmlelement(name "numVoie",o.num_voie),
					xmlelement(name "voie",o.voie),
					xmlelement(name "lieuDit",o.lieu_dit)
				),
				xmlelement(NAME "acces",
					xmlelement(name "type",acc.nom),
					xmlelement(name "largeur",o.largeur_acces),
					xmlelement(name "portailElectrique",
						CASE
							WHEN portail_electrique THEN 'oui'
							ELSE 'non'
						END
					)
				)
			),
			-- Propriétaire
			xmlelement(name "proprietaire",
				xmlelement(name "typeResidence",res.nom),
				xmlelement(name "organisme",prop.raison_sociale),
				xmlelement(name "civilite",prop.civilite),
				xmlelement(name "nom",prop.nom),
				xmlelement(name "prenom",prop.prenom),
				xmlelement(name "adresse",
					xmlelement(name "numVoie",prop.num_voie),
					xmlelement(name "voie",prop.voie),
					xmlelement(name "lieuDit",prop.lieu_dit),
					xmlelement(name "codePostal",prop.code_postal),
					xmlelement(name "ville",prop.ville),
					xmlelement(name "pays",prop.pays)
				),
				xmlelement(name "telephone",prop.telephone),
				xmlelement(name "email",prop.email)
			),
			-- Locataire
			xmlelement(NAME "locataire",
				xmlelement(name "organisme",loc.raison_sociale),
				xmlelement(name "civilite",loc.civilite),
				xmlelement(name "nom",loc.nom),
				xmlelement(name "prenom",loc.prenom),
				xmlelement(name "telephone",loc.telephone),
				xmlelement(name "email",loc.email)
			)
		),
		-- Caracteristiques environnementales
		xmlelement(name "environnement",
			xmlelement(name "volume",o.volume),
			xmlelement(name "caracteristiques",xml_caracteristiques)
		),
		-- Dernière visite
		xmlelement(name "visites",xml_visite)
	) AS xml
FROM
	--Filtrage de l'oldeb sur idcommune,section et parcelle
	(SELECT * FROM remocra.oldeb WHERE commune = ${COMMUNE_ID} AND UPPER(section) = UPPER('${NUM_SECTION}') AND UPPER(parcelle) = UPPER('${NUM_PARCELLE}')) o
	JOIN remocra.commune com ON (com.id = o.commune)
	LEFT JOIN remocra.type_oldeb_zone_urbanisme zurb ON(zurb.id = o.zone_urbanisme)
	LEFT JOIN remocra.type_oldeb_acces acc ON(acc.id = o.acces)
	LEFT JOIN remocra.oldeb_locataire loc ON(loc.oldeb = o.id)
	LEFT JOIN remocra.oldeb_propriete prop_o ON(prop_o.oldeb = o.id)
	LEFT JOIN remocra.oldeb_proprietaire prop ON(prop.id = prop_o.proprietaire)
	LEFT JOIN remocra.type_oldeb_residence res ON(res.id = prop_o.residence)
	--Caracteristiques
	LEFT JOIN (SELECT
		oldeb,
		XMLAGG(
			xmlelement(name "caracteristique",
				xmlelement(name "type", tcat.nom),
				xmlelement(name "libelle", tcar.nom)
			)
		) AS xml_caracteristiques
	FROM
		remocra.oldeb_caracteristique car
		JOIN remocra.type_oldeb_caracteristique tcar ON(tcar.id = car.caracteristique)
		JOIN remocra.type_oldeb_categorie_caracteristique tcat ON(tcat.id = tcar.categorie)
	GROUP BY
		oldeb) carac ON(carac.oldeb = o.id)
	--Info de derniere visite
	LEFT JOIN (SELECT
		oldeb,
		xmlelement(name "visite",
			xmlelement(name "date", to_char(v.date_visite,'dd/mm/yyyy')),
			xmlelement(name "agent",v.agent),
			xmlelement(name "anomalies",xml_anomalies),
			xmlelement(name "avis",avis.nom),
			xmlelement(name "debrParcelle",etat_p.nom),
			xmlelement(name "debrAcces",etat_a.nom),
			xmlelement(name "action",act.nom),
			xmlelement(name "observation",v.observation)
		) AS xml_visite

	FROM
		remocra.oldeb_visite v
		LEFT JOIN remocra.type_oldeb_avis avis ON(avis.id = v.avis)
		LEFT JOIN remocra.type_oldeb_debroussaillement etat_p ON(etat_p.id = v.debroussaillement_parcelle)
		LEFT JOIN remocra.type_oldeb_debroussaillement etat_a ON(etat_a.id = v.debroussaillement_acces)
		LEFT JOIN remocra.type_oldeb_action act ON(act.id = v.action)
		LEFT JOIN (SELECT
				visite,
				XMLAGG(
					xmlelement(name "anomalie",
						xmlelement(name "type", tcat.nom),
						xmlelement(name "libelle", tano.nom)
					)
				) AS xml_anomalies
			FROM
				remocra.oldeb_visite_anomalie ano
				JOIN remocra.type_oldeb_anomalie tano ON(tano.id = ano.anomalie)
				JOIN remocra.type_oldeb_categorie_anomalie tcat ON(tcat.id = tano.categorie)
			GROUP BY
				visite) ano ON(ano.visite = v.id)
			WHERE
				--Filtrage de l'oldeb sur idcommune,section et parcelle
				oldeb IN (SELECT id FROM remocra.oldeb WHERE commune = ${COMMUNE_ID} AND UPPER(section) = UPPER('${NUM_SECTION}') AND UPPER(parcelle) = UPPER('${NUM_PARCELLE}'))
		ORDER BY
			date_visite DESC LIMIT 1) visite ON (visite.oldeb = o.id)
