#!/bin/bash

if [ $# -ne 1 ]
then
  echo 'usage: set-version.sh <version>'
  exit 1
fi

version=$1

mvn -f $(dirname $0)/../pom.xml -DnewVersion="${version}" versions:set -DgenerateBackupPoms=false

