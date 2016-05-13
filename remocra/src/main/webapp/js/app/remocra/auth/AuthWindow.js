
Ext.require('Sdis.Remocra.auth.AuthPanel');

Ext.ns('Sdis.Remocra.auth');
Ext.define('Sdis.Remocra.auth.AuthWindow', {
    extend: 'Ext.Window',
    requires: [],
    alias: 'widget.remocra.authwin',
    
    id: 'authWindow',
    cls: 'remocra-authwin',
    
    authPanel: null, width: 500,

    initComponent: function() {
        this.authPanel = new Sdis.Remocra.auth.AuthPanel();
        
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
                    Sdis.Remocra.util.Util.forceReload();
                }
            }
        });
    }
});

