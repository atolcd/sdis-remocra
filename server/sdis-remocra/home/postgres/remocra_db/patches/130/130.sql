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
    numero_patch := 130;
    description_patch := 'Module de courrier';

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

CREATE TABLE remocra.courrier_parametre
(
  id bigserial NOT NULL, -- Identifiant interne
  nom character varying NOT NULL, -- Nom de la propriété. Sans espace ni caractère spécial
  type_valeur character varying NOT NULL, -- Type de valeur attendue
  obligatoire boolean NOT NULL, -- La saisie d'une valeur pour le paramètre est obligatoire
  source_sql character varying, -- Définition de la requête SQL éventuelle à exécuter sur la base de données REMOCRA pour fournir les informations facilitant ou limitant la saisie de valeurs pour l'utilisateur
  source_sql_valeur character varying, -- Colonne de la requête SQL éventuelle contenant la valeur du paramètre
  source_sql_libelle character varying, -- Colonne de la requête SQL éventuelle contenant le libellé associé à la valeur du paramètre
  formulaire_etiquette character varying NOT NULL, -- Etiquette associée au champ de saisie
  formulaire_type_controle character varying NOT NULL, -- Type de contrôle associé au champ de saisie
  formulaire_num_ordre bigint NOT NULL, -- Position d'affichage du champ de saisie dans le formulaire
  formulaire_valeur_defaut character varying, -- Valeur par défaut proposée dans le champ de saisie
  modele bigint NOT NULL, -- Modèle de courrier associé
  CONSTRAINT courrier_parametre_pkey PRIMARY KEY (id),
  CONSTRAINT courrier_parametre_modele FOREIGN KEY (modele)
    REFERENCES remocra.courrier_modele (id) MATCH SIMPLE
    ON UPDATE CASCADE ON DELETE CASCADE
);

ALTER TABLE remocra.courrier_modele RENAME categorie TO code;
ALTER TABLE remocra.courrier_modele ADD COLUMN message_objet character varying;
ALTER TABLE remocra.courrier_modele ADD COLUMN message_corps character varying;
ALTER TABLE remocra.courrier_modele ADD COLUMN thematique bigint;
ALTER TABLE remocra.courrier_modele ADD CONSTRAINT courrier_modele_thematique_fk FOREIGN KEY (thematique) REFERENCES remocra.thematique (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE remocra.courrier_modele_droit
(
  modele bigint NOT NULL, -- Identifiant du modèle de courrier
  profil_droit bigint NOT NULL, -- Identifiant du profil de droit autorisé
  CONSTRAINT courrier_modele_droit_pkey PRIMARY KEY (modele, profil_droit),
  CONSTRAINT courrier_modele_droit_modele FOREIGN KEY (modele)
      REFERENCES remocra.courrier_modele (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT courrier_modele_droit_profil_droit FOREIGN KEY (profil_droit)
      REFERENCES remocra.profil_droit (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.courrier_modele_droit
  OWNER TO postgres;
COMMENT ON TABLE remocra.courrier_modele_droit
  IS 'Profil de droit autorisé pour générer un courrier';
COMMENT ON COLUMN remocra.courrier_modele_droit.modele IS 'Identifiant du modèle de courrier';
COMMENT ON COLUMN remocra.courrier_modele_droit.profil_droit IS 'Identifiant du profil de droit autorisé';


-- Insertion du droit de création de courrier
INSERT INTO remocra.type_droit(code, description, nom, version, categorie) VALUES ('COURRIER_C', 'Créer des courriers', 'courrier_C', 1, 'Général');

-- Contenu réel du patch fin
--------------------------------------------------

commit;
