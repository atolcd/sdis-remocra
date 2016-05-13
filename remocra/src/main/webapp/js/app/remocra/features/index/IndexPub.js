Ext.define('Sdis.Remocra.features.index.IndexPub', {
    extend: 'Ext.Panel',
    alias: 'widget.crIndexPub',

    title: 'Plateforme départementale des risques REMOcRA',
    id: 'indexpub',
    
    url: null,

    layout:'column',
    defaults: { },
    
    initComponent: function() {
        Ext.getCmp('backHome').setVisible(false);
        
        // Colonne gauche
        var leftCol = {
                columnWidth:'1',
            defaults: { border: false },
            items:[{
                cls: 'htmlpanel',
                border: false,
                loader: {
                    url: BASE_URL+'/../ext-res/html/index/index.html',
                    renderer: 'html',
                    autoLoad: true
                }
            }]
        };
        
        var login = Sdis.Remocra.network.ServerSession.getUserData('login');
        // Colonne droite
        var rightCol = {
            width: login==null||login=='' ? 350 : 0,
            defaults: { border: false},
            items:[{
                style: 'margin: 0px 10px 10px 10px',
                bodyStyle: 'padding:10px 0 10px 10px',
                title: 'S\'identifier',
                cls: 'index-bloc',
                items: new Sdis.Remocra.auth.AuthPanel({
                    id: 'indexAuthPanel',
                    msg: false,
                    inAWindow: false,
                    listeners: {
                        'ok': this.auth
                    }
                })
            },{
                style: 'margin: 20px 10px 10px 10px',
                bodyStyle: 'padding:10px 0 10px 15px',
                cls: 'index-bloc indexbloc-inscription',
                title: 'S\'inscrire',
                loader: {
                    url: BASE_URL+'/../ext-res/html/index/inscription.html',
                    renderer: 'html',
                    autoLoad: true
                }
            }]
        };
        
        Ext.apply(this, {
            items: [ leftCol, rightCol ],
            buttonAlign: 'left'
        });
        
        this.callParent(arguments);
    },
    
    // Scope : le AuthPanel
    // TODO voir pour factoriser ce code
    auth: function() {
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