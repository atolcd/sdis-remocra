
// A voir car a priori, autoload très lent...
//Ext.Loader.setConfig({ enabled : false });

Proj4js.defs["EPSG:2154"] = "+proj=lcc +lat_1=49 +lat_2=44 +lat_0=46.5 +lon_0=3 +x_0=700000 +y_0=6600000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";

Ext.require('Sdis.Remocra.bugfixes.Bugfixes');
Ext.require('Sdis.Remocra.widget.Locale');
Ext.require('Ext.ux.grid.plugin.HeaderFilters');

Ext.require('Ext.data.proxy.Rest');
Ext.require('Ext.data.reader.Json');

Ext.require('Ext.data.association.HasMany');
        
Ext.require('Ext.container.Viewport');
Ext.require('Ext.data.DataProxy');
Ext.require('Ext.util.Observable');
Ext.require('Ext.window.MessageBox');
Ext.require('Ext.tip.QuickTip');

Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.network.FK');

// FIXME Pourquoi mettre cette dépendance ici ? 
//Ext.require('Sdis.Remocra.model.Protocole');

// En première position pour "withBaseUrl" qui est utilisé lors de définitions
Ext.require('Sdis.Remocra.util.Util');
Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.require('Sdis.Remocra.network.ServerSession');
Ext.require('Sdis.Remocra.network.ClientSession');
Ext.require('Sdis.Remocra.AppContainer');
Ext.require('Sdis.Remocra.network.Ajax');
Ext.require('Sdis.Remocra.Rights');


Ext.require('Sdis.Remocra.features.admin.Admin');
Ext.require('Sdis.Remocra.features.adresses.Adresses');
Ext.require('Sdis.Remocra.features.dfci.Dfci');
Ext.require('Sdis.Remocra.features.permis.Permis');
Ext.require('Sdis.Remocra.features.hydrants.Hydrant');
Ext.require('Sdis.Remocra.features.prescrits.Map');
Ext.require('Sdis.Remocra.features.profil.MonProfil');
Ext.require('Sdis.Remocra.features.profil.ResetPassword');
Ext.require('Sdis.Remocra.features.risques.Risques');
Ext.require('Sdis.Remocra.features.rci.Rci');
Ext.require('Sdis.Remocra.features.cartographie.Cartographie');
Ext.require('Sdis.Remocra.features.traitements.Traitements');

Ext.require('Sdis.Remocra.network.TypeAlerteEltStore');

Ext.ns('Sdis.Remocra.App');

//Pour la gestion des dépendances
Ext.define('Sdis.Remocra.App', {});

Ext.application({
    name: 'Sdis.Remocra',
    enableQuickTips: true,
    
    appFolder : '/remocra/resources/js/app/remocra',
    
    controllers: ['Metadonnee',
                  'hydrant.Hydrant', 'hydrant.Fiche', 'hydrant.Admin', 'hydrant.Prescrit',
                  'documents.Admin', 'documents.Fiche',
                  'rci.Rci', 'rci.Fiche',
                  'cartographie.Cartographie',
                  'Router'],
    
    launch: function() {

        // On stocke une référence sur l'instance de l'application
        Sdis.Remocra.appInstance = this;
        
        Ext.getBody().addCls(REMOCRA_INFO_MODE!='info'?'mode-debug':'mode-info');
        
        // Initialisation des informations utilisateur
        this.updUserInfo(false, REMOCRA_USR_IDENTIFIANT);
        
        // Lecture de la configuration des hydrants
        this.configHydrant = Ext.decode(HYDRANT_CFG);
        
        Sdis.Remocra.Rights.load(REMOCRA_USR_RIGHTS);
        
        // AppContainer crée les noeuds DOM avant de créer les composants (pageContent, Toolbar)
        this.appContainer = Ext.create('Sdis.Remocra.AppContainer',{renderTo:'pageCenter'});
        
        // Ecoute des erreurs
        Sdis.Remocra.network.Ajax.startListeningErrors();
        
        // Mise à jour des informations de l'utilisateur
        this.updUserInfoGUI();
        
        // Chargement du premier contenu
        if (!this.executeJsFromParams()) {
            Sdis.Remocra.util.Util.changeHash("");
        }
        
        // Si utilisateur connecté, on gère son (in)activité // session
        var login = Sdis.Remocra.network.ServerSession.getUserData('login');
        if (login!=null && login.length>0) {
            Sdis.Remocra.network.ClientSession.setDelaysSec(
                typeof(REMOCRA_MAXINACTIVEINTERVAL_SEC)=='number' ? REMOCRA_MAXINACTIVEINTERVAL_SEC : 1800,
                30);
            Sdis.Remocra.network.ClientSession.startListeningCom();
            // Mise à jour cookie (cas où one ne passe jamais par formulaire)
            var expires = new Date();
            expires.setDate(expires.getDate()+7); // Expiration à 7 jours
            Ext.util.Cookies.set("loginUser", login, expires);
        }
        
        Sdis.Remocra.network.ServerSession.startLoginWatcher();
    },

    /*******************************************/
    /* Gestion exécution script via paramètre  */
    /*******************************************/
    // Execution éventuelle d'un script si le paramètre "tpircsavaj" est présent
    executeJsFromParams: function() {
        var parameters = Sdis.Remocra.util.Util.getParameters();
        if (parameters.tpircsavaj) {
            // Paramètre javascript "pur"
            eval(parameters.tpircsavaj);
        } else {
            // Paramètre "token" dans l'URL (après #)
            var token = Sdis.Remocra.util.Util.getHashTokenNoSharp();
            this.getController('Router').handleHistoryChange(token);
        }
        return true;
    },
    
    updUserInfo: function(gui, login) {
        
        // Données
        Sdis.Remocra.network.ServerSession.setUserData('login', login);
        
        if (gui) {
            this.updUserInfoGUI();
        }
    },
    
    updUserInfoGUI: function() {
        // Données
        var login = Sdis.Remocra.network.ServerSession.getUserData('login');
        
        var authBtn = Ext.getCmp('authBtn');
        var authInfo = Ext.getCmp('authInfo');
        var deauthBtn = Ext.getCmp('deauthBtn');
        if (!authBtn || !authInfo || !deauthBtn) {
            // Les boutons n'existent pas encore
            return;
        }
        if (login!=null && login!='') {
            // Authentifié
            authInfo.setText(login);
            authInfo.setVisible(true);
            deauthBtn.setVisible(true);
            authBtn.setVisible(false);
        } else {
            // Non authentifié
            authInfo.setText("&nbsp;");
            authInfo.setVisible(false);
            deauthBtn.setVisible(false);
            authBtn.setVisible(true);
        }
    },
    
    downloadUrl: function(url){
             
        //On verifie que l'utilisateur à bien les droits en accedant à l'url mais en ajax/Json.
        // Si le requestmapping JSON n'est pas fait, on telechargera le fichier 2 fois... mais ça ne devrait pas casser.
        Ext.Ajax.request({
            url: url,
            headers : {
                'Accept' : 'application/json,application/xml',
                'Content-Type' : 'application/json'
            },
            success: function (response) {
                Ext.DomHelper.append(document.body, {
                    tag: 'iframe',
                    id:'downloadIframe',
                    cls: 'x-hidden', 
                    src: url
                });
            }
        });
    }
});
