begin;

-- Nouveau type organisme
INSERT INTO remocra.type_organisme (code, nom) VALUES
	('AUTRE_SERVICE_PUBLIC_DECI', 'Autre service public DECI');

-- Nouveau profil d'organisme
INSERT INTO remocra.profil_organisme (code, nom, type_organisme)
SELECT 'AUTRE_SERVICE_PUBLIC_DECI', 'Autre service public DECI', id
FROM remocra.type_organisme
WHERE code = 'AUTRE_SERVICE_PUBLIC_DECI';

/* Aéroports de Paris - Roissy */
-- Insertion de la zone de compétence de l'ADP Roissy
INSERT INTO remocra.zone_competence (code, nom, geometrie)
SELECT 'ADP_ROI', 'Aéroports de Paris - Roissy', St_Union(geometrie)
FROM(
	SELECT DISTINCT zc.geometrie
	FROM remocra.zone_competence zc
		JOIN remocra.hydrant h ON St_Contains(zc.geometrie, h.geometrie) 
			AND h.numero_interne BETWEEN 5000 AND 6000
		JOIN remocra.organisme o ON o.zone_competence = zc.id
		JOIN remocra.type_organisme tor ON tor.id = o.type_organisme AND tor.code = 'COMMUNE'
	) AS roissy;

--Insertion de l'organisme
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT 'ADP_ROI', 'Aéroports de Paris - Roissy', po.id, tor.id, zc.id
FROM remocra.zone_competence zc
	JOIN remocra.profil_organisme po ON po.code = 'AUTRE_SERVICE_PUBLIC_DECI'
	JOIN remocra.type_organisme tor ON tor.code = 'AUTRE_SERVICE_PUBLIC_DECI'
WHERE zc.code = 'ADP_ROI';


/* Aéroports de Paris - Orly */
-- Insertion des zones de compétence des communes manquantes
INSERT INTO remocra.zone_competence (code, nom, geometrie)
SELECT DISTINCT code, nom, geometrie
FROM remocra.commune WHERE code IN ('91689', '91027', '91479');

-- Insertion des organismes communes manquants
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT c.code, c.nom, po.id, tor.id, zc.id
FROM remocra.commune c
	JOIN remocra.profil_organisme po ON po.code = 'COMMUNE'
	JOIN remocra.type_organisme tor ON tor.code = 'COMMUNE'
	JOIN remocra.zone_competence zc ON zc.code = c.code
WHERE c.code IN ('91689', '91027', '91479');

-- Insertion de la zone de compétence ADP Orly
INSERT INTO remocra.zone_competence (code, nom, geometrie)
SELECT 'ADP_ORY', 'Aéroports de Paris - Orly', St_Union(geometrie)
FROM(
	SELECT DISTINCT zc.geometrie
	FROM remocra.zone_competence zc
		JOIN remocra.hydrant h ON St_Contains(zc.geometrie, h.geometrie) 
			AND h.numero_interne BETWEEN 7000 AND 8000
		JOIN remocra.organisme o ON o.zone_competence = zc.id
		JOIN remocra.type_organisme tor ON tor.id = o.type_organisme AND tor.code = 'COMMUNE'
	) AS orly;

-- Insertion de l'organisme ADP Orly
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT 'ADP_ORY', 'Aéroports de Paris - Orly', po.id, tor.id, zc.id
FROM remocra.zone_competence zc
	JOIN remocra.profil_organisme po ON po.code = 'AUTRE_SERVICE_PUBLIC_DECI'
	JOIN remocra.type_organisme tor ON tor.code = 'AUTRE_SERVICE_PUBLIC_DECI'
WHERE zc.code = 'ADP_ORY';


/* Aéroports de Paris - Le Bourget */
-- Insertion des zones de compétence des communes manquantes
INSERT INTO remocra.zone_competence (code, nom, geometrie)
SELECT DISTINCT code, nom, geometrie
FROM remocra.commune WHERE code IN ('95088');

