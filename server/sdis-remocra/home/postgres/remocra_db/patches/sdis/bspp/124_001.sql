begin;

-- Insertions des requêtes personnalisées pour les PIBI et les PENA
INSERT INTO remocra.requete_fiche(libelle, code, source_sql) VALUES

-- PIBI
('Requête personnalisée PIBI', 'RESUME_PIBI', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data,
		cstc.cstc_data
	)
) as TEXT) as xml 
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "observation", h.observation),
			xmlelement(name "debit_renforce", h.debit_renforce),
			xmlelement(name "grosDebit", h.grosDebit),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "jumele", h.jumele),
			xmlelement(name "nature", h.nature)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie) AS adresse,
			c.nom AS commune,  
			h.observation, 
			hp.debit_renforce,
			h.dispo_terrestre,
			th.code AS nature,
			CASE WHEN thn.code = ''PI'' AND hp.diametre IS NOT NULL AND CAST(thd.nom AS INTEGER) = 150 THEN TRUE
				WHEN thn.code = ''BI'' AND hp.jumele >= 0 THEN TRUE
				ELSE FALSE
			END AS grosDebit,
			CASE WHEN hp.jumele >= 0 THEN h2.numero
				ELSE NULL
			END as jumele
			
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.hydrant_pibi hp on hp.id=h.id
			LEFT JOIN remocra.type_hydrant_diametre thd on thd.id=hp.diametre
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			-- Jumelage
			LEFT JOIN remocra.hydrant_pibi hp2 on hp2.id=hp.jumele
			LEFT JOIN remocra.hydrant h2 on h2.id=hp2.id
		WHERE 	h.id=:id)
		AS h) as hydrant,
		
	(SELECT
		XMLAGG(xmlelement(name "anomalie",
			xmlelement(name "nom", a.nom),
			xmlelement(name "indispo", a.indispo))
		)as anomalies_data
	FROM
		(SELECT tha.nom AS nom, 
			than.val_indispo_terrestre AS indispo
		FROM 	remocra.hydrant_anomalies ha
			JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
			JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
			JOIN remocra.hydrant h on h.id=ha.hydrant
		WHERE 	ha.hydrant=:id
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
		WHERE typeO.code = ''CS'' 
			AND ht.hydrant=:id
		ORDER BY o.nom) as c) as cstc;'),
-- PENA
('Requête personnalisée PENA', 'RESUME_PENA', 'SELECT CAST(xmlelement(name "data",
	xmlconcat(
		hydrant.hydrant_data,
		anomalies.anomalies_data,
		cstc.cstc_data
	)
) as TEXT) as xml 
FROM
	(SELECT xmlelement(name "hydrant",
			xmlelement(name "adresse", h.adresse),
			xmlelement(name "commune", h.commune),
			xmlelement(name "observation", h.observation),
			xmlelement(name "dispo_terrestre", h.dispo_terrestre),
			xmlelement(name "nature", h.nature),
			xmlelement(name "capacite", h.capacite),
			xmlelement(name "aspirations", h.aspirations)
	) as hydrant_data
	FROM 
		(SELECT TRIM(COALESCE(h.numero_voie || '' '', '''') || COALESCE(h.suffixe_voie || '' '', '''') || h.voie) AS adresse,
			c.nom AS commune,  
			h.observation, 
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
				WHERE h.id=:id) AS aspirations
			
		FROM 	remocra.hydrant h
			JOIN remocra.commune c on h.commune=c.id
			JOIN remocra.type_hydrant_nature thn on thn.id=h.nature
			JOIN remocra.type_hydrant th on th.id=thn.type_hydrant
			-- PENA
			JOIN remocra.hydrant_pena hp on hp.id=h.id
		WHERE 	h.id=:id) 
		AS h) as hydrant,
		
	(SELECT
		XMLAGG(xmlelement(name "anomalie",
			xmlelement(name "nom", a.nom),
			xmlelement(name "indispo", a.indispo))
		)as anomalies_data
	FROM
		(SELECT tha.nom AS nom, 
			than.val_indispo_terrestre AS indispo
		FROM 	remocra.hydrant_anomalies ha
			JOIN remocra.type_hydrant_anomalie tha on tha.id = ha.anomalies
			JOIN remocra.type_hydrant_anomalie_nature than on than.anomalie=tha.id
			JOIN remocra.hydrant h on h.id=ha.hydrant
		WHERE 	ha.hydrant=:id 
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
		WHERE typeO.code = ''CS''
			AND ht.hydrant=:id
		ORDER BY o.nom) as c) as cstc;');
commit;

