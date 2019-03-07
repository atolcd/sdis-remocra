# Gestion des erreurs
set -e

# ------------------------------
# - INSTALLATION DE REMOCRA - APPLICATION ET TRAITEMENTS
# ------------------------------

# Ajout des utilisateurs dans le groupe remocrasys
if ! grep remocrasys /etc/group > /dev/null; then
  groupadd remocrasys
  usermod -a -G remocrasys postgres
  usermod -a -G remocrasys tomcat
  usermod -a -G remocrasys geoserver
fi

# Droits
chown -R postgres:postgres /home/postgres/


# ------------------------------
# - Bases de données Remocra
# ------------------------------
cd /home/postgres/remocra_db
if ! $(su postgres -c "psql -l | grep '^ remocra\b' > /dev/null"); then
  # On sécurise en demande une confirmation pour éviter toute perte de données
  echo && echo "A priori, la base de données REMOCRA n'existe pas."
  if "${REMOCRA_FORCE_CREATE_DB_IF_ABSENT}" ; then
    echo "Mode forcé"
    REMOCRA_FORCE_CREATE_DB_IF_ABSENT_GO=true
  else
    read -p "Réinitialiser la base de données (oui / non) ? " -r
    if [[ $REPLY =~ ^oui$ ]]; then
      REMOCRA_FORCE_CREATE_DB_IF_ABSENT_GO=true
    else
      REMOCRA_FORCE_CREATE_DB_IF_ABSENT_GO=false
    fi
  fi
  if "${REMOCRA_FORCE_CREATE_DB_IF_ABSENT_GO}" ; then
    echo "Réinitialisation de la base remocra"
    chmod +x /home/postgres/remocra_db/reset_db.sh
    su postgres -c "PGPASSWORD=${POSTGRES_DB_PASSWORD} /home/postgres/remocra_db/reset_db.sh"
    if [ $? = 1 ]; then
      echo "Erreur lors de la création de la base de données"
      exit 1
    fi

    # Communes / Zones spéciales / Zones de compétence
    #cd dev
    #su postgres -c "psql remocra -f ../${SRC_SDIS}/025_refdata-com-COPY.sql > /dev/null 2>&1"
    #su postgres -c "psql remocra -f ../${SRC_SDIS}/025_refdata-zs-COPY.sql > /dev/null 2>&1"
    #su postgres -c "psql remocra -f ../${SRC_SDIS}/025_refdata-zc-COPY.sql > /dev/null 2>&1"
    #su postgres -c "psql remocra -f ../${SRC_SDIS}/025_refdata-voie-COPY.sql > /dev/null 2>&1"

    # Une seule zone de compétence "FR"
    # Géométrie union des bbox des départements 01...95, 2A...2B, 971...978, 986...988, issue d'une requête du type : "select astext(st_union(ST_Box2D(st_transform(geometrie, 2154)))) from xxx.departement"
    su postgres -c "psql remocra -c \"insert into remocra.zone_competence (code, nom, geometrie) values ('FR', 'France', geomfromtext('MULTIPOLYGON(((1157055 6049646.5,1157055 6158599,1159342 6158599,1159342 6233671,1242375.125 6233671,1242375.125 6102227.5,1233711.125 6102227.5,1233711.125 6049646.5,1157055 6049646.5)),((253465.140625 6997399.5,253465.140625 7016562,216607.21875 7016562,216607.21875 7097114,254268.3125 7097114,254268.3125 7114420.5,328282.71875 7114420.5,328282.71875 7039838,327053.5 7039838,327053.5 6997399.5,253465.140625 6997399.5)),((595004 6137163.5,595004 6164369.5,522390.96875 6164369.5,522390.96875 6179671.5,508067 6179671.5,508067 6178845.5,428171.96875 6178845.5,428171.96875 6193171.5,311995 6193171.5,311995 6283552,334283 6283552,334283 6389853,361762.96875 6389853,361762.96875 6448283.5,348630.96875 6448283.5,348630.96875 6582755.5,287967.96875 6582755.5,287967.96875 6649967.5,280972.96875 6649967.5,280972.96875 6704492.5,199376.984375 6704492.5,199376.984375 6760584.5,99225.9921875 6760584.5,99225.9921875 6873212,209750.984375 6873212,209750.984375 6881976,337516 6881976,337516 6856659,343081.96875 6856659,343081.96875 6969694,425027 6969694,425027 6929242,487572.96875 6929242,487572.96875 6998137,583879.9375 6998137,583879.9375 7030667,597189.9375 7030667,597189.9375 7101577,634421.9375 7101577,634421.9375 7110524,788153.0625 7110524,788153.0625 7009226,873032.0625 7009226,873032.0625 6949057,908589.0625 6949057,908589.0625 6943889,1004385.0625 6943889,1004385.0625 6939584,1038956.0625 6939584,1038956.0625 6895581,1082671.125 6895581,1082671.125 6789935.5,1044932.0625 6789935.5,1044932.0625 6710918.5,1006440.0625 6710918.5,1006440.0625 6610704.5,944643 6610704.5,944643 6596484,1013117.0625 6596484,1013117.0625 6541572,1027185 6541572,1027185 6445178.5,1022851.0625 6445178.5,1022851.0625 6370072,1077507 6370072,1077507 6272481.5,1018256.0625 6272481.5,1018256.0625 6214472.5,914649 6214472.5,914649 6232732.5,799606.9375 6232732.5,799606.9375 6263118.5,796333.0625 6263118.5,796333.0625 6234873.5,719561 6234873.5,719561 6172490.5,714448 6172490.5,714448 6137163.5,595004 6137163.5),(643206 6867927.5,643206 6853168,654970.9375 6853168,654970.9375 6867927.5,643206 6867927.5)))', 2154));\" > /dev/null 2>&1" 

    # Organisme SDIS, utilisateur sdis-adm-app et quelques paramètres
    su postgres -c "psql remocra -c \"update remocra.param_conf set valeur='${APP_IGNKEYS}' where cle='CLES_IGN'\" > /dev/null 2>&1" 
    su postgres -c "psql remocra -c \"update remocra.param_conf set valeur='${SRC_SDIS}%' where cle='COMMUNES_INSEE_LIKE_FILTRE_SQL'\" > /dev/null 2>&1" 
    su postgres -c "psql remocra -c \"INSERT INTO remocra.organisme (actif, code, email_contact, nom, type_organisme, profil_organisme, zone_competence, version) VALUES (true, 'SDIS', null, 'SDIS', (select id from remocra.type_organisme where code = 'SDIS'), (select id from remocra.profil_organisme where code = 'SDIS'), (select id from remocra.zone_competence order by st_area(geometrie) desc limit 1), 1);\" > /dev/null 2>&1"
    su postgres -c "psql remocra -c \"insert into remocra.utilisateur(actif, email, identifiant, message_remocra, nom, password, prenom, salt, telephone, version, organisme, profil_utilisateur) values (true, '${TECH_EMAIL}', 'sdis-adm-app', true, 'Min', (md5('${APP_PASSWORD}' || '{' || md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || 'sdis-adm-app') || '}')), 'Ad', (md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || 'sdis-adm-app')), null, 1, (select id from remocra.organisme where code = 'SDIS'), (select id from remocra.profil_utilisateur where code = 'SDIS-ADM-APP'));\" > /dev/null 2>&1"
    su postgres -c "psql remocra -c \"update remocra.param_conf set valeur='DEV : sdis-adm-app / ${APP_PASSWORD} (à modifier)' where cle='MESSAGE_ENTETE'\" > /dev/null 2>&1" 
    # Utilisateurs à notifier
    su postgres -c "psql remocra -c \"update remocra.param_conf set valeur=(select (select id from remocra.utilisateur where identifiant='sdis-adm-app')::text) where cle='PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID' or cle='PDI_NOTIFICATION_KML_UTILISATEUR_ID'\" > /dev/null 2>&1" 

    # URL du site
    su postgres -c "psql remocra -c \"update remocra.param_conf set valeur='${URL_SITE}' where cle='PDI_URL_SITE'\" > /dev/null 2>&1" 

    # Modèles de mails
    su postgres -c "psql remocra -c \"update remocra.email_modele set corps=replace(corps, '83', '${SRC_SDIS}'), objet=replace(objet, '83', '${SRC_SDIS}') \" > /dev/null 2>&1" 

    # Accès
    sed -i "s/PASSWORD '.*'/PASSWORD '${REMOCRA_DB_PASSWORD}'/g" /home/postgres/remocra_db/030_acces.sql
    su postgres -c "psql remocra -f /home/postgres/remocra_db/030_acces.sql > /dev/null 2>&1"

  else
    echo "Pas de création / réinitialisation de la base remocra"
  fi
