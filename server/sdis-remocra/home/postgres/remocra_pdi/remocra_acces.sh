# Redéfinit les accès de l'utilisateur postgres remocra
# Périodicité : A DETERMINER

cd $(dirname $0)

echo "Redéfinit les accès de l'utilisateur postgres remocra"
psql remocra -f /home/postgres/remocra_db/030_acces.sql
if [ $? = 1 ]; then
  echo "Erreur lors de la redéfinition des accès de l'utilisateur postgres remocra"
  exit $?
fi

