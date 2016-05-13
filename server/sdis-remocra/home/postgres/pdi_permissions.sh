# ----------
# Reset des droits sur les fichiers liés PDI
# ----------

# Création des répertoires s'ils n'existent pas déjà
mkdir -p /var/remocra/{alertes,atlas,blocs,declahydrant,deliberations,geoserver_data,getfeatureinfo,html,hydrants,modeles,pdi,permis,receptravaux,rci}
mkdir -p /var/remocra/pdi/{depot,export,kml,log,synchro,tmp}

# Propriétaire et groupe
chown -R postgres:postgres /home/postgres
chown -R postgres:remocrasys /var/remocra

chmod -R g+s /var/remocra

chown -R tomcat:remocrasys /var/remocra/geoserver_data

chmod -R 755 /home/postgres
chmod -R 770 /var/remocra

chmod -R 775 /var/remocra/geoserver_data

# Scripts exécutables
find /home/postgres -type f -name '*.sh' -exec chmod u+x '{}' \+

