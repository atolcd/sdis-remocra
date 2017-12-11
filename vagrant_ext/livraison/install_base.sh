# Gestion des erreurs
set -e

# ---------------------------------------------------
# - Installation de la base technique de Remocra
# ---------------------------------------------------

echo && echo "Paquets de base"

yum -y update && yum -y upgrade


# ------------------------------
# - Utilitaires
# ------------------------------

assumePackageIsPresent unzip
assumePackageIsPresent wget
assumePackageIsPresent curl
assumePackageIsPresent openssh-clients
assumePackageIsPresent gettext

# TODO ? Serveur de temps
# TODO ? Firewall
# TODO ? SE Linux


# ------------------------------
# - Postgresql + base de données
# ------------------------------

if ! isPackagePresent postgresql || ! isPackagePresent postgresql-server || ! isPackagePresent postgresql-contrib || ! isPackagePresent postgis; then
  echo && echo "Installation des paquets PostgreSQL et création user postgres"

  # PostgreSQL en démarrage automatique
  yum -y install postgresql postgresql-server postgresql-contrib
  service postgresql initdb
  chkconfig postgresql on

  # Accès distants
  cp /var/lib/pgsql/data/postgresql.conf /var/lib/pgsql/data/postgresql.conf.save
  sed -i "s/^listen_addresses = '/#listen_addresses = '/g" /var/lib/pgsql/data/postgresql.conf

  cp /var/lib/pgsql/data/pg_hba.conf /var/lib/pgsql/data/pg_hba.conf.save
  sed -i 's/^host\s\+all\s\+all\s\+\(.*\)\s\+trust/host all all \1 md5/g' /var/lib/pgsql/data/pg_hba.conf
  sed -i 's/^host\s\+all\s\+all\s\+\(.*\)\s\+ident/host all all \1 md5/g' /var/lib/pgsql/data/pg_hba.conf

  # Répertoire de l'utilisateur système postgres et droits
  service postgresql stop
  mkdir -p /home/postgres
  cp /var/lib/pgsql/.bash_profile /home/postgres
  chown -R postgres:postgres /home/postgres
  chmod -R 755 /home/postgres
  usermod -d /home/postgres postgres
  service postgresql start
  sleep 10

  # Si besoin, mot de passe postgres système
  #passwd XXXXXXXX

  # Mot de passe rôle postgres
  su postgres -c "psql -c \"alter user postgres encrypted password'${POSTGRES_DB_PASSWORD}'\" postgres" 

  # Repository EPEL
  cd /root
  rpm -Uvh http://mirrors.ircam.fr/pub/fedora/epel/6/x86_64/epel-release-6-8.noarch.rpm
  # (Si besoin, utiliser un autre miroir)
  yum -y install libXp-devel

  # PostGIS et template
  yum -y install postgis

  su postgres -c "createdb template_postgis"
  su postgres -c "createlang plpgsql template_postgis"
  su postgres -c "psql -d template_postgis -f /usr/share/pgsql/contrib/postgis-64.sql"
  su postgres -c "psql -d template_postgis -f /usr/share/pgsql/contrib/postgis-1.5/spatial_ref_sys.sql"
fi


# ------------------------------
# - Serveur web Apache
# ------------------------------
if ! isPackagePresent httpd; then
  yum -y install httpd
  chkconfig httpd on
  chsh -s /bin/sh apache
  service httpd start
fi


# ------------------------------
# - JDK
# ------------------------------
if [ -z "${JAVA_HOME}" ]; then
  echo && echo "Installation de java-1.7.0-openjdk-devel"

  # JAVA
  yum -y install java-1.7.0-openjdk-devel

  # JAVA_HOME
  envsubst >> ~/.bashrc << "EOF" 
