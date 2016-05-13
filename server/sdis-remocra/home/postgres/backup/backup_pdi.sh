# ----------
# Dump de la base remocra_ref_pdi
# ----------

cd $(dirname $0)

echo "Dump de la base remocra_ref_pdi"

pg_dump --port 5432 --format plain --create --encoding UTF8 --verbose --file "pdi_all.sql" "remocra_ref_pdi" > backup_pdi.log  2>&1
if [ $? = 1 ]; then
  echo "Erreur lors du dump de remocra_ref_pdi"
  exit $?
fi

