# Remocra - Web

<img alt="Présentation" src="https://www.atolcd.com/fileadmin/Images_pages_menu/Open_Source/Remocra/ecran_remocra.jpg" width="520">


## Fonctionnalités

* Gérer le **parc des points d’eau et structurer leur contrôle** (tournées)
* Enrichir le système d’alerte opérationnel avec un outil de **localisation d’anomalies**
* Gérer les demandes de **permis de construire**
* Fournir des **cartes liées aux Risques**
* Créer des **rapports diffusés par mails** aux partenaires institutionnels
* Exporter les données vers les systèmes tiers (alertes, système d'information interne,...)
* Interface web de **consultation et d’administration**
* **DFCI** : cartes et dépôt des fichiers de réception des travaux
* **Imprimer, exporter et importer** de données
* Droits paramétrables


## Premier run

La suite a été réalisée à partir d'un Linux.

Installer :
* [git](https://git-scm.com/) pour la gestion des sources
* [docker](https://www.docker.com/), [docker-compose](https://docs.docker.com/compose/) et [psql](http://www.postgresql.org/docs/9.5/static/app-psql.html) (paquet postgresql) pour créer une base de données de développements rapidement
* une [jdk 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) et [maven 3](https://maven.apache.org/) pour la compilation et la gestion des dépendances

Récupérer les sources du projet :

    mkdir -p ~/projets && cd ~/projets
    git clone git://github.com/atolcd/sdis-remocra.git

Démarrer le serveur de données et le serveur de cartes via docker-compose :

    cd ~/projets/sdis-remocra/db-docker && docker-compose up

Compléter l'installation de GeoServer :

    ~/projets/sdis-remocra/db-docker/geoserver/add_plugins.sh

Créer la base de données :

    PGPASSWORD=postgres ~/projets/sdis-remocra/server/sdis-remocra/home/postgres/remocra_db/reset_db.sh

Configurer l'adresse de GeoServer, dans le contexte docker :

    PGPASSWORD=postgres psql -h localhost -U postgres remocra -c "update remocra.param_conf set valeur='http://localhost:8090/geoserver' where cle='WMS_BASE_URL'"

Insérer un jeu de données minimal pour les tests :

    # Base de données
    PGPASSWORD=postgres ~/projets/sdis-remocra/server/sdis-remocra/home/postgres/remocra_db/dev/data_tests.sh
    # GeoServer
    ~/projets/sdis-remocra/db-docker/geoserver/dev/datatest.sh

Lancer l'application :

    cd ~/projets/sdis-remocra/remocra
    mvn install:install-file -Dfile=lib/irstv-cts.jar -DgroupId=org.cts -DartifactId=cts -Dversion=1.69 -Dpackaging=jar
    mvn tomcat:run

Ouvrir l'URL suivante dans un navigateur :
* [http://localhost:8080/remocra/](http://localhost:8080/remocra/)

## Pour continuer

* Changer le mot de passe de l'utilisateur sdis-adm-app
* Insérer les communes, les zones spéciales éventuelles, les zones de compétence, les voies, les utilisateurs
* Paramétrer les cartes
* ...


## Documentation

Se référer à la [page dédiée](../docs/index.adoc) : manuel d'administration, d'installation, PEI, etc.


## Licence

Le SDIS du Var a décidé de faire bénéficier ses confrères de sa démarche en redistribuant gratuitement l'outil sous licence [Creative Commons by-nc-sa 4.0](https://github.com/atolcd/sdis-remocra/LICENSE.txt)

[Atol Conseils et Développements](http://www.atolcd.com), suivez-nous sur twitter [@atolcd](https://twitter.com/atolcd)
