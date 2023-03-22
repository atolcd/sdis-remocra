
#!/bin/bash
# Gestion des erreurs
abort() {
  (RED='\033[0;31m' && NC='\033[0m' && printf "${RED}\n------------------------------------------------------------\n- ABORTED - L'installation / mise à jour a été interrompue -\n------------------------------------------------------------\n${NC}" >&2)
  exit 1
}
trap 'abort' ERR
set -e
# ------------------------------
# - INSTALLATION DE REMOCRA
# ------------------------------
# Dans le répertoire courant
cd $(dirname $0)
# ------------------------------
# - Préparation
# ----------
echo
echo "-------------------------------------------------"
echo "- Installation / mise à jour du serveur REMOcRA -"
echo "-------------------------------------------------"
if ( ! (whoami | grep root > /dev/null) ); then
  echo && echo "not root : sudo su"
  sudo su
fi
### Base de données
export USER_POSTGRES_DB_PASSWORD=${USER_POSTGRES_DB_PASSWORD:-xxxxxxx}
export REMOCRA_DB_PASSWORD=${REMOCRA_DB_PASSWORD:-xxxxxx}
### Courriels (par PDI)
find /livraison/ -type f -name '*.sh' -exec chmod u+x '{}' \+
# Base + remocra
/livraison/install_base_ubuntu.sh

