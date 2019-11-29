#!/bin/bash
set -e
/scripts/setup.sh && \
  \
  /dkron/dkron agent --server \
    --bootstrap-expect=2 --join=${DKRON_JOIN} \
    --node-name=${DKRON_NODE_NAME} --tag="${DKRON_TAG}" \
    --rpc-port=${DKRON_RPC_PORT:-6868} --bind-addr=${DKRON_BIND_ADDR:-0.0.0.0:8946} --http-addr=${DKRON_HTTP_ADDR:-:8080}