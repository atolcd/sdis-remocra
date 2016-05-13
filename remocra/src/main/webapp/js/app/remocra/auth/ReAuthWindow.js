
Ext.require('Sdis.Remocra.auth.AuthPanel');

Ext.ns('Sdis.Remocra.auth');
Ext.define('Sdis.Remocra.auth.ReAuthWindow', {
    extend: 'Ext.Window',
    requires: [],
    alias: 'widget.remocra.reauthwin',
    
    id: 'authWindow',
    cls: 'remocra-authwin',
    
    authPanel: null, width: 500,
    
    initComponent: function() {
        this.authPanel = new Sdis.Remocra.auth.AuthPanel({msg:
            'La session a pris fin.<br/>Vous pouvez vous reconnecter ou bien annuler et retourner sur la page en mode non authentifié.'
        });
        
        this.authPanel.on('ok', Ext.bind(this.ok, this.authPanel));
        this.authPanel.on('cancel', Ext.bind(this.cancel, this.authPanel));
        
        Ext.apply(this, {
            title: 'Authentification',
            modal: true,
            constrain: true,
            autoHeight: true,
            buttonAlign: 'center',
            items: this.authPanel
        });

        this.callParent(arguments);
    },
    
    // Scope : le AuthPanel
    cancel: function() {
        Sdis.Remocra.network.ServerSession.cancelWR();
        this.ownerCt.close();
    },
    
    // Scope : le AuthPanel
    ok: function() {
        var formP = this.query("form")[0];
        if (formP.getForm().isValid() !== true) {
            this.showHideErrorMsg('Les informations saisies sont incomplètes.');
            return;
        }
        this.showHideErrorMsg('Authentification en cours...');
        var extraParams = {
            username: formP.getComponent('loginF').getValue(),
            password: formP.getComponent('passwordF').getValue(),
            dontreauth: true
        };
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../auth/login'),
            method: 'POST',
            params: extraParams,
            scope: this,
            callback: function(options, success, response) {
                var jsResp = Ext.decode(response.responseText);
                if (!success || !jsResp.success) {
                    this.showHideErrorMsg('Impossible de vous identifier avec les informations fournies. Veuillez recommencer.');
                } else {
                    // Test si profil a changé
                    var oldLogin = Sdis.Remocra.network.ServerSession.getUserData('login');
                    var newLogin = jsResp.data.identifiant;
                    
                    if (oldLogin!=null && newLogin!=oldLogin) {
                        // Profils différents => on recharge pour gérer les inclusions qui peuvent être différentes
                        // Si pas d'utilisateur loggué avant : On recharge directement
                        if (oldLogin == ''){
                            Sdis.Remocra.util.Util.forceReload();
                        }
                        //Si un profil était déjà chargé, le loginWatch (Sdis.Remocra.network.ServerSession) s'en chargera
                        
                    } else {
                        // Authentifié
                        Sdis.Remocra.util.Msg.msg("Authentification",
                            "Vous êtes maintenant identifié en '"+newLogin+"'");
                        
                        // Mise à jour des info (GUI) et fermeture fenêtre
                        Sdis.Remocra.appInstance.updUserInfo(true, newLogin);
                        this.ownerCt.close();

                        // On rejoue les requêtes
                        Sdis.Remocra.network.ServerSession.runWR();
                    }
                }
            }
        });
    }
});