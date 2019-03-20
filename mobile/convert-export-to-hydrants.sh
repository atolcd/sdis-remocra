#!/bin/bash

# Conversion d'un fichier issu d'un export XML Android vers un fichier prêt pour un POST /remocra/xml/hydrants
#
# Exemple :
#   convert-export-to-hydrants.sh "20180925184322_utilisateur_export.xml"
#   curl -v -u username:password -d "@20180925184322_utilisateur_export.xml" -H "Content-Type: application/xml;charset=utf-8" -X POST http://remocra.sdisxx.fr/remocra/xml/hydrants

fileToConvert="$1"

if [[ -z "${fileToConvert}" ]]; then
  echo "Fichier à traiter non spécifié"
  exit 1
fi

if [ ! -f ${fileToConvert} ]; then
  echo "${fileToConvert} non trouvé"
  exit 1
fi

# Fonction utilitaire de conversion.
function convertFile {
  echo "Fichier traité : $1" \
    && echo "-- Démarrage" \
    && sed -i 's/<xsi:tournees xmlns:xsi="http:\/\/www.w3.org\/2001\/XMLSchema-instance">/<xsi:hydrants xmlns:xsi="http:\/\/www.w3.org\/2001\/XMLSchema-instance">/g' "$1" \
    && sed -i 's/<tournee><id>[^<]*<\/id><debSync>[^<]*<\/debSync><hydrants>//g' "$1" \
    && sed -i 's/<\/hydrants><\/tournee>//g' "$1" \
    && sed -i 's/<\/xsi:tournees>/<\/xsi:hydrants>/g' "$1" \
    && sed -i 's/<typeSaisie>.*<\/typeSaisie>//g' "$1" \
    && echo "-- Terminé"
}

convertFile "$fileToConvert"

