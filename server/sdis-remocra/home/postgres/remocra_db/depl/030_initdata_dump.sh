# ----------
# Dump des données de référence
# ----------

cd $(dirname $0)

echo "Dump des données initiales"


echo "     -> Table commune"
pg_dump -h localhost -U postgres --data-only --file="../025_refdata-com-COPY.sql" --table=remocra.commune remocra 2> 030_initdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Table zone_speciale"
pg_dump -h localhost -U postgres --data-only --file="../025_refdata-zs-COPY.sql" --table=remocra.zone_speciale remocra 2>> 030_initdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Table zone_competence"
pg_dump -h localhost -U postgres --data-only --file="../025_refdata-zc-COPY.sql" --table=remocra.zone_competence remocra 2>> 030_initdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Table voie"
pg_dump -h localhost -U postgres --data-only --file="../025_refdata-voie-COPY.sql" --table=remocra.voie remocra 2>> 030_initdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Tables organisme et utilisateur"
pg_dump -h localhost -U postgres --data-only --inserts --file="../025_refdata-org-users.sql" --table=remocra.organisme --table=remocra.utilisateur remocra 2>> 030_initdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Table param_conf"
pg_dump -h localhost -U postgres --data-only --inserts --file="../025_refdata-param.sql" --table=remocra.param_conf remocra 2>> 030_initdata.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


echo "     -> Création du fichier zip 025_refdata.zip"
cd ..
rm -f 025_refdata.zip
zip 025_refdata.zip 025_refdata-*.sql
cd $(dirname $0)

