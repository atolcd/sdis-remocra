# API-REMOCRA

Ce projet dépend d'autres projets placés dans le répertoire parent. Les commandes qui suivent sont réalisées dans le répertoire parent :
<pre>
cd ~/projets/atolcd/sdis-remocra/api-remocra
</pre>



## Génération du modèle JOOQ

Pré-requis : avoir une base dans le bon schéma

<pre>
mvn clean jooq-codegen:generate -pl api
</pre>


## Génération de la documentation

### Complète

<pre>
mvn clean package -Denv=dev
</pre>

Fichier généré (exemple) : doc/target/api-remocra-doc-0.1-SNAPSHOT.zip



## Première exécution

### Maven
Exemple en ligne de commande via maven (générer la documentation au préalable si nécessaire) :
<pre>
mvn clean test -Prunserver
</pre>

Et ouvrir la page http://localhost:8881.

### IntelliJ IDEA
Autre exemple de contexte d'exécution défini dans le projet IntelliJ IDEA :

<pre>
Main class : fr.sdis83.remocra.WebApp

Use classpath of module : api-remocra-api

VM arguments :
-Dapi-remocra.http.port=8881
-Dorg.jboss.logging.provider=slf4j
-Dapi-remocra.http.doc-path=doc/target
</pre>

