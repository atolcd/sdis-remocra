#!/bin/bash
# Récupération du schéma, des données de référence et initiales d'une base (serveur de production par défaut)

cd $(dirname $0)

export SRC_SDIS=${SRC_SDIS:=83}
export SRC_SERVER=${SRC_SERVER:=sdis83-remocra-prod.hosting}

echo "Récupération des schémas, données initiales et de référence"
echo "     Code SDIS : $SRC_SDIS"
echo "     Serveur   : $SRC_SERVER"

echo "     -> Dump distant"
cat << "EOF" | ssh $SRC_SERVER -l root
cd /home/postgres/remocra_db/depl
su postgres

sh 010_schemas_dump.sh
sh 020_refdata_dump.sh
sh 030_initdata_dump.sh

EOF

if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Récupération du schéma et des données de référence"

scp root@$SRC_SERVER:/home/postgres/remocra_db/010_schemas.sql .. && scp root@$SRC_SERVER:/home/postgres/remocra_db/020_refdata.sql ..

if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Récupération des données initiales"

mkdir -p ../${SRC_SDIS} && \
scp root@$SRC_SERVER:/home/postgres/remocra_db/025_refdata.zip ../${SRC_SDIS} && \
  unzip -o ../${SRC_SDIS}/025_refdata.zip -d ../${SRC_SDIS} && \
  rm -f ../${SRC_SDIS}/025_refdata.zip

if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Réinitialisation all_patches.sql"
cat << "EOF" > ../patches/all_patches.sql
-- ---------------
-- Script liant patches
-- ---------------

SET CLIENT_ENCODING TO 'UTF8';

/* Schémas
\i patches/XXX/XXX.sql
*/

EOF

