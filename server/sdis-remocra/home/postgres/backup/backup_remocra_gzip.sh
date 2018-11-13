# ----------
# Dump de la base remocra
# ----------

cd $(dirname $0)

echo "Dump de la base remocra"

pg_dump --port 5432 --format plain --create --encoding UTF8 --verbose "remocra" | gzip -9 > remocra_all.sql.gz  2> backup_remocra_gzip.log
if [ $? = 1 ]; then
  echo "Erreur lors du dump de remocra"
  exit $?
fi

