Ext.require('Sdis.Remocra.widget.Iframe');
Ext.require('Sdis.Remocra.features.index.IndexPub');
Ext.require('Sdis.Remocra.features.index.IndexPri');

Ext.require('Sdis.Remocra.features.adresses.Adresses');
Ext.require('Sdis.Remocra.features.dfci.Dfci');
Ext.require('Sdis.Remocra.features.permis.Permis');
Ext.require('Sdis.Remocra.features.hydrants.Hydrant');
Ext.require('Sdis.Remocra.features.oldebs.Oldeb');
Ext.require('Sdis.Remocra.features.hydrants.AdminAnomalie');
Ext.require('Sdis.Remocra.features.risques.Risques');
Ext.require('Sdis.Remocra.features.rci.Rci');
Ext.require('Sdis.Remocra.features.cartographie.Cartographie');
Ext.require('Sdis.Remocra.features.cartographie.Map');

Ext.require('Sdis.Remocra.features.metadonnees.Metadonnees');
Ext.require('Sdis.Remocra.features.traitements.Traitements');

Ext.require('Sdis.Remocra.features.documents.AdminBlocDocument');

Ext.require('Sdis.Remocra.features.hydrants.declahydrant.DeclaHydrantWindow');
Ext.require('Sdis.Remocra.features.adresses.delib.DelibWindow');
Ext.require('Sdis.Remocra.features.dfci.receptravaux.RecepTravauxWindow');

