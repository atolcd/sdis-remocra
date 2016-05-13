# ----------
# Dump de la base remocra
# ----------

cd $(dirname $0)

echo "Dump de la base remocra"

pg_dump --port 5432 --format plain --create --encoding UTF8 --verbose --file "remocra_all.sql" "remocra" > backup_remocra.log  2>&1
if [ $? = 1 ]; then
  echo "Erreur lors du dump de remocra"
  exit $?
fi

