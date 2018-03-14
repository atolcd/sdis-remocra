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
    numero_patch := 80;
    description_patch := 'Ajout du modèle de traitement PEI non disponible';

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


--Vue des modèles de courrier des hydrants indispo
CREATE OR REPLACE VIEW pdi.vue_modele_courrier_hydrants_non_dispo AS 
 SELECT courrier_modele.id, courrier_modele.libelle
   FROM remocra.courrier_modele
  WHERE courrier_modele.categorie::text = 'COURRIER_PEI_NON_DISPONIBLE'::text
  ORDER BY courrier_modele.libelle;

ALTER TABLE pdi.vue_modele_courrier_hydrants_non_dispo
  OWNER TO postgres;


-- Traitement de génération du tableau des hydrants non réceptionnés
INSERT INTO pdi.modele_traitement(
            idmodele, code, description, nom, ref_chemin, ref_nom, type, 
            message_echec, message_succes)
    VALUES ((SELECT MAX(idmodele)+1 FROM pdi.modele_traitement), 1, 'Génère un courrier avec la liste des PEI non disponibles depuis les dernières 24h et la liste des PEI non disponibles', 'Mise en indisponibilité d''un PEI - courrier d''information', '/demandes/generation_courriers/pei_non_disponible', 'gener_et_notifier_hydrants_indispo', 'J', 
            3, 1);


--Paramètre du traitement de génération du tableau des hydrants non réceptionnés
INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Organisme', 1, true, 'vue_organisme',
            'combo', null, 'ORGANISME_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));

INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Modèle de courrier', 2, true, 'vue_modele_courrier_hydrants_non_dispo',
            'combo', null, 'MODELE_COURRIER_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
			
INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Profil d''utilisateur responsable', 3, true, 'vue_profil_sdis',
            'combo', null, 'PROFIL_UTILISATEUR_ID', (SELECT MAX(idmodele) FROM pdi.modele_traitement));
			
INSERT INTO pdi.modele_traitement_parametre(
            idparametre, form_etiquette, form_num_ordre, form_obligatoire,
            form_source_donnee, form_type_valeur, form_valeur_defaut, nom,
            idmodele)
    VALUES ((SELECT MAX(idparametre)+1 FROM pdi.modele_traitement_parametre), 'Notifier par mél la mairie ou l''intercommunalité', 4, true, '',
            'checkbox', null, 'NOTIFIER_COLLECTIVITE', (SELECT MAX(idmodele) FROM pdi.modele_traitement));


--Table de stockage des délégations DECI			
ALTER TABLE remocra.organisme ADD CONSTRAINT organisme_code_key UNIQUE(code);

CREATE TABLE remocra.delegation_deci(
organisme_commune character varying NOT NULL,
organisme_epci character varying NOT NULL,
CONSTRAINT delegation_deci_pkey PRIMARY KEY (organisme_commune,organisme_epci),
CONSTRAINT organisme_commune_key UNIQUE (organisme_commune),
CONSTRAINT delegation_deci_commune_fk FOREIGN KEY (organisme_commune) REFERENCES remocra.organisme (code) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT delegation_deci_epci_fk FOREIGN KEY (organisme_epci) REFERENCES remocra.organisme (code) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
COMMENT ON TABLE remocra.delegation_deci IS 'Organisme REMOCRA de type "COMMUNE" délégant la compétence DECI à un organisme de type "EPCI"';
COMMENT ON COLUMN remocra.delegation_deci.organisme_commune IS 'Code de l''organisme REMOCRA de type "COMMUNE" qui délègue la compétence DECI';
COMMENT ON COLUMN remocra.delegation_deci.organisme_epci IS 'Code de l''organisme REMOCRA de type "EPCI" qui bénéficie de la délégation de la compétence DECI';			

-- Contenu réel du patch fin
--------------------------------------------------

commit;