-- Insertion des organismes communes manquants
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT c.code, c.nom, po.id, tor.id, zc.id
FROM remocra.commune c
	JOIN remocra.profil_organisme po ON po.code = 'COMMUNE'
	JOIN remocra.type_organisme tor ON tor.code = 'COMMUNE'
	JOIN remocra.zone_competence zc ON zc.code = c.code
WHERE c.code IN ('95088');

-- Insertion de la zone de compétence ADP Orly
INSERT INTO remocra.zone_competence (code, nom, geometrie)
SELECT 'ADP_LBG', 'Aéroports de Paris - Le Bourget', St_Union(geometrie)
FROM(
	SELECT DISTINCT zc.geometrie
	FROM remocra.zone_competence zc
		JOIN remocra.hydrant h ON St_Contains(zc.geometrie, h.geometrie) 
			AND h.numero_interne BETWEEN 8000 AND 9000
		JOIN remocra.organisme o ON o.zone_competence = zc.id
		JOIN remocra.type_organisme tor ON tor.id = o.type_organisme AND tor.code = 'COMMUNE'
	) AS bourget;

-- Insertion de l'organisme ADP Orly
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT 'ADP_LBG', 'Aéroports de Paris - Le Bourget', po.id, tor.id, zc.id
FROM remocra.zone_competence zc
	JOIN remocra.profil_organisme po ON po.code = 'AUTRE_SERVICE_PUBLIC_DECI'
	JOIN remocra.type_organisme tor ON tor.code = 'AUTRE_SERVICE_PUBLIC_DECI'
WHERE zc.code = 'ADP_LBG';


/* Direction des routes IDF */
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT 'DIRIF', 'Direction des routes', po.id, tor.id, zc.id
FROM remocra.zone_competence zc
	JOIN remocra.profil_organisme po ON po.code = 'AUTRE_SERVICE_PUBLIC_DECI'
	JOIN remocra.type_organisme tor ON tor.code = 'AUTRE_SERVICE_PUBLIC_DECI'
WHERE zc.code = 'BSPP';


/* Direction Régionale de l'Environnement et de l'Energie */
INSERT INTO remocra.organisme (code, nom, profil_organisme, type_organisme , zone_competence )
SELECT 'DRIEE', 'Direction Régionale de l''Environnement et de l''Energie', po.id, tor.id, zc.id
FROM remocra.zone_competence zc
	JOIN remocra.profil_organisme po ON po.code = 'AUTRE_SERVICE_PUBLIC_DECI'
	JOIN remocra.type_organisme tor ON tor.code = 'AUTRE_SERVICE_PUBLIC_DECI'
WHERE zc.code = 'BSPP';

/* Ajout des préfectures voisines */
-- Zone de compétence de la préfecture du 77
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 77', 'PREF77', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '77%';

-- Organisme Préfecture du 77
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 77', 'PREF77', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF77';

-- Zone de compétence de la préfecture du 78
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 78', 'PREF78', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '78%';

-- Organisme Préfecture du 78
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 78', 'PREF78', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF78';

-- Zone de compétence de la préfecture du 91
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 91', 'PREF91', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '91%';

-- Organisme Préfecture du 91
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 91', 'PREF91', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF91';

-- Zone de compétence de la préfecture du 92
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 92', 'PREF92', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '92%';

-- Organisme Préfecture du 92
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 92', 'PREF92', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF92';

-- Zone de compétence de la préfecture du 93
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 93', 'PREF93', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '93%';

-- Organisme Préfecture du 93
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 93', 'PREF93', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF93';

-- Zone de compétence de la préfecture du 94
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 94', 'PREF94', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '94%';

-- Organisme Préfecture du 94
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 94', 'PREF94', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF94';

-- Zone de compétence de la préfecture du 95
INSERT INTO remocra.zone_competence (nom, code, geometrie )
SELECT 'Préfecture du 95', 'PREF95', St_Union(geometrie)
FROM remocra.commune
WHERE insee LIKE '95%';

