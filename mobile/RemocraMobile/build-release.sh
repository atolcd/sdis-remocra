#!/usr/bin/env bash

# 
#  Compilation des templates et
#  Compilation des fichiers javascript
# 

# Déplacement dans le répertoire du script
cd $(dirname $0)

# Lancement en mode release
../gradlew assembleRelease

