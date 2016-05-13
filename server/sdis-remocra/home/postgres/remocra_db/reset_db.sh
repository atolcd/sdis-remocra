# ----------
# Script de réinitialisation de la base
# ----------

cd $(dirname $0)


echo "Réinitialisation de la base : début"


echo "     -> Suppression de la base"
dropdb -h localhost -U postgres remocra
if [ $? = 1 ]; then
  echo "       Avertissement"
fi


echo "     -> Création de la base"
createdb -h localhost -U postgres -E UTF8 -T template_postgis remocra
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi

echo "     -> Nettoyage des fichiers de log"
rm *.log
if [ $? = 1 ]; then
  echo "       Avertissement"
fi

echo "     -> Création des schémas"
psql -h localhost -U postgres remocra -f 000_remocra_all.sql > 000_remocra_all.log 2> 000_remocra_all_error.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Vérification fichier d'erreurs"
# Test taille fichier d'erreurs
err_file_size=$(stat -c %s 000_remocra_all_error.log)
if [ $err_file_size -gt 0 ]; then
  echo "       Avertissement"
fi


# Données de test
#psql -h localhost -U postgres remocra -f 030_data_tests.sql > 030_data_tests.log 2> 030_data_tests_error.log

