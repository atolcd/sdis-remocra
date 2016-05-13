# ----------
# Dump des schémas remocra, pdi, tracabilite
# ----------

cd $(dirname $0)

echo "Dump des schémas remocra, pdi, tracabilite"


pg_dump -h localhost -U postgres --port 5432 --format plain --schema-only --encoding UTF8 --verbose --file "../010_schemas.sql" --schema "remocra" --schema "pdi" --schema "tracabilite" "remocra" 2> 010_schemas.log
if [ $? = 1 ]; then
  echo "       Erreur"
  exit $?
fi


cat << "EOF" >> ../010_schemas.sql


-- Schémas liés à la synchro
CREATE SCHEMA remocra_referentiel;
ALTER SCHEMA remocra_referentiel OWNER TO postgres;

CREATE SCHEMA sdis_referentiel;
ALTER SCHEMA sdis_referentiel OWNER TO postgres;

EOF


