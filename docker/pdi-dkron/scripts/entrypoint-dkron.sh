#!/bin/bash
set -e
/scripts/setup.sh && /dkron/dkron agent --server --bootstrap-expect=2 --join=${DKRON_JOIN} --node-name=${DKRON_NODE_NAME} --tag="${DKRON_TAG}"