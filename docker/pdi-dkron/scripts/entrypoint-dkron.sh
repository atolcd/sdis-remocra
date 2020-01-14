#!/bin/bash
set -e

DKRON_LOGFILE=${DKRON_LOGFILE:-/var/remocra/pdi/log/dkron.log}
mkdir -p $(dirname ${DKRON_LOGFILE})

/scripts/setup.sh && \
  \
  /dkron/dkron agent --server \
    --bootstrap-expect=2 --join=${DKRON_JOIN} \
    --node-name=${DKRON_NODE_NAME} --tag="${DKRON_TAG}" \
    --rpc-port=${DKRON_RPC_PORT:-6868} --bind-addr=${DKRON_BIND_ADDR:-0.0.0.0:8946} --http-addr=${DKRON_HTTP_ADDR:-:8080} \
    --log-level=${DKRON_LOG_LEVEL:-info} \
    2>&1 | tee -a ${DKRON_LOGFILE}