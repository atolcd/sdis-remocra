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
    numero_patch := 90;
    description_patch := 'Gestion des tournées';

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

ALTER TABLE remocra.tournee ADD nom character varying;
UPDATE remocra.tournee SET nom ='Tournée ' || id;
ALTER TABLE remocra.tournee ALTER COLUMN nom SET NOT NULL;
ALTER TABLE remocra.tournee ALTER COLUMN affectation SET NOT NULL;
ALTER TABLE remocra.tournee ADD CONSTRAINT nom_affectation UNIQUE (nom, affectation);

-- table remocra.hydrant_tournee
DROP TABLE IF EXISTS remocra.hydrant_tournees CASCADE;
CREATE TABLE remocra.hydrant_tournees (
id bigserial NOT NULL,
hydrant bigint,
tournees bigint,
CONSTRAINT hydrant_tournees_pkey PRIMARY KEY (id),
CONSTRAINT hydrant_tournees_hydrant FOREIGN KEY (hydrant) REFERENCES remocra.hydrant (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT hydrant_tournees_tournees FOREIGN KEY (tournees) REFERENCES remocra.tournee (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT uk_hydrant_tournees UNIQUE (hydrant, tournees)
);

-- Récupération des anciennes tournées

INSERT INTO remocra.hydrant_tournees(hydrant, tournees) select id, tournee from remocra.hydrant;

-- Suppression de la colonne tournee
ALTER TABLE remocra.hydrant DROP column tournee;

DROP TRIGGER trig_hydrant_tournee ON remocra.hydrant;

DROP FUNCTION remocra.trg_hydrant_tournee();

DROP FUNCTION remocra.trg_tournee();

DROP FUNCTION remocra.updateinfotournee(bigint);

-- Droit Forcer le porcentage des tournées

INSERT INTO remocra.type_droit (code, nom, description, categorie, version) values
  ('TOURNEE_POURCENTAGE_C', 'tournee.pourcentage_C', 'Forcer le pourcentage des tournées', 'Module PEI', '1');


-- Contenu réel du patch fin
--------------------------------------------------

commit;
