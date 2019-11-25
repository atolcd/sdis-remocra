#!/bin/bash
set -e

# Génération du fichier de propriétés
# Lancement de dkron
/scripts/entrypoint-pdi.sh -rep:"ref_pdi_remocra" -dir:"maintenance" -job:"generer_fichier_proprietes" \
  -user:admin -pass:admin -level:Error \
  -param:"PDI_FICHIER_PARAMETRE=/home/pdi/remocra.properties" \
   \
  && /scripts/entrypoint-dkron.sh