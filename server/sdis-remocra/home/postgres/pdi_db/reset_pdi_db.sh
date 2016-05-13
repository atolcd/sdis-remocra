# ----------
# Script de réinitialisation de la base PDI
# ----------

cd /home/postgres/pdi_db


echo "-reset_pdi_db-> Réinitialisation de la base : début"

echo "-reset_pdi_db-> Suppression de la base"
dropdb remocra_ref_pdi
if [ $? = 1 ]; then
  echo "-reset_pdi_db-> Warning lors de la suppression de la base"
  #exit $?
fi


echo "-reset_pdi_db-> Création de la base"
psql -f 000_remocra_pdi_all.sql > 000_remocra_pdi_all.log 2> 000_remocra_pdi_all_error.log
if [ $? = 1 ]; then
  echo "-reset_pdi_db-> Erreur lors de la création de la base"
  exit $?
fi


echo "-reset_pdi_db-> Vérification fichier d'erreurs"
# Test taille fichier d'erreurs
err_file_size=$(stat -c %s 000_remocra_pdi_all_error.log)
if [ $err_file_size -gt 0 ]; then
  echo "-reset_pdi_db-> WARNING fichier d'erreur non vide"
fi


echo "-reset_pdi_db-> Réinitialisation de la base : fin"
