BEGIN;
-- Notifier responsables
insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(49,'Notifier les responsables',2,TRUE,'','checkbox','true','NOTIFIER_RESPONSABLES',1);

insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(50,'Notifier les responsables',2,TRUE,'','checkbox','true','NOTIFIER_RESPONSABLES',12);

insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(51,'Notifier les responsables',2,TRUE,'','checkbox','true','NOTIFIER_RESPONSABLES',13);

insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(52,'Notifier les responsables',2,TRUE,'','checkbox','true','NOTIFIER_RESPONSABLES',14);

-- Notifier communes
insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(53,'Notifier les communes',3,TRUE,'','checkbox','true','NOTIFIER_COMMUNES',1);

insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(54,'Notifier les communes',3,TRUE,'','checkbox','true','NOTIFIER_COMMUNES',12);

insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(55,'Notifier les communes',3,TRUE,'','checkbox','true','NOTIFIER_COMMUNES',13);

insert into pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	values(56,'Notifier les communes',3,TRUE,'','checkbox','true','NOTIFIER_COMMUNES',14);



-- Mise à jour des traitements en cours
-- etat_hydrant_indisponibles : notifier responsables
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles') and nom ='NOTIFIER_RESPONSABLES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles'));



-- etat_hydrant_indisponibles : notifier communes
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles') and nom ='NOTIFIER_COMMUNES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant_indisponibles'));



-- etat_hydrant : notifier responsables
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant') and nom ='NOTIFIER_RESPONSABLES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant'));


-- etat_hydrant : notifier communes
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant') and nom ='NOTIFIER_COMMUNES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_hydrant'));



-- etat_pena_indisponibles : notifier responsables
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles') and nom ='NOTIFIER_RESPONSABLES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles'));


-- etat_pena_indisponibles : notifier communes
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles') and nom ='NOTIFIER_COMMUNES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena_indisponibles'));


-- etat_pena : notifier responsables
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena') and nom ='NOTIFIER_RESPONSABLES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena'));


-- etat_pena : notifier communes
insert into pdi.traitement_parametre (idparametre,idtraitement,valeur)
(select
  (select idparametre from pdi.modele_traitement_parametre where idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena') and nom ='NOTIFIER_COMMUNES'),
  idtraitement,
  'true'
from pdi.traitement where  idmodele = (select idmodele from pdi.modele_traitement where ref_nom = 'etat_pena'));


COMMIT;

