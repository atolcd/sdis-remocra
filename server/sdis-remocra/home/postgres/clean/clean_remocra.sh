# ----------
# Nettoyage des fichiers et données (après copie serveur par exemple)
# ----------

cd $(dirname $0)

rm -f /tmp/*

rm -rf /home/postgres/*.log
rm -rf /home/postgres/backup/*.sql
rm -rf /home/postgres/pdi_db/*.log
rm -rf /home/postgres/remocra_db/*.log
rm -rf /home/postgres/remocra_db/depl/*.log

rm -f /var/remocra/alertes/*
rm -f /var/remocra/blocs/*
rm -f /var/remocra/declahydrant/*
rm -f /var/remocra/deliberations/*
rm -f /var/remocra/html/*
rm -f /var/remocra/hydrants/*
rm -f /var/remocra/pdi/depot/*
rm -f /var/remocra/pdi/export/*
rm -f /var/remocra/pdi/kml/*
rm -f /var/remocra/pdi/log/*
rm -f /var/remocra/pdi/synchro/EXPORT_SDIS/*
rm -f /var/remocra/pdi/synchro/IMPORT_EXTRANET/*
rm -f /var/remocra/pdi/tmp/*
rm -f /var/remocra/permis/*
rm -f /var/remocra/rci/*
rm -f /var/remocra/receptravaux/*

psql -u postgres -f clean_remocra.sql remocra > clean_remocra.log 2>&1
if [ $? = 1 ]; then
  echo "Erreur lors du vidage des tables de la base"
fi

