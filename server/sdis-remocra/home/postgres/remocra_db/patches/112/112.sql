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
    numero_patch := 112;
    description_patch := 'Ajout des tables Vigicru';

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

CREATE TABLE remocra_referentiel.vigicrue_station (
    id serial NOT NULL,
    geometrie geometry,
    code character varying NOT NULL,
    libelle character varying NOT NULL,
    CONSTRAINT vigicrue_station_pkey PRIMARY KEY (id),
    CONSTRAINT vigicrue_station_srid CHECK ((public.st_srid(geometrie) = 2154))
);



CREATE TABLE remocra_referentiel.vigicrue_station_mesure (
    id serial NOT NULL,
    dateheure timestamp without time zone NOT NULL,
    hauteur integer NOT NULL,
    idstation integer NOT NULL,
    CONSTRAINT vigicrue_station_mesure_pkey PRIMARY KEY (id),
    CONSTRAINT fk_vigicrue_station_idstation FOREIGN KEY (idstation) REFERENCES remocra_referentiel.vigicrue_station(id) ON UPDATE CASCADE ON DELETE CASCADE
);



CREATE TABLE remocra_referentiel.vigicrue_troncon (
    id serial NOT NULL,
    geometrie geometry,
    code character varying NOT NULL,
    libelle character varying NOT NULL,
    CONSTRAINT vigicrue_troncon_pkey PRIMARY KEY (id),
    CONSTRAINT vigicrue_troncon_srid CHECK ((public.st_srid(geometrie) = 2154))
);


CREATE TABLE remocra_referentiel.vigicrue_troncon_niveau (
    id serial NOT NULL,
    dateheure timestamp without time zone NOT NULL,
    niveau integer NOT NULL,
    idtroncon integer NOT NULL,
    CONSTRAINT vigicrue_troncon_niveau_pkey PRIMARY KEY (id),
    CONSTRAINT fk_vigicrue_troncon_idtroncon FOREIGN KEY (idtroncon) REFERENCES remocra_referentiel.vigicrue_troncon(id) ON UPDATE CASCADE ON DELETE CASCADE
);



CREATE INDEX vigicrue_station_geometrie_idx ON remocra_referentiel.vigicrue_station USING gist (geometrie);

CREATE INDEX vigicrue_troncon_geometrie_idx ON remocra_referentiel.vigicrue_troncon USING gist (geometrie);

-- Contenu réel du patch fin
--------------------------------------------------

commit;
