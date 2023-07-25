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
    numero_patch := 180;
    description_patch := 'Ajoute un schéma pour stocker les données envoyées par la tablette';

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

CREATE SCHEMA IF NOT EXISTS incoming;

CREATE TABLE incoming.hydrant_visite(
    id_hydrant_visite uuid NOT NULL,
    id_hydrant bigint REFERENCES remocra.hydrant(id),
    date_hydrant_visite timestamp without time zone DEFAULT NOW(),
    id_type_hydrant_saisie bigint REFERENCES remocra.type_hydrant_saisie(id),
    ctrl_debit_pression boolean,
    agent1_hydrant_visite CHARACTER VARYING,
    agent2_hydrant_visite CHARACTER VARYING,
    debit_hydrant_visite INTEGER,
    pression_hydrant_visite DOUBLE PRECISION,
    pression_dyn_hydrant_visite DOUBLE PRECISION,
    observations_hydrant_visite CHARACTER VARYING,

    CONSTRAINT hydrant_visite_pkey PRIMARY KEY (id_hydrant_visite)
);


CREATE TABLE incoming.hydrant_visite_anomalie(
    id_hydrant_visite uuid REFERENCES incoming.hydrant_visite(id_hydrant_visite),
    id_anomalie bigint REFERENCES remocra.type_hydrant_anomalie(id),

    CONSTRAINT hydrant_anomalie_pkey PRIMARY KEY (id_hydrant_visite, id_anomalie)
);

CREATE TABLE incoming.tournee(
    id_tournee_remocra bigint REFERENCES remocra.tournee(id),
    nom_tournee CHARACTER VARYING,

    CONSTRAINT tournee_pkey PRIMARY KEY (id_tournee_remocra)
);



CREATE TABLE incoming.gestionnaire(
    id_gestionnaire uuid NOT NULL,
    id_gestionnaire_remocra bigint,
    nom_gestionnaire CHARACTER VARYING,
    code_gestionnaire CHARACTER VARYING,

    CONSTRAINT gestionnaire_pkey PRIMARY KEY (id_gestionnaire)
);

CREATE TABLE incoming.contact(
    id_contact uuid NOT NULL,
    id_contact_remocra bigint,
    id_gestionnaire uuid REFERENCES incoming.gestionnaire(id_gestionnaire),
    fonction_contact CHARACTER VARYING,
    civilite_contact CHARACTER VARYING,
    nom_contact CHARACTER VARYING,
    prenom_contact CHARACTER VARYING,
    numero_voie_contact CHARACTER VARYING,
    suffixe_voie_contact CHARACTER VARYING,
    voie_contact CHARACTER VARYING,
    lieu_dit_contact CHARACTER VARYING,
    code_postal_contact CHARACTER VARYING,
    ville_contact CHARACTER VARYING,
    pays_contact CHARACTER VARYING,
    telephone_contact CHARACTER VARYING,
    email_contact CHARACTER VARYING,

    CONSTRAINT contact_pkey PRIMARY KEY (id_contact)
);

CREATE TABLE incoming.contact_role(
    id_role bigint REFERENCES remocra.role(id),
    id_contact uuid REFERENCES incoming.contact(id_contact),

    CONSTRAINT contact_role_pkey PRIMARY KEY (id_role, id_contact)
);

CREATE TABLE incoming.new_hydrant(
    id_new_hydrant uuid NOT NULL,
    geometrie Geometry,
    code_new_hydrant CHARACTER VARYING,
    id_commune bigint REFERENCES remocra.commune(id),
    voie_new_hydrant CHARACTER VARYING,
    id_gestionnaire UUID REFERENCES incoming.gestionnaire(id_gestionnaire),
    id_nature bigint,
    id_natureDeci bigint,
    observation_new_hydrant CHARACTER VARYING,

    CONSTRAINT new_hydrant_pkey PRIMARY KEY (id_new_hydrant)
);

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
