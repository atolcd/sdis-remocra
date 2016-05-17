# Remocra - Mobile

<img alt="Présentation" src="http://docplayer.fr/docs-images/24/2536007/images/6-0.jpg" width="250">


## Fonctionnalités

* Assurer les **tournées de contrôle** et de **reconnaissance opérationnelle** des points d'eau
* Réaliser des **saisies terrain en mode déconnecté**
* Visualiser immédiatement la disponibilité des points d'eau
* **Prendre des photos** des points d'eau en les associant automatiquement
* **Créer un point d'eau en récupérant la position courante**


## Construction

### Prérequis

La suite a été réalisée à partir d'un Linux.

Installer :
* [git](https://git-scm.com/) pour la gestion des sources
* une [jdk >= 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) et [gradle 1.5](http://gradle.org/) pour la compilation et la gestion des dépendances
* un environnement de développement Android (au choix) :
    * Ligne de commandes : [Android SDK](http://developer.android.com/sdk/index.html)
    * Environnement graphique : [Android Studio](http://developer.android.com/tools/studio/index.html)

Créer un keystore puis définir les variables suivantes dans le profil de l'utilisateur pour générer des releases :

    cat << 'EOF' >> ~/.bashrc
    
    export REMOCRA_MOBILE_KEYSTORE=/chemin/vers/le/keystore/a/utiliser/remocra.keystore
    export REMOCRA_MOBILE_KEYSTORE_PASSWORD=le_mot_de_passe_du_keystore
    export REMOCRA_MOBILE_KEY_ALIAS=l_alias_de_la_clé
    export REMOCRA_MOBILE_KEY_PASSWORD=le_mot_de_passe_de_la_clé
    EOF
    
    . ~/.bashrc


### Compilation d'une release

Récupérer les sources du projet :

    mkdir -p ~/projets && cd ~/projets
    git clone git://github.com/atolcd/sdis-remocra.git

Exécuter le script suivant :

    ~/projets/sdis-remocra/mobile/RemocraMobile/build-release.sh

L'APK est disponible ici :
* ~/projets/sdis-remocra/mobile/RemocraMobile/build/outputs/apk/RemocraMobile-release.apk



## Licence

Le SDIS du Var a décidé de faire bénéficier ses confrères de sa démarche en redistribuant gratuitement l'outil sous licence [Creative Commons by-nc-sa 4.0](https://github.com/atolcd/sdis-remocra/LICENSE.txt)

[Atol Conseils et Développements](http://www.atolcd.com), suivez-nous sur twitter [@atolcd](https://twitter.com/atolcd)