fi


# ------------------------------
# REMOCRA
# ------------------------------
# JAVA_OPTS et CATALINA_OPTS
if ( ! (cat /etc/sysconfig/tomcat6 | grep "database.password" > /dev/null) ); then
  sed -i "s/^#JAVA_OPTS=\"-Xminf0\.1 -Xmaxf0\.3\"/#JAVA_OPTS=\"-Xminf0\.1 -Xmaxf0\.3\"\n\nJAVA_OPTS=\"-Ddatabase.password=${POSTGRES_DB_PASSWORD}\"\n/" /etc/sysconfig/tomcat6
fi
# LANG

export REMOCRA_SESSION_TIMEOUT_MINUTES=${REMOCRA_SESSION_TIMEOUT_MINUTES:=20}
if [ -f /var/lib/tomcat6/webapps/remocra/WEB-INF/web.xml ]; then
  sed -i "s/<session-timeout>.*<\/session-timeout>/<session-timeout>$REMOCRA_SESSION_TIMEOUT_MINUTES<\/session-timeout>/g" /var/lib/tomcat6/webapps/remocra/WEB-INF/web.xml
fi

if "${REMOCRA_PRESERVE_REMOCRACONF}" ; then
  echo "Configuration Apache Remocra : préservation"
else
  echo "Configuration Apache Remocra : remplacement"

