#! /bin/sh

cd $(dirname $0)/..

# Build artefacts and runnable image
docker build . -t api-remocra --network=host -f dist/Dockerfile-image \
  \
  || { echo 'Image build failed' ; exit 1; }
