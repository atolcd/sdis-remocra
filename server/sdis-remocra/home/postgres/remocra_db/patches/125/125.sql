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
    numero_patch := 125;
    description_patch := 'Mise en place des débits simultanés';

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

DROP TABLE IF EXISTS remocra.debit_simultane CASCADE;
CREATE TABLE remocra.debit_simultane
(
  id BIGSERIAL NOT NULL,
  site BIGINT NOT NULL,
  geometrie geometry,
  numdossier CHARACTER VARYING NOT NULL,
  CONSTRAINT debit_simultane_pkey PRIMARY KEY (id),
  CONSTRAINT fk_site FOREIGN KEY (site)
  REFERENCES remocra.site (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

DROP TABLE IF EXISTS remocra.debit_simultane_mesure CASCADE;
CREATE TABLE remocra.debit_simultane_mesure
(
  id BIGSERIAL NOT NULL,
  debit_simultane BIGINT,
  debit_requis INTEGER,
  debit_mesure INTEGER,
  date_mesure timestamp without time zone DEFAULT now(),
  debit_retenu INTEGER,
  irv BOOLEAN DEFAULT FALSE,
  commentaire CHARACTER VARYING,
  CONSTRAINT debit_simultane_mesure_pkey PRIMARY KEY (id),
  CONSTRAINT fk_debit_simultane FOREIGN KEY (debit_simultane)
  REFERENCES remocra.debit_simultane (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE
);

DROP TABLE IF EXISTS remocra.debit_simultane_document CASCADE;
CREATE TABLE remocra.debit_simultane_document
(
  id BIGSERIAL NOT NULL,
  document BIGINT NOT NULL,
  debit_simultane_mesure BIGINT NOT NULL,
  CONSTRAINT debit_simultane_document_pkey PRIMARY KEY (id),
  CONSTRAINT fk_document FOREIGN KEY (document)
  REFERENCES remocra.document (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_debit_simultane_mesure FOREIGN KEY (debit_simultane_mesure)
  REFERENCES remocra.debit_simultane_mesure (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE
);

DROP TABLE IF EXISTS remocra.debit_simultane_hydrant CASCADE;
CREATE TABLE remocra.debit_simultane_hydrant
(
  id BIGSERIAL NOT NULL,
  debit BIGINT NOT NULL,
  hydrant BIGINT NOT NULL,
  CONSTRAINT debit_simultane_hydrant_pkey PRIMARY KEY (id),
  CONSTRAINT fk_debit FOREIGN KEY (debit)
  REFERENCES remocra.debit_simultane_mesure (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_hdyrant FOREIGN KEY (hydrant)
  REFERENCES remocra.hydrant (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE remocra.site
ADD COLUMN geometrie geometry;

CREATE INDEX debit_simultane_site_idx ON remocra.debit_simultane USING btree (site);
CREATE INDEX debit_simultane_mesure_debit_simultane_idx ON remocra.debit_simultane_mesure USING btree (debit_simultane);
CREATE INDEX debit_simultane_document_debit_simultane_mesure_idx ON remocra.debit_simultane_document USING btree (debit_simultane_mesure);
CREATE INDEX debit_simultane_document_document_idx ON remocra.debit_simultane_document USING btree (document);
CREATE INDEX debit_simultane_hydrant_debit_simultane_mesure_idx ON remocra.debit_simultane_hydrant USING btree (debit);
CREATE INDEX debit_simultane_hydrant_hydrant_idx ON remocra.debit_simultane_hydrant USING btree (hydrant);

SELECT pg_catalog.setval('remocra.type_droit_id_seq', (select max(id) from remocra.type_droit));
INSERT INTO remocra.type_droit (code, description, nom, version, categorie) VALUES
('DEBITS_SIMULTANES_R', 'Consulter les débits simultanés', 'debits_simultanes_R', 1, 'Module PEI'),
('DEBITS_SIMULTANES_C', 'Créer, éditer les débits simultanés', 'debits_simultanes_C', 1, 'Module PEI');

INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES
('VITESSE_EAU', 'Vitesse nominale retenue d''un réseau d''eau', '2', 1, 'Points d''eau');

INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES
('DOSSIER_DEPOT_ATTESTATION', 'Emplacement du dossier de stockage des attestations de mesure des débits simultanés', '/var/remocra/attestations', 1, 'Chemins sur disque');

-- Contenu réel du patch fin
--------------------------------------------------

commit;