envsubst << "EOF" > /etc/httpd/conf.d/remocra.conf
# ##############################
# REMOcRA
# ##############################

NameVirtualHost *:80

ServerName sdis83-remocra.priv.atolcd.com

<VirtualHost *:80>
    # Redirection racine vers remocra
    RedirectMatch ^/$ /remocra/

    # Base
    ServerAdmin webmaster@sdis83-remocra.priv.atolcd.com
    DocumentRoot /var/lib/tomcat6/webapps

    # Journalisation
    ErrorLog logs/remocra_error.log
    CustomLog logs/remocra_access.log common

    # Proxy vers la webapp
    ProxyRequests Off
    <Proxy *>
        order deny,allow
        allow from all
    </Proxy>
    ProxyPreserveHost On

    # Remocra
    ProxyPass /remocra http://localhost:8080/remocra
    ProxyPassReverse /remocra http://localhost:8080/remocra

    # Geoserver
    ProxyPass /geoserver http://localhost:8090/geoserver
    ProxyPassReverse /geoserver http://localhost:8090/geoserver

    # Compression des flux
    <IfModule mod_deflate.c>
        DeflateCompressionLevel 3
        SetOutputFilter DEFLATE

        AddOutputFilterByType DEFLATE text/plain
        AddOutputFilterByType DEFLATE text/xml
        AddOutputFilterByType DEFLATE text/html
        AddOutputFilterByType DEFLATE text/css
        AddOutputFilterByType DEFLATE text/javascript
        AddOutputFilterByType DEFLATE application/x-javascript
        BrowserMatch ^Mozilla/4 gzip-only-text/html
        BrowserMatch ^Mozilla/4\.0[678] no-gzip
        BrowserMatch \bMSIE !no-gzip !gzip-only-text/html
        BrowserMatch \bMSI[E] !no-gzip !gzip-only-text/html

        SetEnvIfNoCase Request_URI \.(?:gif|jpe?g|png)$ no-gzip dont-vary
        SetEnvIfNoCase Request_URI \.(?:exe|t?gz|zip|bz2|swf|wmv|pdf)$ no-gzip dont-vary
        Header append Vary User-Agent env=!dont-vary
    </IfModule>

    # ##############################
    # Accès aux ressources : REMOcRA
    # #####
    # A voir par la suite ?

</VirtualHost>
# ##############################
# REMOcRA
# ##############################
EOF
fi

service tomcat6 restart


