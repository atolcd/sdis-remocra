BEGIN;

CREATE OR REPLACE VIEW pdi.vue_organisme_cis AS
select org.id,org.libelle, org.tricol
from(
		-- Toutes = toutes les casernes
         SELECT (-1) AS id, 'Toutes' AS libelle, NULL::unknown AS tricol
UNION
         SELECT o.id, o.nom AS libelle, o.nom AS tricol
           FROM remocra.organisme o
      JOIN remocra.type_organisme tyo ON o.type_organisme = tyo.id
     WHERE o.actif = true AND tyo.code::text = 'CIS'::text
) org
ORDER BY org.tricol NULLS FIRST;

ALTER TABLE pdi.vue_organisme_cis OWNER TO postgres;

INSERT INTO  pdi.modele_traitement (idmodele, code, description, nom, ref_chemin, ref_nom, type, message_echec, message_succes) 
	VALUES (15, 1, 'Points d''eau à numéroter', 'Points d''eau à numéroter', '/demandes/statistiques_hydrants', 'hydrants_a_numeroter', 'J', 3, 1);

INSERT INTO  pdi.modele_traitement_parametre (idparametre, form_etiquette, form_num_ordre, form_obligatoire, form_source_donnee, form_type_valeur, form_valeur_defaut, nom, idmodele) 
	VALUES (57, 'Caserne', 1, true, 'vue_organisme_cis', 'combo', NULL, 'ORGANISME_CIS_ID', 15);

INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('ID_TRAITEMENT_HYDRANTS_NON_NUM', 'Traitement des points d''eau non numérotés', '15', 1);
INSERT INTO remocra.type_droit (code, description, nom, version) VALUES ('HYDRANTS_EXPORT_NON_NUM', 'Droit sur l''export des hydrants non numérotés', 'hydrants.exportnonnum', 1);

-- Profils
-- SDIS : référent DECI
-- SDIS : référent PENA
-- SDIS : Administrateur de l'application
-- CIS : Chef de centre
-- CIS : Hydrant Etape 1 uniquement

INSERT INTO remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
SELECT 'TRUE','FALSE','FALSE','FALSE',1, pd.id,td.id
FROM remocra.profil_droit pd, remocra.type_droit td
WHERE td.code = 'HYDRANTS_EXPORT_NON_NUM'
AND pd.code in ('SDIS-REF-DECI','SDIS-REF-PENA','SDIS-ADM-APP','CIS-CHEF', 'CIS-HYDRANT-E1');

COMMIT;
