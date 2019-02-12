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
    numero_patch := 116;
    description_patch := 'Différenciation des alertes de visite selon la nature du PEI';

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

INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES
('HYDRANT_RENOUVELLEMENT_CTRL_PRIVE', 'Délai légal entre 2 contrôles techniques périodiques sur PEI de DECI privée (en jours)', 365, 1, 'Points d''eau'),
('HYDRANT_RENOUVELLEMENT_CTRL_PUBLIC', 'Délai légal entre 2 contrôles techniques périodiques sur PEI de DECI publique ou privée sous convention (en jours)', 365, 1, 'Points d''eau'),
('HYDRANT_RENOUVELLEMENT_RECO_PRIVE', 'Délai légal entre 2 reconnaissances opérationnelles périodiques sur PEI de DECI privée (en jours)', 365, 1, 'Points d''eau'),
('HYDRANT_RENOUVELLEMENT_RECO_PUBLIC', 'Délai légal entre 2 reconnaissances opérationnelles périodiques sur PEI de DECI publique ou privée sous convention (en jours)', 180, 1, 'Points d''eau'),
('HYDRANT_LONGUE_INDISPONIBILITE_MESSAGE', 'Message à afficher en cas de PEI indisponible depuis trop longtemps', 'Un ou plusieurs PEI situé(s) sur votre territoire est/sont indisponible(s) depuis plus de %MOIS% mois et %JOURS% jours :', 1, 'Points d''eau'),
('HYDRANT_LONGUE_INDISPONIBILITE_JOURS', 'Nombre de jours avant de considérer un PEI comme indisponible depuis trop longtemps', 180, 1, 'Points d''eau'),
('HYDRANT_LONGUE_INDISPONIBILITE_TYPEORGANISME', 'Organismes concernés par le message à afficher en cas de PEI indisponible depuis trop longtemps', '^(COMMUNE|EPCI)$', 1, 'Points d''eau');

-- On reprend les valeurs existantes pour ne pas changer le comportement des SDIS
UPDATE remocra.param_conf s
SET valeur = p.valeur
From remocra.param_conf p
WHERE p.cle LIKE 'HYDRANT_RENOUVELLEMENT_CTRL'
AND s.cle LIKE 'HYDRANT_RENOUVELLEMENT_CTRL%';

UPDATE remocra.param_conf s
SET valeur = p.valeur
From remocra.param_conf p
WHERE p.cle LIKE 'HYDRANT_RENOUVELLEMENT_RECO'
AND s.cle LIKE 'HYDRANT_RENOUVELLEMENT_RECO%';

-- On supprime les paramètres obsolètes
DELETE FROM remocra.param_conf WHERE cle LIKE 'HYDRANT_RENOUVELLEMENT_CTRL';
DELETE FROM remocra.param_conf WHERE cle LIKE 'HYDRANT_RENOUVELLEMENT_RECO';

CREATE INDEX hydrant_dispo_terrestre_idx
  ON tracabilite.hydrant
  USING btree
  (dispo_terrestre);

CREATE INDEX hydrant_dispo_hbe_idx
  ON tracabilite.hydrant
  USING btree
  (dispo_hbe);


-- Contenu réel du patch fin
--------------------------------------------------

commit;