-- Organisme Préfecture du 95
INSERT INTO remocra.organisme (nom, code, zone_competence, type_organisme, profil_organisme)
SELECT 'Préfecture du 95', 'PREF95', zc.id, tor.id, po.id
FROM remocra.zone_competence zc
	JOIN remocra.type_organisme tor ON tor.code = 'PREFECTURE'
	JOIN remocra.profil_organisme po ON po.code = 'PREFECTURE'
WHERE zc.code = 'PREF95';

/* Mise en conformité de la table hydrant*/
ALTER TABLE remocra.hydrant DISABLE TRIGGER trig_aui;
ALTER TABLE remocra.hydrant DISABLE TRIGGER trig_bd;

-- Suppression des gestionnaires pour les PEI public
UPDATE remocra.hydrant
SET gestionnaire = NULL
WHERE id IN (
	SELECT h.id
	FROM remocra.hydrant h
		JOIN remocra.type_hydrant_nature_deci thnd ON thnd.id = h.nature_deci
			AND (thnd.code = 'PUBLIC')
	WHERE h.gestionnaire IS NOT NULL
);

/* mise à jour pour ADP */
update remocra.hydrant 
set sp_deci = gestion 
FROM
(SELECT
	h.id,
	o.id AS gestion
FROM remocra.hydrant h
JOIN remocra.commune c ON (c.id = h.commune)
JOIN remocra.organisme o ON (o.code = c.insee)
) as a WHERE a.id = hydrant.id;


update remocra.hydrant 
set sp_deci = (select id FROM remocra.organisme WHERE code = 'PARIS_INTRAMUROS')
WHERE commune IN (select id  FROM remocra.commune WHERE insee like '75%');

UPDATE remocra.hydrant
SET nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'CONVENTIONNE')
WHERE (numero_interne BETWEEN 5000 AND 6000
	OR numero_interne BETWEEN 7000 AND 8000
	OR numero_interne BETWEEN 8000 AND 9000)
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'PUBLIC');

/* Mise en conformité de l'aéroport de Roissy */
update remocra.hydrant 
set sp_deci = (select id FROM remocra.organisme WHERE code = 'ADP_ROI'),
	autorite_deci = (SELECT id FROM remocra.organisme WHERE code = 'PPP'),
	gestionnaire = (SELECT id FROM remocra.gestionnaire WHERE code = 'ADP')
WHERE numero_interne BETWEEN 5000 AND 6000;

update remocra.hydrant 
set suffixe_voie = 'ROI'
WHERE numero_interne BETWEEN 5000 AND 6000
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'CONVENTIONNE');

/* Mise en conformité de l'aéroport de Orly */
update remocra.hydrant 
set sp_deci = (select id FROM remocra.organisme WHERE code = 'ADP_ORY'),
	autorite_deci = (SELECT id FROM remocra.organisme WHERE code = 'PPP'),
	gestionnaire = (SELECT id FROM remocra.gestionnaire WHERE code = 'ADP')
WHERE numero_interne BETWEEN 7000 AND 8000;

update remocra.hydrant 
set suffixe_voie = 'ORY'
WHERE numero_interne BETWEEN 7000 AND 8000
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'CONVENTIONNE');

/* Mise en conformité de l'aéroport du Bourget */
update remocra.hydrant 
set sp_deci = (select id FROM remocra.organisme WHERE code = 'ADP_LBG'),
	autorite_deci = (SELECT id FROM remocra.organisme WHERE code = 'PPP'),
	gestionnaire = (SELECT id FROM remocra.gestionnaire WHERE code = 'ADP')
WHERE numero_interne BETWEEN 8000 AND 9000;

update remocra.hydrant 
set suffixe_voie = 'LBG'
WHERE numero_interne BETWEEN 8000 AND 9000
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'CONVENTIONNE');

