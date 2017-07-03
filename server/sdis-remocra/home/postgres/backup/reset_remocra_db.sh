# ----------
# Script de réinitialisation de la base Remocra
# ----------

cd /home/postgres/backup


read -p "Confirmer le remplacement de la base remocra (o : oui) ? " -n 1 -r
echo
if [[ ! $REPLY =~ ^[OoYy]$ ]]
then
    [[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # exits from shell or function but don't exit interactive shell
fi



echo "-reset_remocra_db-> Réinitialisation de la base : début"

echo "-reset_remocra_db-> Suppression de la base"
dropdb remocra
if [ $? = 1 ]; then
  echo "-reset_remocra_db-> Warning lors de la suppression de la base"
  #exit $?
fi


echo "-reset_remocra_db-> Création de la base"
psql -f remocra_all.sql > remocra_all.log 2> remocra_all_error.log
if [ $? = 1 ]; then
  echo "-reset_remocra_db-> Erreur lors de la création de la base"
  exit $?
fi


echo "-reset_remocra_db-> Vérification fichier d'erreurs"
# Test taille fichier d'erreurs
err_file_size=$(stat -c %s remocra_all.log)
if [ $err_file_size -gt 0 ]; then
  echo "-reset_remocra_db-> WARNING fichier d'erreur non vide"
fi


echo "-reset_remocra_db-> Réinitialisation de la base : fin"
