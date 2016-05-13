SET search_path = remocra, tracabilite, pdi, public, pg_catalog;

BEGIN;


/*----------------------------------------------------------------------------------------
 * 
 * Script faisant suite au premier essaimage :
 *
 * - Appel à updateInfoTournee dans trigger trg_hydrant_tournee
 * - Liste des communes du département
 * - Reset des séquences
 * 
 *----------------------------------------------------------------------------------------*/


-- Mise à jour du trigger trg_hydrant_tournee : préfixe remocra
CREATE OR REPLACE FUNCTION remocra.trg_hydrant_tournee()
  RETURNS trigger AS
$BODY$
BEGIN
	if (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') then
		if (OLD.tournee is not null) then
			perform remocra.updateInfoTournee(OLD.tournee);
		end if;
	end if;

	if (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') then
		if (NEW.tournee is not null) then
			perform remocra.updateInfoTournee(NEW.tournee);
		end if;
	end if;
	if (TG_OP = 'DELETE') then
		RETURN OLD;
	else
		return NEW;
	end if;
END;
$BODY$
  LANGUAGE plpgsql;



-- Liste des communes du département
CREATE OR REPLACE VIEW pdi.vue_communes AS 
 SELECT allin.id, allin.libelle
   FROM (         SELECT (-1) AS id, 'Toutes' AS libelle, NULL::unknown AS tricol
        UNION 
                 SELECT commune.id, commune.nom AS libelle, commune.nom AS tricol
                   FROM remocra.commune
                  WHERE commune.insee::text ~~ (select valeur from remocra.param_conf  where cle = 'COMMUNES_INSEE_LIKE_FILTRE_SQL')) allin
  ORDER BY allin.tricol NULLS FIRST;

ALTER TABLE pdi.vue_communes
  OWNER TO postgres;



-- Reset des séquences

-- Schéma pdi
SELECT pg_catalog.setval('pdi.traitement_idtraitement_seq', (select max(idtraitement) from pdi.traitement));

-- Schéma remocra
SELECT pg_catalog.setval('remocra.alerte_document_id_seq', (select max(id) from remocra.alerte_document));
SELECT pg_catalog.setval('remocra.alerte_elt_ano_id_seq', (select max(id) from remocra.alerte_elt_ano));
SELECT pg_catalog.setval('remocra.alerte_elt_id_seq', (select max(id) from remocra.alerte_elt));
SELECT pg_catalog.setval('remocra.alerte_id_seq', (select max(id) from remocra.alerte));
SELECT pg_catalog.setval('remocra.bloc_document_id_seq', (select max(id) from remocra.bloc_document));
SELECT pg_catalog.setval('remocra.commune_id_seq', (select max(id) from remocra.commune));
SELECT pg_catalog.setval('remocra.dde_mdp_id_seq', (select max(id) from remocra.dde_mdp));
SELECT pg_catalog.setval('remocra.depot_document_id_seq', (select max(id) from remocra.depot_document));
SELECT pg_catalog.setval('remocra.document_id_seq', (select max(id) from remocra.document));
SELECT pg_catalog.setval('remocra.droit_id_seq', (select max(id) from remocra.droit));
SELECT pg_catalog.setval('remocra.email_id_seq', (select max(id) from remocra.email));
SELECT pg_catalog.setval('remocra.email_modele_id_seq', (select max(id) from remocra.email_modele));
-- remocra.hibernate_sequence ?
SELECT pg_catalog.setval('remocra.hydrant_document_id_seq', (select max(id) from remocra.hydrant_document));
SELECT pg_catalog.setval('remocra.hydrant_id_seq', (select max(id) from remocra.hydrant));
SELECT pg_catalog.setval('remocra.hydrant_prescrit_id_seq', (select max(id) from remocra.hydrant_prescrit));
SELECT pg_catalog.setval('remocra.metadonnee_id_seq', (select max(id) from remocra.metadonnee));
SELECT pg_catalog.setval('remocra.organisme_id_seq', (select max(id) from remocra.organisme));
SELECT pg_catalog.setval('remocra.permis_document_id_seq', (select max(id) from remocra.permis_document));
SELECT pg_catalog.setval('remocra.permis_id_seq', (select max(id) from remocra.permis));
SELECT pg_catalog.setval('remocra.profil_droit_id_seq', (select max(id) from remocra.profil_droit));
SELECT pg_catalog.setval('remocra.profil_organisme_id_seq', (select max(id) from remocra.profil_organisme));
SELECT pg_catalog.setval('remocra.profil_organisme_utilisateur_droit_id_seq', (select max(id) from remocra.profil_organisme_utilisateur_droit));
SELECT pg_catalog.setval('remocra.profil_utilisateur_id_seq', (select max(id) from remocra.profil_utilisateur));
SELECT pg_catalog.setval('remocra.sous_type_alerte_elt_id_seq', (select max(id) from remocra.sous_type_alerte_elt));
--SELECT pg_catalog.setval('remocra.synchronisation_id_seq', (select max(id) from remocra.synchronisation));
SELECT pg_catalog.setval('remocra.thematique_id_seq', (select max(id) from remocra.thematique));
SELECT pg_catalog.setval('remocra.tournee_id_seq', (select max(id) from remocra.tournee));
SELECT pg_catalog.setval('remocra.type_alerte_ano_id_seq', (select max(id) from remocra.type_alerte_ano));
SELECT pg_catalog.setval('remocra.type_alerte_elt_id_seq', (select max(id) from remocra.type_alerte_elt));
SELECT pg_catalog.setval('remocra.type_droit_id_seq', (select max(id) from remocra.type_droit));
SELECT pg_catalog.setval('remocra.type_hydrant_anomalie_id_seq', (select max(id) from remocra.type_hydrant_anomalie));
SELECT pg_catalog.setval('remocra.type_hydrant_anomalie_nature_id_seq', (select max(id) from remocra.type_hydrant_anomalie_nature));
SELECT pg_catalog.setval('remocra.type_hydrant_critere_id_seq', (select max(id) from remocra.type_hydrant_critere));
SELECT pg_catalog.setval('remocra.type_hydrant_diametre_id_seq', (select max(id) from remocra.type_hydrant_diametre));
SELECT pg_catalog.setval('remocra.type_hydrant_domaine_id_seq', (select max(id) from remocra.type_hydrant_domaine));
SELECT pg_catalog.setval('remocra.type_hydrant_id_seq', (select max(id) from remocra.type_hydrant));
SELECT pg_catalog.setval('remocra.type_hydrant_marque_id_seq', (select max(id) from remocra.type_hydrant_marque));
SELECT pg_catalog.setval('remocra.type_hydrant_materiau_id_seq', (select max(id) from remocra.type_hydrant_materiau));
SELECT pg_catalog.setval('remocra.type_hydrant_modele_id_seq', (select max(id) from remocra.type_hydrant_modele));
SELECT pg_catalog.setval('remocra.type_hydrant_nature_id_seq', (select max(id) from remocra.type_hydrant_nature));
SELECT pg_catalog.setval('remocra.type_hydrant_positionnement_id_seq', (select max(id) from remocra.type_hydrant_positionnement));
SELECT pg_catalog.setval('remocra.type_hydrant_saisie_id_seq', (select max(id) from remocra.type_hydrant_saisie));
SELECT pg_catalog.setval('remocra.type_hydrant_vol_constate_id_seq', (select max(id) from remocra.type_hydrant_vol_constate));
SELECT pg_catalog.setval('remocra.type_organisme_id_seq', (select max(id) from remocra.type_organisme));
SELECT pg_catalog.setval('remocra.type_permis_avis_id_seq', (select max(id) from remocra.type_permis_avis));
SELECT pg_catalog.setval('remocra.type_permis_interservice_id_seq', (select max(id) from remocra.type_permis_interservice));
SELECT pg_catalog.setval('remocra.utilisateur_id_seq', (select max(id) from remocra.utilisateur));
SELECT pg_catalog.setval('remocra.voie_id_seq', (select max(id) from remocra.voie));
SELECT pg_catalog.setval('remocra.zone_competence_id_seq', (select max(id) from remocra.zone_competence));

-- Schéma tracabilite
SELECT pg_catalog.setval('tracabilite.hydrant_id_seq', (select max(id) from tracabilite.hydrant));



COMMIT;

