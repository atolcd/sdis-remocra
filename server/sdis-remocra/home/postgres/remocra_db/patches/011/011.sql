SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;




-- Renommage du paramètre pour prise en compte par PDI
UPDATE remocra.param_conf SET cle = 'PDI_DOSSIER_DEPOT' WHERE cle = 'DOSSIER_DEPOT_PDI';




-- Nettoyage des traitements

-- "Points d'eau" / "Etat des hydrants" :
-- Renommage du paramètre "Type de référentiel"
update pdi.modele_traitement_parametre set form_etiquette='Commune' where nom='LST_COMMUNES' and idmodele = (select idmodele from pdi.modele_traitement where nom = 'Etat des hydrants');

-- Retrait du paramètre "Depuis le"
delete from pdi.traitement_parametre where
    -- traitement
    idtraitement in (select idtraitement from pdi.traitement where idmodele = (
        select idmodele from pdi.modele_traitement where nom = 'Etat des hydrants'
    )) and
    -- modele_traitement_parametre
    idparametre = (select idparametre from pdi.modele_traitement_parametre where nom = 'DATE_DEB' and idmodele = (
        select idmodele from pdi.modele_traitement where nom = 'Etat des hydrants'
    )
);

delete from pdi.modele_traitement_parametre where nom = 'DATE_DEB' and idmodele = (select idmodele from pdi.modele_traitement where nom = 'Etat des hydrants');



--Suppression des traitements "Synchronisation des tables (alertes, permis, DFCI, risques)" et "Export des données d''une table dans un fichier pour une commune"
-- traitement_parametre
delete from pdi.traitement_parametre where idtraitement in (
    select idtraitement from pdi.traitement where idmodele in (
        select idmodele from pdi.modele_traitement where nom in ('Synchronisation des tables alertes', 'Synchronisation des tables permis', 'Synchronisation des tables DFCI',
        'Synchronisation des tables risques', 'Export des données d''une table dans un fichier pour une commune')
    )
);

-- traitement
delete from pdi.traitement where idmodele in (
    select idmodele from pdi.modele_traitement where nom in ('Synchronisation des tables alertes', 'Synchronisation des tables permis', 'Synchronisation des tables DFCI',
    'Synchronisation des tables risques', 'Export des données d''une table dans un fichier pour une commune')
);

-- modele_traitement_parametre
delete from pdi.modele_traitement_parametre where idmodele in (
    select idmodele from pdi.modele_traitement where nom in ('Synchronisation des tables alertes', 'Synchronisation des tables permis', 'Synchronisation des tables DFCI',
    'Synchronisation des tables risques', 'Export des données d''une table dans un fichier pour une commune')
);

-- modele_traitement
delete from pdi.modele_traitement where nom in ('Synchronisation des tables alertes', 'Synchronisation des tables permis', 'Synchronisation des tables DFCI',
'Synchronisation des tables risques', 'Export des données d''une table dans un fichier pour une commune');




-- param_conf / PDI
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_CHEMIN_SYNCHRO','/var/remocra/pdi/synchro','Dossier de stockage temporaire des fichiers d''échanges APIS – REMOCRA');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_DOSSIER_EXPORT_SDIS','EXPORT_SDIS','Nom du sous-dossier temporaire dans lequel sont déposés les fichiers d''échanges produits par REMOCRA');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_DOSSIER_IMPORT_EXTRANET','IMPORT_EXTRANET','Nom du sous-dossier temporaire dans lequel sont déposés les fichiers d''échanges récupérés d''APIS');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_FTP_DOSSIER_EXTRANET','EXPORT_EXTRANET','Nom du sous-dossier du serveur FTP dans lequel récupérer les fichiers produits par APIS');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_FTP_DOSSIER_SDIS','IMPORT_SDIS','Nom du sous-dossier du serveur FTP dans lequel déposer les fichiers produits par REMOCRA');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_FTP_DOSSIER_SYNCHRO','SYNCHRO','Dossier du serveur FTP dédié à la synchronisation');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_NOM_SCHEMA_ORACLE','REMOCRA','Nom du schéma Oracle de la base APIS pour génération des instructions SQL');
INSERT INTO remocra.param_conf (cle,valeur,description) VALUES (
    'PDI_POSTGRESQL_NOM_SCHEMA_SYNCHRO','sdis_referentiel','Nom du schéma PostgreSQL temporaire pour import des données APIS');

DELETE FROM remocra.param_conf WHERE cle = 'PDI_FTP_DATABASE_SQL_FILE';
DELETE FROM remocra.param_conf WHERE cle = 'PDI_FTP_DATABASE_ZIP_FILE';
DELETE FROM remocra.param_conf WHERE cle = 'PDI_FTP_DOSSIER_DOCUMENT';
DELETE FROM remocra.param_conf WHERE cle = 'PDI_FTP_DOSSIER_SQL';



COMMIT;

