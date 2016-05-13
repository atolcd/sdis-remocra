/**
 * @class Sdis.Remocra.network.ServerSession
 *
 * Gestion de la session serveur :<li>
 * <ul>file d'attente des requêtes en 401,</ul>
 * <ul>reconnexion login/password,</ul>
 * <ul>rejeu de requêtes en attente.</ul>
 * </li>
 */

Ext.require('Sdis.Remocra.auth.AuthWindow');
Ext.require('Sdis.Remocra.auth.ReAuthWindow');

Ext.ns('Sdis.Remocra.network');
Ext.define('Sdis.Remocra.network.ServerSession', {
        singleton: true,
        
    /*******************************************/
    /* Données de l'utilisateur                */
    /*******************************************/
    userData: {},
    getUserData: function(key) {
        return Sdis.Remocra.network.ServerSession.userData[key];
    },
    setUserData: function(key, value) {
        Sdis.Remocra.network.ServerSession.userData[key] = value;
    },
    /*
     *Verifie que l'utilisateur courant n'a pas changé. S'il a changé, il faut rafraichir l'affichage. 
     */
    checkLogin : function(){
        var userLogin = Ext.util.Cookies.get('loginUser');
        
        var localUserLogin = Sdis.Remocra.network.ServerSession.getUserData('login');
        
        if (!Ext.isEmpty(localUserLogin) && userLogin != localUserLogin){
            this.stopLoginWatcher();
            Ext.MessageBox.alert(
                    'Changement de profil',
                    'Un changement de profil à été détecté. L\'application va être rechargée pour refléter ce changement.',
                    Sdis.Remocra.util.Util.forceReload,
                    this
                );
        }
        
    },
    startLoginWatcher : function(){
        this.loginInterval = setInterval(Ext.bind(this.checkLogin, this, []), 5000);
    },
    stopLoginWatcher : function(){
        clearInterval(this.loginInterval);
    },
    /*******************************************/
    /* Déconnexion                             */
    /*******************************************/
    // Conservation de la connexion actuelle via appel Ajax
    keepConnection: function() {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../dummy"),
            method:'GET',
            scope: this
        });
    },
    /*******************************************/
    /* Queue de requêtes en 401                */
    /*******************************************/
    wrDelay : 2000,
    manageWithoutWrDelay : false, //Gestion de la queue 401 par le composant appelant

    // File d'attente (queue FIFO) de requête pour lesquelles l'utilisateur doit être authentifié
    wrQueue : [],
    cancelWR : function() {
        var len=this.wrQueue.length;
        if (len>0) {
            this.wrQueue = [];
            Sdis.Remocra.util.Msg.msg('Gestionnaire de connexions',
            len>1?len+" requêtes ont été annulées":"Une requête a été annulée");
        }
    },
    runWR : function() {
        var len=this.wrQueue.length, i, wr;
        var fnDefer = function(param) {
            Ext.Ajax.request(param);
        };
        if (len>0) {
            for (i=0 ; i<len ; ++i) {
                wr = this.wrQueue[0];
                this.wrQueue.splice(0, 1); // on défile
                Ext.Function.defer(fnDefer, this.manageWithoutWrDelay ? 0 : i*this.wrDelay, this, [wr.options]);
            }
            this.cancelWR(); // au cas où (ajout de requêtes en concurrence)
            Sdis.Remocra.util.Msg.msg('Gestionnaire de connexions',
                len>1?len+" requêtes ont été rejouées":"Une requête a été rejouée");
            if (this.manageWithoutWrDelay) {
                this.manageWithoutWrDelay = false;
            }
        }
    },
    /*******************************************/
    /* Gestion globale des erreurs 401         */
    /*******************************************/
    // 401 Unauthorized http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2
    manage401response : function(requestObj) {

        var dontreauth = (requestObj.options.params && typeof(requestObj.options.params)=="string"
            && requestObj.options.params.indexOf("dontreauth=true")>-1)
        || (requestObj.options.params && requestObj.options.params.dontreauth===true);
        if (dontreauth) {
            // Cas d'un requête de réauthentification (401 alors qu'on est déjà en train de se reconnecter)
            // ou de d'une requête de déconnexion
            return;
        }

        this.wrQueue[this.wrQueue.length] = requestObj; // on enfile
        if (this.wrQueue.length > 1) {
            // La suite (fenêtre de reconnexion) ne concerne que la première requête à réauthentifier
            return;
        }

        // Cas d'un première requête à réauthentifier : on ouvre la fenêtre de réauthentification
        var win = null;
        var login = Sdis.Remocra.network.ServerSession.getUserData('login'); 
        if (login==null || login=='') {
            // Jamais été connecté
            win = new Sdis.Remocra.auth.AuthWindow();
        } else {
            // La session a été perdue
            win = new Sdis.Remocra.auth.ReAuthWindow();
        }
        win.show();
        return false;
    },
    switchUser : function(){
        (new Sdis.Remocra.auth.AuthWindow()).show();
    },
    deauthenticate: function() {
        var theUrl = Sdis.Remocra.util.Util.withBaseUrl("../auth/logout");
        Ext.Ajax.request({
            url: theUrl,
            method: 'POST',
            params: { dontreauth: true },
            scope: this,
            callback: function(options, success, response) {
                // Retour à l'accueil
                Sdis.Remocra.util.Util.changeHash();
                Sdis.Remocra.util.Util.forceReload();
            }
        });
    }
});

