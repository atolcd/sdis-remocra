#!/bin/bash
# Script de mise à jour du serveur vagrant

cd $(dirname $0)

if [ ! -f remocra.war ]
then
  echo 'usage: upd-vagrant.sh'
  echo '       remocra.war must be in the current directory'
  exit 1
fi

sshpass -p "vagrant" scp -P 2222 -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no remocra.war root@localhost:/home/postgres/

sshpass -p "vagrant" ssh localhost -l root -p 2222 -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no < upd.txt

