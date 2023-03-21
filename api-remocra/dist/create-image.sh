#! /bin/sh

cd $(dirname $0)/..

# Build artefacts and runnable image
docker build . -t api-remocra --network=host -f dist/Dockerfile \
  \
  || { echo 'Image build failed' ; exit 1; }
