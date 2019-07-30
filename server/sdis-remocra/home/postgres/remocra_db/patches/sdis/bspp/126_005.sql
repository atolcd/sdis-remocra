begin;

-- Mise à jour des modèle de mail (remplacement de 'le SDIS' par 'la BSPP')

UPDATE remocra.email_modele
SET corps = replace(replace(corps, 'le SDIS', 'la BSPP'), 'SDIS', 'BSPP'), objet = replace(objet, 'SDIS', 'BSPP')
WHERE id = id;


commit;

