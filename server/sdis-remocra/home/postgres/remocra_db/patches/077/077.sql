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
    numero_patch := 77;
    description_patch := 'Simplification des droits (plus de CRUD)';

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
--



-- Création des nouveaux types de droits
alter table remocra.type_droit add column categorie VARCHAR;
insert into remocra.type_droit (code, nom, description, categorie, version) values
  -- Général
  ('UTILISATEUR_FILTER_ALL_R', 'utilisateur.filter.*_R', 'Consulter tous les utilisateurs (administrateur)', 'Général', '1'),
  ('UTILISATEUR_FILTER_ALL_C', 'utilisateur.filter.*_C', 'Administrer tous les utilisateurs (administrateur)', 'Général', '1'),
  ('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_R', 'utilisateur.filter.organisme.utilisateur_R', 'Consulter les utilisateurs de son organisme', 'Général', '1'),
  ('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C', 'utilisateur.filter.organisme.utilisateur_C', 'Administrer les utilisateurs de son organisme', 'Général', '1'),
  ('REFERENTIELS_C', 'referentiels_C', 'Administrer l''application (administrateur)', 'Général', '1'),
  ('TRAITEMENTS_C', 'traitements_C', 'Exécuter des traitements (administrateur)', 'Général', '1'),
  ('DOCUMENTS_R', 'documents_R', 'Consulter les documents (accueil ou thématiques)', 'Général', '1'),
  ('DOCUMENTS_C', 'documents_C', 'Administrer les documents (administrateur)', 'Général', '1'),
  -- Module Alertes / adresses
  ('ADRESSES_C', 'adresses_C', 'Créer des alertes', 'Module Alertes / adresses', '1'),
  ('ALERTES_EXPORT_C', 'alertes.export_C', 'Télécharger nombre d''alertes par utilisateur', 'Module Alertes / adresses', '1'),
  ('DEPOT_DELIB_C', 'depot.delib_C', 'Déposer des délibérations', 'Module Alertes / adresses', '1'),
  -- Module DFCI
  ('DFCI_R', 'dfci_R', 'Consulter la carte DFCI', 'Module DFCI', '1'),
  ('DFCI_EXPORTATLAS_C', 'dfci.exportatlas_C', 'Télécharger l''Atlas DFCI', 'Module DFCI', '1'),
  ('DEPOT_RECEPTRAVAUX_C', 'depot.receptravaux_C', 'Déposer des dossiers de réception de travaux', 'Module DFCI', '1'),
  -- Module PEI (soumis au territoire de compétences)
  ('HYDRANTS_R', 'hydrants_R', 'Consulter les PEI', 'Module PEI', '1'),
  ('HYDRANTS_C', 'hydrants_C', 'Créer, ouvrir la fiche d''un PEI, Supprimer des documents rattachés à un PEI', 'Module PEI', '1'),
  ('HYDRANTS_D', 'hydrants_D', 'Supprimer des PEI', 'Module PEI', '1'),
  ('HYDRANTS_DEPLACEMENT_C', 'hydrants.deplacement_C', 'Déplacer des PEI', 'Module PEI', '1'),
  ('HYDRANTS_RECEPTION_C', 'hydrants.reception_C', 'Réaliser des réceptions', 'Module PEI', '1'),
  ('HYDRANTS_RECONNAISSANCE_C', 'hydrants.reconnaissance_C', 'Réaliser des reconnaissances opérationnelles', 'Module PEI', '1'),
  ('HYDRANTS_CONTROLE_C', 'hydrants.controle_C', 'Réaliser des contrôles', 'Module PEI', '1'),
  ('HYDRANTS_VERIFICATION_C', 'hydrants.verification_C', 'Réaliser des vérifications', 'Module PEI', '1'),
  ('HYDRANTS_NUMEROTATION_R', 'hydrants.numerotation_R', 'Consulter le "numéro interne" d''un PEI', 'Module PEI', '1'),
  ('HYDRANTS_NUMEROTATION_C', 'hydrants.numerotation_C', 'Saisir le "numéro interne" d''un PEI', 'Module PEI', '1'),
  ('HYDRANTS_MCO_C', 'hydrants.mco_C', 'Accéder à l''onglet "Gestionnaire, MCO, Divers"', 'Module PEI', '1'),
  ('INDISPOS_R', 'indisponibilite.temporaire_R', 'Consulter les indisponibilités temporaires (soumis au territoire, organisé par commune)', 'Module PEI', '1'),
  ('INDISPOS_C', 'indisponibilite.temporaire_C', 'Créer des indisponibilités temporaires (soumis au territoire, organisé par commune)', 'Module PEI', '1'),
  ('INDISPOS_U', 'indisponibilite.temporaire_U', 'Activer / lever des indisponibilités temporaires (soumis au territoire, organisé par commune)', 'Module PEI', '1'),
  ('INDISPOS_D', 'indisponibilite.temporaire_D', 'Supprimer des indisponibilités temporaires (soumis au territoire, organisé par commune)', 'Module PEI', '1'),
  ('TOURNEE_R', 'tournee_R', 'Consulter les tournées SDIS', 'Module PEI', '1'),
  ('TOURNEE_C', 'tournee_C', 'Créer, éditer, supprimer des tournées SDIS', 'Module PEI', '1'),
  ('TOURNEE_RESERVATION_D', 'tournee.reservation_D', 'Supprimer la réservation d''une tournée SDIS', 'Module PEI', '1'),
  ('HYDRANTS_EXPORT_NON_NUM_C', 'hydrants.exportnonnum_C', 'Télécharger la liste des PEI non numérotés', 'Module PEI', '1'),
  ('HYDRANTS_TRAITEMENT_C', 'hydrants.traitement_C', 'Exécuter des traitements en lien avec la thématique PEI', 'Module PEI', '1'),
  ('DEPOT_DECLAHYDRANT_C', 'depot.declahydrant_C', 'Déposer un dossier de déclaration de PEI', 'Module PEI', '1'),
  ('HYDRANTS_PRESCRIT_R', 'hydrants.prescrit_R', 'Consulter les points d''eau prescrits', 'Module PEI', '1'),
  ('HYDRANTS_PRESCRIT_C', 'hydrants.prescrit_C', 'Editer les points d''eau prescrits', 'Module PEI', '1'),
  -- Module Permis
  ('PERMIS_R', 'permis_r', 'Consulter les permis (carte et recherches)', 'Module Permis', '1'),
  ('PERMIS_C', 'permis_c', 'Editer les permis', 'Module Permis', '1'),
  ('PERMIS_DOCUMENTS_C', 'permis.documents_C', 'Déposer des documents rattachés à un permis', 'Module Permis', '1'),
  ('PERMIS_TRAITEMENTS_C', 'permis.traitements_C', 'Exécuter des traitements en lien avec la thématique Permis', 'Module Permis', '1'),
  -- Module RCCI
  ('RCI_C', 'rci_C', 'Editer les RCCI. Exécuter des traitements en lien avec la thématique RCCI', 'Module RCCI', '1'),
  -- Module Risques
  ('RISQUES_KML_R', 'risques.kml_R', 'Consulter la couche des "risques Express"', 'Module Risques', '1'),
  ('RISQUES_KML_C', 'risques.kml C', 'Purger la couche des "risques Express"', 'Module Risques', '1'),
  -- Module Débroussaillement
  ('OLDEB_R', 'obligation.debroussaillement_R', 'Consulter les OLD. Exporter la fiche d''une OLD', 'Module Débroussaillement', '1'),
  ('OLDEB_C', 'obligation.debroussaillement_C', 'Créer des OLD', 'Module Débroussaillement', '1'),
  ('OLDEB_U', 'obligation.debroussaillement_U', 'Mettre à jour des OLD', 'Module Débroussaillement', '1'),
  ('OLDEB_D', 'obligation.debroussaillement_D', 'Supprimer des OLD / des documents d''une OLD', 'Module Débroussaillement', '1'),
  -- Module Cartographie
  ('CARTOGRAPHIES_C', 'cartographies_C', 'Accéder au module cartographie', 'Module Cartographie', '1')
