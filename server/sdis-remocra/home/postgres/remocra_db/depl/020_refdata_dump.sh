# ----------
# Dump des données de référence
# ----------

cd $(dirname $0)


echo "Dump des données de référence"

pg_dump -h localhost -U postgres --port 5432 --username "postgres" --no-password  --format plain --data-only --encoding UTF8 --inserts --column-inserts --verbose --file "../020_refdata.sql" \
\
--table "remocra.suivi_patches" \
\
--table "remocra.profil_droit" --table "remocra.droit" --table "remocra.type_droit" --table "remocra.type_organisme" --table "remocra.profil_organisme" --table "remocra.profil_utilisateur" --table "remocra.profil_organisme_utilisateur_droit" \
\
--table "remocra.email_modele" \
\
--table "remocra.thematique" \
\
--table "remocra.sous_type_alerte_elt" \
--table "remocra.type_alerte_ano" \
--table "remocra.type_alerte_elt" \
\
--table "remocra.type_hydrant_anomalie" \
--table "remocra.type_hydrant_anomalie_nature" \
--table "remocra.type_hydrant_anomalie_nature_saisies" \
--table "remocra.type_hydrant_diametre_natures" \
--table "remocra.type_hydrant_critere" \
--table "remocra.type_hydrant_diametre" \
--table "remocra.type_hydrant_domaine" \
--table "remocra.type_hydrant_marque" \
--table "remocra.type_hydrant_materiau" \
--table "remocra.type_hydrant_modele" \
--table "remocra.type_hydrant_positionnement" \
--table "remocra.type_hydrant_vol_constate" \
--table "remocra.type_hydrant_nature" \
--table "remocra.type_hydrant" \
--table "remocra.type_hydrant_saisie" \
\
--table "remocra.type_permis_avis" \
--table "remocra.type_permis_interservice" \
\
--table "remocra.type_rci_degre_certitude" \
--table "remocra.type_rci_origine_alerte" \
--table "remocra.type_rci_prom_categorie" \
--table "remocra.type_rci_prom_famille" \
--table "remocra.type_rci_prom_partition" \
\
\
--table "pdi.modele_message" \
--table "pdi.modele_traitement" \
--table "pdi.modele_traitement_parametre" \
--table "pdi.statut" \
\
"remocra" 2> 020_refdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi

