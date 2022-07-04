BEGIN;

TRUNCATE remocra.type_alerte_elt RESTART IDENTITY CASCADE;
TRUNCATE remocra.type_alerte_ano RESTART IDENTITY CASCADE;

INSERT INTO remocra.type_alerte_ano (code, nom) VALUES 
	('MANQUANT', 'Manquant'),
	('ASUPPR', 'A supprimer'),
	('NOM', 'Pas le bon nom'),
	('PLACE', 'Pas à la bonne place'),
	('AUTRE', 'Autre');
	
INSERT INTO remocra.type_alerte_elt (code, nom) VALUES
	('ETAB', 'Etablissement'),
	('RESROUT', 'Réseau routier'),
	('RESI', 'Résidence'),
	('AUTRE', 'Autre');
	
INSERT INTO remocra.sous_type_alerte_elt (code, nom, type_geom, type_alerte_elt) VALUES
	('ERP', 'ERP/ICPE', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'ETAB')),
	('CAMPI', 'Camping', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'ETAB')),
	('ENTR', 'Entreprise', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'ETAB')),
	('SPORT', 'Sport plein air', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'ETAB')),
	('SERVP', 'Service Public', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'ETAB')),
	('STATSERV', 'Station-service', 0, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'RESROUT')),
	('PLACE', 'Place', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'RESROUT')),
	('ROUTE', 'Route/piste', 1, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'RESROUT')),
	('PARKI', 'Parking', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'RESROUT')),
	('REDI', 'Résidence', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'RESI')),
	('PERIM', 'Périmètre', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'RESI')),
	('HYDRANT', 'Hydrant', 0, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'AUTRE')),
	('PEN', 'Point d''eau naturel', 0, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'AUTRE')),
	('CS', 'Colonne sèche/humide', 0, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'AUTRE')),
	('PREMAR', 'Point remarquable', 0, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'AUTRE')),
	('PARC', 'Square/parc', 2, (SELECT id FROM remocra.type_alerte_elt WHERE code = 'AUTRE'));

COMMIT;
