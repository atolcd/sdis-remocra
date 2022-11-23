begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 174;
    description_patch := 'Correction auteur modification tracabilité des visites';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début
CREATE OR REPLACE FUNCTION tracabilite.update_hydrant_visite(p_id_hydrant_visite bigint, p_operation character varying, p_num_transac bigint)
RETURNS void
LANGUAGE plpgsql
AS $function$
BEGIN
UPDATE tracabilite.hydrant_visite
SET nom_operation = p_operation, auteur_modification =
CASE
-- SI le  niveau de traca est fixé sur utilisateur
WHEN ((SELECT valeur From remocra.param_conf WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur'
-- ET que le auteur_modification_flag est bien USER
-- Si le flag est API ou encore ETL alors on veux connaitre l'organisme pas l'utilisateur
AND ((
SELECT hv.auteur_modification_flag FROM remocra.hydrant_visite hv WHERE hv.id = p_id_hydrant_visite) = 'USER'))
THEN (SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
  FROM remocra.hydrant_visite hv
  JOIN remocra.utilisateur u on (hv.utilisateur_modification = u.id)
  JOIN remocra.organisme o on (u.organisme = o.id)
  WHERE hv.id = p_id_hydrant_visite)
ELSE (SELECT o.nom FROM remocra.hydrant_visite hv 
  JOIN remocra.organisme o ON (hv.organisme = o.id)
  WHERE hv.id = p_id_hydrant_visite) END,
utilisateur_modification = hv.utilisateur_modification, organisme = hv.organisme, auteur_modification_flag = hv.auteur_modification_flag
FROM remocra.hydrant_visite hv
JOIN remocra.hydrant h ON h.id = hv.hydrant
WHERE hv.id = p_id_hydrant_visite
AND num_transac = p_num_transac
AND h.id = p_id_hydrant_visite;
END;
$function$
;

------------------------HYDRANT_VISITE---------------------------
CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant_visite(p_id_hydrant_visite bigint, p_operation character varying)
RETURNS void
LANGUAGE plpgsql
AS $function$
BEGIN
INSERT INTO tracabilite.hydrant_visite (num_transac, nom_operation, date_operation, id_visite, hydrant, "date", "type", ctrl_debit_pression, agent1, agent2, debit,
debit_max, pression, pression_dyn, pression_dyn_deb, anomalies, observations, auteur_modification, utilisateur_modification, organisme, auteur_modification_flag)
SELECT txid_current() ,p_operation,  now() AS date_operation, hv.id, hydrant, hv."date", hv."type", hv.ctrl_debit_pression, hv.agent1, hv.agent2, hv.debit,
hv.debit_max, hv.pression, hv.pression_dyn, hv.pression_dyn_deb, hv.anomalies, hv.observations,

CASE
-- SI le  niveau de traca est fixé sur utilisateur

WHEN ((SELECT valeur From remocra.param_conf
WHERE cle='NIVEAU_TRACABILITE') = 'utilisateur'
-- ET que le auteur_modification_flag est bien USER
-- Si le flag est API ou encore ETL alors on veux connaitre l'organisme pas l'utilisateur
AND ((
SELECT hv.auteur_modification_flag FROM remocra.hydrant_visite hv WHERE hv.id = p_id_hydrant_visite) = 'USER')
)
THEN (
  SELECT (o.nom ||'_' || u.nom ||' '|| u.prenom)
  FROM remocra.hydrant_visite hv
  JOIN remocra.utilisateur u on (hv.utilisateur_modification = u.id)
  JOIN remocra.organisme o on (u.organisme = o.id)
  WHERE hv.id = p_id_hydrant_visite
)

ELSE (
  SELECT o.nom FROM remocra.hydrant_visite hv 
  JOIN remocra.organisme o ON (hv.organisme = o.id)
  WHERE hv.id = p_id_hydrant_visite
) END,
hv.utilisateur_modification, hv.organisme, hv.auteur_modification_flag
FROM remocra.hydrant_visite hv
JOIN remocra.hydrant h ON h.id = hv.hydrant
WHERE hv.id = p_id_hydrant_visite;
END;
$function$
;


-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