# ------------------------------
# GEOSERVER
# ------------------------------
# TODO voir si nécessaire de préciser GEOSERVER_DATA_DIR
#${GEOSERVER_HOME}/webapps/geoserver/WEB-INF/web.xml

#GEOSERVER_URL_CIBLE=${GEOSERVER_URL_CIBLE:-localhost}
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/sdis83-remocra.priv.atolcd.com/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/sdis83-remocra-demo.priv.atolcd.com/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/www.sdis83-remocra.atolcd.com/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/sdis83-remocra-dev/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/sdis83-remocra-prod/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/www.sapeurspompiers-var.fr/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/remocra.sapeurspompiers-var.fr/$GEOSERVER_URL_CIBLE/g" {} \;
#find /var/remocra/geoserver_data -type f -name "*.xml" ! -path "/var/remocra/geoserver_data/data/*" -exec sed -i "s/localhost:8090/$GEOSERVER_URL_CIBLE/g" {} \;

find /var/remocra/geoserver_data/workspaces/remocra/remocra -type f -name "datastore.xml" -exec sed -i "s/<entry key=\"passwd\">.*<\/entry>/<entry key=\"passwd\">plain:${POSTGRES_DB_PASSWORD}<\/entry>/g" {} \;
#sed -i "s/<entry key=\"passwd\">.*<\/entry>/<entry key=\"passwd\">plain:${POSTGRES_DB_PASSWORD}<\/entry>/g" /var/remocra/geoserver_data/workspaces/remocra/remocra/datastore.xml
sed -i "s/password=\".*\"/password=\"plain:${GEOSERVER_ADMIN_PASSWORD}\"/g" /var/remocra/geoserver_data/security/usergroup/default/users.xml

if ( ! (cat /etc/sysconfig/geoserver | grep "/var/remocra/geoserver_data" > /dev/null) ); then
  envsubst >> /etc/sysconfig/geoserver << "EOF"
export GEOSERVER_DATA_DIR=/var/remocra/geoserver_data
EOF
fi

service geoserver restart

#su postgres -c "psql remocra -c \"update remocra.param_conf set valeur='http://localhost:8090/geoserver' where cle='WMS_BASE_URL'\" > /dev/null 2>&1" 

service httpd reload


# ------------------------------
# - PDI 
# ------------------------------
# Référentiel (base)
echo && echo "Mise à jour du référentiel PDI"
chmod +x /home/postgres/pdi_db/reset_pdi_db.sh
su postgres -c "/home/postgres/pdi_db/reset_pdi_db.sh"

echo && echo "Suppression des répertoires inutiles et création des répertoires nécessaires"
rm -rf /home/postgres/pdi/plugins/pentaho-big-data-plugin/hadoop-configurations/mapr
rm -rf /home/postgres/pdi/plugins/spoon/agile-bi
mkdir -p /var/remocra/{alertes,atlas,blocs,declahydrant,deliberations,geoserver_data,getfeatureinfo,html,hydrants,modeles,pdi,permis,receptravaux,rci,layers}
mkdir -p /var/remocra/pdi/{depot,export,kml,log,synchro,tmp}

echo && echo "Mise à jour de paramètres (accès aux bases, SMTP, etc.)"
mkdir -p /home/postgres/.kettle

if "${REMOCRA_PRESERVE_DOTKETTLE}" ; then
  echo "Configuration .kettle : préservation"
else
  echo "Configuration .kettle : remplacement"

envsubst << "EOF" > /home/postgres/.kettle/kettle.properties
#Connexion à la base de données Postgis
REMOCRA_POSTGIS_DATABASE_HOST = localhost
REMOCRA_POSTGIS_DATABASE_NAME = remocra
REMOCRA_POSTGIS_DATABASE_PORT = 5432
REMOCRA_POSTGIS_DATABASE_USER_NAME = postgres
REMOCRA_POSTGIS_DATABASE_USER_PASSWORD = ${POSTGRES_DB_PASSWORD}

#Connexion à la base de données Oracle
REMOCRA_ORACLE_DATABASE_HOST = 
REMOCRA_ORACLE_DATABASE_NAME = 
REMOCRA_ORACLE_DATABASE_PORT = 
REMOCRA_ORACLE_DATABASE_USER_NAME = 
REMOCRA_ORACLE_DATABASE_USER_PASSWORD = 
EOF
fi