;


-- Reprise des droits (anciens types -> nouveaux types)
-- Général
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='UTILISATEUR_FILTER_ALL_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='UTILISATEUR_FILTER_ALL' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='UTILISATEUR_FILTER_ALL_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='UTILISATEUR_FILTER_ALL' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='UTILISATEUR_FILTER_ORGANISME_UTILISATEUR' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='UTILISATEUR_FILTER_ORGANISME_UTILISATEUR' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='REFERENTIELS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='REFERENTIELS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='TRAITEMENTS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='TRAITEMENTS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DOCUMENTS_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DOCUMENTS' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DOCUMENTS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DOCUMENTS' and d.droit_create;
-- Module Alertes / adresses
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='ADRESSES_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='ADRESSES' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='ALERTES_EXPORT_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='ALERTES_EXPORT' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DEPOT_DELIB_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DEPOT_DELIB' and d.droit_create;
-- Module DFCI
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DFCI_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DFCI' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DFCI_EXPORTATLAS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DFCI_EXPORTATLAS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DEPOT_RECEPTRAVAUX_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DEPOT_RECEPTRAVAUX' and d.droit_create;
-- Module PEI (soumis au territoire de compétences)
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_D'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS' and d.droit_delete;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_DEPLACEMENT_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_DEPLACEMENT' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_RECEPTION_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_RECEPTION' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_RECONNAISSANCE_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_RECONNAISSANCE' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_CONTROLE_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_CONTROLE' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_VERIFICATION_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_VERIFICATION' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_NUMEROTATION_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_NUMEROTATION' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_NUMEROTATION_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_NUMEROTATION' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_MCO_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_MCO' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='INDISPOS_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='INDISPOS' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='INDISPOS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='INDISPOS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='INDISPOS_U'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='INDISPOS' and d.droit_update;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='INDISPOS_D'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='INDISPOS' and d.droit_delete;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='TOURNEE_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='TOURNEE' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='TOURNEE_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='TOURNEE' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='TOURNEE_RESERVATION_D'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='TOURNEE_RESERVATION' and d.droit_delete;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_EXPORT_NON_NUM_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_EXPORT_NON_NUM' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_TRAITEMENT_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_TRAITEMENT' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='DEPOT_DECLAHYDRANT_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='DEPOT_DECLAHYDRANT' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_PRESCRIT_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_PRESCRIT' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='HYDRANTS_PRESCRIT_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='HYDRANTS_PRESCRIT' and d.droit_create;
-- Module Permis
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='PERMIS_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='PERMIS' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='PERMIS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='PERMIS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='PERMIS_DOCUMENTS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='PERMIS_DOCUMENTS' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='PERMIS_TRAITEMENTS_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='PERMIS_TRAITEMENTS' and d.droit_create;
-- Module RCCI
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='RCI_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='RCI' and d.droit_create;
-- Module Risques
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='RISQUES_KML_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='RISQUES_KML' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='RISQUES_KML_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='RISQUES_KML' and d.droit_create;
-- Module Débroussaillement
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='OLDEB_R'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='OLDEB' and d.droit_read;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='OLDEB_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='OLDEB' and d.droit_create;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='OLDEB_U'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='OLDEB' and d.droit_update;
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='OLDEB_D'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='OLDEB' and d.droit_delete;
-- Module Cartographie
insert into remocra.droit(type_droit, profil_droit) select
  (select id from remocra.type_droit where code='CARTOGRAPHIES_C'),
  pd.id from remocra.droit d join remocra.type_droit td on (td.id=d.type_droit) join remocra.profil_droit pd on (pd.id=d.profil_droit) where td.code='CARTOGRAPHIES' and d.droit_create;


-- Suppression des anciens droits et types de droits
delete from remocra.droit where type_droit in (select id from remocra.type_droit where categorie is null);
delete from remocra.type_droit where categorie is null;


-- Retrait des colonnes inutiles (la présence de la ligne active le droit pour un profil)
alter table remocra.droit drop column droit_create;
alter table remocra.droit drop column droit_read;
alter table remocra.droit drop column droit_update;
alter table remocra.droit drop column droit_delete;



-- Contenu réel du patch fin
--------------------------------------------------

commit;

