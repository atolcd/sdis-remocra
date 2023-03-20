-- Ajoute la numérotation interne du SDIS 01
UPDATE remocra.param_conf SET valeur = '01' WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';
