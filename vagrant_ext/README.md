# Remocra - Vagrant externe

En partant d'un serveur CentOS 6X vierge (testé avec CentOS 6.4 dans une box Vagrant) :

1. Déposer les éléments dans le répertoire /livraison (package zip de remocra et 3 scripts shell)
2. Modifier les paramètres de launcher.sh  (mots de passe, mail, clé IGN)
3. Exécuter /livraison/launcher.sh

Dans cette version, GeoServer est installé en mode embarqué dans Jetty.

Exemple d'accès :

 - http://remocra.sdisxx.fr/remocra
 - http://remocra.sdisxx.fr/geoserver

Pour commencer :

 - s'authentifier avec l'utilisateur spécifié dans l'entête de la page
 - créer un utilisateur et s'assurer qu'un courriel d'inscription est bien reçu
 - ...

Pour continuer :

 - insérer les communes, les zones spéciales éventuelles, les zones de compétence, les voies, les utilisateurs
 - changer le mot de passe de l'utilisateur sdis-adm-app

