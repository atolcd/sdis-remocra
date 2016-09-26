#!/bin/bash
# Récupère remocra.war d'un serveur

cd $(dirname $0)

if [ $# -lt 1 ]
then
  echo 'usage: get.sh servername [port]'
  echo '       remocra.war will be in the current directory'
  exit 1
fi

serveur=$1

if [ $# -le 2 ]
then
    export port=$2
fi
port=${port:=22}

scp -P $port root@${serveur}:/var/lib/tomcat6/webapps/remocra.war .

