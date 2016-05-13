/**
 * @class Sdis.Remocra.network.ClientSession
 *
 * Gestion d'une "session" cliente :<li>
 * <ul>réinitialisation à chaque requête,</ul>
 * <ul>réinitialisation à chaque clic.</ul>
 * </li>
 */
Ext.ns('Sdis.Remocra.network');
Ext.define('Sdis.Remocra.network.ClientSession', {
        singleton: true,

    logoutInactivityDelay: 30*1000, // délai d'inactivité permise
    logoutResponseDelay: 10*1000, // délai de réponse à la question posée
    autoRefreshProcId: null, // id utilisé pour le timer d'inactivité
    task: null, // task utilisée pour le timer réponse
    listeningCom: false,

    // private
    startLogoutTimer : function() {
        this.stopLogoutTimer();
        this.autoRefreshProcId = setInterval(Ext.bind(this.autoLogout, this, []), this.logoutInactivityDelay);
    },
    stopLogoutTimer : function() {
        if(this.autoRefreshProcId) {
            clearInterval(this.autoRefreshProcId);
        }
    },
    // Handler de l'inactivité
    // private
    autoLogout : function() {
        Ext.MessageBox.confirm(
            'Déconnexion',
            'Le manque d\'activité va entrainer la déconnexion de l\'application dans '
            + Math.round(this.logoutResponseDelay/1000)
            + ' secondes.<br/>Souhaitez-vous continuer votre travail ?',
            this.handleAutoLogout,
            this
        );
        // Démarrage timer réponse pour forcer la déconnexion si aucune réponse dans le délai
        this.task = new Ext.util.DelayedTask(Sdis.Remocra.network.ServerSession.deauthenticate, this);
        this.task.delay(this.logoutResponseDelay);
    },
    // Handler réponse utilisateur cas d'inactivité
    // private
    handleAutoLogout: function(btn) {
        if ('yes' == btn) {
            //reset/restart the timer
            this.startLogoutTimer();
            this.task.cancel();
            Sdis.Remocra.network.ServerSession.keepConnection();
        } else {
            Sdis.Remocra.network.ServerSession.deauthenticate();
        }
    },
    // Définit les délais d'activité
    setDelaysSec : function(logoutInactivityDelay, logoutResponseDelay) {
        this.logoutInactivityDelay = logoutInactivityDelay*1000;
        this.logoutResponseDelay = logoutResponseDelay*1000;
    },
    // Ecoute de l'activité : requêtes serveur
    startListeningCom: function() {
        Ext.Ajax.on('beforerequest', this.startLogoutTimer, this);
        this.startLogoutTimer();
        this.listeningCom = true;
    },
    stopListeningCom: function() {
        // Démarrage de l'écoute de l'activité : requêtes serveur
        Ext.Ajax.un('beforerequest', this.startLogoutTimer, this);
        this.listeningCom = false;
        if (!this.listeningUser) {
            this.stopLogoutTimer();
        }
    }
});