/* mise à jour des suffixes */
UPDATE remocra.hydrant
SET suffixe_voie = 'ADP'
WHERE (numero_interne BETWEEN 5000 AND 6000
	OR numero_interne BETWEEN 7000 AND 8000
	OR numero_interne BETWEEN 8000 AND 9000)
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'PRIVE')
	AND voie LIKE 'COTE PISTE ADP%';

UPDATE remocra.hydrant
SET suffixe_voie = 'CP'
WHERE (numero_interne BETWEEN 5000 AND 6000
	OR numero_interne BETWEEN 7000 AND 8000
	OR numero_interne BETWEEN 8000 AND 9000)
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'PRIVE')
	AND voie LIKE 'COTE PISTE PRIVE%';

UPDATE remocra.hydrant
SET suffixe_voie = 'CV'
WHERE (numero_interne BETWEEN 5000 AND 6000
	OR numero_interne BETWEEN 7000 AND 8000
	OR numero_interne BETWEEN 8000 AND 9000)
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'PRIVE')
	AND voie LIKE 'COTE VILLE PRIVE%';

UPDATE remocra.hydrant
SET suffixe_voie = 'CV'
WHERE (numero_interne BETWEEN 5000 AND 6000
	OR numero_interne BETWEEN 7000 AND 8000
	OR numero_interne BETWEEN 8000 AND 9000)
	AND nature_deci = (SELECT id FROM remocra.type_hydrant_nature_deci WHERE code = 'PRIVE')
	AND voie NOT LIKE 'COTE%'
	AND voie LIKE '%ZP%';

ALTER TABLE remocra.hydrant ENABLE TRIGGER trig_aui;
ALTER TABLE remocra.hydrant ENABLE TRIGGER trig_bd;



/* Mise à jour des modèles et marques*/
INSERT INTO remocra.type_hydrant_marque (code, nom) VALUES ('EDP', 'EDP');