export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk.x86_64/
EOF
  . ~/.bashrc

  # JAI
  cd ${JAVA_HOME}/jre
  cp /livraison/ext/jai-1_1_3-lib-linux-amd64-jre.bin . 2> /dev/null || (echo "Téléchargement JAI" && wget -q http://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib-linux-amd64-jre.bin)
  echo y | sh jai-1_1_3-lib-linux-amd64-jre.bin > /dev/null
  rm -f jai-1_1_3-lib-linux-amd64-jre.bin

  # JAI Image I/O
  cd ${JAVA_HOME}/jre
  cp /livraison/ext/jai_imageio-1_1-lib-linux-amd64-jre.bin . 2> /dev/null || (echo "Téléchargement JAI-IMAGEIO" && wget -q http://download.java.net/media/jai-imageio/builds/release/1.1/jai_imageio-1_1-lib-linux-amd64-jre.bin)
  export _POSIX2_VERSION=199209
  echo y | sh jai_imageio-1_1-lib-linux-amd64-jre.bin > /dev/null
  rm -f jai_imageio-1_1-lib-linux-amd64-jre.bin
fi


# ------------------------------
# - TOMCAT
# ------------------------------
if ! isPackagePresent tomcat6 || ! isPackagePresent tomcat6-webapps || ! isPackagePresent tomcat6-admin-webapps; then
  echo && echo "Installation de Tomcat"
  yum -y install tomcat6 tomcat6-webapps tomcat6-admin-webapps
  #service tomcat6 start
  chkconfig tomcat6 on

  # Gestion des URIs avec accents (exemple : GET avec QueryParam)
  cp /etc/tomcat6/server.xml /etc/tomcat6/server.xml_save
  sed -i "s/<Connector/<Connector URIEncoding=\"utf-8\"/" /etc/tomcat6/server.xml

  if [ $(grep "https://" <<<"${URL_SITE}") ]; then
    # Pour une configuration HTTPS prise en charge au niveau de la passerelle :
    sed -i "s/<Connector URIEncoding=\"utf-8\" port=\"8080\"/<Connector URIEncoding=\"utf-8\" secure=\"true\" scheme=\"https\" proxyPort=\"443\" port=\"8080\"/" /etc/tomcat6/server.xml
  fi

  # Administrateur Tomcat
  cp /etc/tomcat6/tomcat-users.xml /etc/tomcat6/tomcat-users.xml.save
  sed -i "s/^<tomcat-users>/<tomcat-users>\n  <user username=\"${TOMCAT_ADMIN_USERNAME}\" password=\"${TOMCAT_ADMIN_PASSWORD}\" roles=\"admin,manager\"\/>\n/" /etc/tomcat6/tomcat-users.xml

  cp /etc/sysconfig/tomcat6 /etc/sysconfig/tomcat6.save
  # JAVA_OPTS et CATALINA_OPTS
  sed -i 's/^#JAVA_OPTS="-Xminf0\.1 -Xmaxf0\.3"/#JAVA_OPTS="-Xminf0\.1 -Xmaxf0\.3"\nJAVA_OPTS="\$\{JAVA_OPTS\} -Xms1G -Xmx5G -XX:PermSize=1G -XX:MaxPermSize=2G"\nJAVA_OPTS="\$\{JAVA_OPTS\} -server -Xverify:none -XX:\+DisableExplicitGC -XX:\+UseCompressedOops -Djdk\.map\.althashing\.threshold=512 -Djava\.awt\.headless=true"\nJAVA_OPTS="\$\{JAVA_OPTS\} -XX:\+UseConcMarkSweepGC -XX:\+CMSIncrementalMode"\nJAVA_OPTS="\$\{JAVA_OPTS\} -Xloggc:\/var\/log\/tomcat6\/gc-`date \+%Y%m%d-%H%M%S`\.log -XX:\+PrintGCDetails -XX:\+PrintTenuringDistribution"\nCATALINA_OPTS="\$CATALINA_OPTS -Dcom\.sun\.management\.jmxremote=true -Dcom\.sun\.management\.jmxremote\.port=9010 -Dcom\.sun\.management\.jmxremote\.authenticate=false -Dcom\.sun\.management\.jmxremote\.ssl=false"/' /etc/sysconfig/tomcat6
  # LANG
  sed -i 's/^#LANG="\(.*\)/#LANG="\1\nLANG="fr_FR.UTF-8"/' /etc/sysconfig/tomcat6

  service tomcat6 restart
fi


# ------------------------------
# - GEOSERVER
# ------------------------------
export GEOSERVER_VERSION=${GEOSERVER_VERSION:=2.6.2}
if ( ! (ls /var/lib/geoserver > /dev/null 2> /dev/null) ); then
  cd /root
  cp /livraison/ext/geoserver-${GEOSERVER_VERSION}-bin.zip . 2> /dev/null || (echo "Téléchargement GeoServer" && wget -q http://sourceforge.net/projects/geoserver/files/GeoServer/${GEOSERVER_VERSION}/geoserver-${GEOSERVER_VERSION}-bin.zip)
  unzip -q geoserver-${GEOSERVER_VERSION}-bin.zip -d /var/lib
  mv -f /var/lib/geoserver-${GEOSERVER_VERSION} /var/lib/geoserver

  # GEOSERVER_HOME et GEOSERVER_DATA_DIR
  envsubst >> /var/lib/geoserver/.bashrc << "EOF" 
export JAVA_HOME=${JAVA_HOME}
export GEOSERVER_HOME=/var/lib/geoserver
export GEOSERVER_DATA_DIR=/var/remocra/geoserver_data
#export JAVA_OPTS="-XX:MaxPermSize=128m"
EOF
  envsubst >> ~/.bashrc << "EOF" 
export GEOSERVER_HOME=/var/lib/geoserver
export GEOSERVER_DATA_DIR=/var/remocra/geoserver_data
#export JAVA_OPTS="-XX:MaxPermSize=128m"
EOF
  . ~/.bashrc

  # Utilisateur et groupe
  groupadd geoserver
  useradd --no-create-home -s /sbin/nologin -g geoserver -d $GEOSERVER_HOME geoserver

  chown -R geoserver:geoserver $GEOSERVER_HOME

  cd $GEOSERVER_HOME/webapps/geoserver/WEB-INF/lib
  cp /livraison/ext/geoserver-${GEOSERVER_VERSION}-querylayer-plugin.zip . 2> /dev/null || (echo "Téléchargement GeoServer Querylayer plugin" && wget -q http://downloads.sourceforge.net/geoserver/geoserver-${GEOSERVER_VERSION}-querylayer-plugin.zip)
  unzip -qn geoserver-${GEOSERVER_VERSION}-querylayer-plugin.zip
  rm -f geoserver-*-querylayer-plugin.zip
  chown geoserver:geoserver gs-querylayer-*.jar
  chmod 644 gs-querylayer-*.jar

  sed -i "s/SystemProperty name=\"jetty.port\" default=\"8080\"/SystemProperty name=\"jetty.port\" default=\"8090\"/g" $GEOSERVER_HOME/etc/jetty.xml
  sed -i "s/<Set name=\"confidentialPort\">8443<\/Set>/<Set name=\"confidentialPort\">9443<\/Set>/g" $GEOSERVER_HOME/etc/jetty.xml
  sed -i "s/name=\"port\">8080/name=\"port\">8090/g" $GEOSERVER_HOME/etc/stresstest.xml

  # Geoserver en tant que service
  envsubst > /etc/sysconfig/geoserver << "EOF"
# Service-specific configuration file for geoserver. This will be sourced by
# the SysV init script after the global configuration file
# /etc/geoserver/geoserver.conf, thus allowing values to be overridden in
# a per-service manner.
#
# NEVER change the init script itself. To change values for all services make
# your changes in /etc/geoserver/geoserver.conf
#
# To change values for a specific service make your edits here.
# To create a new service create a link from /etc/init.d/<your new service> to
# /etc/init.d/geoserver (do not copy the init script) and make a copy of the
# /etc/sysconfig/geoserver file to /etc/sysconfig/<your new service> and change
# the property values so the two services won't conflict. Register the new
# service in the system as usual (see chkconfig and similars).
#

CONNECTOR_PORT=8090
export JAVA_HOME=${JAVA_HOME}
export GEOSERVER_HOME=${GEOSERVER_HOME}

export JAVA_OPTS="-Xms1G -Xmx5G -XX:PermSize=1G -XX:MaxPermSize=2G"
export JAVA_OPTS="${JAVA_OPTS} -server -Xverify:none -XX:+DisableExplicitGC -XX:+UseCompressedOops -Djdk.map.althashing.threshold=512 -Djava.awt.headless=true"
export JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode"
export JAVA_OPTS="${JAVA_OPTS} -Xloggc:/var/log/geoserver/gc-`date +%Y%m%d-%H%M%S`.log -XX:+PrintGCDetails -XX:+PrintTenuringDistribution"

EOF
  chmod 755 /etc/sysconfig/geoserver

  cat > /etc/init.d/geoserver << "EOF"
#!/bin/bash
#
# geoserver      This shell script takes care of starting and stopping GeoServer
#
# chkconfig: - 80 20
#
### BEGIN INIT INFO
# Provides: geoserver
# Required-Start: $network $syslog
# Required-Stop: $network $syslog
# Default-Start:
# Default-Stop:
# Description: Release implementation for Servlet 2.5 and JSP 2.1
# Short-Description: start and stop geoserver
### END INIT INFO
#
# - originally written by Henri Gomez, Keith Irwin, and Nicolas Mailhot
# - heavily rewritten by Deepak Bhole and Jason Corley
# - adpated by cva@atolcd.com for standalone CentOS Geoserver config
#

## Source function library.
#. /etc/rc.d/init.d/functions
# Source LSB function library.
if [ -r /lib/lsb/init-functions ]; then
    . /lib/lsb/init-functions
else
    exit 1
fi

DISTRIB_ID=`lsb_release -i -s 2>/dev/null`

NAME="$(basename $0)"
unset ISBOOT
if [ "${NAME:0:1}" = "S" -o "${NAME:0:1}" = "K" ]; then
    NAME="${NAME:3}"
    ISBOOT="1"
fi

# For SELinux we need to use 'runuser' not 'su'
if [ -x "/sbin/runuser" ]; then
    SU="/sbin/runuser -s /bin/sh"
else
    SU="/bin/su -s /bin/sh"
fi

# Get the geoserver config (use this for environment specific settings)
GEOSERVER_CFG="/etc/${NAME}/${NAME}.conf"
if [ -r "$GEOSERVER_CFG" ]; then
    . $GEOSERVER_CFG
fi

# Get instance specific config file
if [ -r "/etc/sysconfig/${NAME}" ]; then
    . /etc/sysconfig/${NAME}
fi

# Shutdown
SHUTDOWN_WAIT="${SHUTDOWN_WAIT:-10}"
SHUTDOWN_VERBOSE="${SHUTDOWN_VERBOSE:-true}"

# Define which connector port to use
CONNECTOR_PORT="${CONNECTOR_PORT:-8080}"

# Geoserver program name
GEOSERVER_PROG="${NAME}"
        
# Define the geoserver username
GEOSERVER_USER="${GEOSERVER_USER:-geoserver}"

# Define the geoserver group
GEOSERVER_GROUP="${GEOSERVER_GROUP:-`id -gn $GEOSERVER_USER`}"

# Define the geoserver log file
GEOSERVER_LOG="${GEOSERVER_LOG:-/var/log/${NAME}-initd.log}"

# Define the pid file name
# If change is needed, use sysconfig instead of here
export GEOSERVER_PID="${GEOSERVER_PID:-/var/run/${NAME}.pid}"

# Define the Geoserver home
GEOSERVER_HOME=${GEOSERVER_HOME:-/var/lib/geoserver}

RETVAL="0"


# rhbz 757632
function version() {
        result=$($SU $GEOSERVER_USER -c "awk '/^version/ {print \$3;}' $GEOSERVER_HOME/VERSION.txt")
        if [ $? != 0 ]; then
            RETVAL="4"
            return
        fi
        echo $result && echo $result >> ${GEOSERVER_LOG} 2>&1
}

# See how we were called.
function start() {
    echo -n "Starting ${GEOSERVER_PROG}: "

    if [ "$RETVAL" != "0" ]; then 
        log_failure_msg
        return
    fi

    if [ $(/usr/bin/pgrep -d , -u ${GEOSERVER_USER} -G ${GEOSERVER_GROUP} java) ]; then
        log_success_msg
        if [ "$DISTRIB_ID" = "MandrivaLinux" ]; then
            echo
        fi
        RETVAL="0"
        return
    fi

    $SU $GEOSERVER_USER -c "${GEOSERVER_HOME}/bin/startup.sh &" >> ${GEOSERVER_LOG} 2>&1 || RETVAL="4"
    sleep 2

    if [ ! $(/usr/bin/pgrep -d , -u ${GEOSERVER_USER} -G ${GEOSERVER_GROUP} java) ]; then
        RETVAL="5"
    fi

    if [ "$RETVAL" -eq "0" ]; then 
        log_success_msg
    else
        log_failure_msg "Error code ${RETVAL}"
    fi
    if [ "$DISTRIB_ID" = "MandrivaLinux" ]; then
        echo
    fi
}

function stop() {
    echo -n "Stopping ${GEOSERVER_PROG}: "

    $SU $GEOSERVER_USER -c "${GEOSERVER_HOME}/bin/shutdown.sh &" >> ${GEOSERVER_LOG} 2>&1 || RETVAL="4"

      if [ "$RETVAL" -eq "0" ]; then
         count="0"
         kpid=$(/usr/bin/pgrep -d , -u ${GEOSERVER_USER} -G ${GEOSERVER_GROUP} java)
         if [ $kpid ]; then
            until [ "$(ps --pid $kpid | grep -c $kpid)" -eq "0" ] || \
                      [ "$count" -gt "$SHUTDOWN_WAIT" ]; do
                    if [ "$SHUTDOWN_VERBOSE" = "true" ]; then
                        echo "waiting for processes $kpid to exit"
                    fi
                    sleep 1
                    let count="${count}+1"
                done
                if [ "$count" -gt "$SHUTDOWN_WAIT" ]; then
                    if [ "$SHUTDOWN_VERBOSE" = "true" ]; then
                        log_warning_msg "killing processes which did not stop after ${SHUTDOWN_WAIT} seconds"
                    fi
                    kill -9 $kpid
                fi
                log_success_msg
        else
            log_failure_msg
            RETVAL="5"
        fi
    else
        log_success_msg
        RETVAL="0"
    fi
    if [ "$DISTRIB_ID" = "MandrivaLinux" ]; then
        echo
    fi
}

function status()
{
    kpid="$(/usr/bin/pgrep -d , -u ${GEOSERVER_USER} -G ${GEOSERVER_GROUP} java)"
    if [ -z "$kpid" ]; then
        log_success_msg "${NAME} is stopped"
        RETVAL="3"
     else
         log_success_msg "${NAME} (pid ${kpid}) is running..."
         RETVAL="0"
     fi
}

function usage()
{
   echo "Usage: $0 {start|stop|restart|condrestart|try-restart|reload|force-reload|status|version}"
   RETVAL="2"
}

# See how we were called.
RETVAL="0"
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        sleep 10
        start
        ;;
    condrestart|try-restart)
        if [ $(/usr/bin/pgrep -d , -u ${GEOSERVER_USER} -G ${GEOSERVER_GROUP} java) ]; then
            stop
            start
        fi
        ;;
    reload)
        RETVAL="3"
        ;;
    force-reload)
        if [ $(/usr/bin/pgrep -d , -u ${GEOSERVER_USER} -G ${GEOSERVER_GROUP} java) ]; then
            stop
            start
        fi
        ;;
    status)
        status
        ;;
    version)
	version
        ;;
    *)
      usage
      ;;
