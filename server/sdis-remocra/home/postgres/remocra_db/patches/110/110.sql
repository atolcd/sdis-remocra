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
    numero_patch := 110;
    description_patch := 'Gestion des interventions ';

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


CREATE TABLE remocra.type_moyen
(
  id bigserial NOT NULL,
  actif boolean DEFAULT true,
  code character varying NOT NULL,
  nom character varying NOT NULL,
  CONSTRAINT type_moyen_pkey PRIMARY KEY (id),
  CONSTRAINT type_moyen_code_key UNIQUE (code),
  CONSTRAINT type_moyen_nom_key UNIQUE (nom)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.type_moyen
  OWNER TO postgres;
COMMENT ON TABLE remocra.type_moyen
  IS 'Type de moyen déployé, lors d''une intervention, pour filtre dans REMOcRA';
COMMENT ON COLUMN remocra.type_moyen.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.type_moyen.actif IS 'Sélectionnable dans l''interface';
COMMENT ON COLUMN remocra.type_moyen.code IS 'Code du moyen. Facilite les échanges de données';
COMMENT ON COLUMN remocra.type_moyen.nom IS 'Libellé du type de moyen';


INSERT INTO remocra.type_moyen (code, nom) VALUES
  ('ENGIN', 'Engin'), ('HUMAIN', 'Humain');

CREATE TABLE remocra.intervention
(
  id bigserial NOT NULL,
  code character varying,
  code_type character varying,
  libelle_type character varying,
  priorite integer,
  date_creation timestamp without time zone,
  date_modification timestamp without time zone,
  date_cloture timestamp without time zone,
  cloture character varying,
  num_voie character varying,
  voie character varying,
  commune bigint,
  geometrie geometry,
  CONSTRAINT intervention_pkey PRIMARY KEY (id),
  CONSTRAINT fk_intervention_commune FOREIGN KEY (commune)
    REFERENCES remocra.commune (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
  CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POINT'::text),
  CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);

COMMENT ON TABLE remocra.intervention
  IS 'Table de synchronisation des interventions pour utilisation dans REMOcRA';
COMMENT ON COLUMN remocra.intervention.id IS 'Identifiant autogénéré';
COMMENT ON COLUMN remocra.intervention.code IS 'Code unique de l''intervention';
COMMENT ON COLUMN remocra.intervention.code_type IS 'Code du type de l''intervention';
COMMENT ON COLUMN remocra.intervention.libelle_type IS 'Libellé du type de l''intervention';
COMMENT ON COLUMN remocra.intervention.priorite IS 'Priorité de l''intervention';
COMMENT ON COLUMN remocra.intervention.date_creation IS 'Date de création de l''intervention';
COMMENT ON COLUMN remocra.intervention.date_modification IS 'Date de modification de l''intervention';
COMMENT ON COLUMN remocra.intervention.date_cloture IS 'Date de clotûre de l''intervention';
COMMENT ON COLUMN remocra.intervention.num_voie IS 'Numéro de la voie du lieu de l''intervention';
COMMENT ON COLUMN remocra.intervention.voie IS 'Nom de la voie du lieu de l''intervention';
COMMENT ON COLUMN remocra.intervention.commune IS 'Commune de l''intervention';
COMMENT ON COLUMN remocra.intervention.geometrie IS 'Géométrie de l''intervention';

CREATE INDEX intervention_geometrie_idx
  ON remocra.intervention
  USING gist
  (geometrie);

CREATE TABLE remocra.moyen
(
  id bigserial NOT NULL,
  type bigint,
  nom character varying,
  intervention bigint,
  geometrie geometry,
  CONSTRAINT engin_pkey PRIMARY KEY (id),
  CONSTRAINT fk_moyen_type_moyen FOREIGN KEY (type)
    REFERENCES remocra.type_moyen (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_moyen_intervention FOREIGN KEY (intervention)
    REFERENCES remocra.intervention (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
  CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POINT'::text),
  CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
);

COMMENT ON TABLE remocra.moyen
  IS 'Table de synchronisation des moyens pour utilisation dans REMOcRA en lien avec les interventions';
COMMENT ON COLUMN remocra.moyen.id IS 'Identifiant autogénéré';
COMMENT ON COLUMN remocra.moyen.nom IS 'Nom du moyen';
COMMENT ON COLUMN remocra.moyen.intervention IS 'Intervention à laquelle est rattachée le moyen';
COMMENT ON COLUMN remocra.moyen.geometrie IS 'Dernier emplacement connu du moyen';

CREATE INDEX moyen_geometrie_idx
  ON remocra.moyen
  USING gist
      (geometrie);

CREATE TABLE remocra.crise_intervention
(
  crise bigint,
  intervention bigint,
  CONSTRAINT fk_crise_intervention_crise FOREIGN KEY (crise)
    REFERENCES remocra.crise (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_crise_intervention_intervention FOREIGN KEY (intervention)
    REFERENCES remocra.intervention (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

COMMENT ON TABLE remocra.crise_intervention
  IS 'Table de liaison entre les crises et les interventions';
COMMENT ON COLUMN remocra.crise_intervention.crise IS 'Identifiant de la crise';
COMMENT ON COLUMN remocra.crise_intervention.intervention IS 'Identifiant de l''intervention';


CREATE TABLE remocra.crise_evenement_intervention
(
  crise_evenement bigint,
  intervention bigint,
  CONSTRAINT fk_crise_evenement_intervention_evenement FOREIGN KEY (crise_evenement)
    REFERENCES remocra.crise_evenement (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_crise_intervention_intervention FOREIGN KEY (intervention)
    REFERENCES remocra.intervention (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

COMMENT ON TABLE remocra.crise_evenement_intervention
  IS 'Table de liaison entre des évènement d''une crises et des interventions';
COMMENT ON COLUMN remocra.crise_evenement_intervention.crise_evenement IS 'Identifiant de l''evenement de la crise';
COMMENT ON COLUMN remocra.crise_evenement_intervention.intervention IS 'Identifiant de l''intervention';

CREATE TABLE remocra.crise_indicateur
(
  id bigserial NOT NULL,
  libelle character varying NOT NULL,
  code character varying NOT NULL,
  source_sql character varying NOT NULL,
  CONSTRAINT crise_indicateur_pkey PRIMARY KEY (id),
  CONSTRAINT crise_indicateur_code_key UNIQUE (code)
);

COMMENT ON TABLE remocra.crise_indicateur
  IS 'Table de stockage de la requête pour récupérer les données des indicateurs d''une crise';
COMMENT ON COLUMN remocra.crise_indicateur.id IS 'Identifiant interne';
COMMENT ON COLUMN remocra.crise_indicateur.libelle IS 'Libellé de la requête pour description';
COMMENT ON COLUMN remocra.crise_indicateur.code IS 'Code unique de la requête';
COMMENT ON COLUMN remocra.crise_indicateur.source_sql IS 'Requête de sélection des données pour les indicateurs';

INSERT INTO remocra.crise_indicateur(libelle, code, source_sql) VALUES ('Indicateurs de crise', 'CRISE_INDICATEUR', 'SELECT
    cast(xmlelement(name "indicateurs",xmlagg(xml_groupe)) AS text)
FROM
(
SELECT
    xmlelement(name "groupe",
        xmlelement(name "nom","Moyens"),
        XMLAGG(xml_indicateur)
    ) AS xml_groupe
FROM
(SELECT
    xmlelement(name "indicateur",
            xmlelement(name clef,"Indicateur" || i),
            xmlelement(name valeur,i*i)
    ) AS xml_indicateur

FROM
    generate_series(1,10,1) AS i) AS groupe_1
UNION ALL
SELECT
    xmlelement(name "groupe",
        xmlelement(name "nom","Interventions"),
        XMLAGG(xml_indicateur)
    ) AS xml_groupe
FROM

(SELECT
    xmlelement(name "indicateur",
            xmlelement(name clef,"Indicateur" || i),
            xmlelement(name valeur,i*i)
    ) AS xml_indicateur

FROM
    generate_series(1,5,1) AS i) AS groupe_2
) as total');
 
-- Contenu réel du patch fin
--------------------------------------------------

commit;

