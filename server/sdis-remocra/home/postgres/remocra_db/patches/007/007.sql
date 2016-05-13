SET search_path = remocra, pdi, public, pg_catalog;

BEGIN;




-- Droits toulon (selon le jeu de données) sur les référentiels
update remocra.droit set droit_create = true, droit_read = true, droit_update = true, droit_delete = false where
profil_droit = 2 and type_droit = (SELECT id from remocra.type_droit where code = 'REFERENTIELS');

update remocra.droit set droit_create = false, droit_read = true, droit_update = false, droit_delete = false where
profil_droit = 2 and type_droit = (SELECT id from remocra.type_droit where code = 'UTILISATEUR_FILTER_ALL');

insert into remocra.droit (droit_create, droit_read, droit_update, droit_delete, profil_droit, type_droit) values (
false, true, false, false, 2, (SELECT id from remocra.type_droit where code = 'UTILISATEUR_FILTER_ALL')
);


-- Type organisme
ALTER TABLE remocra.type_organisme add COLUMN actif boolean NOT NULL DEFAULT true;
ALTER TABLE remocra.type_organisme ALTER COLUMN nom set NOT NULL;
ALTER TABLE remocra.type_organisme ALTER COLUMN code set NOT NULL;

-- Profil utilisateur
ALTER TABLE remocra.profil_utilisateur drop COLUMN version;
ALTER TABLE remocra.profil_utilisateur rename COLUMN libelle to nom;
ALTER TABLE remocra.profil_utilisateur add COLUMN actif boolean NOT NULL DEFAULT true;
ALTER TABLE remocra.profil_utilisateur add COLUMN code character varying;
UPDATE remocra.profil_utilisateur p1 set code = (upper(regexp_replace(nom, '[^a-zA-Z0-9]', '', 'g')));
ALTER TABLE remocra.profil_utilisateur alter COLUMN code set NOT NULL;

-- Profil organisme
ALTER TABLE remocra.profil_organisme drop COLUMN version;
ALTER TABLE remocra.profil_organisme rename COLUMN libelle to nom;
ALTER TABLE remocra.profil_organisme ALTER COLUMN nom set NOT NULL;
ALTER TABLE remocra.profil_organisme add COLUMN actif boolean NOT NULL DEFAULT true;
ALTER TABLE remocra.profil_organisme add COLUMN code character varying;
UPDATE remocra.profil_organisme p1 set code = (upper(regexp_replace(nom, '[^a-zA-Z0-9]', '', 'g')));
ALTER TABLE remocra.profil_organisme alter COLUMN code set NOT NULL;

-- Profil droit
ALTER TABLE remocra.profil_droit rename COLUMN libelle to nom;
ALTER TABLE remocra.profil_droit add COLUMN code character varying;
UPDATE remocra.profil_droit p1 set code = (upper(regexp_replace(nom, '[^a-zA-Z0-9]', '', 'g')));
ALTER TABLE remocra.profil_droit alter COLUMN code set NOT NULL;
ALTER TABLE remocra.profil_droit ADD CONSTRAINT profil_droit_code_key UNIQUE (code);

-- Droit : Contrainte unicité profil_droit / type_droit
ALTER TABLE remocra.droit add CONSTRAINT droit_profil_droit_type_droit UNIQUE (profil_droit, type_droit);




-- Table nécessaire à la synchronisation
CREATE FUNCTION synchrosinexistepas415345() RETURNS void AS $$
BEGIN

IF NOT EXISTS (
    SELECT *
    FROM   pg_catalog.pg_tables 
    WHERE  schemaname = 'remocra'
    AND    tablename  = 'synchronisation'
    ) THEN

CREATE TABLE remocra.synchronisation
(
  id bigserial NOT NULL,
  date_synchro timestamp without time zone NOT NULL,
  succes boolean NOT NULL,
  version integer,
  thematique bigint NOT NULL,
  CONSTRAINT synchronisation_pkey PRIMARY KEY (id),
  CONSTRAINT fk43a80607d27676e2 FOREIGN KEY (thematique)
      REFERENCES remocra.thematique (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.synchronisation
  OWNER TO postgres;
 
END IF;
END;
$$ LANGUAGE plpgsql;

select synchrosinexistepas415345();
DROP FUNCTION synchrosinexistepas415345();





-- ProfilOrganismeUtilisateurDroit : Contrainte unicité profil_organisme / profil_utilisateur
ALTER TABLE remocra.profil_organisme_utilisateur_droit add CONSTRAINT profil_organisme_utilisateur_droit_profil_organisme_profil_util UNIQUE (profil_organisme, profil_utilisateur);
ALTER TABLE remocra.profil_organisme_utilisateur_droit drop COLUMN type_organisme;


-- Renommage document.date
ALTER TABLE remocra.document rename COLUMN date to date_doc;






CREATE TABLE remocra.bloc_document
(
  id bigserial NOT NULL,
  description character varying,
  titre character varying,
  document bigint NOT NULL,
  CONSTRAINT bloc_document_pkey PRIMARY KEY (id),
  CONSTRAINT fkcd6c849c36f0130a FOREIGN KEY (document)
      REFERENCES remocra.document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.bloc_document
  OWNER TO postgres;

CREATE TABLE remocra.bloc_document_profil_droits
(
  bloc_document bigint NOT NULL,
  profil_droits bigint NOT NULL,
  CONSTRAINT bloc_document_profil_droits_pkey PRIMARY KEY (bloc_document, profil_droits),
  CONSTRAINT fk36b2bc674995249 FOREIGN KEY (bloc_document)
      REFERENCES remocra.bloc_document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk36b2bc6798a29b26 FOREIGN KEY (profil_droits)
      REFERENCES remocra.profil_droit (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.bloc_document_profil_droits
  OWNER TO postgres;

CREATE TABLE remocra.bloc_document_thematiques
(
  bloc_document bigint NOT NULL,
  thematiques bigint NOT NULL,
  CONSTRAINT bloc_document_thematiques_pkey PRIMARY KEY (bloc_document, thematiques),
  CONSTRAINT fk140d14a94995249 FOREIGN KEY (bloc_document)
      REFERENCES remocra.bloc_document (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk140d14a9fdec3d27 FOREIGN KEY (thematiques)
      REFERENCES remocra.thematique (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE remocra.bloc_document_thematiques
  OWNER TO postgres;


INSERT INTO remocra.param_conf(cle, description, valeur, version) VALUES ('DOSSIER_DEPOT_BLOC', 'Emplacement du dossier de stockage des documents des blocs', '/var/remocra/blocs', 1);


-- Droits
INSERT INTO remocra.type_droit(code, nom, description, version) VALUES ('HYDRANTS_BLOCDOCUMENTS', 'hydrants.blocdocuments', 'Accès à la banque de document hydrants', 1);
update remocra.type_droit set code='HYDRANTS', nom='hydrants', description='Droit la thématique hydrants' where code='POINTSEAU';


INSERT INTO remocra.type_droit(code, nom, description, version) VALUES ('DOCUMENTS', 'documents', 'Accès à la banque de document (blocs)', 1);


COMMIT;

