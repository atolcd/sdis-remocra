# ----------
# Script d'insertion des données de référence et du jeu de données
# ----------

cd $(dirname $0)

echo "Insertion des données de référence et du jeu de données"

psql -h localhost -U postgres remocra -f data_tests_01.sql > data_tests_01.log 2> data_tests_01_error.log
err_file_size=$(stat -c %s data_tests_01_error.log)
if [ $err_file_size -gt 0 ]; then
  echo "       Avertissement"
fi

