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
    numero_patch := 149;
    description_patch := 'Création de la table type_rci_risque_meteo ';

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

DROP TABLE IF EXISTS remocra.type_rci_risque_meteo CASCADE;
CREATE TABLE remocra.type_rci_risque_meteo(
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL UNIQUE,
	nom character varying NOT NULL UNIQUE
);
COMMENT ON TABLE remocra.type_rci_risque_meteo IS 'Risque météo lié constaté';
COMMENT ON COLUMN remocra.type_rci_risque_meteo.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_rci_risque_meteo.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_rci_risque_meteo.code IS 'Code du risque. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_rci_risque_meteo.nom IS 'Libellé du risque';

INSERT INTO remocra.type_rci_risque_meteo(code,nom) VALUES ('F','Faible');
INSERT INTO remocra.type_rci_risque_meteo(code,nom) VALUES ('L','Léger');
INSERT INTO remocra.type_rci_risque_meteo(code,nom) VALUES ('M','Modéré');
INSERT INTO remocra.type_rci_risque_meteo(code,nom) VALUES ('S','Sévère');
INSERT INTO remocra.type_rci_risque_meteo(code,nom) VALUES ('TS','Très sévère');
INSERT INTO remocra.type_rci_risque_meteo(code,nom) VALUES ('E','Extrème');

alter table remocra.rci add column risque_meteo bigint;
alter table only remocra.rci add constraint fk_rci_risque_meteo foreign key (risque_meteo) references remocra.type_rci_risque_meteo(id) match simple on update cascade on delete cascade;



-- Contenu réel du patch fin
--------------------------------------------------

commit;

