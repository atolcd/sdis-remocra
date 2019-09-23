# ----------
# Reset des droits sur les fichiers liés PDI
# ----------

# Création des répertoires s'ils n'existent pas déjà
mkdir -p /var/remocra/{alertes,atlas,blocs,declahydrant,deliberations,geoserver_data,getfeatureinfo,html,hydrants,modeles,pdi,permis,receptravaux,rci,layers}
mkdir -p /var/remocra/pdi/{depot,export,kml,log,synchro,tmp}

# Propriétaire et groupe
chown -R postgres:postgres /home/postgres
find /var/remocra -not -path "*/tms*" -not -path "*/geoserver_data*" -exec chown postgres:remocrasys {} \;
chown -R tomcat:remocrasys /var/remocra/geoserver_data

# Accès
find /var/remocra -not -path "*/tms*" -exec chmod g+s {} \;
chmod -R 755 /home/postgres
find /var/remocra -not -path "*/tms*" -not -path "*/geoserver_data*" -exec chmod 770 {} \;
chmod -R 775 /var/remocra/geoserver_data

# Scripts exécutables
find /home/postgres -type f -name '*.sh' -exec chmod u+x '{}' \+