Ext.ns('Sdis.Remocra');
Ext.define('Sdis.Remocra.controller.Router', {
    extend: 'Ext.app.Controller',
    routes: [],
    TOKEN_DELIM: '/',

    init: function() {
        this.initRoutes();
        Ext.util.History.init(function() {
            Ext.util.History.on('change', this.handleHistoryChange, this);
        }, this);
    },

    handleHistoryChange: function(token1, eOpts, forceReload) {
        var token = token1 || "";
        var desc = this.getDescFromRoute(token);
        if (!desc) {
            Sdis.Remocra.util.Msg.msg("Routing", "Action inconnue", 3);
            return;
        } else if (desc === 'nothingmore') {
            // le code 'nothingmore' indique qu'il n'y a plus rien à faire.
            // Par exemple, une action sans mise à jour du panneau central
            // Comme on souhaite historiser quand même, on retourne VRAI
            return true;
        }
        var contentPanel = Ext.getCmp('pageContent');
        var currentFeature = contentPanel.items.getAt(0);
        // Si le panel accepte le changement d'url : On ne recharge pas tout et
        // on signale au panel le changement.
        if (!forceReload && currentFeature.xtype == desc.xtype && currentFeature.urlChanged) {
            currentFeature.urlChanged(desc);
            return true;
        }

        contentPanel.removeAll(true);

        var cfg = {
            cls: "remocra-cmp",
            ctCls: 'remocra-cmp-ct',
            border: false,
            bodyBorder: false,
            defaults: {
                border: false
            }
        };
        try {
            Ext.getCmp('backHome').setVisible(true);
            contentPanel.add(Ext.apply(cfg, desc));
        } catch (e) {
            var errorTypeMsg = (e.message == "types[config.xtype || defaultType] is not a constructor" ? "Cette fonctionnalité n'est pas encore implémentée :"
                    : "Une erreur est survenue :");
            var html = "<div style='padding:20px;'><div style='font-weight:bold;'>" + errorTypeMsg + "<br/><br/></div>"
                    + "<div style='font-size:small;font-style:italic;color:grey;'>Token : " + token + "<br/></div>" + "<div style='font-size:small;font-style:italic;color:red;'>"
                    + e + "</div>" + "<div style='font-size:small;font-style:italic;color:red;'>" + (e.stack ? e.stack.replace(/@/g, "@<br/>").replace(/ at /g, " at<br/>") : "")
                    + "</div>";
            contentPanel.add(Ext.apply(cfg, {
                xtype: 'panel',
                html: html
            }));
            // throw e;
        }
        contentPanel.doLayout();
        return true;
    },

    getDefaultDesc: function(token) {
        return this.getDescFromRoute("gene/index");
    },
    getDescFromRoute: function(token) {
        var i;
        var parts = token.split(this.TOKEN_DELIM);
        if (parts.length < 2) {
            if (parts.length < 1) {
                return this.getDefaultDesc(); // page par défaut
            }
            parts.push('index');
        }
        var p1 = parts[0];
        var p2 = parts[1];

        var mapP1 = this.routes[p1];
        if (!mapP1) {
            return this.getDefaultDesc(); // page par défaut
        }
        var desc = mapP1[p2];
        if (!desc) {
            return this.getDefaultDesc(); // page par défaut
        }
        // Cas d'une description dynamique (par exemple, dépend du mode
        // authentifié, etc.)
        if (Ext.isFunction(desc.dynamicDesc)) {
            desc = desc.dynamicDesc();
        }
        desc.p1 = p1;
        desc.p2 = p2;
        var extraParams = {};
        var key, value;
        for (i = 2; i + 1 < parts.length; i = i + 2) {
            key = parts[i];
            value = parts[i + 1];
            extraParams[key] = value;
        }
        desc.extraParams = extraParams;
        return desc;
    },

    initRoutes: function() {
        this.routes['gene'] = this.getGeneRoutes();

        this.routes['adresses'] = this.getAdressesRoutes();
        this.routes['dfci'] = this.getDfciRoutes();
        this.routes['permis'] = this.getPermisRoutes();
        this.routes['hydrants'] = this.getHydrantsRoutes();
        this.routes['oldebs'] = this.getOldebsRoutes();
        this.routes['risques'] = this.getRisquesRoutes();
        this.routes['rci'] = this.getRciRoutes();
        this.routes['cartographie'] = this.getCartographieRoutes();

        this.routes['metadonnees'] = this.getMetadonneesRoutes();
        this.routes['traitements'] = this.getTraitementsRoutes();

        this.routes['admin'] = this.getAdmiRoutes();
        this.routes['profil'] = this.getProfilRoutes();
    },
    getGeneRoutes: function() {
        var routes = [];
        // Cas d'une description dynamique : si utilisateur connecté, privé,
        // sinon public
        routes['index'] = {
            dynamicDesc: function() {
                var login = Sdis.Remocra.network.ServerSession.getUserData('login');
                if (login == null || login == '') {
                    return routes['indexpub'];
                }
                return routes['indexpriv'];
            }
        };
        routes['indexpub'] = {
            xtype: 'crIndexPub'
        };
        routes['indexpriv'] = {
            xtype: 'crIndexPri'
        };
        return routes;
    },
    getOldebsRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crOldeb'
        };
        routes['obligation'] = {
            xtype: 'crOldeb'
        };
        routes['localisation'] = {
            xtype: 'crOldeb'
        };
        routes['access'] = {
            xtype: 'crOldeb'
        };
        routes['documents'] = {
            xtype: 'crOldeb'
        };
        return routes;
    },
    getAdressesRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crAdresses'
        };
        // Cas d'une description dynamique : si dépot délib, fenêtre
        routes['delib'] = {
            dynamicDesc: function() {
                if (Sdis.Remocra.Rights.hasRight('DEPOT_DELIB_C')) {
                    new Sdis.Remocra.features.adresses.delib.DelibWindow().show();
                }
                // Le code 'nothing' permet d'indiquer qu'il n'y a plus rien à
                // faire
                return 'nothingmore';
            }
        };
        return routes;
    },
    getDfciRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crDfci'
        };
        // Cas d'une description dynamique : si dépot délib, fenêtre
        routes['receptravaux'] = {
            dynamicDesc: function() {
                if (Sdis.Remocra.Rights.hasRight('DEPOT_RECEPTRAVAUX_C')) {
                    new Sdis.Remocra.features.dfci.receptravaux.RecepTravauxWindow().show();
                }
                // Le code 'nothing' permet d'indiquer qu'il n'y a plus rien à
                // faire
                return 'nothingmore';
            }
        };
        return routes;
    },
    getPermisRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crPermis'
        };
        return routes;
    },
    getHydrantsRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crHydrants'
        };
        routes['access'] = {
            xtype: 'crHydrants'
        };
        routes['indispos'] = {
            xtype: 'crHydrants'
        };
        routes['requetage'] = {
            xtype: 'crHydrants'
        };
        routes['tournees'] = {
            xtype: 'crHydrants'
        };
        routes['localisation'] = {
            xtype: 'crHydrants'
        };
        routes['hydrants'] = {
            xtype: 'crHydrants'
        };
        routes['documents'] = {
            xtype: 'crHydrants'
        };
        routes['anomalies'] = {
            xtype: 'adminanomalie'
        };
        routes['prescrits'] = {
            xtype: 'prescritsMap'
        };
        // Cas d'une description dynamique : si dépot délib, fenêtre
        routes['declahydrant'] = {
            dynamicDesc: function() {
                if (Sdis.Remocra.Rights.hasRight('DEPOT_DECLAHYDRANT_C')) {
                    new Sdis.Remocra.features.hydrants.declahydrant.DeclaHydrantWindow().show();
                }
                // Le code 'nothing' permet d'indiquer qu'il n'y a plus rien à
                // faire
                return 'nothingmore';
            }
        };
        return routes;
    },
    getRisquesRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crRisques'
        };
        return routes;
    },
    getRciRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crRci'
        };
        return routes;
    },
    getCartographieRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crCartographie'
        };
        routes['carte'] = {
                xtype: 'crCartographieMap'
            };
        return routes;
    },

    getMetadonneesRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crMetadonnees'
        };
        return routes;
    },
    getTraitementsRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crTraitements'
        };
        return routes;
    },

    getAdmiRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crAdmin'
        };
        routes['documents'] = {
            xtype: 'crAdminBlocDocument'
        };
        return routes;
    },

    getProfilRoutes: function() {
        var routes = [];
        routes['index'] = {
            xtype: 'crMonProfil'
        };
        routes['password'] = {
            xtype: 'crResetPassword'
        };
        return routes;
    }
});
