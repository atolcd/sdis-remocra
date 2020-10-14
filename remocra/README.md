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
* [docker](https://www.docker.com/), [docker-compose](https://docs.docker.com/compose/) et [psql](http://www.postgresql.org/docs/9.5/static/app-psql.html) (paquet postgresql)

Docker est exploité pour :
* créer un serveur de données PostgreSQL et un serveur cartographique GeoServer de développements
* compiler / exécuter l'application via un conteneur qui contient les outils à utiliser : une [jdk 1.8](https://www.oracle.com/fr/java/technologies/javase/javase-jdk8-downloads.html), [maven 3](https://maven.apache.org/) et [sencha-cmd 3.0.2](https://docs.sencha.com/cmd/)

Récupérer les sources du projet :

```bash
    mkdir -p ~/projets/atolcd && cd ~/projets/atolcd
    git clone git://github.com/atolcd/sdis-remocra.git
```

Démarrer le serveur de données et le serveur de cartes via docker-compose :

```bash
    cd ~/projets/atolcd/sdis-remocra/db-docker && docker-compose up
```

Compléter l'installation de GeoServer :

```bash
    ~/projets/atolcd/sdis-remocra/db-docker/geoserver/add_plugins.sh
```

Créer la base de données :

```bash
    PGPASSWORD=postgres ~/projets/atolcd/sdis-remocra/server/sdis-remocra/home/postgres/remocra_db/reset_db.sh
```

Insérer un jeu de données minimal pour les tests :

```bash
    # Base de données
    PGPASSWORD=postgres ~/projets/atolcd/sdis-remocra/server/sdis-remocra/home/postgres/remocra_db/dev/data_tests.sh
    # GeoServer
    ~/projets/atolcd/sdis-remocra/db-docker/geoserver/dev/datatest.sh
```

Configurer l'adresse de GeoServer, dans le contexte docker :

```bash
    PGPASSWORD=postgres psql -h localhost -U postgres remocra -c "update remocra.param_conf set valeur='http://geoserver.sdisxx.fr:8080/geoserver' where cle='WMS_BASE_URL'" 
```

Lancer l'application :

```bash
cd ~/projets/atolcd/sdis-remocra
docker run --rm \
  --name remocra \
  -u $(id -u):$(id -g) \
  -v "$(pwd)":/app -w /app \
  -p 0.0.0.0:8080:8080 \
  -v "/var/remocra/layers":/var/remocra/layers \
  --link dbdocker_postgres_1:postgis.sdisxx.fr --link dbdocker_geoserver_1:geoserver.sdisxx.fr \
  -v ~/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS="-Duser.home=/var/maven" -e "npm_config_cache=npm-cache" \
  cvagner/docker-jdk-maven-sencha-cmd:8-3.6.3-3.0.2 \
  \
  mvn tomcat7:run -Dclient-ng.dir=client-ng/dist/remocra/static -Ddatabase.url=jdbc:postgresql://postgis.sdisxx.fr:5432/remocra
```

Ouvrir l'URL suivante dans un navigateur :
* [http://localhost:8080/remocra/](http://localhost:8080/remocra/)


## Client NG

Le client NG fournit des composants réalisés avec des versions plus récentes de librairies / frameworks :
* [Vue.js](https://vuejs.org/)
* [OpenLayers 5](https://openlayers.org/)
* [npm](https://www.npmjs.com/)
* [Vue CLI](https://cli.vuejs.org/)

Ces composants sont utilisés dans la version classique de Remocra.

Pour avoir accès au client NG (crise, nouvelle fiche PEI), il faut au préalable construire les assets du projet concerné (cf. propriété système "client-ng.dir" définie plus haut pour modifier le chemin) :

    cd ~/projets/atolcd/sdis-remocra/client-ng
    npm install && npm run build

De manière à simplifier le développement, il est possible d'exécuter le projet de manière indépendante :

    cd ~/projets/atolcd/sdis-remocra/client-ng
    npm install && npm run serve

S'identifier sur remocra classique et ouvrir la crise 1 (exemple) dans un nouvel onglet :
* [http://localhost:8081/?1](http://localhost:8081/?1)


## Pour continuer

* Changer le mot de passe de l'utilisateur sdis-adm-app
* Insérer les communes, les zones spéciales éventuelles, les zones de compétence, les voies, les utilisateurs
* Paramétrer les cartes
* ...


## Documentation

Se référer à la [page dédiée](../docs/index.adoc) : manuel d'administration, d'installation, PEI, etc.


## Licence

Le SDIS du Var a décidé de faire bénéficier ses confrères de sa démarche en redistribuant gratuitement l'outil sous licence [Creative Commons by-nc-sa 4.0](https://github.com/atolcd/sdis-remocra/blob/master/LICENSE.txt)

[Atol Conseils et Développements](http://www.atolcd.com), suivez-nous sur twitter [@atolcd](https://twitter.com/atolcd)