INSERT INTO remocra.type_hydrant_modele (code, nom, marque) VALUES
('DAUPHINE', 'DAUPHINE', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'AVK')),
('ORION', 'ORION', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'AVK')),
('PEGASE', 'PEGASE', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'AVK')),
('PHENIX', 'PHENIX', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'AVK')),
('ORION', 'ORION', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'AVK')),
('A2', 'A2 10-15', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'BAYARD')),
('DAUPHIN', 'DAUPHIN', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'BAYARD')),
('EMERAUDE', 'EMERAUDE', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'BAYARD')),
('RETRO', 'RETRO', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'BAYARD')),
('SAPHIR', 'SAPHIR', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'BAYARD')),
('RUETIL', 'RUETIL', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'EDP')),
('VP', 'VP', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'EDP')),
('AJAX', 'AJAX', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('ATLAS', 'ATLAS', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('C9', 'C9+', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('ELANCIO', 'ELANCIO', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('HERMES', 'HERMES', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('HIDRO', 'HIDRO', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('PAM', 'PAM', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('RATIONNEL', 'RATIONNEL +', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('SELECTA', 'SELECTA 3D', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('TANGO', 'TANGO', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON')),
('TRI', 'TRI', (SELECT id FROM remocra.type_hydrant_marque WHERE code = 'PONT A MOUSSON'));

CREATE OR REPLACE FUNCTION marque_modele() RETURNS integer AS $$
DECLARE
	id_marque integer;
	id_modele integer;
	rec_pibi RECORD;
	cur_pibi CURSOR FOR
		SELECT id, marque
 		FROM remocra.hydrant_pibi
 		WHERE marque IN (SELECT id FROM remocra.type_hydrant_marque WHERE code IN ('RUETIL', 'VP'));
BEGIN
	OPEN cur_pibi;
	
	LOOP
		FETCH cur_pibi INTO rec_pibi;
		EXIT WHEN NOT FOUND;
	
		SELECT id INTO id_modele FROM remocra.type_hydrant_modele WHERE code = (SELECT code FROM remocra.type_hydrant_marque WHERE id = rec_pibi.marque);
		SELECT marque INTO id_marque FROM remocra.type_hydrant_modele WHERE id = id_modele;
	
		UPDATE remocra.hydrant_pibi
		SET marque = id_marque, modele = id_modele
		WHERE id = rec_pibi.id;
	END LOOP;

	CLOSE cur_pibi;

	RETURN 0;
END; $$
LANGUAGE plpgsql; 

SELECT marque_modele();

DROP FUNCTION marque_modele();

DELETE FROM remocra.type_hydrant_marque
WHERE code IN ('RUETIL', 'VP');


/* Mise en place de la table de référence du Carré des 9 */
-- Fusion des zones du CS de PUTX
UPDATE remocra.organisme
SET zone_competence = (SELECT MIN(id) FROM remocra.zone_competence WHERE nom LIKE '%PUTX%' GROUP BY nom)
WHERE zone_competence = (SELECT MAX(id) FROM remocra.zone_competence WHERE nom LIKE '%PUTX%' GROUP BY nom);

UPDATE remocra.zone_competence
SET geometrie = St_Union(zone_competence.geometrie, zc.geometrie)
FROM remocra.zone_competence zc
WHERE zc.id = (SELECT MAX(id) FROM remocra.zone_competence WHERE nom LIKE '%PUTX%' GROUP BY nom)
	AND zone_competence.id = (SELECT MIN(id) FROM remocra.zone_competence WHERE nom LIKE '%PUTX%' GROUP BY nom);
	
DELETE FROM remocra.zone_competence
WHERE id = (SELECT MAX(id) FROM remocra.zone_competence WHERE nom LIKE '%PUTX%' GROUP BY nom);

--Ajout de la table du carre des 9
CREATE TABLE remocra_referentiel.carre (
	referent bigserial NOT NULL,
	cs bigserial NOT NULL,
	CONSTRAINT carre_pkey PRIMARY KEY (referent, cs),
	CONSTRAINT carre_referent FOREIGN KEY (referent) REFERENCES organisme(id),
	CONSTRAINT carre_cs FOREIGN KEY (cs) REFERENCES organisme(id)
);

-- Initialisation des données
WITH cis AS (
  SELECT o.id, zc.geometrie
  FROM remocra.organisme o
    JOIN remocra.zone_competence zc ON zc.id = o.zone_competence
    JOIN remocra.type_organisme tor ON tor.id = o.type_organisme AND tor.code = 'CS'
),
carre9 AS (
	SELECT id AS referent, id AS cs, 0 AS num
	FROM cis
	UNION
	SELECT c.id , oc.id, row_number() OVER (PARTITION BY c.id ORDER BY oc.Id) AS num
	FROM cis c
		JOIN cis oc ON ST_Touches(oc.geometrie, c.geometrie)
			OR ST_Overlaps(oc.geometrie, c.geometrie)
)
INSERT INTO remocra_referentiel.carre
SELECT DISTINCT referent, cs
FROM carre9 o
WHERE num < 9
ORDER BY referent, cs;

-- Compléments
INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS MTMA'
WHERE refe.nom = 'CS AUBE';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS DRAN'
WHERE refe.nom = 'CS AUBE';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS LAND'
WHERE refe.nom = 'CS PANT';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS AUBE'
WHERE refe.nom = 'CS DRAN';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS AUBE'
WHERE refe.nom = 'CS MTMA';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS MALA'
WHERE refe.nom = 'CS BSLT';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS PARM'
WHERE refe.nom = 'CS BITC';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS PARM'
WHERE refe.nom = 'CS CHAR';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS BOND'
WHERE refe.nom = 'CS TREM';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS BLME'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS CLIC'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS DRAN'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS BLAN'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS MTMA'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS STOU'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS LAND'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS MENI'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS MTRL'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS NEUIL'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS STDE'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS PIER'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS AUBE'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS PANT'
WHERE refe.nom = 'CS PCDG';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS MTRL'
WHERE refe.nom = 'CS NEUI';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS CHLY'
WHERE refe.nom = 'CS VINC';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS CHLY'
WHERE refe.nom = 'CS MASS';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS BITC'
WHERE refe.nom = 'CS PARM';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS CHAR'
WHERE refe.nom = 'CS PARM';

INSERT INTO remocra_referentiel.carre 
SELECT refe.id, cs.id
FROM remocra.organisme refe
	JOIN remocra.organisme cs ON cs.nom = 'CS STMR'
WHERE refe.nom = 'CS VILC';

INSERT INTO remocra.hydrant_aspiration (numero, pena)
SELECT '1', id
FROM remocra.hydrant_pena;


UPDATE remocra.hydrant_visite
SET anomalies = a.anomalies
FROM remocra.hydrant_visite hv
	JOIN (SELECT hv.id, CASE 
		WHEN hv.anomalies = '[]' THEN '[' || tha.id::text || ']'
		WHEN hv.anomalies LIKE '%' || tha.id::text || '%' THEN hv.anomalies
		ELSE REPLACE(hv.anomalies, ']', ',' || tha.id::text || ']')
	END anomalies
	FROM remocra.hydrant_visite hv
		JOIN remocra.type_hydrant_anomalie tha ON tha.code = 'NON_NORMALISE_NC'
	WHERE hv.id IN (
		SELECT DISTINCT ON (hv.hydrant) hv.id
		FROM remocra.hydrant_visite hv
			JOIN remocra.hydrant h ON h.id = hv.hydrant
				AND h.code = 'PENA'
		ORDER BY hv.hydrant, hv.date DESC
	)) a ON a.id = hv.id
WHERE hv.id = hydrant_visite.id;

INSERT INTO remocra.hydrant_anomalies (hydrant, anomalies)
SELECT id, (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'NON_NORMALISE_NC')
FROM remocra.hydrant h 
WHERE h.code = 'PENA'
	AND id NOT IN (
		SELECT hydrant
		FROM remocra.hydrant_anomalies
		WHERE anomalies IN (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'NON_NORMALISE_NC')
	)
/* Ajout d'une aire d'aspiration et de l'ano NON CONFORME au PENA*/
INSERT INTO remocra.hydrant_aspiration (numero, pena)
SELECT '1', id
FROM remocra.hydrant_pena;


UPDATE remocra.hydrant_visite
SET anomalies = a.anomalies
FROM remocra.hydrant_visite hv
	JOIN (SELECT hv.id, CASE 
		WHEN hv.anomalies = '[]' THEN '[' || tha.id::text || ']'
		WHEN hv.anomalies LIKE '%' || tha.id::text || '%' THEN hv.anomalies
		ELSE REPLACE(hv.anomalies, ']', ',' || tha.id::text || ']')
	END anomalies
	FROM remocra.hydrant_visite hv
		JOIN remocra.type_hydrant_anomalie tha ON tha.code = 'NON_NORMALISE_NC'
	WHERE hv.id IN (
		SELECT DISTINCT ON (hv.hydrant) hv.id
		FROM remocra.hydrant_visite hv
			JOIN remocra.hydrant h ON h.id = hv.hydrant
				AND h.code = 'PENA'
		ORDER BY hv.hydrant, hv.date DESC
	)) a ON a.id = hv.id
WHERE hv.id = hydrant_visite.id;

INSERT INTO remocra.hydrant_anomalies (hydrant, anomalies)
SELECT id, (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'NON_NORMALISE_NC')
FROM remocra.hydrant h 
WHERE h.code = 'PENA'
	AND id NOT IN (
		SELECT hydrant
		FROM remocra.hydrant_anomalies
		WHERE anomalies IN (SELECT id FROM remocra.type_hydrant_anomalie WHERE code = 'NON_NORMALISE_NC')
	)

commit;