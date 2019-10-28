SET search_path = remocra, tracabilite, public, pg_catalog;

BEGIN;


/*----------------------------------------------------------------------------------------
 * Script de mise en place des zones spéciales Partie 1 :
 *   
 *   - Modification du schéma (table zone_speciale, hydrant.zone_speciale et contrainte de numérotation)
 *   - Et ajout des autres index spatiaux non créés jusqu'à présent (alerte, alerte_elt, commune, permis)
 *
 *----------------------------------------------------------------------------------------*/



-- Table: remocra.zone_speciale

-- DROP TABLE remocra.zone_speciale;

CREATE TABLE remocra.zone_speciale
(
  id bigserial NOT NULL,
  code character varying NOT NULL,
  geometrie geometry NOT NULL,
  nom character varying NOT NULL,
  CONSTRAINT zone_speciale_pkey PRIMARY KEY (id),
  CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(geometrie) = 2),
  CONSTRAINT enforce_geotype_geometrie CHECK (geometrytype(geometrie) = 'POLYGON'::text OR geometrytype(geometrie) = 'MULTIPOLYGON'::text),
  CONSTRAINT enforce_srid_geometrie CHECK (st_srid(geometrie) = 2154)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.zone_speciale
  OWNER TO postgres;

-- Index: remocra.zone_speciale_geometrie_idx

-- DROP INDEX remocra.zone_speciale_geometrie_idx;

CREATE INDEX zone_speciale_geometrie_idx
  ON remocra.zone_speciale
  USING gist
  (geometrie);


-- --------------------------------------------------
-- Ajout des autres index spatiaux non créés jusqu'à présent
-- --------------------------------------------------

-- Index spatiaux
CREATE INDEX alerte_geometrie_idx
  ON remocra.alerte
  USING gist
  (geometrie);

CREATE INDEX alerte_elt_geometrie_idx
  ON remocra.alerte_elt
  USING gist
  (geometrie);

CREATE INDEX commune_geometrie_idx
  ON remocra.commune
  USING gist
  (geometrie);

-- hydrant_geometrie_idx

CREATE INDEX permis_geometrie_idx
  ON remocra.permis
  USING gist
  (geometrie);

-- voie_geometrie_idx
-- zone_competence_geometrie_idx

-- --------------------------------------------------------------------
-- Ajout de la colonne zone_speciale à la table hydrant
-- --------------------------------------------------------------------
ALTER TABLE remocra.hydrant
 ADD COLUMN zone_speciale bigint;

ALTER TABLE remocra.hydrant
 ADD CONSTRAINT fk_zone_speciale FOREIGN KEY (zone_speciale)
    REFERENCES remocra.zone_speciale (id);

-- --------------------------------------------------------------------
-- Suppresion Contrainte UNIQUE SUR LA TABLE HYDRANT
-- --------------------------------------------------------------------
ALTER TABLE remocra.hydrant DROP constraint hydrant_code_numero_commune_key;

-- --------------------------------------------------------------------
-- Ajout de la contrainte : numero de l'hydrant unique
-- --------------------------------------------------------------------
ALTER TABLE remocra.hydrant ADD constraint hydrant_numero_key UNIQUE (numero);


COMMIT;

