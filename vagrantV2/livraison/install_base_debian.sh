
# Gestion des erreurs
set -e
# ---------------------------------------------------
# - Installation de la base technique de Remocra
# ---------------------------------------------------
  install_paquet_base(){
    apt -y update && apt -y upgrade
    # ------------------------------
    # - Utilitaires
    # ------------------------------
    apt-get -y install unzip \
    wget \
    curl 
  }
  install_postgres(){
    sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

    # Import the repository signing key:
    wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -

    # Installation de PostgreSQL-11.
    apt-get update
    apt-get -y install postgresql-11
    # Accès distants
    cp  /etc/postgresql/11/main/postgresql.conf  /etc/postgresql/11/main/postgresql.conf.save
    sed -i "s/^#port = /port = /g"  /etc/postgresql/11/main/postgresql.conf
    sed -i "s/^#listen_addresses = 'localhost'/listen_addresses = '*'/g"  /etc/postgresql/11/main/postgresql.conf
    cp /etc/postgresql/11/main/pg_hba.conf /etc/postgresql/11/main/pg_hba.conf.save
    sed -i 's/^host\s\+all\s\+all\s\+\(.*\)\s\+trust/host all all \1 md5/g' /etc/postgresql/11/main/pg_hba.conf
    sed -i 's/^host\s\+all\s\+all\s\+\(.*\)\s\+ident/host all all \1 md5/g' /etc/postgresql/11/main/pg_hba.conf
    sed -i 's/^local\s\+all\s\+all\s\+\(.*\)\s\+peer/local all all \1 md5/g' /etc/postgresql/11/main/pg_hba.conf
    # Répertoire de l'utilisateur système postgres et droits
    systemctl stop postgresql
    systemctl start postgresql
    sleep 10
    # Mot de passe rôle postgres
    su postgres -c "psql -c \"alter user postgres encrypted password '${POSTGRES_DB_PASSWORD}'\" postgres" 
  }
  install_postgis(){
      cd /root
    # PostGIS et template
    # Install postgis dependencies
    echo "Installation de Postgis"
    apt-get -y install postgis postgresql-11-postgis-2.5
    #echo "Installation des paquets PostgreSQL et création user postgres"
    su postgres -c "createdb template_postgis"
    su postgres -c "psql -d template_postgis -f /usr/share/postgresql/11/contrib/postgis-2.5/postgis.sql"
    su postgres -c "psql -d template_postgis -f /usr/share/postgresql/11/contrib/postgis-2.5/spatial_ref_sys.sql"
  }
  
install_docker(){
 apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common

    curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -
    sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/debian \
   $(lsb_release -cs) \
   stable"
   apt-get update
   apt-get -y install docker-ce docker-ce-cli containerd.io
   systemctl enable docker

}  

install_docker_compose(){
 sudo curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  mv /usr/local/bin/docker-compose /usr/bin/docker-compose
  chmod +x /usr/bin/docker-compose
}


echo "**********Installation des paquets de base**************"
install_paquet_base
echo "**********Installation de postgresql********************"
install_postgres
echo "**********Installation de postgis***********************"
install_postgis
echo "**********Installation de docker************************"
install_docker
echo "**********Installation de docker-compose****************"
install_docker_compose 
echo "**********Fin d'installation****************************"

