#!/bin/bash

# Dans le répertoire courant
cd $(dirname $0)/..

docker run --rm \
  --name remocra-build \
  -u $(id -u):$(id -g) \
  -w /app \
  -v "$(pwd)":/app \
  -v "${HOME}":/var/maven \
  -e HOME=/var/maven \
  -e MAVEN_CONFIG=/var/maven/.m2 \
  -e MAVEN_OPTS="-Duser.home=/var/maven" \
  cvagner/docker-jdk-maven-sencha-cmd:8-3.6.3-3.0.2 \
  \
  mvn clean verify -P modeinfo-able -P install