esac

exit $RETVAL
EOF
  chmod 755 /etc/init.d/geoserver

  chkconfig --add geoserver
  chkconfig --list geoserver
  chkconfig geoserver on

  service geoserver start
fi


# ------------------------------
# - ETL Pentaho Data Integrator
# ------------------------------

if [ ! -f "/home/postgres/pdi/kitchen.sh" ]; then
  cd /home/postgres
  cp /livraison/ext/pdi-ce-4.4.0-stable.zip . 2> /dev/null || (echo "Téléchargement PDI CE" && wget -q http://sourceforge.net/projects/pentaho/files/Data%20Integration/4.4.0-stable/pdi-ce-4.4.0-stable.zip)
  unzip -qo /home/postgres/pdi-ce-4.4.0-stable.zip -d /home/postgres
  yes | /bin/cp -rf /home/postgres/pdi/*  /home/postgres/data-integration
  rm -rf /home/postgres/pdi
  mv /home/postgres/data-integration /home/postgres/pdi

  cd /home/postgres/pdi
  chmod +x *.sh
  chown -R postgres:postgres /home/postgres

  cp /home/postgres/pdi/kitchen.sh /home/postgres/pdi/kitchen.sh.save
  sed -i "s/-cp \$CLASSPATH/-cp \$CLASSPATH -Dfile\.encoding=utf8/g" /home/postgres/pdi/kitchen.sh
fi

