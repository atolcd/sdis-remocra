
Ext.require('Ext.data.proxy.Proxy');

Ext.ns('Sdis.Remocra.network');
Ext.define('Sdis.Remocra.network.Ajax', {
    singleton: true,
    
    // Ecoute de l'activité : requêtes serveur
    startListeningErrors: function() {
        // Ecoute des erreurs sur les statuts HTTP des réponses 
        Ext.util.Observable.observe(Ext.data.Connection);
        Ext.data.Connection.on('requestexception', function(conn, response, options, eOpts) {
            // 401 Unauthorized http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2
            if (response.status == 401) {
                return Sdis.Remocra.network.ServerSession.manage401response(response.request);
            }
            
            // 403 Forbidden http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4
            if (response.status == 403) {
                if (!conn.ignorealert) {
                    Ext.Msg.alert('Gestionnaire de connexions',
                        'Vous n\'avez pas les autorisations requises pour accéder à la ressource demandée.');
                }
                return false;
            }
            
            // 404 Not Found http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5
            if (response.status == 404) {
                if (response.request.options && response.request.options.baseParams
                        && response.request.options.baseParams.manage404) {
                    return true;
                }
                if (!conn.ignorealert) {
                    Ext.Msg.alert('Gestionnaire de connexions',
                        'La ressource demandée n\'a pas été trouvée.');
                }
                return false;
            }
            
            //405 Method Not Allowed http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6
            if (response.status == 405) {
                if (!conn.ignorealert) {
                    Ext.Msg.alert('Gestionnaire de connexions',
                        'La méthode n\'est pas authorisée pour la ressource demandée.');
                }
                return false;
            }
            
            // Autre erreur, sans doute 500
            // 500 Internal Server Error http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1
            if (response.status == 500) {
                if (response.request.options && response.request.options.baseParams
                        && response.request.options.baseParams.manage500) {
                    return true;
                }
            }
            var resp = response.raw!=null ? response.raw : Ext.decode(response.responseText);
            var msg = null;
            if (resp && !resp.success) {
                msg = resp.msg || resp.message;    
            }
            if (msg == null) {
                msg = "Une erreur inconnue est survenue.";
            }
            Ext.MessageBox.show({
                title: document.title || "Gestion des erreurs",
                msg: msg,
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.ERROR
            });
            return false;
        });
        
        // Ecoute des erreurs sur les statuts propriétés success des proxies
        Ext.util.Observable.observe(Ext.data.proxy.Proxy);
        Ext.data.proxy.Proxy.addListener('exception', function(proxy, response, operation, eOpts) {
            
            // On ne gere ici que les success = false donc avec status 200. On pourrait gérer les erreurs en utilisant un code 500.
            if (response.status == 200) {
                var msg = null;
                try {
                    var jsResponse = null;
                    if (response.responseText!=null) {
                        jsResponse = Ext.decode(response.responseText);
                        if (typeof(jsResponse.success)!='undefined' && !jsResponse.success) {
                            msg = typeof(jsResponse.msg)!='undefined'?jsResponse.msg
                                : (typeof(jsResponse.message)!='undefined'?jsResponse.message:null);    
                        }
                    }
                } catch(e) {
                    // Rien
                }
                if (msg == null) {
                    var part2 = typeof(operation.action)!='undefined'?" lors de l'action '"+operation.action+"'":"";
                    msg = "Une erreur inconnue est survenue"+part2;
                }
                Ext.MessageBox.show({
                   title: document.title || "Gestion des erreurs",
                   msg: msg,
                   buttons: Ext.Msg.OK,
                   icon: Ext.MessageBox.ERROR
                });
            }
          });
    } 
});
