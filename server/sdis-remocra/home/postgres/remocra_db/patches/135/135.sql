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
    numero_patch := 135;
    description_patch :='Visualisation des courriers';

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

INSERT INTO remocra.type_droit(code, description, nom, categorie, version) VALUES
('COURRIER_UTILISATEUR_R', 'Consulter ses courriers', 'courrier_utilisateur_R', 'Général', 1),
('COURRIER_ORGANISME_R', 'Consulter les courriers de son organisme', 'courrier_organisme_R', 'Général', 1),
('COURRIER_ADMIN_R', 'Consulter tous les courriers', 'courrier_admin_R', 'Général', 1);

ALTER TABLE remocra.courrier_document
ALTER COLUMN id_destinataire TYPE bigint
USING id_destinataire::bigint;

ALTER TABLE remocra.courrier_document
ADD COLUMN objet CHARACTER VARYING,
ADD COLUMN reference CHARACTER VARYING,
ADD COLUMN expediteur CHARACTER VARYING NOT NULL DEFAULT 'Application Remocra';

COMMENT ON COLUMN remocra.courrier_document.objet IS 'Objet du courrier';
COMMENT ON COLUMN remocra.courrier_document.reference IS 'Reference du courrier';
COMMENT ON COLUMN remocra.courrier_document.expediteur IS 'Expéditeur du courrier';


-- Contenu réel du patch fin
--------------------------------------------------

commit;
