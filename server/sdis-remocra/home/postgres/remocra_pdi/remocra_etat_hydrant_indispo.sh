# Crée les demandes d'export des hydrants indisponible de la veille
# Périodicité : A DETERMINER

cd $(dirname $0)

echo "Enregistrement de demandes des traitements Hydrants indisponibles (PIBI et PENA)"
psql remocra -f remocra_etat_hydrant_indispo.sql
if [ $? = 1 ]; then
  echo "Erreur lors de l'enregistrement des demandes de traitements Hydrants indisponibles (PIBI et PENA)"
  exit $?
fi

