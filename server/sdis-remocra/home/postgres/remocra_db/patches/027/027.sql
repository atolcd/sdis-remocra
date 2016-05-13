SET search_path = remocra, pdi, public, pg_catalog; 

BEGIN;  


-- Paramètre de configuration "purge des alertes"
delete from remocra.param_conf where cle = 'PDI_PURGE_ALERTE_JOURS';
insert into remocra.param_conf (cle,valeur,description, version) values (
    'PDI_PURGE_ALERTE_JOURS', '31', 'Nombre de jours avant suppression des alertes prises en compte', 1);


COMMIT;