envsubst << "EOF" > /home/postgres/pdi/simple-jndi/jdbc.properties
#Connexion à la base de données Remocra Postgis
remocra_postgis/type=javax.sql.DataSource
remocra_postgis/driver=org.postgresql.Driver
remocra_postgis/url=jdbc:postgresql://localhost:5432/remocra
remocra_postgis/user=postgres
remocra_postgis/password=${POSTGRES_DB_PASSWORD}
EOF

sed -i "s/<password>.*<\/password>/<password>${POSTGRES_DB_PASSWORD}<\/password>/g" /home/postgres/pdi/repositories.xml

/home/postgres/pdi_permissions.sh

su postgres -c "/home/postgres/remocra_pdi/remocra_generer_fichier_proprietes.sh"


# ------------------------------
# TACHES PLANIFIEES
# ------------------------------
if ! crontab -u postgres -l > /dev/null; then
  echo && echo "Installation des tâches planifiées de l'utilisateur postgres"
  envsubst << "EOF" > /tmp/postgrescrontab.txt
# REMOcRA
# Alimente les risques express [toutes les 5 minutes]
#*/5 * * * * /home/postgres/remocra_pdi/remocra_integrer_kml.sh >> /var/remocra/pdi/log/remocra_integrer_kml.log 2>&1
# Exécute les demandes de traitements [toutes les 10 minutes]
*/10 * * * * /home/postgres/remocra_pdi/remocra_traiter_demandes.sh >> /var/remocra/pdi/log/remocra_traiter_demandes.log 2>&1
# Envoie les emails [toutes les 2 minutes]
*/2 * * * * /home/postgres/remocra_pdi/remocra_notifier_utilisateurs.sh >> /var/remocra/pdi/log/remocra_notifier_utilisateurs.log 2>&1
# Purge les fichiers (emails, traitements, demandes) [6h00]
0 6 * * * /home/postgres/remocra_pdi/remocra_purger.sh >> /var/remocra/pdi/log/remocra_purger.log 2>&1
#
# Crée les demandes d'export des hydrants indisponible de la veille [00:10]
# Voir au cas par cas
#
# Notification des indisponibilités temporaires à vérifier
*/5 * * * * /home/postgres/remocra_pdi/remocra_notifier_indispo_temporaires.sh >> /var/remocra/pdi/log/remocra_notifier_indispo_temporaires.log 2>&1
#
# Importe le référentiel et synchronise les données métier (à partir du zip sur APIS) [23:30]
#30 23 * * * /home/postgres/remocra_pdi/remocra_importer_referentiel_et_synchroniser_metier.sh >> /var/remocra/pdi/log/remocra_importer_referentiel_et_synchroniser_metier.log 2>&1
# Synchronise les alertes, exporte les hydrants et permis (jusqu'au zip sur APIS) [01:00]
#0 1 * * * /home/postgres/remocra_pdi/remocra_exporter_donnees_metier.sh >> /var/remocra/pdi/log/remocra_exporter_donnees_metier.log 2>&1
#
# Regeneration du fichier remocra.properties [toutes les 10 minutes]
*/10 * * * * /home/postgres/remocra_pdi/remocra_generer_fichier_proprietes.sh >> /var/remocra/pdi/log/remocra_generer_fichier_proprietes.log 2>&1
#
# Redefinition des acces de l'utilisateur postgres remocra [02:00]
#0 2 * * * /home/postgres/remocra_pdi/remocra_acces.sh  >> /var/remocra/pdi/log/remocra_acces.log 2>&1
#
# Sauvegardes des bases de données [21:00 et 21:10]
#10 21 * * * /home/postgres/backup/backup_remocra.sh
#00 21 * * * /home/postgres/backup/backup_pdi.sh
EOF
  crontab -u postgres /tmp/postgrescrontab.txt
  rm -f /tmp/postgrescrontab.txt
  service crond restart
fi


# Fixe les permissions
echo && echo "Redéfinition des permissions"
/home/postgres/pdi_permissions.sh

echo && echo "Fin de l'installation / mise à jour"

