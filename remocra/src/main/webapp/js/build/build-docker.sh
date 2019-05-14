#!/bin/bash

# Gestion des erreurs
set -e

# 
#  Compilation des fichiers javascript
# 

# Déplacement dans le répertoire adéquat
echo "Go one directory up"
cd $(dirname $0)/..

echo "Nettoyage des all-classes*.js"
rm -rf all-classes*.js

echo "Génération du all-classes.js"

docker run --rm \
  --name remocra-senchacmd \
  -u $(id -u):$(id -g) \
  -v `pwd`:/app -w /app \
  -v ~/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS="-Duser.home=/var/maven" \
  cvagner/docker-jdk-maven-sencha-cmd:7-3.6.1-3.0.2 \
  \
  sencha compile -classpath=openlayers-2.12/OpenLayers.js,openlayers-2.12/lib/OpenLayers/Lang/fr.js,proj4js-1.1.0/lib/proj4js-compressed.js,proj4js-1.1.0/lib/defs/EPSG3857.js,ext-4.1.1a/src,ext-4.1.1a/locale/ext-lang-fr.js,app/remocra \
    -debug=false \
    concat -yui all-classes.js

#echo "Suppression des appels à require dans les JS compilés"
# Suppression des appels à require
#sed -i 's/Ext.require([^)]*);//g' all-classes.js

