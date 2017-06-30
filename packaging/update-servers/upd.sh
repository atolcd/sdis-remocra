#!/bin/bash
# Script de mise à jour d'un serveur

cd $(dirname $0)

if [ $# -lt 1 ]
then
  echo 'usage: upd.sh servername [port] [warfile]'
  echo '       warfile : remocra.war par défaut'
  exit 1
fi

serveur=$1
port=$2
port=${port:=22}
warfile=$3
warfile=${warfile:=remocra.war}

if [ ! -f ${war} ]
then
  echo 'usage: upd.sh servername [port] [warfile]'
  echo '       warfile : remocra.war par défaut'
  echo ''
  echo "${warfile} non trouvé"
  exit 1
fi

echo "       host : $serveur, port : $port, file : ${warfile}"

echo '       transfert du war'
scp -P $port ${warfile} root@$serveur:/home/postgres/remocra.war

echo '       mise à jour du serveur'
ssh $serveur -p $port -l root < upd.txt

echo '       fin'
