#!/bin/bash

# Dans le répertoire courant
cd $(dirname $0)/..

docker run --rm \
  --name remocra \
  -u $(id -u):$(id -g) \
  -v "$(pwd)":/app -w /app \
  -p 0.0.0.0:8080:8080 \
  -v ~/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS="-Duser.home=/var/maven" \
  cvagner/docker-jdk-maven-sencha-cmd:7-3.6.0-3.0.2 \
  \
  mvn clean verify -P modeinfo-able -P update
