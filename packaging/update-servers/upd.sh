#!/bin/bash
# Script de mise à jour d'un serveur

cd $(dirname $0)

if [ $# -lt 1 ] || [ ! -f remocra.war ]
then
  echo 'usage: upd.sh servername [port]'
  echo '       remocra.war must be in the current directory'
  exit 1
fi

serveur=$1

if [ $# -le 2 ]
then
    export port=$2
fi
port=${port:=22}

echo "       host : $serveur, port : $port"

echo '       transfert du war'
scp -P $port remocra.war root@$serveur:/home/postgres/

echo '       mise à jour du serveur'
ssh $serveur -p $port -l root < upd.txt

echo '       fin'
